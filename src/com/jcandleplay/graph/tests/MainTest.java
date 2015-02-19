/*
 * This source file is part of Grideasy
 * For the latest info, see https://code.google.com/p/grideasy/
 * 
 * Grideasy is free software: you can redistribute it
 * and/or modify it under the terms of the MIT License.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.jcandleplay.graph.tests;

import java.awt.Container;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;

import com.jcandleplay.graph.data.Candle;
import com.jcandleplay.graph.data.CandlePlay;
import com.jcandleplay.graph.data.Tick;

public class MainTest {
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		Container pane = frame.getContentPane();
		
		CandlePlay candlePlay = new CandlePlay(pane, 1024, 768);
		
		// creating tick list
		List<Tick> tickList = new Vector<>();
		long currTime = System.currentTimeMillis();
		for (int i = 0; i < 300; i++) {
			Candle candle = new Candle();
			for (int j = 0; j < 60; j++) {
				double value = 1 + Math.random() * 0.0001 * 5;
				long timestamp = currTime + i * 1000l * 60l + j * 1000l;
				Tick tick = new Tick(value, timestamp);
				tickList.add(tick);
				candle.updateCandle(tick);
			}
		}
		
		candlePlay.setTickList(tickList);
		
		candlePlay.play();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}