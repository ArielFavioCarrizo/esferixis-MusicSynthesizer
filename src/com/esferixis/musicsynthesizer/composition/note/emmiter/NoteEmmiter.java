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
package com.esferixis.musicsynthesizer.composition.note.emmiter;

import java.util.Arrays;
import java.util.Collection;

import com.esferixis.misc.ElementCallback;
import com.esferixis.musicsynthesizer.composition.note.Note;
import com.esferixis.musicsynthesizer.composition.note.NoteGroup;

public abstract class NoteEmmiter<T, D> {
	/**
	 * @post Emite la nota especificada
	 */
	protected abstract void emitInternal(Note<T, D> note);
	
	/**
	 * @post Emite la nota especificada y la devuelve
	 */
	public final Note<T, D> emit(Note<T, D> note) {
		if ( note != null ) {
			this.emitInternal(note);
			return note;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Emite un comienzo en el tiempo especificado
	 */
	public abstract void emitStartMark(float time);

	/**
	 * @post Emite un fin en el tiempo especificado
	 */
	public abstract void emitEndMark(float time);
	
	/**
	 * @post Invoca el "callback" especificado con el emisor de notas,
	 * 		 y devuelve el emisor
	 */
	public final NoteEmmiter<T, D> invoke(ElementCallback<NoteEmmiter<T, D>> elementCallBack) {
		elementCallBack.run(this);
		
		return this;
	}
	
	/**
	 * @post Emite el grupo de notas especificado, y lo devuelve
	 */
	public final NoteGroup<T, D> emit(NoteGroup<T, D> noteGroup) {
		for ( Note<T, D> eachNote : noteGroup.getNotes() ) {
			this.emit(eachNote);
		}
		
		return noteGroup;
	}
}
