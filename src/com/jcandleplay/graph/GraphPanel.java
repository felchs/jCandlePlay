/*
 * This source file is part of jCandlePlay
 * 
 * jCandlePlay is free software: you can redistribute it
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
package com.jcandleplay.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import com.jcandleplay.graph.data.Candle;
import com.jcandleplay.graph.utils.GraphDateUtils;

/**
 * This class represents the Graph of candlestick
 * It has the render loop and candle data to be draw
 * @author Felipe Santos
 *
 */
public class GraphPanel extends JPanel {
	/**
	 * Serial version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The fixed step rate time for rendering
	 * 24 frames per seconds
	 */
	private final static long stepRateInTime = (long) ((1 / (double)24) * 1000);
	
	/**
	 * The open candle color
	 */
	private final static Color openCandleColor = new Color(0, 190, 0);
	
	/**
	 * The close candle color
	 */
	private final static Color closeCandleColor = new Color(190, 0, 0);
	
	/**
	 * A light gray color
	 */
	private final static Color lightGrayColor = new Color(118, 118, 118);
	
	/**
	 * A  light blue color
	 */
	private final static Color lightBlueColor = new Color(230, 230, 250);
	
	/**
	 * The lock for rendering loop
	 */
	private final Object redrawLock = new Object();
	
	/**
	 * The current list of candles to draw
	 */
	private Collection<Candle> candleList;
	
	/**
	 * The x offset in percentage 
	 */
	private double xOffsetPerc = 0;
	
	/**
	 * The y offset in percentage
	 */
	private double yOffsetPerc = 0;
	
	private double horizontalOffset = 0;

	/**
	 * The top padding in percentage 
	 */
	private double verticalPadding = 0;
	
	/**
	 * The zoom the data is submited to
	 */
	private double horizontalZoom = 1;
	
	/**
	 * The height of the time line
	 */
	private final int heightTimeLine = 15;
	
	/**
	 * The width of the right vertical label
	 */
	private final int widthVertLabel = 70;
	
	/**
	 * The y position of time line 
	 */
	private final int yTimeLine = 0;
	
	/**
	 * Internal variable to handle box width of time line
	 */
	private int internalLastBoxLimit = 0;
	
	/**
	 * The current position of mouse X
	 */
	private int mouseX;
	
	/**
	 * The current position of mouse Y
	 */
	private int mouseY;

	/**
	 * Constructor to setup the GUI components
	 * @param width the width of the graph
	 * @param height the height of the graph
	 **/
	public GraphPanel(int width, int height) {
		setPreferredSize(new Dimension(width, height));
	}
	
	/**
	 * Sets the current candle list to be used when drawing the graph each render step
	 * @param candleList the current candle list to be used when drawing the graph each render step
	 */
	public void setCandleList(List<Candle> candleList) {
		this.candleList = candleList;
	}
	
	/**
	 * Gets the current candle list to be used when drawing the graph each render step
	 * @return the current candle list to be used when drawing the graph each render step
	 */
	public Collection<Candle> getCandleList() {
		return candleList;
	}
	
	/**
	 * Gets the X offset percentage of the graph
	 * @return the X offset percentage of the graph
	 */
	public double getXOffsetPerc() {
		return xOffsetPerc;
	}
	
	/**
	 * Sets the x offset of graph
	 * @param xOffsetPerc the x offset in percentage in the graph
	 */
	public void setXOffsetPerc(double xOffsetPerc) {
		this.xOffsetPerc = xOffsetPerc;
	}
	
	/**
	 * Sets the y offset of graph
	 * @param yOffsetPerc the y offset in percentage in the graph
	 */
	public void setYOffsetPerc(double yOffsetPerc) {
		this.yOffsetPerc = yOffsetPerc;
	}
	
	/**
	 * Sets the padding of graph
	 * @param verticalPadding the right padding in percentage in the graph
	 */
	public void setGraphVerticalPadding(double verticalPadding) {
		this.verticalPadding = verticalPadding;
	}

