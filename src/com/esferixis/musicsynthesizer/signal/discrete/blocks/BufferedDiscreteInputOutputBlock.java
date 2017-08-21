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

import java.util.AbstractList;
import java.util.List;

import com.esferixis.musicsynthesizer.signal.discrete.DiscreteInput;
import com.esferixis.musicsynthesizer.signal.discrete.DiscreteOutput;

public abstract class BufferedDiscreteInputOutputBlock extends DiscreteInputOutputBlock {
	private final float[] samples;
	private int inputIndex, outputIndex;
	
	/**
	 * @pre La cantidad de muestras tiene que ser positiva, y
	 * 		la entrada de excitación no puede ser nula
	 * @post Crea un retardadador con la cantidad de muestras especificada
	 * @param samples
	 */
	public BufferedDiscreteInputOutputBlock(int samples) {
		if ( samples > 0 ) {
			this.samples = new float[samples];
			this.inputIndex = 0;
			this.outputIndex = 0;
			
			this.setOutput( new DiscreteOutput() {

				@Override
				public float read() {
					float value = BufferedDiscreteInputOutputBlock.this.calculateValue(new AbstractList<Float>() {

						@Override
						public Float get(int index) {
							if ( ( index >= 0 ) && ( index < this.size() ) ) {
								float value = BufferedDiscreteInputOutputBlock.this.samples[ ( BufferedDiscreteInputOutputBlock.this.inputIndex+BufferedDiscreteInputOutputBlock.this.samples.length-index ) % BufferedDiscreteInputOutputBlock.this.samples.length ];
								
								return value;
							}
							else {
								throw new IndexOutOfBoundsException();
							}
						}

						@Override
						public int size() {
							return BufferedDiscreteInputOutputBlock.this.samples.length;
						}
						
					});
					
					BufferedDiscreteInputOutputBlock.this.inputIndex = (BufferedDiscreteInputOutputBlock.this.inputIndex+1) % BufferedDiscreteInputOutputBlock.this.samples.length;
					
					return value;
				}
				
			});
			
			this.setInput( new DiscreteInput() {

				@Override
				public void write(float value) {
					BufferedDiscreteInputOutputBlock.this.samples[BufferedDiscreteInputOutputBlock.this.outputIndex] = value;
					BufferedDiscreteInputOutputBlock.this.outputIndex = (BufferedDiscreteInputOutputBlock.this.outputIndex+1) % BufferedDiscreteInputOutputBlock.this.samples.length;
				}
				
			});
		}
		else {
			throw new IllegalArgumentException("Expected positive samples quantity");
		}
	}
	
	/**
	 * @pre La lista de valores anteriores no puede ser nula, y es de sólo
	 * 		lectura
	 * @post Devuelve el valor con los valores anteriores especificados
	 */
	protected abstract float calculateValue(List<Float> oldValues);
}
