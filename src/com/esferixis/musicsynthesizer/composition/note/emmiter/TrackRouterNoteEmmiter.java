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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.esferixis.musicsynthesizer.composition.note.Note;

public final class TrackRouterNoteEmmiter<S, D, T> extends NoteEmmiter<S, T> {
	public static final class Entry<S, D, T> {
		private final S sourceTrack;
		private final NoteEmmiter<D, T> destinationEmmiter;
		private final D destinationTrack;
		
		/**
		 * @post Crea la entrada con la pista origen, el emisor destino y
		 * 		 la pista destino especificados
		 */
		public Entry(S sourceTrack, NoteEmmiter<D, T> destinationEmmiter, D destinationTrack) {
			this.sourceTrack = sourceTrack;
			this.destinationEmmiter = destinationEmmiter;
			this.destinationTrack = destinationTrack;
		}
		
		/**
		 * @post Devuelve la pista origen
		 */
		public S getSourceTrack() {
			return this.sourceTrack;
		}
		
		/**
		 * @post Devuelve el emisor de notas destino
		 */
		public NoteEmmiter<D, T> getDestinationEmmiter() {
			return this.destinationEmmiter;
		}
		
		/**
		 * @post Devuelve la pista destino
		 */
		public D getDestinationTrack() {
			return this.destinationTrack;
		}
	}
	
	private final Map<S, Entry<S, D, T>> sourceTrackToDestinationEntryMapper;
	
	/**
	 * @post Crea el emisor de notas con la clase depista de entrada y
	 * 		 las entradas especificadas
	 */
	public TrackRouterNoteEmmiter(Class<S> sourceTrack, Collection<Entry<S, D, T>> entries) {
		if ( ( sourceTrack != null ) && ( entries != null ) ) {
			if ( Enum.class.isAssignableFrom(sourceTrack) ) {
				this.sourceTrackToDestinationEntryMapper = new EnumMap(sourceTrack);
			}
			else {
				this.sourceTrackToDestinationEntryMapper = new HashMap<S, Entry<S, D, T>>();
			}
			
			for ( Entry<S, D, T> eachEntry : entries ) {
				this.sourceTrackToDestinationEntryMapper.put(eachEntry.getSourceTrack(), eachEntry);
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Crea el emisor de notas con la clase depista de entrada y
	 * 		 las entradas especificadas
	 */
	@SafeVarargs
	public TrackRouterNoteEmmiter(Class<S> sourceTrack, Entry<S, D, T>... entries) {
		this(sourceTrack, Arrays.asList(entries));
	}

	/* (non-Javadoc)
	 * @see com.esferixis.musicsynthesizer.composition.note.emmiter.NoteEmmiter#emitInternal(com.esferixis.musicsynthesizer.composition.note.Note)
	 */
	@Override
	protected void emitInternal(Note<S, T> note) {
		final Entry<S, D, T> entry = this.sourceTrackToDestinationEntryMapper.get(note.getTrack());
		entry.getDestinationEmmiter().emit(note.changeTrack(entry.getDestinationTrack()));
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.musicsynthesizer.composition.note.emmiter.NoteEmmiter#emitStartMark(float)
	 */
	@Override
	public void emitStartMark(float time) {
		for ( Entry<S, D, T> eachEntry : this.sourceTrackToDestinationEntryMapper.values() ) {
			eachEntry.getDestinationEmmiter().emitStartMark(time);
		}
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.musicsynthesizer.composition.note.emmiter.NoteEmmiter#emitEndMark(float)
	 */
	@Override
	public void emitEndMark(float time) {
		for ( Entry<S, D, T> eachEntry : this.sourceTrackToDestinationEntryMapper.values() ) {
			eachEntry.getDestinationEmmiter().emitEndMark(time);
		}
	}
}
