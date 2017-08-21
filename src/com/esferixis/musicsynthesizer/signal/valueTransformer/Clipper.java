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
package com.esferixis.musicsynthesizer.signal.valueTransformer;

public final class Clipper extends ValueTransformer {
	public static final Clipper CANONICAL = new Clipper(1.0f);
	
	private final float maxAbsValue;
	
	/**
	 * @pre El valor máximo absoluto no puede ser negativo
	 * @post Crea el clipper con el valor máximo absoluto especificado
	 */
	public Clipper(float maxAbsValue) {
		if ( maxAbsValue > 0.0f ) {
			this.maxAbsValue = maxAbsValue;
		}
		else {
			throw new IllegalArgumentException("Invalid max value");
		}
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.musicsynthesizer.signal.valueTransformer.ValueTransformer#transform(float)
	 */
	@Override
	public float transform(float value) {
		if ( value > this.maxAbsValue ) {
			value = this.maxAbsValue;
		}
		else if ( value < -this.maxAbsValue ) {
			value = -this.maxAbsValue;
		}
		
		return value;
	}
}
