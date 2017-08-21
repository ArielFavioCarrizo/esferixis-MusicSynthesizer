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
package com.esferixis.musicsynthesizer.instrument.signalSynthesizer;

import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.musicsynthesizer.composition.note.Note;
import com.esferixis.musicsynthesizer.composition.note.emmiter.NoteEmmiter;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSignal;

/**
 * @param <D> Datos de evento
 */
public abstract class SignalSynthesizer<D> {
	private NoteEmmiter<Void, D> noteEmmiter;
	private boolean constructedSignal;
	
	private float startTimeLimit;
	private float endTimeLimit;
	
	/**
	 * @post Crea el constructor de señal con el emisor de notas especificado
	 */
	public SignalSynthesizer(final NoteEmmiter<Void, D> sourceNoteEmmiter) {
		this.setNoteEmmiter(sourceNoteEmmiter);
	}
	
	/**
	 * @post Crea el constructor de señal
	 */
	public SignalSynthesizer() {
		this.noteEmmiter = null;
	}
	
	/**
	 * @post Especifica el emisor de notas
	 */
	protected final void setNoteEmmiter(final NoteEmmiter<Void, D> sourceNoteEmmiter) {
		if ( this.noteEmmiter == null ) {
			if ( sourceNoteEmmiter != null ) {
				this.constructedSignal = false;
				
				this.startTimeLimit = Float.NEGATIVE_INFINITY;
				this.endTimeLimit = Float.POSITIVE_INFINITY;
				
				this.noteEmmiter = new NoteEmmiter<Void, D>() {
		
					@Override
					public void emitInternal(Note<Void, D> note) {
						if ( note != null ) {
							if ( !SignalSynthesizer.this.constructedSignal ) {
								sourceNoteEmmiter.emit(note);
							}
							else {
								throw new IllegalStateException("Signal has been synthesized");
							}
						}
						else {
							throw new NullPointerException();
						}
					}

					@Override
					public void emitStartMark(float time) {
						SignalSynthesizer.this.startTimeLimit = Math.max(time, SignalSynthesizer.this.startTimeLimit);
					}

					@Override
					public void emitEndMark(float time) {
						SignalSynthesizer.this.endTimeLimit = Math.min(time, SignalSynthesizer.this.endTimeLimit);
					}
					
				};
			}
			else {
				throw new NullPointerException();
			}
		}
		else {
			throw new IllegalStateException("Cannot set note emmiter when it has been setted");
		}
	}
	
	/**
	 * @post Devuelve el emisor de notas del constructor de señal
	 */
	public NoteEmmiter<Void, D> getNoteEmmiter() {
		return this.noteEmmiter;
	}
	
	/**
	 * @post Sintetiza la señal
	 */
	public final ContinuousSignal synthesize() {
		if ( !this.constructedSignal ) {
			ContinuousSignal resultSignal = this.uncheckedSynthesize();
			
			resultSignal = resultSignal.truncate(new FloatClosedInterval(this.startTimeLimit, this.endTimeLimit));
			
			this.constructedSignal = true;
			return resultSignal;
		}
		else {
			throw new IllegalStateException("Signal has been synthesized");
		}
	}
	
	/**
	 * @post Sintetiza la señal (Implementación interna)
	 */
	protected abstract ContinuousSignal uncheckedSynthesize();
}
