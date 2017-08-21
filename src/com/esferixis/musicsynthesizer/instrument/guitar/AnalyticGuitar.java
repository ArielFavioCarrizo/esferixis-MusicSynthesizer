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
package com.esferixis.musicsynthesizer.instrument.guitar;

import com.esferixis.musicsynthesizer.instrument.DiscreteInstrument;

public final class AnalyticGuitar extends DiscreteInstrument {
	private final float gamma;
	private int calculatedHarmonics;
	private float[] An;
	private float[] dfn;
	
	/**
	 * @pre Todos los parámetros tienen que ser positivos
	 * @post Crea una guitarra analítica con la frecuencia de muestreo, el gamma, el d y el b especificados
	 */
	public AnalyticGuitar(final float gamma, final float d, final float b) {
		super(44100, 0.01f);
		if ( ( gamma > 0.0f ) && ( b > 0.0f ) && ( d > 0.0f ) ) {
			this.gamma = gamma;
			this.calculatedHarmonics = this.frequencyRate / 2;
			
			this.dfn = new float[this.calculatedHarmonics];
			this.An = new float[this.calculatedHarmonics];
			
			float accumulatedIntensity = 0.0f;
			
			float squarePI = (float) (Math.PI * Math.PI);
					
			for ( int i = 0 ; i<this.calculatedHarmonics ; i++ ) {
				int j = i + 1;
				float doubleJ = (float) (j * j);
				
				An[i] = 2.0f / ( squarePI * doubleJ * d * (1.0f - d) ) * (float) Math.sin(j * (float) Math.PI * d);
				dfn[i] = (float) Math.sqrt(1.0f + doubleJ * b * b );
				
				accumulatedIntensity += An[i];
			}
			
			for ( int i = 0 ; i<this.calculatedHarmonics; i++ ) {
				An[i] /= accumulatedIntensity;
			}
		}
		else {
			throw new IllegalArgumentException();
		}
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.musicsynthesizer.instrument.DiscreteInstrument#createSignalGenerator()
	 */
	@Override
	protected SignalGenerator createSignalGenerator() {
		return new SignalGenerator() {
			private int nSample = 0;
			private float frequencyAcumulation;
			
			@Override
			public float nextSample(float frequency) {
				int nharmonics = Math.min( AnalyticGuitar.this.calculatedHarmonics, (int) Math.floor(AnalyticGuitar.this.frequencyRate / (2.0f * frequency)) );
				
				float value = 0.0f;
				final float t = this.nSample / (float) AnalyticGuitar.this.frequencyRate;
				this.frequencyAcumulation += frequency;
				
				final float auxFactor = (float) (2.0f * Math.PI) * this.frequencyAcumulation / (float) AnalyticGuitar.this.frequencyRate;
				for ( int i = 0 ; i<nharmonics; i++ ) {
					int ih = i + 1;
					value += An[i] * Math.exp(-gamma * (float) ih * t) * Math.sin(auxFactor * ih * dfn[i] );
				}
				
				this.nSample++;
				
				return value;
			}
			
		};
	}
}
