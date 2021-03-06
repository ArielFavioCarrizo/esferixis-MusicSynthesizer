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
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousLinearAttackDecaySignal;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSignal;

public class ChurchOrgan extends HomogeneousOrgan {
	/**
	 * @param homogeneousSignal
	 */
	public ChurchOrgan() {
		super(homogeneousSignal);
	}

	static {
		homogeneousSignal = new ContinuousSignal() {

			@Override
			protected float uncheckedGetValue(float t) {
				float value = 0.0f;
				float frequency = 1.0f;
				float maxIntensity = 0.0f;
				
				frequency = 1.0f;
				
				for ( int i = 0 ; i < 20 ; i++ ) {
					float localIntensity = (float) Math.exp(-(float) i * 0.7f);
					
					value += (Math.sin(2.0f * Math.PI * frequency * t) * localIntensity);
					
					frequency *= 2.0f;
					
					maxIntensity += localIntensity;
				}
				
				return value / maxIntensity;
			}
			
		};
	}
	
	private static final ContinuousSignal homogeneousSignal;
	
	@Override
	protected ContinuousSignal processEmitSignal(ContinuousSignal signal) {
		return signal.multiplicate(new ContinuousLinearAttackDecaySignal(signal.getTimeInterval(), 0.01f));
	}
}
