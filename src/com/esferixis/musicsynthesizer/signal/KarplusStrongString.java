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
package com.esferixis.musicsynthesizer.signal;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.esferixis.math.ExtraMath;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSampledSignal;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSignal;

public abstract class KarplusStrongString {
	/**
	 * @post Crea la cuerda
	 */
	public KarplusStrongString() {
		
	}
	
	public static class SampleInputData {
		private final int length;
		private final float decayFactor;
		
		/**
		 * @post Crea los parámetros de entrada de muestra, con la longitud
				 de cuerda, y el factor de caída especificado
		 * @param length
		 * @param decayFactor
		 */
		public SampleInputData(int length, float decayFactor) {
			this.length = length;
			this.decayFactor = decayFactor;
		}
		
		/**
		 * @post Devuelve la longitud
		 */
		public int getLength() {
			return this.length;
		}
		
		/**
		 * @post Devuelve el factor de caída
		 */
		public float getDecayFactor() {
			return this.decayFactor;
		}
	}
	
	/**
	 * @post Crea las muestras con el array de parámetros de entrada de muestra,
	 * 		 por cada muestra especificada
	 */
	public float[] createSamples(SampleInputData[] inputDataPerSample, int seed) {
		if ( inputDataPerSample.length > 0 ) {
			inputDataPerSample = inputDataPerSample.clone();
			
			float[] stringSamples = new float[inputDataPerSample[0].getLength()];
			
			final float[] resultSamples = new float[inputDataPerSample.length];
			
			int startIndex = 0;
				
			this.generateInitialState(stringSamples, seed);
			
			float average=0.0f;
			
			for ( int i=0 ; i<inputDataPerSample.length ; i++ ) {
				if ( inputDataPerSample[i].getLength() != stringSamples.length ) {
					final int oldSize = stringSamples.length;
					final int newSize = inputDataPerSample[i].getLength();
					ContinuousSignal originalString = new ContinuousSampledSignal(new FloatClosedInterval(0.0f, newSize-1), stringSamples);
					
					stringSamples = new float[newSize];
					for ( int j = 0 ; j<newSize; j++ ) {
						stringSamples[j] = originalString.getValue((float) j);
					}
					
					startIndex = (int) ((float) startIndex * (float) newSize / (float) oldSize);
				}
				
				final float newSampleValue;
				
				{
					final int localStartIndex = startIndex;
					final float[] localStringSamples = stringSamples;
					newSampleValue = this.newSample(new AbstractList<Float>() {
						
						@Override
						public Float get(int index) {
							if ( ( index >= 0 ) && ( index < this.size() ) ) {
								return localStringSamples[(index+localStartIndex) % this.size()];
							}
							else {
								throw new IndexOutOfBoundsException();
							}
						}
		
						@Override
						public int size() {
							return localStringSamples.length;
						}
						
					}, inputDataPerSample[i].getDecayFactor());
				}
				
				average += newSampleValue;
				
				resultSamples[i] = newSampleValue;
				
				stringSamples[startIndex] = newSampleValue;
				
				if ( startIndex == stringSamples.length-1 ) {
					startIndex = 0;
				}
				else {
					startIndex++;
				}
			}
			
			average /= (float) resultSamples.length;
			
			for ( int i=0 ; i<resultSamples.length; i++ ) {
				resultSamples[i] -= average;
			}
			
			return resultSamples;
		}
		else {
			throw new IllegalArgumentException("Invalid samples length");
		}
	}
	
	/**
	 * @post Crea las muestras con la longitud de muestras, el factor de caída, y la duración especificada
	 */
	public float[] createSamples(int stringSamplesLength, float decayFactor, int samplesDuration, int seed) {
		final SampleInputData[] samplesInputData = new SampleInputData[samplesDuration];
		Arrays.fill(samplesInputData, new SampleInputData(stringSamplesLength, decayFactor));
		return this.createSamples(samplesInputData, seed);
	}
	
	/**
	 * @post Crea la muestra inicial en el array especificado
	 */
	protected abstract void generateInitialState(float[] initialState, int seed);
	
	/**
	 * @post Calcula una nueva muestra a partir de la lista
	 * 		 de muestras especificadas, indicando el factor
	 * 		 de caida
	 */
	protected abstract float newSample(List<Float> samples, float decayFactor);
}
