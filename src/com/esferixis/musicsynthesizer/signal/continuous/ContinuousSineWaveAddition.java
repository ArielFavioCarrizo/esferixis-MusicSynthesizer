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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.esferixis.math.intervalarithmetic.FloatClosedInterval;

public final class ContinuousSineWaveAddition extends ContinuousSignal {
	public static final class Wave {
		private float frequency;
		private float phaseOffset;
		
		public Wave(float frequency, float phaseOffset) {
			this.frequency = frequency;
			this.phaseOffset = phaseOffset;
		}
		
		/**
		 * @post Devuelve la frecuencia
		 */
		public float getFrequency() {
			return this.frequency;
		}
		
		/**
		 * @post Devuelve el offset de fase
		 */
		public float getPhaseOffset() {
			return this.phaseOffset;
		}
	}
	
	public interface FrequencyAmplitudesFunction {
		/**
		 * @post Devuelve la amplitud con la frecuencia y el tiempo
		 * 		 especificados
		 */
		public float getAmplitude(float f, float t);
	}
	
	private final Collection<Wave> waves;
	private final FrequencyAmplitudesFunction frequencyAmplitudesFunction;
	private float amplitudeFactor;
	
	/**
	 * @post Crea la señal con el intervalo de tiempo, las ondas especificadas, la función de amplitud de frecuencias
	 * 		 y el factor de amplitud especificados
	 */
	public ContinuousSineWaveAddition(FloatClosedInterval timeInterval, Collection<Wave> waves, FrequencyAmplitudesFunction frequencyAmplitudesFunction, float amplitudeFactor) {
		super(timeInterval);
		if ( ( waves != null ) && ( frequencyAmplitudesFunction != null ) ) {
			this.waves = Collections.unmodifiableList(new ArrayList<Wave>(waves));
			this.frequencyAmplitudesFunction = frequencyAmplitudesFunction;
			this.amplitudeFactor = amplitudeFactor;
		}
		else {
			throw new NullPointerException();
		}
	}

	/**
	 * @post Crea la señal con las ondas especificadas, la función de amplitud de frecuencias
	 * 		 y el factor de amplitud especificados
	 */
	public ContinuousSineWaveAddition(Collection<Wave> waves, FrequencyAmplitudesFunction frequencyAmplitudesFunction, float amplitudeFactor) {
		this(FloatClosedInterval.ALLRANGE, waves, frequencyAmplitudesFunction, amplitudeFactor);
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.musicsynthesizer.signal.FloatSignal#uncheckedGetValue(float)
	 */
	@Override
	protected final float uncheckedGetValue(float t) {
		float value = 0.0f;
		final float doublePI = (float) (Math.PI * 2.0d);
		
		for ( Wave eachWave : this.waves ) {
			value += Math.sin(t * doublePI * eachWave.getFrequency()+eachWave.getPhaseOffset()) * this.frequencyAmplitudesFunction.getAmplitude(eachWave.getFrequency(), t);
		}
		
		return value * this.amplitudeFactor;
	}
}
