package videoplayer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class PlaySound implements Runnable {

	private SourceDataLine dataLine;
	private AudioFormat audioFormat;
	//private InputStream waveStream;
	private final int EXTERNAL_BUFFER_SIZE = 524288;	// 128Kb
	AudioInputStream audioInputStream = null;
	String audioFileName;

	public PlaySound(String audioFileName) {		
		try {
			this.audioFileName = audioFileName;
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

	public void run(){
		try {
			this.play();
		} 
		catch (PlayWaveException e) {
			e.printStackTrace();
			return;
		}
	}

	public void play() throws PlayWaveException {			
		// Starts the music :P
		dataLine.start();

		int readBytes = 0;
		byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];

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

	public long getPosition() {
		return dataLine.getLongFramePosition();
	}

	public float getSampleRate() {
		return audioFormat.getFrameRate();
	}

}
