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
package com.esferixis.musicsynthesizer.songs;

import com.esferixis.misc.ElementCallback;
import com.esferixis.musicsynthesizer.composition.Event;
import com.esferixis.musicsynthesizer.composition.note.Note;
import com.esferixis.musicsynthesizer.composition.note.emmiter.NoteEmmiter;
import com.esferixis.musicsynthesizer.composition.note.emmiter.TrackRouterNoteEmmiter;
import com.esferixis.musicsynthesizer.instrument.Instrument;
import com.esferixis.musicsynthesizer.instrument.NiceOrgan;
import com.esferixis.musicsynthesizer.instrument.guitar.KarplusStrongStringGuitar;
import com.esferixis.musicsynthesizer.instrument.signalSynthesizer.SignalSynthesizer;
import com.esferixis.musicsynthesizer.instrument.string.StringEventData;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSignal;
import com.esferixis.musicsynthesizer.signal.continuous.transformers.DiscreteInputOutputBlockSignalTransformer;
import com.esferixis.musicsynthesizer.signal.continuous.transformers.TimeInvariantSignalTransformer;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.filters.AverageLowpassFilterBlock;
import com.esferixis.musicsynthesizer.signal.valueTransformer.ArctanDistorter;
import com.esferixis.musicsynthesizer.signal.valueTransformer.SquareWaveDistorter;
import com.esferixis.musicsynthesizer.signal.valueTransformer.ValueScaleTransformer;

public class Metal {
	public enum Track {
		GUITAR
	}
	
	/**
	 * @post Carga la señal
	 */
	public static ContinuousSignal loadSignal() {
		final float decayFactor = 0.7f;
		
		final Instrument<StringEventData> organ = new KarplusStrongStringGuitar().transform(new TimeInvariantSignalTransformer(new SquareWaveDistorter())).transform(new DiscreteInputOutputBlockSignalTransformer(AverageLowpassFilterBlock.createFactory(30), 44100));
		
		final SignalSynthesizer<StringEventData> organSignalSynthesizer = organ.createSignalSynthesizer();
		
		final NoteEmmiter<Void, StringEventData> organEventEmmiter = organSignalSynthesizer.getNoteEmmiter();
		
		final NoteEmmiter<Void, StringEventData> eventEmmiter = organEventEmmiter;
		
		new NoteEmmiter<Void, StringEventData>() {

			@Override
			protected void emitInternal(Note<Void, StringEventData> note) {
				eventEmmiter.emit(note);
				/*
				eventEmmiter.emit(note.addPitch(7.0f));
				eventEmmiter.emit(note.addPitch(12.0f));
				eventEmmiter.emit(note.addPitch(19.0f));
				*/
			}

			@Override
			public void emitStartMark(float time) {
				eventEmmiter.emitStartMark(time);
			}

			@Override
			public void emitEndMark(float time) {
				eventEmmiter.emitEndMark(time);
			}
			
		}.invoke(new ElementCallback<NoteEmmiter<Void, StringEventData>>() {
			@Override
			public void run(final NoteEmmiter<Void, StringEventData> eventEmmiter) {
				float startTime = 0.0f;
				
				for ( int i = 0 ; i<3 ; i++ ) {
					eventEmmiter.emit(new Note<Void, StringEventData>(null, 2.0f + startTime, new Event<StringEventData>(0.0f + startTime, new StringEventData( 21.0f, decayFactor ) )));
					
					eventEmmiter.emit(new Note<Void, StringEventData>(null, 2.5f + startTime, new Event<StringEventData>(2.0f + startTime, new StringEventData( 24.0f, decayFactor) )));
					eventEmmiter.emit(new Note<Void, StringEventData>(null, 3.25f + startTime, new Event<StringEventData>(2.5f + startTime, new StringEventData( 23.0f, decayFactor ))));
					
					eventEmmiter.emit(new Note<Void, StringEventData>(null, 6.25f + startTime, new Event<StringEventData>(3.25f + startTime, new StringEventData( 21.0f, decayFactor) )));
					
					startTime += 6.25f;
				}
				
				float[] pitches = { 21.0f, 21.0f, 23.0f };
				for ( int i = 0 ; i<30 ; i++ ) {
					startTime += 0.15f;
					float endTime = startTime + 0.15f;
					eventEmmiter.emit(new Note<Void, StringEventData>(null, endTime, new Event<StringEventData>(startTime, new StringEventData( pitches[i % pitches.length], decayFactor))));
				}
			}
		});
		
		return organSignalSynthesizer.synthesize();
	}
}
