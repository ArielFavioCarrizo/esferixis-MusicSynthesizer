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
package com.esferixis.musicsynthesizer.composition.note;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.ElementCallback;
import com.esferixis.musicsynthesizer.composition.note.emmiter.NoteEmmiter;

public final class NoteGroup<T, D> {
	private final List<Note<T, D>> notes;
	
	public final static class BuilderEmmiter<T, D> extends NoteEmmiter<T, D> {
		private List<Note<T, D>> notes;
		
		float startTimeLimit;
		float endTimeLimit;

		public BuilderEmmiter() {
			this.notes = new ArrayList<Note<T, D>>();
			this.startTimeLimit = Float.NEGATIVE_INFINITY;
			this.endTimeLimit = Float.POSITIVE_INFINITY;
		}
		
		private void checkContext() {
			if ( this.notes == null ) {
				throw new IllegalStateException("Cannot use the note emmiter when the note group has been builded");
			}
		}
		
		/* (non-Javadoc)
		 * @see com.esferixis.musicsynthesizer.composition.NoteEmmiter#emitInternal(com.esferixis.musicsynthesizer.composition.Note)
		 */
		@Override
		protected void emitInternal(Note<T, D> note) {
			this.checkContext();
			
			if ( note != null ) {
				this.notes.add(note);
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @post Construye el grupo de notas
		 */
		public NoteGroup<T, D> buildNoteGroup() {
			this.checkContext();
			
			List<Note<T, D>> filteredNotes = new ArrayList<Note<T, D>>(this.notes.size());
			
			for ( Note<T, D> eachNote : this.notes ) {
				if ( ( eachNote.getStartTime() <= this.endTimeLimit ) && ( eachNote.getEndTime() >= this.startTimeLimit ) ) {
					filteredNotes.add(eachNote);
				}
			}
			
			final NoteGroup<T, D> noteGroup = new NoteGroup<T, D>(filteredNotes);
			this.notes = null;
			
			return noteGroup;
		}

		/* (non-Javadoc)
		 * @see com.arielcarrizo.musicsynthesizer.composition.note.emmiter.NoteEmmiter#emitStartMark(float)
		 */
		@Override
		public void emitStartMark(float time) {
			this.startTimeLimit = Math.max(time, this.startTimeLimit);
		}

		/* (non-Javadoc)
		 * @see com.arielcarrizo.musicsynthesizer.composition.note.emmiter.NoteEmmiter#emitEndMark(float)
		 */
		@Override
		public void emitEndMark(float time) {
			this.endTimeLimit = Math.max(time, this.endTimeLimit);
		}
	}
	
	/**
	 * @post Crea el conjunto de notas con el callback de emisor de notas
	 * 		 especificado
	 */
	public static <T, D> NoteGroup<T, D> create(ElementCallback<NoteEmmiter<T, D>> noteEmmiterCallBack) {
		if ( noteEmmiterCallBack != null ) {
			BuilderEmmiter<T, D> builderEmmiter = new BuilderEmmiter<T, D>();
			noteEmmiterCallBack.run(builderEmmiter);
			return builderEmmiter.buildNoteGroup();
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Crea el conjunto de notas, con las notas especificadas
	 */
	public NoteGroup(Collection<Note<T, D>> notes) {
		this.notes = Collections.unmodifiableList( new ArrayList<Note<T, D>>(notes) );
	}
	
	/**
	 * @post Devuelve las notas (Visión de sólo lectura)
	 */
	public List<Note<T, D>> getNotes() {
		return this.notes;
	}
	
	/**
	 * @post Desplaza el conjunto con el delta de tiempo especificado
	 */
	public NoteGroup<T, D> addTime(float timeDelta) {
		List<Note<T, D>> notes = new ArrayList<Note<T, D>>(this.notes.size());
		
		for ( Note<T, D> eachNote : this.notes ) {
			notes.add(eachNote.addTime(timeDelta));
		}
		
		return new NoteGroup<T, D>(notes);
	}
	
	/**
	 * @post Escala en el tiempo el conjunto, con el delta de tiempo especificado
	 */
	public NoteGroup<T, D> timeScale(float timeScale) {
		List<Note<T, D>> notes = new ArrayList<Note<T, D>>(this.notes.size());
		
		for ( Note<T, D> eachNote : this.notes ) {
			notes.add(eachNote.timeScale(timeScale));
		}
		
		return new NoteGroup<T, D>(notes);
	}
	
	/**
	 * @post Escala en el tiempo el conjunto, sin cambiar su origen, con el delta de tiempo especificado
	 */
	public NoteGroup<T, D> relativeTimeScale(float timeScale) {
		float startTime = this.getStartTime();
		
		List<Note<T, D>> notes = new ArrayList<Note<T, D>>(this.notes.size());
		
		for ( Note<T, D> eachNote : this.notes ) {
			notes.add(eachNote.addTime(-startTime).timeScale(timeScale).addTime(startTime));
		}
		
		return new NoteGroup<T, D>(notes);
	}
	
	/**
	 * @post Devuelve el tiempo de comienzo
	 */
	public float getStartTime() {
		float startTime = Float.POSITIVE_INFINITY;
		
		if ( !this.notes.isEmpty() ) {
			for ( Note<T, D> eachNote : notes ) {
				startTime = Math.min(startTime, eachNote.getStartTime());
			}
		}
		else {
			startTime = 0.0f;
		}
		
		return startTime;
	}
	
	/**
	 * @post Devuelve el tiempo de fin
	 */
	public float getEndTime() {
		float endTime = Float.NEGATIVE_INFINITY;
		
		if ( !this.notes.isEmpty() ) {
			for ( Note<T, D> eachNote : notes ) {
				endTime = Math.max(endTime, eachNote.getEndTime());
			}
		}
		else {
			endTime = 0.0f;
		}
		
		return endTime;
	}
	
	/**
	 * @post Devuelve el intervalo de tiempo
	 */
	public FloatClosedInterval getTimeInterval() {
		return new FloatClosedInterval(this.getStartTime(), this.getEndTime());
	}
}
