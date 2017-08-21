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
package com.esferixis.musicsynthesizer.signal.discrete.blocks;

import com.esferixis.musicsynthesizer.signal.discrete.DiscreteInput;
import com.esferixis.musicsynthesizer.signal.discrete.DiscreteOutput;

public class DiscreteForkerMixerBlock extends DiscreteInputOutputBlock {
	private float storedValue;
	private DiscreteInputOutputBlock[] blocks;
	
	/**
	 * @pre El array de fábricas no puede ser nulo, ni tampoco ninguno de sus elementos
	 * @post Crea la fábrica con las fábricas especificadas
	 */
	public static DiscreteInputOutputBlock.Factory createFactory(DiscreteInputOutputBlock.Factory... factories) {
		if ( factories != null ) {
			final DiscreteInputOutputBlock.Factory[] factories_new = factories.clone();
			
			for ( DiscreteInputOutputBlock.Factory eachFactory : factories_new ) {
				if ( eachFactory == null ) {
					throw new NullPointerException();
				}
			}
			
			return new DiscreteInputOutputBlock.Factory() {
				
				@Override
				public DiscreteInputOutputBlock create() {
					DiscreteInputOutputBlock[] blocks = new DiscreteInputOutputBlock[factories_new.length];
					
					for ( int i = 0 ; i<factories_new.length; i++ ) {
						blocks[i] = factories_new[i].create();
					}
					
					return new DiscreteForkerMixerBlock(blocks);
				}
			};
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El array de bloques no puede ser nulo ni tampoco ninguno
	 * 		de ellos
	 * @post Crea el bifurcador mezclador con los bloques especificados
	 * @param blocks
	 */
	public DiscreteForkerMixerBlock(DiscreteInputOutputBlock... blocks) {
		if ( blocks != null ) {
			blocks = blocks.clone();
			
			for ( DiscreteInputOutputBlock eachBlock : blocks ) {
				if ( eachBlock == null ) {
					throw new NullPointerException();
				}
			}
			
			this.blocks = blocks;
			
			this.setInput(new DiscreteInput() {

				@Override
				public void write(float value) {
					float resultValue = 0.0f;
					
					for ( DiscreteInputOutputBlock eachBlock : DiscreteForkerMixerBlock.this.blocks ) {
						eachBlock.getInput().write(value);
						resultValue += eachBlock.getOutput().read();
					}
					
					DiscreteForkerMixerBlock.this.storedValue = resultValue;
				}
				
			});
			
			this.setOutput(new DiscreteOutput() {

				@Override
				public float read() {
					return DiscreteForkerMixerBlock.this.storedValue;
				}
				
			});
		}
		else {
			throw new NullPointerException();
		}
	}
}
