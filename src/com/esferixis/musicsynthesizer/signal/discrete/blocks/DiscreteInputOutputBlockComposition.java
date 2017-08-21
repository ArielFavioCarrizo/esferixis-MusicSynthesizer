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

public final class DiscreteInputOutputBlockComposition extends DiscreteInputOutputBlock {
	private final DiscreteInputOutputBlock inBlock, outBlock;
	
	/**
	 * @pre Ninguna de las fábricas puede ser nula
	 * @post Crea la fábrica con la fábrica del bloque de entrada, y la fábrica del bloque de salida
	 */
	public static DiscreteInputOutputBlock.Factory createFactory(final DiscreteInputOutputBlock.Factory inBlockFactory, final DiscreteInputOutputBlock.Factory outBlockFactory) {
		if ( ( inBlockFactory != null ) && ( outBlockFactory != null ) ) {
			return new DiscreteInputOutputBlock.Factory() {
				
				@Override
				public DiscreteInputOutputBlock create() {
					return new DiscreteInputOutputBlockComposition(inBlockFactory.create(), outBlockFactory.create());
				}
			};
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Ninguna de las fábricas puede ser nula y tiene que tener por lo menos
	 * 		un elemento
	 * @post Crea una cadena de fábricas de bloques.
	 * 		 En caso de haber una sóla fábrica, se limita a devolverla
	 */
	public static DiscreteInputOutputBlock.Factory createFactory(DiscreteInputOutputBlock.Factory... blocksFactory) {
		if ( blocksFactory != null ) {
			blocksFactory = blocksFactory.clone();
			
			DiscreteInputOutputBlock.Factory result;
			
			if ( blocksFactory.length == 0 ) {
				throw new IllegalArgumentException("Expected one factory");
			}
			else {
				result = blocksFactory[blocksFactory.length-1];
				
				for ( int i = blocksFactory.length-2 ; i >= 0; i-- ) {
					result = createFactory(blocksFactory[i], result);
				}
				
				return result;
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Ninguno de los bloques puede ser nulo
	 * @post Crea la composición con el bloque de entrada y el de salida especificados
	 */
	public DiscreteInputOutputBlockComposition(DiscreteInputOutputBlock inBlock, DiscreteInputOutputBlock outBlock) {
		if ( ( inBlock != null ) && ( outBlock != null ) ) {
			this.inBlock = inBlock;
			this.outBlock = outBlock;
			
			this.setInput(new DiscreteInput() {

				@Override
				public void write(float value) {
					DiscreteInputOutputBlockComposition.this.inBlock.getInput().write(value);
					DiscreteInputOutputBlockComposition.this.outBlock.getInput().write(DiscreteInputOutputBlockComposition.this.inBlock.getOutput().read());
				}
				
			});
			
			this.setOutput(new DiscreteOutput() {

				@Override
				public float read() {
					return DiscreteInputOutputBlockComposition.this.outBlock.getOutput().read();
				}
				
			});
		}
		else {
			throw new NullPointerException();
		}
	}
}
