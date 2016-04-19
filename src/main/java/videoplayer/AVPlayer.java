package videoplayer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import videoplayer.PlaySound;
import videoplayer.ReadImage;

public class AVPlayer {

	public static void main(String[] args) {
		try {
			// get the command line parameters
			if (args.length < 2) {
				System.err.println("usage: java videoPlayback video.rgb audio.wav");
				return;
			}
			String videoFileName = args[0];
			String audioFileName = args[1];	 

			// opens the inputStream
			FileInputStream inputStream = new FileInputStream(audioFileName);

			// initializes the playSound and ReadImage Objects
			PlaySound playSound = new PlaySound(inputStream);
			ReadImage imageReader = new ReadImage(videoFileName, audioFileName, playSound);

			Thread t1 = new Thread(playSound);
			Thread t2 = new Thread(imageReader);
			t1.start();
			t2.start();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}