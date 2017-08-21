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

import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSignal;

public final class BellOrgan extends HomogeneousOrgan {
	private static final float doublePI = 2.0f * (float) Math.PI;
	
	/**
	 * @post Crea la campana predeterminada
	 */
	public BellOrgan() {
		this(5.0f / 7.0f, 1.0f, 0.004f);
	}
	
	/**
	 * @post Crea la campana con la frecuencia "carrier", la frecuencia de modulación y
	 * 		 el tau de decrecimiento especificado.
	 * 		 En condiciones de frecuencia fundamental canónica (f = 1)
	 * @param homogeneousSignal
	 */
	public BellOrgan(final float canonicalCarrierFrequency, final float canonicalModulationFrequency, final float canonicalTau) {
		final FloatClosedInterval timeInterval = new FloatClosedInterval(0.0f, (float) (-Math.log(0.01f) / canonicalTau));
		
		this.setHomogeneousSignal( new ContinuousSignal(timeInterval) {
			final ContinuousSignal amplitude = new ContinuousSignal(timeInterval) {

				@Override
				protected float uncheckedGetValue(float t) {
					return 1.0f * (float) Math.exp(-t * canonicalTau);
				}
				
			};
			
			final ContinuousSignal modulationIndex = new ContinuousSignal(timeInterval) {

				@Override
				protected float uncheckedGetValue(float t) {
					return 5.0f * (float) Math.exp(-t * canonicalTau);
				}
				
			};
			
			@Override
			protected float uncheckedGetValue(float t) {
				return this.amplitude.getValue(t) * (float) Math.cos( doublePI * canonicalCarrierFrequency * t + this.modulationIndex.getValue(t) * (float) Math.cos(doublePI * canonicalModulationFrequency * t) );
			}
			
		} );
	}
	
}
