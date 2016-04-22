package videoplayer;

public class AVPlayer {

	public static void main(String[] args) {

		// get the command line parameters
		if (args.length < 2) {
			System.err.println("usage: java videoPlayback video.rgb audio.wav");
			return;
		}
		String videoFileName = args[0];
		String audioFileName = args[1];	 
		System.out.println("Video File: " + videoFileName);
		System.out.println("Audio File: " + audioFileName);

		// opens the inputStream
		//FileInputStream inputStream = new FileInputStream(audioFileName);

		// initializes the playSound and ReadImage Objects
		PlaySound playSound = new PlaySound(audioFileName);
		ReadImage imageReader = new ReadImage(videoFileName, playSound);

		Thread soundThread = new Thread(playSound);
		Thread imageThread = new Thread(imageReader);
		
		soundThread.setName("soundT");
		imageThread.setName("imageT");
		soundThread.start();
		imageThread.start();
	}

}