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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jcandleplay.graph.GraphPanel;
import com.jcandleplay.graph.PlayThreadStatus;

/**
 * This class holds the candle play of candlesticks
 * The play can be set on a given period.
 * It can be real time, accelerated or all the data can be drawn immediately
 *  
 * @author Felipe Santos
 *
 */
public class CandlePlay {
	/**
	 * Container to add graph compoenents
	 */
	private Container container;
		
	/**
	 * The data tick list 
	 */
	private List<Tick> tickList;
	
	/**
	 * The width of the {@link CandlePlay#graph}
	 */
	private int width;
	
	/**
	 * The height of the {@link CandlePlay#graph}
	 */
	private int height;
	
	/**
	 * The graph of render of candles
	 */
	private GraphPanel graph;
	
	/**
	 * The velocity the time is playing
	 * 1 x is the real time acceleration
	 */
	private double timeAcceleration = 1;
	
	private double timePosition = 0;
	
	/**
	 * The play thread that handles the candles input for drawing
	 */
	private Thread playThread;

	/**
	 * The play thread status
	 */
	private PlayThreadStatus playThreadStatus = PlayThreadStatus.PLAYING;
	
	/**
	 * The internal variable to handle accumulated time since last candle draw
	 */
	private long internalAnimatedAccumTime = 0;

	/**
	 * The interval of the candle
	 * It initializes with one second
	 */
	private long internalIntervalCandle = 1000 * 60;

	/**
	 * Constructor passing fields
	 * @param width the width of the {@link CandlePlay#graph}
	 * @param height the height of the {@link CandlePlay#graph}
	 */
	public CandlePlay(Container container, int width, int height) {
		this(container, null, width, height);
	}

	/**
	 * Constructor passing fields
	 * @param tickList the data tick list to be drawn
	 * @param width the width of the {@link CandlePlay#graph}
	 * @param height the height of the {@link CandlePlay#graph}
	 */
    public CandlePlay(Container container, List<Tick> tickList, int width, int height) {
    	this.container = container;
    	this.tickList = tickList;
    	this.width = width;
    	this.height = height;
    	
    	initGraph();
	}

    /**
     * Initializes the graph components
     */
	private void initGraph() {
    	this.graph = new GraphPanel(width, height);
		
		graph.setPreferredSize(new Dimension(width, height));
		container.add(graph, BorderLayout.CENTER);
		
		setupRightScroll(container);
		setupBottomScroll(container);
		
		setupSliderTimeVelocity(container);
		
		setupSliderTimePosition(container);
	}
	
