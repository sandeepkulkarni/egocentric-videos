package player;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang3.StringUtils;

import player.PlayingTimer;


public class AVPlayer extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	
	PlaySound audioPlayer = new PlaySound();
	ReadImage imageReader = new ReadImage();
	
	private Thread imageThread;
	private Thread audioThread;
	
	private PlayingTimer timer;
	
	private boolean isPlaying = false;
	private boolean isPause = false;
	
	private String imageFileName;
	private String imageLastOpenPath;
	
	private String audioFileName;
	private String audioLastOpenPath;
	
	private JLabel labelFileNameImage = new JLabel("Image File:");
	private JLabel labelFileNameAudio = new JLabel("Audio File:");
	private JLabel labelTimeCounter = new JLabel("00:00:00");
	private JLabel labelDuration = new JLabel("00:00:00");
	
	private JButton buttonOpenImage = new JButton("Open Image");
	private JButton buttonOpenAudio = new JButton("Open Audio");
	private JButton buttonPlay = new JButton("Play");
	private JButton buttonPause = new JButton("Pause");
	
	private JSlider sliderTime = new JSlider();
	
	// Icons used for buttons
	private ImageIcon iconOpen = new ImageIcon(getClass().getResource("/images/Open.png"));
	private ImageIcon iconPlay = new ImageIcon(getClass().getResource("/images/Play.gif"));
	private ImageIcon iconStop = new ImageIcon(getClass().getResource("/images/Stop.gif"));
	private ImageIcon iconPause = new ImageIcon(getClass().getResource("/images/Pause.png"));
	
	ImageReaderComponent component = new ImageReaderComponent();

	private final int width = 480;
	private final int height = 270;
	private final double fps = 15; // Frames Per Second
	private BufferedImage img;
	
	public AVPlayer() {		
		super("SK Player");
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.anchor = GridBagConstraints.WEST;
		
		buttonOpenImage.setFont(new Font("Sans", Font.BOLD, 14));
		buttonOpenImage.setIcon(iconOpen);
		buttonOpenAudio.setFont(new Font("Sans", Font.BOLD, 14));
		buttonOpenAudio.setIcon(iconOpen);
		
		buttonPlay.setFont(new Font("Sans", Font.BOLD, 14));
		buttonPlay.setIcon(iconPlay);
		buttonPlay.setEnabled(false);
		
		buttonPause.setFont(new Font("Sans", Font.BOLD, 14));
		buttonPause.setIcon(iconPause);
		buttonPause.setEnabled(false);
		
		labelTimeCounter.setFont(new Font("Sans", Font.BOLD, 12));
		labelDuration.setFont(new Font("Sans", Font.BOLD, 12));
		
		sliderTime.setPreferredSize(new Dimension(500, 20));
		sliderTime.setEnabled(false);
		sliderTime.setValue(0);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 3;
		add(labelFileNameAudio, constraints);
		
		constraints.gridy = 1;
		add(labelFileNameImage, constraints);
		
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		add(labelTimeCounter, constraints);
		
		constraints.gridx = 1;
		add(sliderTime, constraints);
		
		constraints.gridx = 2;
		add(labelDuration, constraints);
		
		JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
		panelButtons.add(buttonOpenImage);
		panelButtons.add(buttonOpenAudio);
		panelButtons.add(buttonPlay);
		panelButtons.add(buttonPause);
		
		constraints.gridwidth = 3;
		constraints.gridx = 0;
		constraints.gridy = 3;
		add(panelButtons, constraints);
		
		
		component.setPreferredSize(new Dimension(500, 420));
		constraints.gridheight = 3;
		constraints.gridwidth = 12;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 12;		
		add(component, constraints);
		
		buttonOpenImage.addActionListener(this);
		buttonOpenAudio.addActionListener(this);
		buttonPlay.addActionListener(this);
		buttonPause.addActionListener(this);
		
		pack();
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

	}
	
	/**
	 * Handle click events on the buttons.
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source instanceof JButton) {
			JButton button = (JButton) source;
			if (button == buttonOpenImage) {
				openFileImage();
			}else if (button == buttonOpenAudio) {
				openFileAudio();
			} else if (button == buttonPlay) {
				if (!isPlaying) {
					playBack();
				} else {
					stopPlaying();
				}
			} else if (button == buttonPause) {
				if (!isPause) {
					pausePlaying();
				} else {
					resumePlaying();
				}
			}
		}
	}
	
	private void openFileImage() {
		JFileChooser fileChooser = null;
		
		if (imageLastOpenPath != null && !imageLastOpenPath.equals("")) {
			fileChooser = new JFileChooser(imageLastOpenPath);
		} else {
			fileChooser = new JFileChooser();
		}
		
		FileFilter rgbFilter = new FileFilter() {
			@Override
			public String getDescription() {
				return "Image file (*.RGB)";
			}

			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				} else {
					return file.getName().toLowerCase().endsWith(".rgb");
				}
			}
		};

		
		fileChooser.setFileFilter(rgbFilter);
		fileChooser.setDialogTitle("Open Image File");
		fileChooser.setAcceptAllFileFilterUsed(false);

		int userChoice = fileChooser.showOpenDialog(this);
		if (userChoice == JFileChooser.APPROVE_OPTION) {
			imageFileName = fileChooser.getSelectedFile().getAbsolutePath();
			imageLastOpenPath = fileChooser.getSelectedFile().getParent();
			
			if (isPlaying || isPause) {
				stopPlaying();
				
			}
			
		}
	}
	
	private void openFileAudio() {
		JFileChooser fileChooser = null;
		
		if (StringUtils.isNotBlank(imageLastOpenPath)){
			fileChooser = new JFileChooser(imageLastOpenPath);
		}else if(StringUtils.isNotBlank(audioLastOpenPath)) {
			fileChooser = new JFileChooser(audioLastOpenPath);
		} else {
			fileChooser = new JFileChooser();
		}
		
		FileFilter wavFilter = new FileFilter() {
			@Override
			public String getDescription() {
				return "Sound file (*.WAV)";
			}

			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				} else {
					return file.getName().toLowerCase().endsWith(".wav");
				}
			}
		};

		
		fileChooser.setFileFilter(wavFilter);
		fileChooser.setDialogTitle("Open Audio File");
		fileChooser.setAcceptAllFileFilterUsed(false);

		int userChoice = fileChooser.showOpenDialog(this);
		if (userChoice == JFileChooser.APPROVE_OPTION) {
			audioFileName = fileChooser.getSelectedFile().getAbsolutePath();
			audioLastOpenPath = fileChooser.getSelectedFile().getParent();
			if (isPlaying || isPause) {
				stopPlaying();
				
				while(audioPlayer.getAudioClip().isRunning()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
			
			//playBack();
			if(StringUtils.isNotBlank(imageLastOpenPath) && StringUtils.isNotBlank(audioLastOpenPath)){
				buttonPlay.setEnabled(true);
			}
			
		}
	}
	
	
	private void stopPlaying() {
		isPause = false;
		buttonPause.setText("Pause");
		buttonPause.setEnabled(false);
		timer.reset();
		timer.interrupt();	
		
		audioPlayer.stop();
		
		imageReader.resetInputStream(imageFileName);
		
		imageThread.interrupt();
		audioThread.interrupt();
		
		System.out.println("--------Stop completed-------");
		
	}
	
	private void pausePlaying() {
		buttonPause.setText("Resume");
		isPause = true;
		audioPlayer.pause();
		timer.pauseTimer();
		
		imageThread.interrupt();
		audioThread.interrupt();
	}
	
	private void resumePlaying() {
		buttonPause.setText("Pause");
		isPause = false;
		audioPlayer.resume();
		timer.resumeTimer();
		
		imageThread.interrupt();
		audioThread.interrupt();	
	}
	
	private void resetControls() {
		timer.reset();
		timer.interrupt();

		buttonPlay.setText("Play");
		buttonPlay.setIcon(iconPlay);
		
		buttonPause.setEnabled(false);
		
		isPlaying = false;		
	}

		

	/**
	 * Start playing sound and images in sync
	 */
	private void playBack() {
		timer = new PlayingTimer(labelTimeCounter, sliderTime);
		timer.start();
		isPlaying = true;
		
		System.out.println("--------in playback------");
		
		try {
			audioPlayer.load(audioFileName);
		} catch (UnsupportedAudioFileException e1) {
			JOptionPane.showMessageDialog(AVPlayer.this,  
					"The audio format is unsupported!", "Error", JOptionPane.ERROR_MESSAGE);
			resetControls();
			e1.printStackTrace();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(AVPlayer.this,  
					"Could not play the audio file because line is unavailable!", "Error", JOptionPane.ERROR_MESSAGE);
			resetControls();
			e1.printStackTrace();
		} catch (LineUnavailableException e1) {
			JOptionPane.showMessageDialog(AVPlayer.this,  
					"I/O error while playing the audio file!", "Error", JOptionPane.ERROR_MESSAGE);
			resetControls();
			e1.printStackTrace();
		}
		
		audioThread = new Thread(new Runnable() {
			public void run() {
				try {
					
					System.out.println("# audio run...");
					
					buttonPlay.setText("Stop");
					buttonPlay.setIcon(iconStop);
					buttonPlay.setEnabled(true);
					
					buttonPause.setText("Pause");
					buttonPause.setEnabled(true);
					
					timer.setAudioClip(audioPlayer.getAudioClip());
					labelFileNameAudio.setText("Audio File: " + audioFileName);
					sliderTime.setMaximum((int) audioPlayer.getClipSecondLength());
					
					labelDuration.setText(audioPlayer.getClipLengthString());
					
					audioPlayer.play();
					
					resetControls();
					
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(AVPlayer.this,  
							"Could not play the audio file because line is unavailable!", "Error", JOptionPane.ERROR_MESSAGE);
					resetControls();
					e1.printStackTrace();
				}
			}
		});
		
		imageReader.load(imageFileName, audioPlayer);		
		imageThread = new Thread(new Runnable() {
			public void run() {						
				
				labelFileNameImage.setText("Image File: " + imageFileName);
				
				System.out.println("$ image run...");
				long length = width*height*3;
				long numFrames = imageReader.getImageFileLength()/length;

				// audio Samples Per video Frame
				double spf = audioPlayer.getSampleRate()/fps;
				// Video Frame offsets to sync audio and video. 
				//Audio ahead of video, roll video forward to catch up
				int j = 0;		

				while(j < Math.round(audioPlayer.getPosition()/spf)) {
					img = imageReader.readBytes();
					component.setImg(img);
					repaint();
					j++;
				}

				// Video ahead of audio, wait for audio to catch up
				while(j > Math.round(audioPlayer.getPosition()/spf)) {
					// Do Nothing
					System.out.println("### Doing Nothing...");
				}

				for(int i = j; i < numFrames; i++) {
					// Video ahead of audio, wait for audio to catch up
					while(i > Math.round(audioPlayer.getPosition()/spf)) {
						// Do Nothing
						//System.out.println("@@@ Doing Nothing...");
					}

					while(i < Math.round(audioPlayer.getPosition()/spf)) {
						img = imageReader.readBytes();
						component.setImg(img);
						repaint();
						i++;
					}

					img = imageReader.readBytes();
					component.setImg(img);
					repaint();			
					
					System.gc();
				}
				
				
			}
		});

		System.out.println("--------start audio thread-----");
		audioThread.start();
		System.out.println("--------start image thread-----");
		imageThread.start();
		
	}
	
	
	public static void main(String[] args) {
		// get the command line parameters
		/*if (args.length < 2) {
			System.err.println("usage: java videoPlayback video.rgb audio.wav");
			return;
		}*/
		//imageFileName = "D:\\576project\\Alin_Day1_003\\Alin_Day1_003.rgb";
		//audioFileName = "D:\\576project\\Alin_Day1_003\\Alin_Day1_003.wav";	 
//		System.out.println("Video File: " + imageFileName);
//		System.out.println("Audio File: " + audioFileName);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new AVPlayer().setVisible(true);
			}
		});
	}

}