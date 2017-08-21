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

package com.esferixis.musicsynthesizer.signal.continuous.mixer;/**

 * 
 * Queda terminantemente prohibida la reproducción parcial y/o total, y
 * la creación de trabajos derivados sin la expresa autorización del autor.
 * 
 * Éste archivo es propietario y confidencial.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSignal;

public final class SignalMix extends ContinuousSignal {
	private final Collection<ContinuousSignal> signals;
	
	/**
	 * @post Crea la mezcla de las señales especificadas
	 */
	public static ContinuousSignal create(Collection<ContinuousSignal> signals) {
		float min = Float.POSITIVE_INFINITY, max = Float.NEGATIVE_INFINITY;
		signals = new ArrayList<ContinuousSignal>(signals);
		
		for ( ContinuousSignal eachSignal : signals ) {
			min = Math.min(min, eachSignal.getTimeInterval().getMin());
			max = Math.max(max, eachSignal.getTimeInterval().getMax());
		}
		
		return new SignalMix(new FloatClosedInterval(min, max), signals);
	}
	
	/**
	 * @post Crea la mezcla de las señales especificadas
	 */
	public static ContinuousSignal create(ContinuousSignal... signals) {
		return create(Arrays.asList(signals));
	}
	
	private SignalMix(FloatClosedInterval timeInterval, Collection<ContinuousSignal> signals) {
		super(timeInterval);
		this.signals = signals;
	}

	@Override
	public float uncheckedGetValue(float t) {
		float value = 0.0f;
		for ( ContinuousSignal eachSignal : signals ) {
			if ( eachSignal.getTimeInterval().contains(t) ) {
				value += eachSignal.getValue(t);
			}
		}
		
		return value;
	}
}
