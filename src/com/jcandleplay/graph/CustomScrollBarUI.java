package com.jcandleplay.graph;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.plaf.metal.MetalScrollBarUI;

public class CustomScrollBarUI extends MetalScrollBarUI {

	static class ButtonPlux extends JButton {
		private static final long serialVersionUID = 1L;
		private Image imageBtn;

		public ButtonPlux(int width, int height) {
			try {
				imageBtn = ImageIO.read(new File("plusbtn.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			setPreferredSize(new Dimension(width, height));
		}

		@Override
		protected void paintComponent(Graphics g) {
			g.drawImage(imageBtn, 0, 0, null);
		}
	}

	static class ButtonMinus extends JButton {
		private static final long serialVersionUID = 1L;
		private Image imageBtn;

		public ButtonMinus(int width, int height) {
			try {
				imageBtn = ImageIO.read(new File("minusbtn.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			setPreferredSize(new Dimension(width, height));
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			g.drawImage(imageBtn, 0, 0, null);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////

	private int width;
	
	private int height;
	
	private boolean inverted;
	
	public CustomScrollBarUI(int width, int height) {
		this(width, height, false);
	}
	
	public CustomScrollBarUI(int width, int height, boolean inverted) {
		this.width = width;
		this.height = height;
		this.inverted = inverted;
	}

	@Override
	protected JButton createIncreaseButton(int orientation) {
		if (inverted) {
			return new ButtonMinus(width, height);			
		} else {
			return new ButtonPlux(width, height);
		}
	}

	@Override
	protected JButton createDecreaseButton(int orientation) {
		if (inverted) {
			return new ButtonPlux(width, height);
		} else {
			return new ButtonMinus(width, height);			
		}
	}
}