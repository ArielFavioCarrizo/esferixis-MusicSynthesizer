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
package com.esferixis.musicsynthesizer.composition.note;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.musicsynthesizer.composition.Event;

public final class Note<T, D> {
	private final T track;
	private final List<Event<D>> events;
	private final float endTime;
	
	/**
	 * @pre El array de eventos no puede ser nulo, y tiene que
	 * 		tener por lo menos un evento
	 * @post Crea la nota con la pista, el tiempo final, y los eventos
	 * 		 especificados
	 */
	@SafeVarargs
	public Note(T track, float endTime, Event<D>... events) {
		if ( events != null ) {
			if ( events.length > 0 ) {
				events = events.clone();
				
				Arrays.sort(events);
				this.track = track;
				this.events = Collections.unmodifiableList(Arrays.asList(events));
				this.endTime = endTime;
			}
			else {
				throw new IllegalArgumentException("Expected at least one event");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre La colección de eventos no puede ser nula, y tiene que tener por lo menos
	 * 		un elemento
	 * @post Crea la nota con el pista, el tiempo final y los eventos
	 * 		 especificados
	 */
	public Note(T track, float endTime, Collection<Event<D>> events) {
		this(track, endTime, events.toArray(new Event[0]));
	}
	
	/**
	 * @post Devuelve el tiempo final
	 */
	public float getEndTime() {
		return this.endTime;
	}
	
	/**
	 * @post Devuelve los eventos
	 */
	public List<Event<D>> getEvents() {
		return this.events;
	}
	
	/**
	 * @post Devuelve el tiempo de comienzo
	 */
	public float getStartTime() {
		return this.getEvents().get(0).getTime();
	}
	
	/**
	 * @post Devuelve el intervalo de tiempo
	 */
	public FloatClosedInterval getTimeInterval() {
		return new FloatClosedInterval(this.getStartTime(), this.getEndTime());
	}
	
	/**
	 * @post Devuelve la pista
	 */
	public T getTrack() {
		return this.track;
	}
	
	/**
	 * @post Devuelve la nota con la pista cambiada
	 */
	public <S> Note<S, D> changeTrack(S newTrack) {
		return new Note<S, D>(newTrack, this.endTime, this.events);
	}
	
	/**
	 * @post Desplaza en el tiempo de la nota con el valor especificado
	 */
	public Note<T, D> addTime(float timeDelta) {
		List<Event<D>> events = new ArrayList<Event<D>>(this.events.size());
		
		for ( Event<D> eachEvent : this.events ) {
			events.add(eachEvent.addTime(timeDelta));
		}
		
		return new Note<T, D>(this.track, this.endTime+timeDelta, events);
	}
	
	/**
	 * @post Escala la notas en el tiempo, en el factor especificado
	 */
	public Note<T, D> timeScale(float scaleFactor) {
		List<Event<D>> events = new ArrayList<Event<D>>(this.events.size());
		
		for ( Event<D> eachEvent : this.events ) {
			events.add(eachEvent.timeScale(scaleFactor));
		}
		
		return new Note<T, D>(this.track, this.endTime*scaleFactor, events);
	}
}
