/**
 * Copyright (c) 2017 Ariel Favio Carrizo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'esferixis' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.esferixis.musicsynthesizer.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.esferixis.math.ExtraMath;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.ElementCallback;
import com.esferixis.musicsynthesizer.composition.Event;
import com.esferixis.musicsynthesizer.composition.note.Note;
import com.esferixis.musicsynthesizer.composition.note.NoteGroup;
import com.esferixis.musicsynthesizer.composition.note.emmiter.NoteEmmiter;
import com.esferixis.musicsynthesizer.instrument.ChurchOrgan;
import com.esferixis.musicsynthesizer.instrument.HomogeneousOrgan;
import com.esferixis.musicsynthesizer.instrument.Instrument;
import com.esferixis.musicsynthesizer.instrument.NiceOrgan;
import com.esferixis.musicsynthesizer.instrument.SineOrgan;
import com.esferixis.musicsynthesizer.instrument.SquareOrgan;
import com.esferixis.musicsynthesizer.instrument.organ.OrganEventData;
import com.esferixis.musicsynthesizer.instrument.signalSynthesizer.SignalSynthesizer;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousInterpolatedSignal;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSignal;
import com.esferixis.musicsynthesizer.signal.continuous.mixer.SignalMix;
import com.esferixis.musicsynthesizer.signal.continuous.periodic.SineWave;
import com.esferixis.musicsynthesizer.signal.continuous.periodic.SquareWave;
import com.esferixis.musicsynthesizer.signal.discrete.DiscreteSignal;
import com.esferixis.musicsynthesizer.signal.discrete.NoiseDiscreteSignal;
import com.esferixis.musicsynthesizer.signal.valueTransformer.PowerDistorter;

public final class TestSong {
	/**
	 * @post Carga la señal
	 */
	public static ContinuousSignal loadSignal() {
		//final Organ organ = new SineOrgan().transform(new PowerDistorter(0.5f)).transform(new SignalScaler(0.1f));
		final Instrument<OrganEventData> organ = new HomogeneousOrgan(new ContinuousSignal() {
			final DiscreteSignal noiseSignal = new NoiseDiscreteSignal();
			final int maxLevel = 16;
			
			private float levelSignalValue(final int level, final float t) {
				return new DiscreteSignal() {

					@Override
					public float getValue(int t) {
						return noiseSignal.getValue((t / (maxLevel - level + 1) * (maxLevel - level + 1)) % maxLevel);
					}
					
				}.continuousInterpolatedSignal(FloatClosedInterval.WIDESTFINITE).getValue(t / (float) maxLevel);
			}
			
			@Override
			protected float uncheckedGetValue(float t) {
				final float level = (float) Math.exp((float) maxLevel / (t / 44100.0f));
				final float levelFloor = (float) Math.floor(level);
				final int levelFloor_int = (int) levelFloor;
				final float levelFrac = level - levelFloor;
				final float result;
				
				if ( levelFrac == 0.0f ) {
					result = levelSignalValue(levelFloor_int, t);
				}
				else {
					result = ExtraMath.polynomialInterpolation(levelSignalValue(levelFloor_int, t), levelSignalValue(levelFloor_int+1, t), levelFrac);
				}
				
				return result;
			}
			
		});
		final SignalSynthesizer<OrganEventData> organSignalSynthesizer = organ.createSignalSynthesizer();
		
		final NoteEmmiter<Void, OrganEventData> organEventEmmiter = organSignalSynthesizer.getNoteEmmiter();
		
		final NoteEmmiter<Void, OrganEventData> noteEmmiter = organEventEmmiter;
		
		NoteGroup<Void, OrganEventData> noteGroup = NoteGroup.create(new ElementCallback<NoteEmmiter<Void, OrganEventData>>() {
	
			@Override
			public void run(final NoteEmmiter<Void, OrganEventData> noteEmmiter) {
				noteEmmiter.emit(new Note<Void, OrganEventData>(null, 8.0f, new Event<OrganEventData>(0.0f, new OrganEventData(50.0f))));
			}
			
		});
		
		for ( int i = 0 ; i < 2 ; i++ ) {
			noteGroup = noteEmmiter.emit(noteGroup).addTime(noteGroup.getTimeInterval().length());
		}
		
		return organSignalSynthesizer.synthesize().valueScale(0.5f);
	}
}
