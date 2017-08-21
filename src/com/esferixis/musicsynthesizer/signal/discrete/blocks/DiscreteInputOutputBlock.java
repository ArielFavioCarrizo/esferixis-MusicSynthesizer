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

public abstract class DiscreteInputOutputBlock {
	private DiscreteOutput output;
	private DiscreteInput input;
	
	public interface Factory {
		public DiscreteInputOutputBlock create();
	}
	
	/**
	 * @post Crea el bloque
	 */
	public DiscreteInputOutputBlock() {
		
	}
	
	/**
	 * @pre La entrada no puede ser nula
	 * @post Inicializa la entrada
	 */
	protected final void setInput(DiscreteInput input) {
		if ( input != null ) {
			this.input = input;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre La salida no puede ser nula
	 * @post Inicializa la salida
	 */
	protected final void setOutput(DiscreteOutput output) {
		if ( output != null ) {
			this.output = output;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la entrada
	 */
	public final DiscreteInput getInput() {
		return this.input;
	}
	
	/**
	 * @post Devuelve la salida
	 */
	public final DiscreteOutput getOutput() {
		return this.output;
	}
}