	private void setupSliderTimePosition(Container pane) {
		final int minAcceleration = 1;
		final int initialAcceleration = 1;
		final int maxAcceleration = 100;
		JSlider timeAccelerationSlider = new JSlider(JSlider.HORIZONTAL, minAcceleration, maxAcceleration, initialAcceleration);
		timeAccelerationSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
					timePosition = source.getValue() / 100f;
					
					internalAnimatedAccumTime = (long)((tickList.get(tickList.size() - 1).timestamp - tickList.get(0).timestamp) * timePosition);
				}
			}
		});
		timeAccelerationSlider.setMajorTickSpacing(10);
		timeAccelerationSlider.setMinorTickSpacing(1);
		//timeAccelerationSlider.setPaintTicks(true);
		//timeAccelerationSlider.setPaintLabels(true);
		timeAccelerationSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		Font font = new Font("Arial", Font.PLAIN, 12);
		timeAccelerationSlider.setFont(font);
		graph.add(timeAccelerationSlider);
		timeAccelerationSlider.setAlignmentY(0.1f);
	}

	/**
	 * It sets up the graph's acceleration slider
	 * @param pane the container to put the slider
	 */
	private void setupSliderTimeVelocity(Container pane) {
		final int minAcceleration = 1;
		final int initialAcceleration = 1;
		final int maxAcceleration = 100;
		JSlider timeAccelerationSlider = new JSlider(JSlider.HORIZONTAL, minAcceleration, maxAcceleration, initialAcceleration);
		timeAccelerationSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
					timeAcceleration = source.getValue();
				}
			}
		});
		timeAccelerationSlider.setMajorTickSpacing(10);
		timeAccelerationSlider.setMinorTickSpacing(1);
		//timeAccelerationSlider.setPaintTicks(true);
		//timeAccelerationSlider.setPaintLabels(true);
		timeAccelerationSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		Font font = new Font("Arial", Font.PLAIN, 12);
		timeAccelerationSlider.setFont(font);
		graph.add(timeAccelerationSlider);
		timeAccelerationSlider.setAlignmentY(0.1f);
	}

	/**
	 * It sets up the graph's bottom scroll
	 * @param pane the container to put the scroll
	 */
	private void setupBottomScroll(Container pane) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	 
	    JScrollBar scrollOffset = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 100);
	    c.weightx = 0.9;
	    c.fill = GridBagConstraints.HORIZONTAL;
	    panel.add(scrollOffset, c);
	    scrollOffset.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int scrollVerticalOffsetValue = e.getValue();
				graph.setHorizontalOffset(scrollVerticalOffsetValue / 100.0);
			}
		});

	    JScrollBar scrollZoom = new JScrollBar(JScrollBar.HORIZONTAL, 100, 0, 1, 100);
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.weightx = 0.1;
	    panel.add(scrollZoom, c);
	    scrollZoom.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int scrollVerticalZoomValue = e.getValue();
				graph.setHorizontalZoom(scrollVerticalZoomValue / 100.0);
			}
		});
		
		pane.add(panel, BorderLayout.PAGE_END);
	}

	/**
	 * It sets up the graph's right scroll
	 * @param pane the container to put the scroll 
	 */
	private void setupRightScroll(Container pane) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	 
	    JScrollBar scrollOffset = new JScrollBar(JScrollBar.VERTICAL, 0, 0, 0, 100);
	    c.weighty = 0.85;
	    c.fill = GridBagConstraints.VERTICAL;
	    panel.add(scrollOffset, c);
	    scrollOffset.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int scrollHorizontalOffsetValue = e.getValue();
				graph.updateVerticalOffsetValue(scrollHorizontalOffsetValue / 100.0);
			}
		});
	    
	    JScrollBar scrollZoom = new JScrollBar(JScrollBar.VERTICAL, 0, 0, 0, 100);
	    c.fill = GridBagConstraints.VERTICAL;
	    c.gridy = 1;
	    c.weighty = 0.15;
	    panel.add(scrollZoom, c);
	    scrollZoom.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int scrollHorizontalZoomValue = e.getValue();
				graph.updateVerticalZoomValue(scrollHorizontalZoomValue / 100.0);
			}
		});
		
		pane.add(panel, BorderLayout.LINE_END);
	}

	/**
	 * Sets the time acceleration of graph plotting
	 * @param timeAcceleration the time acceleration of graph plotting
	 */
	public void setTimeAcceleration(double timeAcceleration) {
		this.timeAcceleration = timeAcceleration;
	}

	/**
	 * It initializes the graph play of candles
	 */
	public void play() {
		// it starts the graph render loop
		graph.startRenderLoop();
		
		this.playThread = new Thread("CandlePlay.play thread") {
			long lastTime = System.currentTimeMillis();
			
			public void run() {
				while (playThreadStatus != PlayThreadStatus.INACTIVATED) {
					if (playThreadStatus == PlayThreadStatus.PLAYING) {
						if (tickList != null && !tickList.isEmpty()) {
							List<Tick> tickListFiltered = new Vector<>();
							
							{ // updates the current initial time and window by horizontal zoom and offset
								int fromIndexWithZoom = ((tickList.size() - 1) - (int) ((tickList.size() - 1) * graph.getHorizontalZoom()));
								int fromIndex = 0;
								int toIndex = tickList.size() - 1;
								
								int range = toIndex - fromIndexWithZoom;
								
								int middleIdx = (int) (tickList.size() * graph.getHorizontalOffset());
								int rangeBy2 = (int) (range / 2f);
								int toIndexWithOffset = middleIdx + rangeBy2;
								int fromIndexWithOffset = middleIdx - rangeBy2;
								
								if (fromIndexWithOffset < fromIndex) {
									fromIndexWithOffset = fromIndex;
								}
								if (toIndexWithOffset > toIndex) {
									toIndexWithOffset = toIndex;
								}
								
								//System.out.println("middleIdx: " + middleIdx + ", rangeBy2: " + rangeBy2 + ", fromIndexWithOffset: " + fromIndexWithOffset + ", toIndexWithOffset: " + toIndexWithOffset);
								
								tickListFiltered = tickList.subList(fromIndexWithOffset, toIndexWithOffset);
							}
							
							long currTime = System.currentTimeMillis();
							long diffFromLastTime = (long) ((currTime - lastTime) * timeAcceleration);
							List<Candle> dataTickList = getCandleList(diffFromLastTime, tickListFiltered);
							
							graph.setCandleList(dataTickList);
							lastTime = currTime;
							
							// frame rate of candle insertions
							final long millis = 100;
							try {
								Thread.sleep(millis);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		};
		
		playThread.start();
	}
	
	/**
	 * Sets the current play thread status
	 * It also handles the thread wait notify 
	 * @param playThreadStatus the graph {@link PlayThreadStatus}
	 */
	public void setPlayThreadStatus(PlayThreadStatus playThreadStatus) {
		this.playThreadStatus = playThreadStatus;
	}

	/**
	 * Gets the list of candles to added since the last print
	 * 
	 * @param diffFromLastTime the last time the candles were printed
	 * @param tickList the tick list of candles to filter
	 * @return the list of candles to be added
	 */
	private List<Candle> getCandleList(long diffFromLastTime, List<Tick> tickList) {
		Tick lastTick = null;
		
		internalAnimatedAccumTime += diffFromLastTime;
		
		SortedMap<Long, Candle> candleMap = new TreeMap<Long, Candle>();
		long initialTime = 0;
		if (tickList.size() > 0) {
			initialTime = tickList.get(0).timestamp;
		}
		for (Tick tick : tickList) {
			if (tick.timestamp <= initialTime + internalAnimatedAccumTime) {
				long timeCandle = tick.timestamp - tick.timestamp % internalIntervalCandle;
				Candle candleOnMap = candleMap.get(timeCandle);
				if (candleOnMap == null) {
					candleOnMap = new Candle();
					candleMap.put(timeCandle, candleOnMap);
					
					if (lastTick != null) {
						candleOnMap.updateCandleValues(lastTick);
					}
				}
				
				candleOnMap.updateCandle(tick);
				
				lastTick = tick;
			}
		}
		Collection<Candle> candleCollection = candleMap.values();
		List<Candle> candleList = new Vector<Candle>();
		for (Candle candle : candleCollection) {
			candleList.add(candle);			
		}
			
		return candleList;
	}
	
	/**
	 * Sets the current list of candles to be printed
	 * @param tickList
	 */
	public void setTickList(List<Tick> tickList) {
		this.tickList = tickList;
	}
}