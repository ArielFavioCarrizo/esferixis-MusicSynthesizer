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
package com.esferixis.musicsynthesizer.songs;

import com.esferixis.misc.ElementCallback;
import com.esferixis.musicsynthesizer.composition.Event;
import com.esferixis.musicsynthesizer.composition.note.Note;
import com.esferixis.musicsynthesizer.composition.note.emmiter.NoteEmmiter;
import com.esferixis.musicsynthesizer.composition.note.emmiter.TrackRouterNoteEmmiter;
import com.esferixis.musicsynthesizer.instrument.HomogeneousOrgan;
import com.esferixis.musicsynthesizer.instrument.Instrument;
import com.esferixis.musicsynthesizer.instrument.SawtoothOrgan;
import com.esferixis.musicsynthesizer.instrument.SquareOrgan;
import com.esferixis.musicsynthesizer.instrument.guitar.KarplusStrongStringGuitar;
import com.esferixis.musicsynthesizer.instrument.organ.OrganEventData;
import com.esferixis.musicsynthesizer.instrument.signalSynthesizer.SignalSynthesizer;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSignal;
import com.esferixis.musicsynthesizer.signal.continuous.transformers.DiscreteInputOutputBlockSignalTransformer;
import com.esferixis.musicsynthesizer.signal.continuous.transformers.TimeInvariantSignalTransformer;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.DiscreteFlanger;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.DiscreteInputOutputBlockComposition;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.ValueTransformerInputOutputBlock;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.filters.AverageLowpassFilterBlock;
import com.esferixis.musicsynthesizer.signal.valueTransformer.Clipper;
import com.esferixis.musicsynthesizer.signal.valueTransformer.SquareWaveDistorter;
import com.esferixis.musicsynthesizer.signal.valueTransformer.ValueScaleTransformer;

public class Song2 {
	public enum Track {
		ORGAN1
	}
	
	/**
	 * @post Carga la señal
	 */
	public static ContinuousSignal loadSignal() {
		final Instrument<OrganEventData> organ = new HomogeneousOrgan(new ContinuousSignal() {

			@Override
			protected float uncheckedGetValue(float t) {
				final float doublePI = (float) (Math.PI * 2.0f);
				return (float) Math.sin(t * doublePI + ( Math.exp(-t * 0.03f) * 5.0f + 2.0f ) * Math.sin(t * doublePI * 3.0f) ) * 3.0f;
			}
			
		}).transform(new TimeInvariantSignalTransformer(new Clipper(1.0f)));
		
		final SignalSynthesizer<OrganEventData> organSignalSynthesizer = organ.createSignalSynthesizer();
		
		new TrackRouterNoteEmmiter<Track, Void, OrganEventData>(Track.class,
				new TrackRouterNoteEmmiter.Entry<Track, Void, OrganEventData>(Track.ORGAN1, organSignalSynthesizer.getNoteEmmiter(), null)
		).invoke(new ElementCallback<NoteEmmiter<Track, OrganEventData>>() {

			@Override
			public void run(NoteEmmiter<Track, OrganEventData> noteEmmiter) {
				float startTime = 0.0f;
				float endTime;
				
				{
					float base = 20.0f;
					for ( int j = 3 ; j>=0 ; j-- ) {
						for ( int i = 0 ; i<10 ; i++ ) {
							if ( ( i == 10 ) && ( j == 3 ) ) {
								endTime = startTime + 2.0f;
							}
							else {
								endTime = startTime + 0.25f;
							}
							
							noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData( base ))));
							noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData( base +7.0f ))));
							
							startTime = endTime;
						}
						
						if ( j !=0 ) {
							endTime = startTime + 1.0f;
							
							noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData( base + 1.0f ))));
							noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData( base + 7.0f + 1.0f ))));
								
							startTime = endTime;
						}
					}
				}
				
				//noteEmmiter.emitEndMark(startTime);
				
				for ( int m = 0 ; m<=0 ; m++ ) {
					for ( int l = 0 ; l<2 ; l++ ) {
						{
							int k=3;
							for ( int j = 0 ; k>0 ; j++ ) {
								for ( int i = 0 ; i<=3 ; i++ ) {
									endTime = startTime + 0.125f;
									
									noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(20.0f))));
									noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(27.0f))));
									
									startTime = endTime;
								}
								
								if ( (j+1) % 4 == 0 ) {
									k--;
									
									if ( k != 0 ) {
										endTime = startTime + 0.25f;
										
										noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData( 20.0f+2.0f ))));
										noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(27.0f+2.0f))));
										
										startTime = endTime;
										
										endTime = startTime + 0.25f;
										
										noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(20.0f+1.0f))));
										noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(27.0f+1.0f))));
										
										startTime = endTime;
									}
								}
								else {
									endTime = startTime + 0.25f;
									
									noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(20.0f+1.0f))));
									noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(27.0f+1.0f))));
									
									startTime = endTime;
								}
								
							}
						}
						
						{
							int k = 3;
							int j = 0;
							
							while ( k>0 ) {
								endTime = startTime + 0.125f;
								
								float offset = 2.0f + j;
								
								if ( j == 3 ) {
									k--;
									j = 0;
								}
								else {
									j++;
								}
								
								noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData( 20.0f+offset ))));
								noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(27.0f+offset))));
								
								startTime = endTime;
							}
						}
					}
					
					for ( int i = 0 ; i<2 ; i++ ) {
						endTime = startTime + 2.0f;
							
						noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(18.0f))));
						noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(18.0f+7.0f))));
							
						startTime = endTime;
						
						endTime = startTime + 2.0f;
						
						noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(17.0f))));
						noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(17.0f+7.0f))));
							
						startTime = endTime;
						
						endTime = startTime + 2.0f;
						
						noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(18.0f))));
						noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(18.0f+7.0f))));
							
						startTime = endTime;
						
						endTime = startTime + 2.0f;
						
						noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(15.0f))));
						noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(15.0f+7.0f))));
							
						startTime = endTime;
					}
					
					for ( int k = 0 ; k<= 1; k++ ) {
						for ( int j = 0 ; j <=3*4-1 ; j++ ) {
							for ( int i = 0 ; i<=3 ; i++ ) {
								endTime = startTime + 0.125f;
								
								float offset = i % 2;
								
								noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(18.0f+offset))));
								noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(18.0f+7.0f+offset))));
								
								startTime = endTime;
							}
							
							endTime = startTime + 0.25f;
							
							float offset = j % 4 + 2;
							
							noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(18.0f+offset))));
							noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(18.0f+7.0f+offset))));
							
							startTime = endTime;
						}
						
						for ( int i = 0 ; i<=2*6-1 ; i++ ) {
							endTime = startTime + 0.125f;
							float offset = ( (i+1) % 2 );
							
							noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(23.0f+offset))));
							noteEmmiter.emit(new Note<Track, OrganEventData>(Track.ORGAN1, endTime, new Event<OrganEventData>(startTime, new OrganEventData(23.0f+7.0f+offset))));
							
							startTime = endTime;
						}
					}
				}
			}
			
		});
		
		return organSignalSynthesizer.synthesize();
	}
}
