package player;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class PlaySound implements LineListener {
	private static final int SECONDS_IN_HOUR = 60 * 60;
	private static final int SECONDS_IN_MINUTE = 60;
	
	/**
	 * this flag indicates whether the playback completes or not.
	 */
	private boolean playCompleted;

	/**
	 * this flag indicates whether the playback is stopped or not.
	 */
	private boolean isStopped;

	private boolean isPaused;

	private Clip audioClip;
	
	private AudioFormat format = null;

	/**
	 * Load audio file before playing back
	 * 
	 * @param audioFilePath
	 *            Path of the audio file.
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws LineUnavailableException
	 */
	public void load(String audioFilePath)
			throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		
		format = null;
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(audioFilePath));
		format = audioStream.getFormat();

		DataLine.Info info = new DataLine.Info(Clip.class, format);

		audioClip = null;
		audioClip = (Clip) AudioSystem.getLine(info);

		audioClip.addLineListener(this);

		audioClip.open(audioStream);
	}
	
	public long getClipSecondLength() {
		return audioClip.getMicrosecondLength() / 1000000;
	}
	
	public String getClipLengthString() {
		String length = "";
		long hour = 0;
		long minute = 0;
		long seconds = audioClip.getMicrosecondLength() / 1000000;
		
		System.out.println(seconds);
		
		if (seconds >= SECONDS_IN_HOUR) {
			hour = seconds / SECONDS_IN_HOUR;
			length = String.format("%02d:", hour);
		} else {
			length += "00:";
		}
		
		minute = seconds - hour * SECONDS_IN_HOUR;
		if (minute >= SECONDS_IN_MINUTE) {
			minute = minute / SECONDS_IN_MINUTE;
			length += String.format("%02d:", minute);
			
		} else {
			minute = 0;
			length += "00:";
		}
		
		long second = seconds - hour * SECONDS_IN_HOUR - minute * SECONDS_IN_MINUTE;
		
		length += String.format("%02d", second);
		
		return length;
	}

	/**
	 * Play a given audio file.
	 * 
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws LineUnavailableException
	 */
	void play() throws IOException {

		audioClip.start();

		playCompleted = false;
		isStopped = false;

		while (!playCompleted) {
			// wait for the playback completes
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				//ex.printStackTrace();
				if (isStopped) {
					audioClip.stop();
					break;
				}
				if (isPaused) {
					audioClip.stop();
				} else {
					System.out.println("!!!!");
					audioClip.start();
				}
			}
		}

		audioClip.close();

	}

	/**
	 * Stop playing back.
	 */
	public void stop() {
		isStopped = true;
	}

	public void pause() {
		isPaused = true;
	}

	public void resume() {
		isPaused = false;
	}

	/**
	 * Listens to the audio line events to know when the playback completes.
	 */
	public void update(LineEvent event) {
		LineEvent.Type type = event.getType();
		if (type == LineEvent.Type.STOP) {
			System.out.println("STOP EVENT");
			if (isStopped || !isPaused) {
				playCompleted = true;
			}
		}
	}
	
	public Clip getAudioClip() {
		return audioClip;
	}
	
	public long getPosition() {
		//if(audioClip != null){
			return audioClip.getLongFramePosition();
		//}else{
			//return -1;
		//}
	}

	public float getSampleRate() {
		return format.getFrameRate();
	}
	

	/*private SourceDataLine dataLine;
	private AudioFormat audioFormat;
	private final int EXTERNAL_BUFFER_SIZE = 524288;	// 128Kb
	private AudioInputStream audioInputStream = null;
	private boolean isStopped;
	private boolean isPaused;
	private static final int SECONDS_IN_HOUR = 60 * 60;
	private static final int SECONDS_IN_MINUTE = 60;	
	//this flag indicates whether the playback completes or not.
	private boolean playCompleted;

	public void load(String audioFileName){
		try {
			//this.audioFileName = audioFileName;
			// Obtain the information about the AudioInputStream
			InputStream bufferedIn = new BufferedInputStream(new FileInputStream(audioFileName));
			audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
			audioFormat = audioInputStream.getFormat();

			Info info = new Info(SourceDataLine.class, audioFormat);
			dataLine = (SourceDataLine) AudioSystem.getLine(info);
			dataLine.open(audioFormat, this.EXTERNAL_BUFFER_SIZE);
		} catch (FileNotFoundException e) {			
		} catch (UnsupportedAudioFileException e1) {			
		} catch (IOException e1) { 			
		} catch (LineUnavailableException e1) {			
		}
	}



	public void stop() {
		isStopped = true;
	}

	public void pause() {
		isPaused = true;
	}

	public void resume() {
		isPaused = false;
	}

	public void play() throws PlayWaveException {			
		// Starts the music :P
		dataLine.start();

		playCompleted = false;
		isStopped = false;

		int readBytes = 0;
		byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];

		while (!playCompleted) {
			try {
				while (readBytes != -1) {
					readBytes = audioInputStream.read(audioBuffer, 0, audioBuffer.length);
					if (readBytes >= 0) {
						dataLine.write(audioBuffer, 0, readBytes);
					}
				}
			} catch (IOException e1) {
				throw new PlayWaveException(e1);
			} 

			finally {	
				// plays what's left and and closes the audioChannel
				dataLine.drain();
				//dataLine.close();
			}
		}
	}

	public long getPosition() {
		return dataLine.getLongFramePosition();
	}

	public float getSampleRate() {
		return audioFormat.getFrameRate();
	}

	public SourceDataLine getAudioDataLine(){
		return dataLine;
	}*/

}
