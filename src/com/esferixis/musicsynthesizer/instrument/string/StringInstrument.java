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
package com.esferixis.musicsynthesizer.instrument.string;

import java.util.List;

import com.esferixis.math.ExtraMath;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.musicsynthesizer.composition.Event;
import com.esferixis.musicsynthesizer.composition.note.Note;
import com.esferixis.musicsynthesizer.instrument.Instrument;
import com.esferixis.musicsynthesizer.instrument.organ.OrganEventData;
import com.esferixis.musicsynthesizer.instrument.signalSynthesizer.PerNoteSignalSynthesizer;
import com.esferixis.musicsynthesizer.instrument.signalSynthesizer.SignalSynthesizer;
import com.esferixis.musicsynthesizer.signal.KarplusStrongString;
import com.esferixis.musicsynthesizer.signal.KarplusStrongString.SampleInputData;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSampledSignal;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSignal;

public class StringInstrument extends Instrument<StringEventData> {
	private final KarplusStrongString karplusStrongString;
	private final int frequencyRate;
	private final float decayTime;
	
	/**
	 * @post Crea el instrumento con el tipo de cuerda, la frecuencia
	 * 		 de muestreo y el tiempo de decaimento especificados
	 */
	public StringInstrument(KarplusStrongString karplusStrongString, int frequencyRate, float decayTime) {
		this.karplusStrongString = karplusStrongString;
		this.frequencyRate = frequencyRate;
		this.decayTime = decayTime;
	}
	
	/**
	 * @post Crea el instrumento con el tipo de cuerda y la frecuencia
	 * 		 de muestreo especificados
	 */
	public StringInstrument(KarplusStrongString karplusStrongString, int frequencyRate) {
		this(karplusStrongString, frequencyRate, 0.0f);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.musicsynthesizer.instrument.Organ#createSignalSynthesizer()
	 */
	@Override
	public SignalSynthesizer<StringEventData> createSignalSynthesizer() {
		return new PerNoteSignalSynthesizer<StringEventData>() {

			@Override
			protected ContinuousSignal synthesizeNote(Note<Void, StringEventData> note) {
				final int samplesLength = (int) ( Math.floor( (note.getEndTime()-note.getStartTime()+decayTime) * (float) StringInstrument.this.frequencyRate) ) + 1;
				
				final KarplusStrongString.SampleInputData[] samplesStringLengthPerSample = new KarplusStrongString.SampleInputData[samplesLength];
				final float[] amplitude = new float[samplesLength];
				
				int segments = note.getEvents().size() + ( ( StringInstrument.this.decayTime != 0.0f) ? 1 : 0 );
				
				for ( int i = 0 ; i<segments; i++ ) {
					final float pitch1, pitch2;
					final float decayFactor1, decayFactor2;
					final float volume1, volume2;
					final float startTime, endTime;
					
					if ( i< note.getEvents().size() ) {
						startTime = note.getEvents().get(i).getTime();
						pitch1 = note.getEvents().get(i).getData().getPitch();
						decayFactor1 = note.getEvents().get(i).getData().getDecayFactor();
						volume1 = 1.0f;
					
						if ( i == note.getEvents().size()-1 ) {
							endTime = note.getEndTime();
							pitch2 = pitch1;
							decayFactor2 = decayFactor1;
							volume2 = 1.0f;
						}
						else {
							final Event<StringEventData> nextEvent = note.getEvents().get(i+1);
							endTime = nextEvent.getTime();
							pitch2 = nextEvent.getData().getPitch();
							decayFactor2 = nextEvent.getData().getDecayFactor();
							volume2 = 1.0f;
						}
					}
					else {
						final Event<StringEventData> lastEvent = note.getEvents().get(note.getEvents().size()-1);
						
						startTime = note.getEndTime();
						endTime = note.getEndTime() + StringInstrument.this.decayTime;
						decayFactor1 = lastEvent.getData().getDecayFactor();
						decayFactor2 = lastEvent.getData().getDecayFactor();
						pitch1 = lastEvent.getData().getPitch();
						pitch2 = lastEvent.getData().getPitch();
						volume1 = 1.0f;
						volume2 = 0.0f;
						
					}
					
					final int startSample = (int) Math.floor( ( startTime - note.getStartTime() ) * (float) StringInstrument.this.frequencyRate);
					final int endSample = Math.min( samplesLength-1,  (int) Math.ceil( ( endTime - note.getStartTime() ) * (float) StringInstrument.this.frequencyRate ) );
					
					for ( int j = startSample ; j <= endSample ; j++ ) {
						final float timeFactor = (float) (j - startSample) / (float) ( endSample - startSample);
						final float pitchFrequency = Event.getPitchFrequency(ExtraMath.linearInterpolation(pitch1, pitch2, timeFactor));
						
						final float decayFactor = ExtraMath.linearInterpolation(decayFactor1, decayFactor2, timeFactor);
						final float volume = ExtraMath.linearInterpolation(volume1, volume2, timeFactor);
						
						samplesStringLengthPerSample[j] = new SampleInputData( (int) ( (float) StringInstrument.this.frequencyRate / pitchFrequency ), decayFactor);
						amplitude[j] = volume;
					}
				}
				
				final float[] samples = StringInstrument.this.karplusStrongString.createSamples(samplesStringLengthPerSample, Float.hashCode(note.getStartTime()) + Float.hashCode(note.getEvents().get(0).getData().getPitch()));
				
				for ( int i = 0 ; i<samples.length ; i++ ) {
					samples[i] *= amplitude[i];
				}
				
				return new ContinuousSampledSignal(new FloatClosedInterval(note.getStartTime(), note.getEndTime()+StringInstrument.this.decayTime), samples);
			}
			
		};
	}
	
}
