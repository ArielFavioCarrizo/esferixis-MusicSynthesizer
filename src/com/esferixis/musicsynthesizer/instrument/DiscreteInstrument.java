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
package com.esferixis.musicsynthesizer.instrument;

import java.util.List;

import com.esferixis.math.ExtraMath;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.musicsynthesizer.composition.Event;
import com.esferixis.musicsynthesizer.composition.note.Note;
import com.esferixis.musicsynthesizer.instrument.organ.OrganEventData;
import com.esferixis.musicsynthesizer.instrument.signalSynthesizer.PerNoteSignalSynthesizer;
import com.esferixis.musicsynthesizer.instrument.signalSynthesizer.SignalSynthesizer;
import com.esferixis.musicsynthesizer.signal.KarplusStrongString;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSampledSignal;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSignal;

/**
 * @param <D> Datos de evento
 */
public abstract class DiscreteInstrument extends Instrument<OrganEventData> {
	protected final int frequencyRate;
	private final float decayTime;
	
	protected static interface SignalGenerator {
		/**
		 * @post Obtiene la siguiente muestra con la frecuencia
		 * 		 especificada
		 */
		public float nextSample(float frequency);
	}
	
	/**
	 * @post Crea el instrumento con la frecuencia
	 * 		 de muestreo, y el tiempo de decaimento especificados
	 */
	public DiscreteInstrument(int frequencyRate, float decayTime) {
		this.frequencyRate = frequencyRate;
		this.decayTime = decayTime;
	}
	
	/**
	 * @post Crea el generador de señal
	 */
	protected abstract SignalGenerator createSignalGenerator();

	/* (non-Javadoc)
	 * @see com.esferixis.musicsynthesizer.instrument.Organ#createSignalSynthesizer()
	 */
	@Override
	public SignalSynthesizer createSignalSynthesizer() {
		return new PerNoteSignalSynthesizer<OrganEventData>() {

			@Override
			protected ContinuousSignal synthesizeNote(Note<Void, OrganEventData> note) {
				final SignalGenerator signalGenerator = DiscreteInstrument.this.createSignalGenerator();
				
				final float[] samples = new float[(int) ( Math.floor( (note.getEndTime()-note.getStartTime()+decayTime) * (float) DiscreteInstrument.this.frequencyRate) ) + 1];
				
				int segments = note.getEvents().size() + ( ( DiscreteInstrument.this.decayTime != 0.0f) ? 1 : 0 );
				
				int lastEndSample = 0;
				
				int nSample = 0;
				
				for ( int i = 0 ; i<segments; i++ ) {
					final float pitch1, pitch2;
					final float volume1, volume2;
					final float startTime, endTime;
					
					if ( i< note.getEvents().size() ) {
						startTime = note.getEvents().get(i).getTime();
						pitch1 = note.getEvents().get(i).getData().getPitch();
						volume1 = note.getEvents().get(i).getData().getVolume();
					
						if ( i == note.getEvents().size()-1 ) {
							endTime = note.getEndTime();
							pitch2 = pitch1;
							volume2 = volume1;
						}
						else {
							final Event<OrganEventData> nextEvent = note.getEvents().get(i+1);
							endTime = nextEvent.getTime();
							pitch2 = nextEvent.getData().getPitch();
							volume2 = nextEvent.getData().getVolume();
						}
					}
					else {
						final Event<OrganEventData> lastEvent = note.getEvents().get(note.getEvents().size()-1);
						
						startTime = note.getEndTime();
						endTime = note.getEndTime() + DiscreteInstrument.this.decayTime;
						volume1 = lastEvent.getData().getVolume();
						volume2 = 0.0f;
						pitch1 = lastEvent.getData().getPitch();
						pitch2 = lastEvent.getData().getPitch();
					}
					
					int startSample = (int) Math.floor( ( startTime - note.getStartTime() ) * (float) DiscreteInstrument.this.frequencyRate);
					int endSample = Math.min( samples.length-1,  (int) Math.ceil( ( endTime - note.getStartTime() ) * (float) DiscreteInstrument.this.frequencyRate ) );
					
					if ( startSample <= lastEndSample ) {
						startSample = lastEndSample+1;
					}
					
					if ( endSample >= samples.length ) {
						endSample = samples.length-1;
					}
					
					if ( endSample >= startSample ) {
						
						for ( int j = startSample ; j <= endSample ; j++ ) {
							final float timeFactor = (float) (j - startSample) / (float) ( endSample - startSample);
							final float pitchFrequency = Event.getPitchFrequency(ExtraMath.linearInterpolation(pitch1, pitch2, timeFactor));
							final float amplitude = ExtraMath.linearInterpolation(volume1, volume2, timeFactor);
							
							samples[nSample++] = signalGenerator.nextSample(pitchFrequency) * amplitude;
						}
						
						lastEndSample = endSample;
					}
				}
				
				return new ContinuousSampledSignal(new FloatClosedInterval(note.getStartTime(), note.getEndTime()+DiscreteInstrument.this.decayTime), samples);
			}
			
		};
	}
	
}
