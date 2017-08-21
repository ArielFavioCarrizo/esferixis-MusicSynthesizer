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

import java.util.Arrays;

import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.musicsynthesizer.composition.Event;
import com.esferixis.musicsynthesizer.composition.note.Note;
import com.esferixis.musicsynthesizer.instrument.organ.OrganEventData;
import com.esferixis.musicsynthesizer.instrument.signalSynthesizer.PerNoteSignalSynthesizer;
import com.esferixis.musicsynthesizer.instrument.signalSynthesizer.SignalSynthesizer;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSignal;

public class HomogeneousOrgan extends Instrument<OrganEventData> {
	private ContinuousSignal homogeneousSignal;
	
	private boolean homogeneousSignalHasOwnTimeInterval;
	
	/**
	 * @pre La señal homogenea no puede ser nula
	 * @post Crea el órgano con la señal homogénea especificada
	 */
	public HomogeneousOrgan(ContinuousSignal homogeneousSignal) {
		this.homogeneousSignal = null;
		this.setHomogeneousSignal(homogeneousSignal);
	}
	
	/**
	 * @post Crea un órgano homogeneo sin especificar la señal homogenea
	 */
	protected HomogeneousOrgan() {
		
	}
	
	/**
	 * @post Especifica la señal homogenea
	 */
	protected final void setHomogeneousSignal(ContinuousSignal homogeneousSignal) {
		if ( this.homogeneousSignal == null ) {
			if ( homogeneousSignal != null ) {
				this.homogeneousSignal = homogeneousSignal;
				this.homogeneousSignalHasOwnTimeInterval = !this.homogeneousSignal.getTimeInterval().equals(FloatClosedInterval.ALLRANGE);
			}
			else {
				throw new NullPointerException();
			}
		}
		else {
			throw new IllegalStateException("Homogeneous signal has been setted");
		}
	}
	
	/**
	 * @post Aplica un postprocesamiento de la señal de una
	 * 		 emisión de eventos
	 */
	protected ContinuousSignal processEmitSignal(ContinuousSignal signal) {
		return signal;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.musicsynthesizer.instrument.Organ#createSignalSynthesizer()
	 */
	@Override
	public final SignalSynthesizer<OrganEventData> createSignalSynthesizer() {
		if ( this.homogeneousSignal != null ) {
			return new PerNoteSignalSynthesizer<OrganEventData>() {
				private final float ln2 = (float) Math.log(2.0f);
				
				/**
				 * @post Devuelve la fase para el evento con
				 * 		 el índice especificado, en el array de eventos
				 * 		 y en el instante de tiempo especificado
				 */
				private float getLocalPhaseByEvent(Event<OrganEventData>[] event, int index, float time) {
					float phase;
					final float localT = time - event[index].getTime();
					if ( ( index <= event.length-2 ) && ( event[index].getData().getPitch() != event[index+1].getData().getPitch() ) ) {
						final float deltaPitch = event[index+1].getData().getPitch() - event[index].getData().getPitch();
						final float deltaT = event[index+1].getTime() - event[index].getTime();
						
						final float v0 = (event[index].getData().getPitch() - 49) / 12.0f;
						final float v1 = ( localT * deltaPitch / deltaT + event[index].getData().getPitch() - 49 ) / 12.0f;
						
						phase = (5280.0f * deltaT / ( ln2 * deltaPitch ) * (float) ( Math.pow(2.0f, v1) - Math.pow(2.0f, v0) ) );
					}
					else {
						phase = Event.getPitchFrequency(event[index].getData().getPitch()) * localT;
					}
					
					return phase;
				}
				
				@Override
				protected ContinuousSignal synthesizeNote(Note<Void, OrganEventData> note) {
					final Event<OrganEventData>[] events = note.getEvents().toArray(new Event[0]);
					final float[] eventPhase = new float[events.length];
					
					final float endPhase;
					
					{
						float phase = 0.0f;
						
						for ( int i = 0 ; i < events.length ; i++ ) {
							eventPhase[i] = phase;
							
							if ( i <= events.length-2 ) {
								phase += this.getLocalPhaseByEvent(events, i, events[i+1].getTime());
							}
						}
						
						endPhase = phase;
					}
					
					float timeDelta;
					
					if ( HomogeneousOrgan.this.homogeneousSignalHasOwnTimeInterval ) {
						timeDelta = Event.getPitchFrequency((HomogeneousOrgan.this.homogeneousSignal.getTimeInterval().getMax() - endPhase) / events[events.length-1].getData().getPitch());
					}
					else {
						timeDelta = 0.0f;
					}
					
					return HomogeneousOrgan.this.processEmitSignal( new ContinuousSignal(new FloatClosedInterval(events[0].getTime(), note.getEndTime() + timeDelta )) {

						@Override
						protected float uncheckedGetValue(float t) {
							int eventIndex = Arrays.binarySearch(events, new Event<OrganEventData>(t, events[0].getData()));
							if ( eventIndex < 0 ) {
								eventIndex = -(eventIndex + 1)-1;
							}
							
							final float value;
							
							if ( eventIndex >= 0 ) {
								float volume;
								if ( eventIndex <= events.length-2 ) {
									volume = new FloatClosedInterval(events[eventIndex].getData().getVolume(), events[eventIndex+1].getData().getVolume()).linearInterpolation( ( t - events[eventIndex].getTime() ) / ( events[eventIndex+1].getTime() - events[eventIndex].getTime() ) );
								}
								else {
									volume = events[eventIndex].getData().getVolume();
								}
								
								value = HomogeneousOrgan.this.homogeneousSignal.getValue( eventPhase[eventIndex] + getLocalPhaseByEvent(events, eventIndex, t) ) * volume;
							}
							else {
								value = 0.0f;
							}
							
							return value;
						}
						
					});
				}
				
			};
		}
		else {
			throw new IllegalStateException("Expected initialized homogeneous signal");
		}
	}

}
