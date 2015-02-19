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

/**
 * Tick is every tick data that is composed by a timeStamp and a value
 * A {@link Candle} is composed by a list of ticks
 * 
 * @author Felipe Santos
 *
 */
public class Tick
{
	/**
	 * The value of this tick
	 */
	public double value;
	
	/**
	 * The tick timestamp
	 */
	public long timestamp;
	
	/**
	 * Empty constructor
	 */
	public Tick()
	{
	}
	
	/**
	 * Constructor passing fields
	 * @param value the value of this tick
	 * @param timestamp the timeStamp of this tick
	 */
	public Tick(double value, long timestamp)
	{
		this.timestamp = timestamp;
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return "Value: " + value + ", Timestamp: " + timestamp; 
	}

	/**
	 * Creates a copy of this Tick
	 * @return a copy of this {@link Tick}
	 */
	public Tick getCopy() 
	{
		Tick tick = new Tick();
		tick.value = value;
		tick.timestamp = timestamp;
		return tick;
	}
}