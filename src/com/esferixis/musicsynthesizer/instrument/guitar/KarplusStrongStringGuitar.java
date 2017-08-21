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
package com.esferixis.musicsynthesizer.instrument.guitar;

import java.util.List;
import java.util.Random;

import com.esferixis.math.ExtraMath;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.musicsynthesizer.instrument.string.StringInstrument;
import com.esferixis.musicsynthesizer.signal.KarplusStrongString;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSampledSignal;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSignal;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.DiscreteInputOutputBlock;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.filters.AverageLowpassFilterBlock;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.filters.LowpassRCFilterBlock;

/**
 * Añadir un filtro bandpass según...
 * https://fazli.sapuan.org/blog/electric-guitar-synth-in-html5/
 * Para suprimir el "aliasing" a bajas frecuencias
 */
public final class KarplusStrongStringGuitar extends StringInstrument {

	/**
	 * @param karplusStrongString
	 * @param frequencyRate
	 */
	public KarplusStrongStringGuitar() {
		super(new KarplusStrongString() {

			@Override
			protected void generateInitialState(float[] initialState, int seed) {
				Random rng = new Random(seed);
				
				DiscreteInputOutputBlock filterBlock = new AverageLowpassFilterBlock((int) ( (float) initialState.length * 0.1f ));
				
				for ( int i = 0 ; i < initialState.length ; i++ ) {
					filterBlock.getInput().write(rng.nextBoolean() ? 1.0f : -1.0f);
					initialState[i] = filterBlock.getOutput().read();
				}
			}

			@Override
			protected float newSample(List<Float> samples, float decayFactor) {
				return ExtraMath.linearInterpolation(samples.get(samples.size()-1), samples.get(0), decayFactor);
			}
			
		}, 44100, 0.01f);
	}
	
}