	/**
	 * Get the minimum value from the candles of the graph
	 * @return the minimum value from the candles of the graph
	 */
	private double getMinValue() {
		Candle min = Collections.min(candleList, new Comparator<Candle>() {
			@Override
			public int compare(Candle o1, Candle o2) {
				return o1.low - o2.low > 0 ? 1 : -1;
			}
		});
		return min.low;
	}

	/**
	 * Get the max value from the candles of the graph
	 * @return the max value from the candles of the graph
	 */
	private double getMaxValue() {
		Candle max = Collections.max(candleList, new Comparator<Candle>() {
			@Override
			public int compare(Candle o1, Candle o2) {
				return o1.high - o2.high > 0 ? 1 : -1;
			}
		});
		return max.high;
	}
	
	/**
	 * It draw a candle within the graph
	 * @param g the {@link Graphics} to render the candles into
	 * @param candle the {@link Candle} itself
	 * @param index the index of this candle in the graph
	 * @param numCandles number of candles in the graph
	 */
	private void drawCandle(Graphics g, Candle candle, int index, int numCandles, double minValue, double maxValue) {
		int graphWidth = this.getWidth() - widthVertLabel;
		int graphHeight = this.getHeight() - heightTimeLine;
		int xOffset = (int) (graphWidth * xOffsetPerc);
		
		double verticalPaddingDiff = verticalPadding * graphHeight;
		graphHeight = (int) (graphHeight - verticalPaddingDiff);
		int yPaddingOffset = (int) (verticalPaddingDiff * 0.5);
		int yOffset = (int) (graphHeight * yOffsetPerc) + heightTimeLine + yPaddingOffset;

		int candleWidth = (int) (graphWidth / (double)numCandles);
		int spaceBetwenCandles = (int) (candleWidth * 0.05);
		candleWidth -= spaceBetwenCandles;
		double xPerc = index / (double)numCandles;
		int x = (int) (xPerc * graphWidth) + xOffset;
		
		double yOpenPerc = 1 - (candle.open - minValue) / (double)(maxValue - minValue);
		double yHighPerc = 1 - (candle.high - minValue) / (double)(maxValue - minValue);
		double yLowPerc = 1 - (candle.low - minValue) / (double)(maxValue - minValue);
		double yClosePerc = 1 - (candle.close - minValue) / (double)(maxValue - minValue);
		
		double yMinOC = yOpenPerc > yClosePerc ? yOpenPerc : yClosePerc;
		double yMaxOC = yOpenPerc < yClosePerc ? yOpenPerc : yClosePerc;
		
		int yHighPos = (int) (yHighPerc * graphHeight) + yOffset;
		int yMaxPos = (int) (yMaxOC * graphHeight) + yOffset;
		int yMinPos = (int) (yMinOC * graphHeight) + yOffset;
		int yLowPos = (int) (yLowPerc * graphHeight + yOffset);
		
		int candleMiddleOffset = (int) (candleWidth * 0.5);
		
		// light gray
		g.setColor(lightGrayColor);
		
		// high stick
		int highStickSz = yMaxPos - yHighPos;
		g.drawRect(x + candleMiddleOffset, yHighPos, 0, highStickSz);
		
		// low stick
		int lowStickSz = yLowPos - yMinPos;
		g.drawRect(x + candleMiddleOffset, yMinPos, 0, lowStickSz);
		
		// open to close
		int candleSz = yMinPos - yMaxPos;
		if (yOpenPerc > yClosePerc) {
			g.setColor(openCandleColor);
		} else {
			g.setColor(closeCandleColor);
		}
		g.fillRect(x, yMaxPos, candleWidth, candleSz);
		
		g.setColor(lightGrayColor);
		
		g.drawRect(x, yMaxPos, candleWidth, candleSz);
		
		{// draw timeline
			final int szBoxWidth = (int) (graphWidth / (double)numCandles);
			int boxWidth = szBoxWidth;
			
			int idx = 1;
			while (boxWidth < 200) {
				boxWidth = (szBoxWidth * idx++);
			}

			if (x >= internalLastBoxLimit) {
				g.setColor(Color.DARK_GRAY);
				g.drawRect(x, yTimeLine, 0, heightTimeLine);
				
				g.setColor(Color.BLACK);
				String strDate = GraphDateUtils.longToStrDate(candle.finalDate);
				g.drawString(strDate, x + 5, yTimeLine + heightTimeLine - 3);
				internalLastBoxLimit = x + boxWidth;
			}
		}
	}
	
	
	/**
	 * It resumes the render loop after draw objects
	 */
	@SuppressWarnings("unused")
	private void resumeLoop() {
		synchronized (redrawLock) {
			redrawLock.notify();
		}
	}
	
