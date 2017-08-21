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
package com.esferixis.musicsynthesizer.signal.continuous.periodic;

import com.esferixis.math.intervalarithmetic.FloatClosedInterval;

public final class TriangleWave extends PeriodicWave {
	public static TriangleWave UNBOUNDEDCANONICAL = new TriangleWave(0.0f, 1.0f);
	
	/**
	 * @post Crea la onda con el intervalo de tiempo, el tiempo de comienzo
	 * 		 y la frecuencia especificados
	 * @param timeInterval
	 */
	public TriangleWave(FloatClosedInterval timeInterval, float t0, float frequency) {
		super(timeInterval, t0, frequency);
	}
	
	/**
	 * @post Crea la onda senoidal con el el tiempo de comienzo
	 * 		 y la frecuencia especificados
	 * 		 El intervalo de tiempo no es acotado
	 * @param timeInterval
	 */
	public TriangleWave(float t0, float frequency) {
		super(FloatClosedInterval.ALLRANGE, t0, frequency);
	}
	
	/**
	 * @post Crea la onda con el intervalo de tiempo y la frecuencia
	 * 		 especificados.
	 * 		 El tiempo de comienzo es el comienzo del intervalo de tiempo
	 */
	public TriangleWave(FloatClosedInterval timeInterval, float frequency) {
		super(timeInterval, frequency);
	}

	@Override
	protected float uncheckedGetValue(float t) {
		float localT = (t - t0) * frequency;
		localT = localT - (float) Math.floor(localT);
		
		final float value;
		
		if ( localT < 0.5f ) {
			value = localT * 2.0f - 1.0f;
		}
		else {
			value = 1.0f - (localT - 0.5f) * 2.0f;
		}
		
		return value;
	}
}
