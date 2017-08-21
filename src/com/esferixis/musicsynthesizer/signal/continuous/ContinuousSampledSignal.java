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
package com.esferixis.musicsynthesizer.signal.continuous;

import java.util.List;

import com.esferixis.math.ExtraMath;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.musicsynthesizer.signal.discrete.DiscreteOutput;

public final class ContinuousSampledSignal extends ContinuousSignal {
	private float[] samples;
	
	/**
	 * @post Crea una señal muestreada con el intervalo de tiempo
	 * 		 y el array de muestras especificado
	 */
	public ContinuousSampledSignal(FloatClosedInterval timeInterval, float[] samples) {
		super(timeInterval);
		if ( ( timeInterval != null ) && ( samples != null ) ) {
			this.samples = samples.clone();
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre La salida discreta no puede ser nula, y la cantidad de muestras
	 * 		y la frecuencia de salida tienen que ser positivas.
	 * @post Crea una señal muestrada con la
	 * 		 salida discreta, la cantidad de muestras, y la frecuencia de muestreo
	 * 		 especificada
	 */
	public static ContinuousSampledSignal create(DiscreteOutput output, int samplesQuantity, float frequencyRate) {
		if ( output != null ) {
			if ( samplesQuantity > 0 ) {
				if ( frequencyRate > 0.0f ) {
					float[] samples = new float[samplesQuantity];
					
					for ( int i = 0 ; i < samples.length ; i++ ) {
						samples[i] = output.read();
					}
					
					return new ContinuousSampledSignal(new FloatClosedInterval(0.0f, (float) samplesQuantity / frequencyRate), samples);
				}
				else {
					throw new IllegalArgumentException("Expected positive frequency rate");
				}
			}
			else {
				throw new IllegalArgumentException("Expected positive samples quantity");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Crea una señal muestreada con el intervalo de tiempo
	 * 		 y la lista de muestras especificada
	 */
	public ContinuousSampledSignal(FloatClosedInterval timeInterval, List<Float> samples) {
		super(timeInterval);
		if ( ( timeInterval != null ) && ( samples != null ) ) {
			this.samples = new float[samples.size()];
			
			int i = 0;
			
			for ( Float eachValue : samples ) {
				this.samples[i++] = eachValue;
			}
		}
		else {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.musicsynthesizer.signal.FloatSignal#uncheckedGetValue(float)
	 */
	@Override
	protected float uncheckedGetValue(float t) {
		final float position = ( t - this.getTimeInterval().getMin() ) * this.samples.length / this.getTimeInterval().length();
		final float value;
		
		if ( position >= (float) (samples.length-1) ) {
			value = this.samples[samples.length-1];
		}
		else if ( position <= 0.0f ) {
			value = this.samples[0];
		}
		else {
			final float floatIndex = (float) Math.floor(position);
			final int intIndex = (int) floatIndex;
			final float intersamplePosition = position - floatIndex;
			
			value = ExtraMath.linearInterpolation(this.samples[intIndex], this.samples[intIndex+1], intersamplePosition);
		}
		
		return value;
	}
}