	/**
	 * It waits for objects to be draw to continue the render loop
	 */
	@SuppressWarnings("unused")
	private void waitForPaint() {
        try {
            synchronized (redrawLock) {
                redrawLock.wait();
            }
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
    }	

	/**
	 * Custom painting
	 **/
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		super.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
			}
		});
		
		int width = this.getWidth();
		int height = this.getHeight();
		
		setBackground(Color.CYAN);

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);

		internalLastBoxLimit = 0;
		if (candleList != null && !candleList.isEmpty()) {
			{ // timeline background
				g.setColor(Color.WHITE);
				g.fillRect(0, yTimeLine, width, heightTimeLine);
			}
			
			{ // vertical label background
				g.setColor(Color.WHITE);
				g.fillRect(width - widthVertLabel, heightTimeLine, widthVertLabel, height);
			}
			
			double minValue = getMinValue();
			double maxValue = getMaxValue();
		
			long initialTime = 0;
			long finalTime = 0;
			
			int sz = candleList.size();
			Iterator<Candle> candleIt = candleList.iterator();
			int index = 0;
			while (candleIt.hasNext()) {
				Candle candle = candleIt.next();
				drawCandle(g, candle, index++, sz, minValue, maxValue);
				
				if (initialTime == 0) {
					initialTime = candle.finalDate;
				}
				finalTime = candle.finalDate;
			}
			
			int graphHeight = this.getHeight() - heightTimeLine;
			double verticalPaddingDiff = verticalPadding * graphHeight;
			graphHeight = (int) (graphHeight - verticalPaddingDiff);
			
			{ // vertical label background
				int yPaddingOffset = (int) (verticalPaddingDiff * 0.5);
				int yOffset = (int) (graphHeight * yOffsetPerc) + heightTimeLine + yPaddingOffset;
				
				int numVertBox = 12;
				for (int i = 0; i <= numVertBox; i++) {
					double heightValuePerc = i / (double)numVertBox;
					double currValue = minValue +  heightValuePerc * (maxValue - minValue);
					double percY = 1 - (currValue - minValue) / (maxValue - minValue);
					int yBox = (int) (percY * graphHeight) + yOffset;
					g.setColor(Color.DARK_GRAY);
					int offsetLabel = 12;
					int xLabel = width - widthVertLabel + offsetLabel;
					int yLabel = yBox;
					String valueLabel = "" + currValue;
					String[] valueSplit = valueLabel.split("\\.");
					valueLabel = valueSplit[0] + "." + valueSplit[1].substring(0, (valueSplit[1].length() < 5 ? valueSplit[1].length() : 5));
					g.drawString(valueLabel, xLabel, yLabel);
				}
			}
			
			{ // draw line cross
				if (mouseX < width - widthVertLabel && mouseY > heightTimeLine) {
					// value
					g.drawLine(0, mouseY, width - widthVertLabel, mouseY);
					
					// time line
					g.drawLine(mouseX, heightTimeLine, mouseX, height);
				}
				
				// draw current cross time line value
				double percX = mouseX / (double)(width - widthVertLabel);
				//System.out.println(width + ", widthVert: " + widthVertLabel + ", "+ (width - widthVertLabel) + ", percX: " + percX);
				
				g.setColor(lightBlueColor);
				g.fillRect(mouseX - widthVertLabel, 0, 120, heightTimeLine - 1);
				g.setColor(Color.LIGHT_GRAY);
				g.drawRect(mouseX - widthVertLabel, 0, 120, heightTimeLine - 1);
				
				// top date
				long currDate = initialTime + (long)(percX * (finalTime - initialTime));
				String strDate = GraphDateUtils.longToStrDate(currDate);
				//System.out.println("|--- initaltime: " + GraphDateUtils.longToStrDate(initialTime) + ", finaltime: " + GraphDateUtils.longToStrDate(finalTime) + ", perc: " + percX + ", " + strDate);
				g.setColor(Color.BLACK);
				g.drawString(strDate, mouseX + 3  - widthVertLabel, heightTimeLine - 2);

				// draw current cross vertical value
				int heightReducePx = (int) ((height - graphHeight) * 0.5);
				double percY = (mouseY - heightReducePx) / (double)(graphHeight);
				
				g.setColor(lightBlueColor);
				g.fillRect(width - widthVertLabel, mouseY - (int)(heightTimeLine * 0.5) - 1, widthVertLabel - 1, heightTimeLine + 2);
				g.setColor(Color.LIGHT_GRAY);
				g.drawRect(width - widthVertLabel, mouseY - (int)(heightTimeLine * 0.5) - 1, widthVertLabel - 1, heightTimeLine + 2);
				
				g.setColor(Color.BLACK);
				String valueLabel = "" + (minValue + (maxValue - minValue) * (1 - percY));
				String[] valueSplit = valueLabel.split("\\.");
				valueLabel = valueSplit[0] + "." + valueSplit[1].substring(0, (valueSplit[1].length() < 5 ? valueSplit[1].length() : 5));
				g.drawString(valueLabel, width - widthVertLabel + 12, mouseY + (int)(heightTimeLine * 0.5) - 2);
			}
			
			{ // top right box
				g.setColor(Color.WHITE);
				g.fillRect(width - widthVertLabel, 0, widthVertLabel, heightTimeLine);
				g.setColor(Color.LIGHT_GRAY);
				g.drawRect(width - widthVertLabel - 1, 0, widthVertLabel, heightTimeLine);
			}
		}

		//resumeLoop();
	}
	
	/**
	 * It starts a candle render loop
	 */
	public void startRenderLoop() {
		Thread renderLoopThread = new Thread() {
			public void run() {
				long lastRender = System.currentTimeMillis();
				while (true) {
					long currTime = System.currentTimeMillis();
					
					repaint();
					
					//waitForPaint();
					
					long timeFromLastRender = currTime - lastRender;
					lastRender = currTime;
					
					long timeToSleep = stepRateInTime - timeFromLastRender;
					
					if (timeToSleep > 0) {
						try {
							Thread.sleep(timeToSleep);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		
		renderLoopThread.start();
	}

	/**
	 * It updates the horizontal offset in percentage between 0 and 1
	 * 0->
	 * 1->
	 * @param percentage value between 0 and 1
	 */
	public void setHorizontalOffset(double horizontalOffset) {
		this.horizontalOffset = horizontalOffset;
	}
	
	/**
	 * Gets the horizontal offset
	 * @return the horizontal offset
	 */
	public double getHorizontalOffset() {
		return horizontalOffset;
	}
	
	/**
	 * Gets the horizontal zoom the data is submitted to
	 * @return the horizontal zoom the data is submitted to
	 */
	public double getHorizontalZoom() {
		return horizontalZoom;
	}

	/**
	 * It updates the horizontal zoom value in percentage between 0 and 1
	 * 0->
	 * 1->
	 * @param percentage value between 0 and 1
	 */
	public void setHorizontalZoom(double horizontalZoom) {
		this.horizontalZoom = horizontalZoom;
	}

	/**
	 * It updates the vertical offset in percentage between 0 and 1
	 * 0-> 
	 * 1-> 
	 * @param percentage value between 0 and 1
	 */
	public void updateVerticalOffsetValue(double percentage) {
		setYOffsetPerc(percentage);
	}

	/**
	 * It updates the vertical zoom in percentage between 0 and 1
	 * 0-> 
	 * 1->
	 * @param percentage value between 0 and 1
	 */
	public void updateVerticalZoomValue(double percentage) {
		setGraphVerticalPadding(percentage);
	}
}