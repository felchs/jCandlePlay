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
package com.jcandleplay.graph.data;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * This class represents a Candlestick
 * 
 *       |      high           |       high
 *       |                     |
 *       |                     |
 *   /-------\   open      /-------\
 *   |       |             |▒▒▒▒▒▒▒|   open
 *   |       |             |▒▒▒▒▒▒▒|
 *   |       |             |▒▒▒▒▒▒▒|
 *   |       |             |▒▒▒▒▒▒▒|
 *   |       |             |▒▒▒▒▒▒▒|
 *   |       |             |▒▒▒▒▒▒▒|
 *   |       |             |▒▒▒▒▒▒▒|
 *   \_______/  close      \-------/   close
 *       |                     |
 *       |                     |
 *       |                     |
 *       |                     |
 *       |      low            |        low
 *
 *       
 * The values open, high low and close of a candlesSick are 
 * given by the tick list values: {@link Candle#tickList}
 * </pre>
 * 
 * @author Felipe Santos
 *
 */
public class Candle 
{
	/**
	 * High of a candle
	 */
	public double high = -1;
	
	/**
	 * Low of a candle
	 */
	public double low = -1;
	
	/**
	 * Open of a candle
	 */
	public double open = -1;
	
	/**
	 * Close of a candle
	 */
	public double close = -1;
	
	/**
	 * The initial date of this candle
	 */
	public long initDate;
	
	/**
	 * The final date of this candle
	 */
	public long finalDate;
	
	/**
	 * The list of ticks of this candle
	 */
	public List<Tick> tickList = new ArrayList<Tick>();
	
	/**
	 * Updates the value of this candle including {@link Candle#tickList} and {@link Candle#finalDate}
	 * @param tick the {@link Tick} to update
	 */
	public void updateCandle(Tick tick) {
		tickList.add(tick);
		
		updateCandleValues(tick);
		
		this.finalDate = tick.timestamp;
	}
	
	/**
	 * Updates the values of this candle
	 * @param tick the {@link Tick} to update
	 */
	public void updateCandleValues(Tick tick)
	{
		if (high == -1) {
			high = tick.value;
		} else if (tick.value > high) {
			high = tick.value;
		}
		
		if (low == -1) {
			low = tick.value;
		} else if (tick.value < low) {
			low = tick.value;
		}
		
		if (open == -1) {
			open = tick.value;
		}
		
		close = tick.value;
	}
	
	/**
	 * Creates a copy of this candle
	 * @return a copy of this candle
	 */
	public Candle getCopy()
	{
		Candle candle = new Candle();
		candle.high = high;
		candle.low = low;
		candle.open = open;
		candle.close = close;
		candle.initDate = initDate;
		candle.finalDate = finalDate;
		
		for (Tick tick : tickList) 
		{
			Tick tickCopy = tick.getCopy();
			candle.tickList.add(tickCopy);
		}
		return candle;
	}
}