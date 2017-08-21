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
package com.esferixis.musicsynthesizer.signal.continuous.transformers;

import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSampledSignal;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSignal;
import com.esferixis.musicsynthesizer.signal.discrete.DiscreteOutput;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.DiscreteInputOutputBlock;

public final class DiscreteInputOutputBlockSignalTransformer extends SignalTransformer {
	private final float frequencyRate;
	private final DiscreteInputOutputBlock.Factory discreteBlockFactory;

	/**
	 * @pre El bloque discreto de entrada/salida y la frecuencia de muestreo
	 * 		no pueden ser nulas
	 * @post Crea el transformador con la fábrica de bloque discreto de entrada/salida, y la frecuencia
	 * 		 de muestreo especificada
	 */
	public DiscreteInputOutputBlockSignalTransformer(DiscreteInputOutputBlock.Factory discreteBlockFactory, float frequencyRate) {
		if ( discreteBlockFactory != null ) {
			if ( frequencyRate > 0.0f ) {
				this.discreteBlockFactory = discreteBlockFactory;
				this.frequencyRate = frequencyRate;
			}
			else {
				throw new IllegalArgumentException("Expected positive frequency rate");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.arielcarrizo.musicsynthesizer.signal.continuous.transformers.SignalTransformer#uncheckedTransform(com.arielcarrizo.musicsynthesizer.signal.continuous.ContinuousSignal)
	 */
	@Override
	protected ContinuousSignal uncheckedTransform(final ContinuousSignal sourceSignal) {
		sourceSignal.displace(-sourceSignal.getTimeInterval().getMin());
		
		int samplesQuantity = (int) Math.ceil( sourceSignal.getTimeInterval().length() * (float) this.frequencyRate );
		
		return ContinuousSampledSignal.create(new DiscreteOutput() {
			final DiscreteOutput originalOutput = sourceSignal.discreteOutput(DiscreteInputOutputBlockSignalTransformer.this.frequencyRate);
			final DiscreteInputOutputBlock transformerDiscreteBlock = DiscreteInputOutputBlockSignalTransformer.this.discreteBlockFactory.create();
			
			@Override
			public float read() {
				this.transformerDiscreteBlock.getInput().write(this.originalOutput.read());
				
				return this.transformerDiscreteBlock.getOutput().read();
			}
			
		}, samplesQuantity, frequencyRate);
	}
	
}
