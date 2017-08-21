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
package com.esferixis.musicsynthesizer.player.console;/**

 * 
 * Queda terminantemente prohibida la reproducción parcial y/o total, y
 * la creación de trabajos derivados sin la expresa autorización del autor.
 * 
 * Éste archivo es propietario y confidencial.
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSignal;

public final class SignalGetter {
	private SignalGetter() {}
	
	/**
	 * @pre Ninguno de los dos puede ser nulo.
	 * 		Y la clase de valor tiene que coincidir con el especificado
	 * @post Obtiene la clase cargadora de la señal con la clase de valor y el nombre
	 * 		 de clase especificado
	 */
	public static <V> ContinuousSignal getSignal(String signalCreatorClassName) {
		if ( signalCreatorClassName != null ) {
			try {
				Class<?> signalGetterClass = Class.forName(signalCreatorClassName);
				Method method;
				try {
					method = signalGetterClass.getMethod("loadSignal");
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e);
				} catch (SecurityException e) {
					throw new RuntimeException(e);
				}
				
				if ( Modifier.isStatic( method.getModifiers() ) ) {
					ContinuousSignal signal;
					try {
						signal = (ContinuousSignal) method.invoke(null);
					} catch (RuntimeException e) {
						throw e;
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					
					return signal;
				}
				else {
					throw new IllegalArgumentException("Static run method expected");
				}
					
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		else {
			throw new NullPointerException();
		}
	}
}
