package com.jcandleplay.graph.data;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.plaf.metal.MetalScrollBarUI;

public class CustomScrollbarUIExample {

	public static void main(String[] args) {
		JScrollPane before = makeExamplePane();
		JScrollPane after = makeExamplePane();

		JScrollBar sb = after.getVerticalScrollBar();
		sb.setUI(new CustomScrollbarUI());

		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container c = f.getContentPane();
		c.setLayout(new GridLayout(2, 1, 0, 1));
		c.add(before);
		c.add(after);
		f.setSize(450, 400);
		f.setVisible(true);
	}

	private static JScrollPane makeExamplePane() {
		String exampleText = "Lorem ipsum dolor sit amet,\n consetetur sadipscing elitr,\n sed diam nonumy eirmod \ntempor invidunt ut labore et dolore \nmagna aliquyam erat,\n sed diam voluptua. At vero eos et accusam et \njusto duo dolores et ea rebum. Stet clita\n kasd gubergren, no sea\n takimata sanctus est Lorem ipsum dolor sit amet.\n Lorem ipsum dolor sit amet,\n consetetur sadipscing elitr, sed diam\n nonumy eirmod tempor invidunt \nut labore et dolore\n magna aliquyam erat, sed diam voluptua.\n At vero eos et accusam et justo \nduo\n dolores et ea rebum. Stet clita kasd gubergren, no sea\n takimata sanctus est Lorem\n ipsum dolor sit amet. Lorem ipsum dolor\n sit amet, consetetur sadipscing elitr,\n sed diam nonumy\n eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos\n et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est \nLorem ipsum dolor sit amet.Duis\n autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel\n illum dolore eu feugiat nulla facilisis at vero eros et \naccumsan et iusto odio \ndignissim qui blandit praesent luptatum zzril delenit augue\n duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer\n adipiscing elit, sed diam nonummy nibh euismod \ntincidunt ut laoreet\n dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam,\n quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea\n commodo consequat. Duis autem vel eum iriure \ndolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla \nfacilisis at vero eros et accumsan et iusto odio dignissim qui blandit\n praesent luptatum zzril delenit augue duis dolore \nte feugait nulla facilisi.";
		JTextArea text = new JTextArea(exampleText);
		JScrollPane scroll = new JScrollPane(text);
		return scroll;
	}

	static class ButtonPlux extends JButton {
		private static final long serialVersionUID = 1L;
		private Image imageBtn;

		public ButtonPlux() {
			try {
				imageBtn = ImageIO.read(new File("plusbtn.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			setPreferredSize(new Dimension(20, 18));
		}

		@Override
		protected void paintComponent(Graphics g) {
			g.drawImage(imageBtn, 0, 0, null);
		}
	}

	static class ButtonMinus extends JButton {
		private static final long serialVersionUID = 1L;
		private Image imageBtn;

		public ButtonMinus() {
			try {
				imageBtn = ImageIO.read(new File("minusbtn.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			setPreferredSize(new Dimension(20, 18));
		}

		@Override
		protected void paintComponent(Graphics g) {
			g.drawImage(imageBtn, 0, 0, null);
		}
	}

	static class CustomScrollbarUI extends MetalScrollBarUI {

		@Override
		protected JButton createIncreaseButton(int orientation) {
			return new ButtonPlux();
		}
		
		@Override
		protected JButton createDecreaseButton(int orientation) {
			return new ButtonMinus();
		}
	}
}