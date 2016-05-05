package imagesearch;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import dumptopng.DumpRgbToPng;
import player.PlayWaveException;
import summarization.AudioAnalyze;

public class ImageSearch {
	// Image Constants
	//private static final Integer ImageWidth = 1280;
	//private static final Integer ImageHeight = 720;

	// Member variables
	//private static BufferedImage searchImage;
	//private static InputStream queryVideoFile;
	//private static boolean[][] audioFile;

	// Input arguments
	private static String inputVideoPath;
	private static String searchImagePath;
	private static String audioFilePath;
	private static int numberOfFrames=4500;
	private static String pngDataSetPath = "C:\\Python27\\vacation-image-search-engine\\dataset";
	private static String pngQueryPath = "C:\\Python27\\vacation-image-search-engine\\queries";
	
	/* Public methods */
	public static void main(String[] args) {
		// Check for expected number of input arguments
		if (args.length != 3) {
			System.out.println("Error: Invalid number of input arguments.");
			return;
		}
		inputVideoPath = args[0];
		searchImagePath = args[1];
		audioFilePath = args[2];
				
		System.out.println("Process input video and write pngs...");
		DumpRgbToPng rgbToPng = new DumpRgbToPng(inputVideoPath, pngDataSetPath, searchImagePath, pngQueryPath);
		rgbToPng.writeRgbVideoToPng();

		System.out.println("Write query rgb image to png...");
		rgbToPng.writeRgbToPng();
		
		System.out.println("Indexing and Search Query Image...");
		String datasetPath = pngDataSetPath;
		String indexFilePath = "C:\\Python27\\vacation-image-search-engine\\index.csv ";
		String queryFilePath = pngQueryPath + "\\" + "queryImg.png";
		String PythonIndexFilePath = "C:\\Python27\\vacation-image-search-engine\\index.py";
		String PythonSearchFilePath = "C:\\Python27\\vacation-image-search-engine\\search.py";
		String result1 = null;
		String result2 = null;
		String[] FileName;

		try {
			Runtime rt = Runtime.getRuntime();
			String command = "python " + PythonIndexFilePath + " --dataset " + datasetPath + " --index "
					+ indexFilePath;
			Process proc = rt.exec(command);
			proc.waitFor();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			// read the output from the command
			System.out.println("Here is the standard output of the command:\n");
			String s = null;
			while ((s = stdInput.readLine()) != null) {
				result1 = s;
				System.out.println(s);
			}

			// read any errors from the attempted command
			System.out.println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (result1 != null) {
			try {
				Runtime rt = Runtime.getRuntime();
				String command = "python " + PythonSearchFilePath + " --index " + indexFilePath + " --query "
						+ queryFilePath + " --result-path " + datasetPath;
				Process proc = rt.exec(command);
				proc.waitFor();
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

				BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

				// read the output from the command
				System.out.println("Here is the standard output of the command:\n");
				String s = null;
				while ((s = stdInput.readLine()) != null) {
					if (s != "-1") {
						result2 = s;
						FileName = result2.split("\\\\");
						int length = FileName.length;
						result2 = FileName[length - 1];
						FileName = result2.split("\\.");
						result2 = FileName[0];
						System.out.println(result2);

						int result = Integer.parseInt(result2);
						if (result == -1) {
							System.out.println("No image found");
							System.exit(0);
						}

						// TODO Create the video based on the frame number
						ArrayList<Integer> shots = new ArrayList<Integer>();
						if (result - 75 > 0)
							shots.add(result - 75);
						else
							shots.add(0);
						
						if (result +75 >= numberOfFrames)
							shots.add(4499);
						else
							shots.add(result+75);

						AudioAnalyze aa = new AudioAnalyze(inputVideoPath, audioFilePath, 1);
						aa.writeVideo(shots);
						try {
							aa.writeAudio(shots);
						} catch (PlayWaveException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}

				// read any errors from the attempted command
				System.out.println("Here is the standard error of the command (if any):\n");
				while ((s = stdError.readLine()) != null) {
					System.out.println(s);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return;
	}

}