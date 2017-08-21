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

package com.esferixis.musicsynthesizer.instrument;

import com.esferixis.musicsynthesizer.instrument.signalSynthesizer.SignalSynthesizer;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSignal;
import com.esferixis.musicsynthesizer.signal.continuous.transformers.SignalTransformer;

/**
 * @param <D> Dato de evento
 */
public abstract class Instrument<D> {
	/**
	 * @post Crea un sintetizador de señal
	 */
	public abstract SignalSynthesizer<D> createSignalSynthesizer();
	
	/**
	 * @post Devuelve el órgano transformado con el transformador de señal
	 * 		 especificado
	 */
	public final Instrument<D> transform(final SignalTransformer signalTransformer) {
		if ( signalTransformer != null ) {
			final Instrument<D> originalOrgan = this;
			
			return new Instrument<D>() {
				@Override
				public SignalSynthesizer<D> createSignalSynthesizer() {
					final SignalSynthesizer<D> originalSignalSynthesizer = originalOrgan.createSignalSynthesizer();
					
					return new SignalSynthesizer<D>(originalSignalSynthesizer.getNoteEmmiter()) {

						@Override
						protected ContinuousSignal uncheckedSynthesize() {
							return signalTransformer.transform(originalSignalSynthesizer.synthesize());
						}
						
					};
				}
				
			};
		}
		else {
			throw new NullPointerException();
		}
	}
}
