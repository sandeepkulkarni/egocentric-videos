package imagesearch;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

import summarization.AudioAnalyze;
import player.PlayWaveException;

public class ImageSearch {
	// Image Constants
	private static final Integer ImageWidth = 1280;
	private static final Integer ImageHeight = 720;

	// Member variables
	private static BufferedImage searchImage;
	private static InputStream queryVideoFile;
	private static boolean[][] audioFile;

	// Input arguments
	private static String queryVideoFilePath;
	private static String searchImageFilePath;
	private static String audioFilePath;

	/* Public methods */
	public static void main(String[] args) {
		// Check for expected number of input arguments
		if (args.length != 3) {
			System.out.println("Error: Invalid number of input arguments.");
			return;
		}

		// Parse input
		queryVideoFilePath = args[0];
		searchImageFilePath = args[1];
		audioFilePath = args[2];
		

		searchImageFilePath = "C:\\Python27\\vacation-image-search-engine\\dataset ";

		String datasetPath = searchImageFilePath;
		String indexFilePath = "C:\\Python27\\vacation-image-search-engine\\index.csv ";
		String queryFilePath = "C:\\Python27\\vacation-image-search-engine\\queries\\16700.png ";
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
						shots.add(result + 75);

						AudioAnalyze aa = new AudioAnalyze(queryVideoFilePath, audioFilePath, 1);
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