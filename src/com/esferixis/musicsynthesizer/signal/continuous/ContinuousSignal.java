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

import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.musicsynthesizer.signal.continuous.transformers.TimeInvariantSignalTransformer;
import com.esferixis.musicsynthesizer.signal.discrete.DiscreteOutput;
import com.esferixis.musicsynthesizer.signal.valueTransformer.ValueScaleTransformer;

public abstract class ContinuousSignal {
	private final FloatClosedInterval timeInterval;
	
	/**
	 * @post Crea la señal con un intervalo infinito
	 */
	public ContinuousSignal() {
		this(FloatClosedInterval.ALLRANGE);
	}
	
	/**
	 * @post Crea la señal con el intervalo de tiempo especificado
	 */
	public ContinuousSignal(FloatClosedInterval timeInterval) {
		if ( timeInterval != null ) {
			this.timeInterval = timeInterval;
		}
		else {
			throw new NullPointerException();
		}
	}

	/**
	 * @post Devuelve el intervalo de tiempo
	 */
	public FloatClosedInterval getTimeInterval() {
		return this.timeInterval;
	}
	
	/**
	 * @post Devuelve el valor en el instante de tiempo especificado
	 * @param t
	 * @return
	 */
	public final Float getValue(float t) {
		if ( this.getTimeInterval().contains(t) ) {
			return this.uncheckedGetValue(t);
		}
		else {
			return 0.0f;
		}
	}
	
	/**
	 * @post Multiplica la señal por la señal especificada
	 */
	public ContinuousSignal multiplicate(final ContinuousSignal other) {
		if ( other != null ) {
			return new ContinuousSignal(this.getTimeInterval().intersection(other.getTimeInterval())) {
	
				@Override
		
				protected float uncheckedGetValue(float t) {
					return ContinuousSignal.this.getValue(t) * other.getValue(t);
				}
				
			};
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Suma la señal con la señal especificada, si
	 * 		 la señal es nula devuelve la misma señal
	 */
	public ContinuousSignal add(final ContinuousSignal other) {
		if ( other != null ) {
			return new ContinuousSignal(this.getTimeInterval().unionBound(other.getTimeInterval())) {
	
				@Override
				protected float uncheckedGetValue(float t) {
					return ContinuousSignal.this.getValue(t) + other.getValue(t);
				}
				
			};
		}
		else {
			return this;
		}
	}
	
	/**
	 * @post Desplaza la señal con el desplazamiento especificado
	 */
	public ContinuousSignal displace(final float deltaT) {
		return new ContinuousSignal(this.getTimeInterval().add(deltaT)) {

			@Override
			protected float uncheckedGetValue(float t) {
				return ContinuousSignal.this.getValue(t-deltaT);
			}
			
		};
	}
	
	/**
	 * @post Escala la señal con el valor especificado
	 */
	public ContinuousSignal valueScale(final float scaleFactor) {
		return (new TimeInvariantSignalTransformer(new ValueScaleTransformer(scaleFactor))).transform(this);
	}
	
	/**
	 * @post Escala la señal en el tiempo
	 */
	public ContinuousSignal inverseTimeScale(final float inverseScaleFactor) {
		return new ContinuousSignal(this.getTimeInterval().mul(1.0f / inverseScaleFactor)) {

			@Override
			protected float uncheckedGetValue(float t) {
				return ContinuousSignal.this.getValue(t*inverseScaleFactor);
			}
			
		};
	}
	
	/**
	 * @post Trunca la señal en el intervalo especificado
	 */
	public ContinuousSignal truncate(final FloatClosedInterval timeInterval) {
		return new ContinuousSignal(this.timeInterval.intersection(timeInterval)) {

			@Override
			protected float uncheckedGetValue(float t) {
				return ContinuousSignal.this.getValue(t);
			}
			
		};
	}
	
	/**
	 * @post Transforma una señal canónica con el intervalo y la frecuencia especificados
	 */
	public ContinuousSignal transformCanonical(final FloatClosedInterval timeInterval, final float frequency) {
		return new ContinuousSignal(timeInterval) {

			@Override
			protected float uncheckedGetValue(float t) {
				return ContinuousSignal.this.getValue(t*frequency-timeInterval.getMin());
			}
			
		};
	}
	
	/**
	 * @post Transforma la señal, componiendo la señal monótona especificada
	 * 		 en el tiempo
	 */
	public ContinuousSignal morph(final ContinuousSignal monotonalMorphSignal) {
		if ( monotonalMorphSignal != null ) {
			float t1 = monotonalMorphSignal.getValue( this.getTimeInterval().getMin() );
			float t2 = monotonalMorphSignal.getValue( this.getTimeInterval().getMax() );
			
			return new ContinuousSignal(new FloatClosedInterval( (float) Math.min(t1, t2), (float) Math.max(t1, t2))) {
	
				@Override
				protected float uncheckedGetValue(float t) {
					return ContinuousSignal.this.getValue( monotonalMorphSignal.getValue(t) );
				}
				
			};
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Integra la señal, con la cantidad de muestras por unidad de tiempo
	 * 		 especificada
	 */
	public ContinuousSignal integrate(int samplesPerTimeUnit) {
		if ( samplesPerTimeUnit > 0 ) {
			final float[] samples = new float[(int) Math.ceil( (float) samplesPerTimeUnit * this.getTimeInterval().length() )];
			
			double value = 0.0f;
			
			for ( int i = 0 ; i < samples.length ; i++ ) {
				value += this.getValue(this.getTimeInterval().linearInterpolation( ( (float) i + 0.5f) / (float) samples.length ));
				samples[i] = (float) ( value * (double) this.getTimeInterval().length() / (double) samples.length );
			}
			
			return new ContinuousSampledSignal(this.getTimeInterval(), samples);
		}
		else {
			throw new IllegalArgumentException("Invalid samples per time unit number");
		}
	}
	
	/**
	 * @post Devuelve el valor
	 */
	protected abstract float uncheckedGetValue(float t);
	
	/**
	 * @post Devuelve una salida discreta con la frecuencia de muestreo
	 * 		 especificada
	 */
	public DiscreteOutput discreteOutput(final float frequencyRate) {
		return new DiscreteOutput() {
			int i = 0;
			final int maxSamples = (int) Math.ceil(ContinuousSignal.this.getTimeInterval().length() * (float) frequencyRate);
			
			@Override
			public float read() {
				if ( i != maxSamples ) {
					return ContinuousSignal.this.getValue((float) (i++) / (float) frequencyRate + ContinuousSignal.this.getTimeInterval().getMin());
				}
				else {
					return 0.0f;
				}
			}
			
		};
	}
	
	/**
	 * @post Crea una señal periódica infinita a partir de ésta
	 */
	public ContinuousSignal convertToPeriodic() {
		return new ContinuousSignal() {

			@Override
			protected float uncheckedGetValue(float t) {
				t = (t - ContinuousSignal.this.timeInterval.getMin()) / ContinuousSignal.this.timeInterval.length();
				t = (t - (float) Math.floor(t));
				t = t * ContinuousSignal.this.timeInterval.length() + ContinuousSignal.this.timeInterval.getMin();
				return ContinuousSignal.this.getValue(t);
			}
			
		};
	}
	
	/**
	 * @post Convierte en una señal "sampleada"
	 */
	public ContinuousSignal convertToSampled(float frequencyRate) {
		return ContinuousSampledSignal.create(this.discreteOutput(frequencyRate), (int) Math.ceil(this.getTimeInterval().length() / frequencyRate), frequencyRate);
	}
}
