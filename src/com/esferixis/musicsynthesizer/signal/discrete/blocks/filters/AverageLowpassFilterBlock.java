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
package com.esferixis.musicsynthesizer.signal.discrete.blocks.filters;

import java.util.List;

import com.esferixis.musicsynthesizer.signal.discrete.blocks.BufferedDiscreteInputOutputBlock;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.DiscreteInputOutputBlock;

public final class AverageLowpassFilterBlock extends BufferedDiscreteInputOutputBlock {

	/**
	 * @param samples
	 */
	public AverageLowpassFilterBlock(int samples) {
		super(samples);
	}
	
	/**
	 * @pre La cantidad de muestras tiene que ser positiva
	 * @post Crea la fábrica con la cantidad de muestras especificada
	 */
	public static DiscreteInputOutputBlock.Factory createFactory(final int samples) {
		if ( samples > 0 ) {
			return new DiscreteInputOutputBlock.Factory() {
				
				@Override
				public DiscreteInputOutputBlock create() {
					return new AverageLowpassFilterBlock(samples);
				}
			};
		}
		else {
			throw new IllegalArgumentException();
		}
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.musicsynthesizer.signal.discrete.blocks.BufferedDiscreteInputOutputBlock#calculateValue(java.util.List)
	 */
	@Override
	protected float calculateValue(List<Float> oldValues) {
		float result = 0.0f;
		
		for ( int i = 0 ; i<oldValues.size() ; i++ ) {
			result += oldValues.get(i) / (float) oldValues.size();
		}
		
		return result;
	}
	
}
