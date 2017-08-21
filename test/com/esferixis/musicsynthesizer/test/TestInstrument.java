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
import com.esferixis.musicsynthesizer.instrument.BellOrgan;
import com.esferixis.musicsynthesizer.instrument.ChurchOrgan;
import com.esferixis.musicsynthesizer.instrument.Drum;
import com.esferixis.musicsynthesizer.instrument.HomogeneousOrgan;
import com.esferixis.musicsynthesizer.instrument.Instrument;
import com.esferixis.musicsynthesizer.instrument.NiceOrgan;
import com.esferixis.musicsynthesizer.instrument.SawtoothOrgan;
import com.esferixis.musicsynthesizer.instrument.SineOrgan;
import com.esferixis.musicsynthesizer.instrument.SquareOrgan;
import com.esferixis.musicsynthesizer.instrument.guitar.AnalyticGuitar;
import com.esferixis.musicsynthesizer.instrument.guitar.KarplusStrongStringGuitar;
import com.esferixis.musicsynthesizer.instrument.signalSynthesizer.SignalSynthesizer;
import com.esferixis.musicsynthesizer.instrument.string.StringEventData;
import com.esferixis.musicsynthesizer.instrument.string.StringInstrument;
import com.esferixis.musicsynthesizer.signal.KarplusStrongString;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousInterpolatedSignal;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSignal;
import com.esferixis.musicsynthesizer.signal.continuous.mixer.SignalMix;
import com.esferixis.musicsynthesizer.signal.continuous.periodic.SawtoothWave;
import com.esferixis.musicsynthesizer.signal.continuous.periodic.SineWave;
import com.esferixis.musicsynthesizer.signal.continuous.periodic.SquareWave;
import com.esferixis.musicsynthesizer.signal.continuous.transformers.DiscreteInputOutputBlockSignalTransformer;
import com.esferixis.musicsynthesizer.signal.continuous.transformers.SignalTransformer;
import com.esferixis.musicsynthesizer.signal.continuous.transformers.TimeInvariantSignalTransformer;
import com.esferixis.musicsynthesizer.signal.discrete.DiscreteSignal;
import com.esferixis.musicsynthesizer.signal.discrete.NoiseDiscreteSignal;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.DiscreteFlanger;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.DiscreteForkerMixerBlock;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.DiscreteInputOutputBlockComposition;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.ValueTransformerInputOutputBlock;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.filters.AverageLowpassFilterBlock;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.filters.HighpassRCFilterBlock;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.filters.LowpassRCFilterBlock;
import com.esferixis.musicsynthesizer.signal.valueTransformer.ArctanDistorter;
import com.esferixis.musicsynthesizer.signal.valueTransformer.Clipper;
import com.esferixis.musicsynthesizer.signal.valueTransformer.PowerDistorter;
import com.esferixis.musicsynthesizer.signal.valueTransformer.SquareWaveDistorter;
import com.esferixis.musicsynthesizer.signal.valueTransformer.ValueScaleTransformer;
import com.esferixis.musicsynthesizer.signal.valueTransformer.ValueTransformer;

