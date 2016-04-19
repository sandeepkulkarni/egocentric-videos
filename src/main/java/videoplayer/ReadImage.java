package videoplayer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class ReadImage implements Runnable {

	private long frameCount;
	private PlaySound playSound;
	private String videoFileName;
	private String audioFileName;
	private final int width = 480;
	private final int height = 270;
	private final double fps = 15; // Frames Per Second
	private InputStream is;
	private BufferedImage img;
	private byte[] bytes;
	JFrame frame;
	JLabel lbIm1;
	
	public void run(){
		play();
	}

	/**
	 * Constructor for imageReader
	 * @param fileName The raw RGB input file name
	 * @param pSound The PlaySound object, used to get the sample rate and current audio position
	 */
	public ReadImage(String videoFileName, String audioFileName, PlaySound pSound){
		this.videoFileName = videoFileName;
		this.audioFileName = audioFileName;
		this.playSound = pSound;
	}

	/**
	 * Plays the video file to a JFrame.
	 */
	private void play(){

		// Used to output frame number for debugging
		frameCount=0;

		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		try {
			File file = new File(videoFileName);
			is = new FileInputStream(file);

			long len = width*height*3;
			long numFrames = file.length()/len;

			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setTitle("CSCI 576 Team Player");
			frame.setSize(width + 100, height + 100);	
			
			GridBagLayout gLayout = new GridBagLayout();
			frame.getContentPane().setLayout(gLayout);

			JLabel lbText1 = new JLabel("Video: " + videoFileName);
			lbText1.setHorizontalAlignment(SwingConstants.LEFT);
			JLabel lbText2 = new JLabel("Audio: " + audioFileName);
			lbText2.setHorizontalAlignment(SwingConstants.LEFT);
			
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
			//frame.getContentPane().add(lbIm1, c);
			//frame.pack();
			//frame.setVisible(true);	

			bytes = new byte[(int)len];
			
			// audio Samples Per video Frame
			double spf = playSound.getSampleRate()/fps;
			// Video Frame offsets to sync audio and video
			int offset = 5;	
			// Audio ahead of video, roll video forward to catch up
			int j = 0;

			/*if(frameCount == 200){
			try {
				Thread.sleep(1000*60*2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}*/
			
			while(j < Math.round(playSound.getPosition()/spf)) {
				readBytes();
				//component.setImg(img);
				//frame.add(component);
				lbIm1 = new JLabel(new ImageIcon(img));
				frame.getContentPane().add(lbIm1, c);				
				frame.repaint();
				frame.pack();
				frame.setVisible(true);
				j++;
			}

			// Video ahead of audio, wait for audio to catch up
			while(j > Math.round(/*offset+*/playSound.getPosition()/spf)) {
				// Do Nothing
			}

			for(int i = j; i < numFrames; i++) {
				// Video ahead of audio, wait for audio to catch up
				while(i > Math.round(/*offset+*/playSound.getPosition()/spf)) {
					// Do Nothing
				}

				// Audio ahead of video, roll video forward to catch up
				while(i < Math.round(playSound.getPosition()/spf)) {
					readBytes();
					//component.setImg(img);
					//frame.add(component);
					lbIm1 = new JLabel(new ImageIcon(img));
					frame.getContentPane().add(lbIm1, c);
					frame.repaint();
					frame.pack();
					frame.setVisible(true);	
					i++;
				}
				
				readBytes();
				//component.setImg(img);
				//frame.add(component);
				lbIm1 = new JLabel(new ImageIcon(img));
				frame.getContentPane().add(lbIm1, c);
				frame.repaint();
				frame.pack();
				frame.setVisible(true);
				
				System.out.println("frameCount: "+frameCount);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads in the bytes of raw RGB data for a frame.
	 */
	private  void readBytes() {
		frameCount++;

		try {
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
				offset += numRead;
			}
			int ind = 0;
			for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = 0xff000000 | (r << 16) | (g << 8) | b;
					img.setRGB(x,y,pix);
					ind++;
				} 
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}