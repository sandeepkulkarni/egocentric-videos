package imagesearch;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import summarization.AudioAnalyze;
import videoplayer.PlayWaveException;

public class ImageSearch
{	
	// Image Constants
	private static final Integer ImageWidth = 270;
	private static final Integer ImageHeight = 480;
	
	// Member variables
	private static BufferedImage searchImage;
	private static InputStream queryVideoFile;
	private static MediaSearchEngine searchEngine;
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
		searchImageFilePath = args[2];
		audioFilePath = args[1];
		
		
		File file = new File(queryVideoFilePath);
		try {
			queryVideoFile = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		searchImage = new BufferedImage(ImageWidth, ImageHeight, BufferedImage.TYPE_INT_RGB);
		
		//read the query image
		try {
			file = new File(searchImageFilePath);
			InputStream is = new FileInputStream(file);

			long len = file.length();
			byte[] bytes = new byte[(int) len];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			int ind = 0;
			for (int h = 0; h < ImageHeight; h++) {

				for (int x = 0; x < ImageWidth; x++) {

					int a = 0;
					int r = bytes[ind];
					int g = bytes[ind + ImageHeight * ImageWidth];
					int b = bytes[ind + ImageHeight * ImageWidth * 2];
					
					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					searchImage.setRGB(x, h, pix);
					
					ind++;
				}
			}
		}
			catch(Exception e){
				
			}

		
				
		// Initialize image search engine		
		searchEngine = new MediaSearchEngine(ImageWidth, ImageHeight, searchImage,queryVideoFile);
		
		
		// Get HSV histogram for Query Image
		searchEngine.createHSV(searchImage);
		
		
		// Search for image and output result
		int result = searchEngine.find();
		if(result==-1){
			System.out.println("No image found");
			System.exit(0);
		}
		
		//TODO Create the video based on the frame number
		ArrayList<Integer> shots=new ArrayList<Integer>();
		if(result!=0)
			shots.add(result-75);
		else
			shots.add(0);
		shots.add(result+75);
		
		AudioAnalyze aa = new AudioAnalyze(queryVideoFilePath,audioFilePath,1);
		aa.writeVideo(shots);
		try {
			aa.writeAudio(shots);
		} catch (PlayWaveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
	
}