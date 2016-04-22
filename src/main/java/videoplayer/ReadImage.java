package videoplayer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ReadImage implements Runnable {

	private long frameCount;		//for debug purpose
	private PlaySound playSound;
	private String videoFileName;
	private String audioFileName;
	private final int width = 480;
	private final int height = 270;
	private final double fps = 15; // Frames Per Second
	private InputStream is;
	private BufferedImage img;
	private byte[] bytes = new byte[(int)width*height*3];
	//JFrame frame;
	//JLabel lbIm1;
	//ImageIcon imgIcon = new ImageIcon();
	ImageReaderComponent component = new ImageReaderComponent();
	JFrame frame = new JFrame();

	ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

	public void run(){
		play();
		System.out.println("frameCount: " + frameCount);
	}

	/**
	 * Constructor for imageReader
	 * @param fileName The raw RGB input file name
	 * @param pSound The PlaySound object, used to get the sample rate and current audio position
	 */
	public ReadImage(String videoFileName, PlaySound pSound){
		this.videoFileName = videoFileName;
		this.audioFileName = pSound.audioFileName;
		this.playSound = pSound;
	}



	private void initializeFrame(String videoFileName, String audioFileName) {

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("CSCI 576 Team Player");
		frame.setBounds(100, 100, 500, 420);		

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);

		JLabel lblInputVideo = new JLabel("Input Video: " + videoFileName);
		GridBagConstraints gbc_lblInputVideo = new GridBagConstraints();
		gbc_lblInputVideo.anchor = GridBagConstraints.WEST;
		gbc_lblInputVideo.gridwidth = 12;
		gbc_lblInputVideo.insets = new Insets(5, 5, 5, 5);
		gbc_lblInputVideo.gridx = 0;
		gbc_lblInputVideo.gridy = 0;
		frame.getContentPane().add(lblInputVideo, gbc_lblInputVideo);

		JLabel lblInputAudio = new JLabel("Input Audio: " + audioFileName);
		GridBagConstraints gbc_lblInputAudio = new GridBagConstraints();
		gbc_lblInputAudio.anchor = GridBagConstraints.WEST;
		gbc_lblInputAudio.gridwidth = 12;
		gbc_lblInputAudio.insets = new Insets(5, 5, 5, 5);
		gbc_lblInputAudio.gridx = 0;
		gbc_lblInputAudio.gridy = 1;
		frame.getContentPane().add(lblInputAudio, gbc_lblInputAudio);

		//Play, Pause, Stop Buttons
		JButton btnPlay = new JButton("Play");
		btnPlay.setPreferredSize(new Dimension(75, 25));
		GridBagConstraints gbc_btnPlay = new GridBagConstraints();
		gbc_btnPlay.insets = new Insets(10, 50, 10, 25);
		gbc_btnPlay.gridx = 3;
		gbc_btnPlay.gridy = 2;
		//gbc_btnPlay.weightx = 0.5;
		frame.getContentPane().add(btnPlay, gbc_btnPlay);

		JButton btnPause = new JButton("Pause");
		btnPause.setPreferredSize(new Dimension(75, 25));
		GridBagConstraints gbc_btnPause = new GridBagConstraints();
		gbc_btnPause.insets = new Insets(10, 25, 10, 25);
		gbc_btnPause.gridx = 5;
		gbc_btnPause.gridy = 2;
		//gbc_btnPause.weightx = 0.5;
		frame.getContentPane().add(btnPause, gbc_btnPause);

		JButton btnStop = new JButton("Stop");
		btnStop.setPreferredSize(new Dimension(75, 25));
		GridBagConstraints gbc_btnStop = new GridBagConstraints();
		gbc_btnStop.insets = new Insets(10, 25, 10, 25);
		gbc_btnStop.gridx = 7;
		gbc_btnStop.gridy = 2;
		//gbc_btnStop.weightx = 0.5;
		frame.getContentPane().add(btnStop, gbc_btnStop);

		//Video
		GridBagConstraints gbc_videoPane = new GridBagConstraints();
		gbc_videoPane.gridheight = 3;
		gbc_videoPane.gridwidth = 12;
		gbc_videoPane.insets = new Insets(5, 5, 5, 5);
		gbc_videoPane.fill = GridBagConstraints.BOTH;
		gbc_videoPane.gridx = 0;
		gbc_videoPane.gridy = 3;
		frame.getContentPane().add(component, gbc_videoPane);

		//frame.add(component);
		frame.setVisible(true);

		//ActionListeners
		btnPlay.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				System.out.println("You clicked play");
			}
		}); 
		btnPause.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				System.out.println("You clicked pause");
				Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
				Thread soundT = null;
				Thread imageT = null;
				for(Thread t : threadSet){
					System.out.println(t.getId() + " , " + t.getName());
					if(t.getName().equals("imageT")){
						imageT = t;
					}
					if(t.getName().equals("soundT")){
						soundT = t;
					}
				}
//				soundT.suspend();
//				imageT.suspend();
			}
		});
		btnStop.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				System.out.println("You clicked stop");
				Thread.currentThread();
				
			}
		});
	}

	/**
	 * Plays the video file to a JFrame.
	 */
	private void play(){
		frameCount=0;		//Used to output frame number for debugging		
		initializeFrame(videoFileName, audioFileName);

		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		try {
			File file = new File(videoFileName);
			is = new FileInputStream(file);

			long len = width*height*3;
			long numFrames = file.length()/len;

			// audio Samples Per video Frame
			double spf = playSound.getSampleRate()/fps;
			// Video Frame offsets to sync audio and video
			// Audio ahead of video, roll video forward to catch up
			int j = 0;		

			while(j < Math.round(playSound.getPosition()/spf)) {
				readBytes();
				component.setImg(img);
				frame.repaint();
				j++;
			}

			// Video ahead of audio, wait for audio to catch up
			while(j > Math.round(playSound.getPosition()/spf)) {
				// Do Nothing
			}

			for(int i = j; i < numFrames; i++) {
				// Video ahead of audio, wait for audio to catch up
				while(i > Math.round(playSound.getPosition()/spf)) {
					// Do Nothing
				}

				while(i < Math.round(playSound.getPosition()/spf)) {
					readBytes();
					component.setImg(img);
					frame.repaint();
					i++;
				}

				readBytes();
				component.setImg(img);
				frame.repaint();			
				//System.out.println("frameCount: " + frameCount);
				System.gc();
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