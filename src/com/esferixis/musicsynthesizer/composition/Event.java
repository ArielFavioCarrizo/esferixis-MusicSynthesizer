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
package com.esferixis.musicsynthesizer.composition;

import com.esferixis.math.intervalarithmetic.FloatClosedInterval;

/**
 * @param <D> Dato de evento
 */
public final class Event<D> implements Comparable<Event<D>> {
	private final float time;
	private final D data;
	
	/**
	 * @pre Los datos no pueden ser nulos
	 * @post Crea el evento en el instante de tiempo, y con los datos especificados
	 */
	public Event(float time, D data) {
		if ( data != null ) {
			this.time = time;
			this.data = data;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el instante de tiempo especificado
	 */
	public float getTime() {
		return this.time;
	}
	
	/**
	 * @post Devuelve los datos
	 */
	public final D getData() {
		return this.data;
	}
	
	/**
	 * @post Devuelve la frecuencia para la altura especificada
	 */
	public static float getPitchFrequency(float pitch) {
		return (float) Math.pow(2.0f, ( pitch - 49 ) / 12.0f) * 440.0f;
	}
	
	/**
	 * @post Desplaza en el tiempo
	 */
	public Event<D> addTime(float timeDelta) {
		return new Event<D>(this.time+timeDelta, this.data);
	}
	
	/**
	 * @post Escala el evento en el tiempo, con el factor especificado
	 */
	public Event<D> timeScale(float scaleFactor) {
		return new Event<D>(this.time*scaleFactor, this.data);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Event<D> other) {
		if ( other != null ) {
			return Float.compare(this.getTime(), other.getTime());
		}
		else {
			throw new NullPointerException();
		}
	}
}