public final class TestInstrument {
	/**
	 * @post Carga la señal
	 */
	public static ContinuousSignal loadSignal() {
		//final Organ organ = new SineOrgan().transform(new PowerDistorter(0.5f)).transform(new SignalScaler(0.1f));
		final Instrument<StringEventData> organ = new KarplusStrongStringGuitar().transform(new DiscreteInputOutputBlockSignalTransformer(
			DiscreteInputOutputBlockComposition.createFactory(
				ValueTransformerInputOutputBlock.createFactory(new ArctanDistorter().compose(new ValueScaleTransformer(150.0f))),
				DiscreteFlanger.createFactory(20),
				DiscreteForkerMixerBlock.createFactory(
					DiscreteInputOutputBlockComposition.createFactory(LowpassRCFilterBlock.createFactory(0.001f), ValueTransformerInputOutputBlock.createFactory(new ValueScaleTransformer(1.0f))),
					DiscreteInputOutputBlockComposition.createFactory(DiscreteFlanger.createFactory(50), HighpassRCFilterBlock.createFactory(0.15f), ValueTransformerInputOutputBlock.createFactory(new ValueScaleTransformer(1.0f)))
				)
			)
		, 44100));
		
		final SignalSynthesizer<StringEventData> organSignalSynthesizer = organ.createSignalSynthesizer();
		
		final NoteEmmiter<Void, StringEventData> organEventEmmiter = organSignalSynthesizer.getNoteEmmiter();
		
		final NoteEmmiter<Void, StringEventData> eventEmmiter = organEventEmmiter;
		
		eventEmmiter.invoke(new ElementCallback<NoteEmmiter<Void, StringEventData>>() {
			@Override
			public void run(final NoteEmmiter<Void, StringEventData> eventEmmiter) {
				float startTime = 0.0f;
				float endTime;
				
				for ( int k = 0 ; k<3; k++ ) {
					for ( int j = 0 ; j<3; j++ ) {
						for ( int i = 0 ; i<3; i++ ) {
							endTime = startTime + 0.15f;
							eventEmmiter.emit(new Note<Void, StringEventData>(null, endTime, new Event<StringEventData>(startTime, new StringEventData(21.0f, 0.2f))));
							eventEmmiter.emit(new Note<Void, StringEventData>(null, endTime, new Event<StringEventData>(startTime, new StringEventData(21.0f+7.0f, 0.2f))));
							startTime = endTime;
							
							startTime = startTime + 0.025f;
						}
						
						startTime = startTime + 0.5f;
					}
					
					endTime = startTime + 0.75f;
					eventEmmiter.emit(new Note<Void, StringEventData>(null, endTime, new Event<StringEventData>(startTime, new StringEventData(27.0f, 0.5f))));
					eventEmmiter.emit(new Note<Void, StringEventData>(null, endTime, new Event<StringEventData>(startTime, new StringEventData(27.0f+7.0f, 0.5f))));
					startTime = endTime;
				}
				
				//eventEmmiter.emitStartMark(startTime);
				
				final float lightDecayFactor = 0.2f;
				final float heavyDecayFactor = 0.4f;
				
				for ( int l = 0 ; l<5; l++ ) {
					for ( int k = 0 ; k<3; k++ ) {
						for ( int i = 0 ; i<6; i++ ) {
							endTime = startTime + 0.15f;
							eventEmmiter.emit(new Note<Void, StringEventData>(null, endTime, new Event<StringEventData>(startTime, new StringEventData(20.0f, lightDecayFactor))));
							eventEmmiter.emit(new Note<Void, StringEventData>(null, endTime, new Event<StringEventData>(startTime, new StringEventData(20.0f+7.0f, lightDecayFactor))));
							startTime = endTime;
						}
						
						endTime = startTime + 0.3f;
						eventEmmiter.emit(new Note<Void, StringEventData>(null, endTime, new Event<StringEventData>(startTime, new StringEventData(20.0f, lightDecayFactor))));
						eventEmmiter.emit(new Note<Void, StringEventData>(null, endTime, new Event<StringEventData>(startTime, new StringEventData(20.0f+7.0f, lightDecayFactor))));
						startTime = endTime;
					}
					
					for ( int i = 0 ; i<6; i++ ) {
						endTime = startTime + 0.1f;
						eventEmmiter.emit(new Note<Void, StringEventData>(null, endTime, new Event<StringEventData>(startTime, new StringEventData(20.0f, lightDecayFactor))));
						eventEmmiter.emit(new Note<Void, StringEventData>(null, endTime, new Event<StringEventData>(startTime, new StringEventData(20.0f+7.0f, lightDecayFactor))));
						startTime = endTime;
					}
					
					endTime = startTime + 0.5f;
					eventEmmiter.emit(new Note<Void, StringEventData>(null, endTime, new Event<StringEventData>(startTime, new StringEventData(25.0f, heavyDecayFactor))));
					eventEmmiter.emit(new Note<Void, StringEventData>(null, endTime, new Event<StringEventData>(startTime, new StringEventData(25.0f+7.0f, heavyDecayFactor))));
					startTime = endTime;
				}
			}
		});
		
		return organSignalSynthesizer.synthesize();
	}
}
