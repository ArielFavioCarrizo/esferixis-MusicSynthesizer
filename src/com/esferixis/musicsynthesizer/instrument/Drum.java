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
import com.esferixis.musicsynthesizer.signal.discrete.NoiseDiscreteSignal;

public class Drum extends HomogeneousOrgan {
	/**
	 * @post Crea un tambor
	 */
	public Drum(final float waveLength, final float waveSpeed, final float tauDecay) {
		super(new ContinuousSignal() {
			private final ContinuousSignal rawSignal = new ContinuousSignal() {
				private final ContinuousSignal noise = new NoiseDiscreteSignal().continuousInterpolatedSignal(FloatClosedInterval.WIDESTFINITE);
				
				@Override
				protected float uncheckedGetValue(float t) {
					final float value;
					if ( t >= 0.0f ) {
						float localT = t / waveLength;
						localT = (localT - (float) Math.floor(localT))*2.0f;
						if ( localT > 1.0f ) {
							localT = 2.0f - localT;
						}
						
						value = noise.getValue(localT*waveLength*waveSpeed) * (float) Math.exp(-t * tauDecay);
					}
					else {
						value = 0.0f;
					}
					
					return value;
				}
			};
			
			@Override
			protected float uncheckedGetValue(float t) {
				return rawSignal.getValue(t);
			}
		});
	}
}
