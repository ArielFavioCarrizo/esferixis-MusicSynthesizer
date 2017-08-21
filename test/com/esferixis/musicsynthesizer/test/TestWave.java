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
package com.esferixis.musicsynthesizer.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.esferixis.geometry.plane.finite.LineSegment;
import com.esferixis.math.ExtraMath;
import com.esferixis.math.Vector2f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.musicsynthesizer.composition.Event;
import com.esferixis.musicsynthesizer.signal.KarplusStrongString;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSampledSignal;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSignal;
import com.esferixis.musicsynthesizer.signal.continuous.periodic.SawtoothWave;
import com.esferixis.musicsynthesizer.signal.continuous.periodic.SineWave;
import com.esferixis.musicsynthesizer.signal.continuous.periodic.SquareWave;
import com.esferixis.musicsynthesizer.signal.continuous.periodic.TriangleWave;
import com.esferixis.musicsynthesizer.signal.discrete.DiscreteInput;
import com.esferixis.musicsynthesizer.signal.discrete.DiscreteOutput;
import com.esferixis.musicsynthesizer.signal.discrete.DiscreteSignal;
import com.esferixis.musicsynthesizer.signal.discrete.NoiseDiscreteSignal;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.DiscreteDelay;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.DiscreteInputOutputBlock;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.filters.AverageLowpassFilterBlock;
import com.esferixis.musicsynthesizer.signal.discrete.blocks.filters.ConvolutionFilterBlock;
import com.esferixis.musicsynthesizer.signal.discrete.outputs.NoiseDiscreteOutput;
import com.esferixis.musicsynthesizer.signal.valueTransformer.ArctanDistorter;

public final class TestWave {
	private static final float doublePI = (float) Math.PI * 2.0f;
	
	/**
	 * @post Carga la señal
	 */
	public static ContinuousSignal loadSignal() {
		final float baseFrequency = Event.getPitchFrequency(40.0f);
		final float doublePI = (float) (Math.PI * 2.0f);
		
		return new ContinuousSignal(new FloatClosedInterval(0.0f, 100.0f)) {

			private final ContinuousSignal noise = new NoiseDiscreteSignal().continuousInterpolatedSignal(FloatClosedInterval.WIDESTFINITE);
			
			@Override
			protected float uncheckedGetValue(float t) {
				t *= baseFrequency;
				
				return noise.getValue(t*30.0f);
			}
			
		};
	}
}
