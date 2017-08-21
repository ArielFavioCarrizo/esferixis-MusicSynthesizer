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

package com.esferixis.musicsynthesizer.player.console;

import java.io.BufferedReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.musicsynthesizer.signal.continuous.ContinuousSignal;

public class ConsolePlayer {
	
	public static void main(String[] args) {
		ContinuousSignal signal;
		
		if ( args.length == 1 ) {
			signal = (ContinuousSignal) SignalGetter.getSignal(args[0]);
		}
		else {
			System.err.println("Se esperaba la clase creadora de la señal");
			return;
		}
		
		AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0f, 16, 1, 2, 44100.0f, true);
		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
		
		SourceDataLine sourceDataLine;
		try {
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
		} catch (LineUnavailableException e) {
			throw new RuntimeException(e);
		}
		
		sourceDataLine.start();
		
		Scanner inScanner = new Scanner(System.in);
		
		ContinuousSignal playSignal;
		
		boolean haveClipping;
		
		System.out.println("Intervalo de tiempo de la señal: " + signal.getTimeInterval());
		
		playSignal = signal;
		
		if ( playSignal != null ) {
			int numberOfSamples = (int) (playSignal.getTimeInterval().length() * audioFormat.getSampleRate());
			
			ByteBuffer byteBuffer = ByteBuffer.allocate(numberOfSamples * 2);
				
			byteBuffer.rewind();
			
			System.out.println("Sintetizando, espere un momento por favor...");
			
			float[] floatSamples = new float[numberOfSamples];
			float maxAbsoluteValue = 0.0f;
			
			for ( int i = 0 ; i < numberOfSamples ; i++ ) {
				float t = (float) i * playSignal.getTimeInterval().length() / (float) numberOfSamples + playSignal.getTimeInterval().getMin();
				float value = playSignal.getValue(t);
				
				floatSamples[i] = value;
				maxAbsoluteValue = Math.max(maxAbsoluteValue, Math.abs(value));
			}
			
			for ( int i = 0 ; i < numberOfSamples ; i++ ) {
				float value = floatSamples[i] / maxAbsoluteValue;
				
				value = Math.min(1.0f, value);
				value = Math.max(-1.0f, value);
				
				short shortValue = (short) ((value + 1.0f) / 2.0f * 65535.0f + 32768.0f);
				byteBuffer.putShort(shortValue);
			}
			
			System.out.println("Reproduciendo...");
			
			sourceDataLine.write(byteBuffer.array(), 0, numberOfSamples * 2);
			
			sourceDataLine.drain();
		}
			
		sourceDataLine.stop();
		sourceDataLine.close();
	}

}
