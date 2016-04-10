package videoplayer;


import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class AVPlayer {

	JFrame frame;
	JLabel lbIm1;
	JLabel lbIm2;
	BufferedImage img;

	public void initialize(String[] args){
		int width = 480;
		int height = 270;

		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		try {
			File file = new File(args[0]);
			InputStream is = new FileInputStream(file);

			//long len = file.length();
			long len = width*height*3;
			byte[] bytes = new byte[(int)len];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
				offset += numRead;
			}


			int ind = 0;
			for(int y = 0; y < height; y++){

				for(int x = 0; x < width; x++){

					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x,y,pix);
					ind++;
				}
			}


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Use labels to display the images
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		JLabel lbText1 = new JLabel("Video: " + args[0]);
		lbText1.setHorizontalAlignment(SwingConstants.LEFT);
		JLabel lbText2 = new JLabel("Audio: " + args[1]);
		lbText2.setHorizontalAlignment(SwingConstants.LEFT);
		lbIm1 = new JLabel(new ImageIcon(img));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		frame.getContentPane().add(lbText1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbText2, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		frame.getContentPane().add(lbIm1, c);

		frame.pack();
		frame.setVisible(true);
		
		
	}
	
	public void playWAV(String filename){
		// opens the inputStream
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		// initializes the playSound Object
		PlaySound playSound = new PlaySound(inputStream);

		// plays the sound
		try {
			playSound.play();
		} catch (PlayWaveException e) {
			e.printStackTrace();
			return;
		}
	}

	public static void main(String[] args) {
		if (args.length < 2) {
		    System.err.println("usage: java -jar AVPlayer.jar [RGB file] [WAV file]");
		    return;
		}
		AVPlayer ren = new AVPlayer();
		ren.initialize(args);
		ren.playWAV(args[1]);
	}

}