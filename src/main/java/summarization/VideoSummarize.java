package summarization;


import java.util.ArrayList;

import videoplayer.PlayWaveException;

/**
 * The driver for the video summarization algorithms.
 * 
 */
public class VideoSummarize {

    /**
     * Main method for videoSummarize
     * @param args Command line arguments
     */
    public static void main(String[] args) {
	try {
	    if (args.length < 3) {
		System.err.println("usage: java videoSummarize videoInput.rgb audioInput.wav percentage");
		return;
	    }
	    String vFileName = args[0];
	    String aFileName = args[1];	
	    double percent = Double.parseDouble(args[2]);

	    AudioAnalyze aa = new AudioAnalyze(vFileName,aFileName,percent);
	    ArrayList<Integer> shots = new ArrayList<Integer>();
	    shots = aa.calcAudioWeights();
	    aa.writeVideo(shots);
	    aa.writeAudio(shots);

	    System.out.println("Summarization Complete!");
	}	

	catch (PlayWaveException e) {
	    e.printStackTrace();
	    return;
	}
    }
}