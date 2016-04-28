package imagesearch;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MediaSearchEngine {
	private int width, height;
	private ArrayList<BufferedImage> _searchImages;
	private ArrayList<Histogram> _searchImageHistograms;
	private double THRESHOLD=0.3;
	
	private Double[][][] _hsvQueryImage;
	private Histogram queryImageHistogram;
	private Histogram searchImageHistogram;
	private Integer[][] _backProjectedArray;
	private BufferedImage _backProjectedImage;
	private int _range = 2;
	private int numberOfFrames=4500;
	private byte[] bytes;
	private InputStream videoFile;

	
	public MediaSearchEngine(int width, int height, BufferedImage searchImage,InputStream queryVideoFile) {
		
		this.width = width;
		this.height = height;
		_searchImageHistograms = new ArrayList<Histogram>();
		_backProjectedArray = new Integer[width][height];
		_backProjectedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		_hsvQueryImage = new Double[width][height][3];
		queryImageHistogram = new Histogram();
		searchImageHistogram= new Histogram();
		this.videoFile=queryVideoFile;
		long frameByteSize = width*height*3;
	    bytes = new byte[(int)frameByteSize];

		
		
	}	
	// Create HSV histogram for Query Image
	public void createHSV(BufferedImage query) {
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				
					int pixel = query.getRGB(x, y);
					int r = (pixel >> 16) & 0x000000FF;
					int g = (pixel >> 8) & 0x000000FF;
					int b = pixel & 0x000000FF;
					
					if (r > 255) {
						r = 255;
					}
					else if (r < 0) {
						r = 0;
					}
					if (g > 255){
						g = 255;
					}
					else if (g < 0) {
						g = 0;
					}
					if (b > 255) {
						b = 255;
					}
					else if (b < 0) {
						b = 0;
					}
					
					float[] hsv = new float[3];
					Color.RGBtoHSB(r, g, b, hsv);
					Float h_value = new Float(hsv[0]); // hue
					Float s_value = new Float(hsv[1]); // sat
					Float v_value = new Float(hsv[2]); // val
					_hsvQueryImage[x][y][0] = new Double(h_value.doubleValue());
					_hsvQueryImage[x][y][1] = new Double(s_value.doubleValue());
					_hsvQueryImage[x][y][2] = new Double(v_value.doubleValue());
					
					queryImageHistogram.AddValue((hsv));					
				
			}
		}
		
		// Normalize the histogram
		queryImageHistogram.Normalize();	
		
	}
	
	// Create HSV histogram for Query Image
		public Histogram createHSVForEachFrame(BufferedImage query) {
			Histogram h=new Histogram();
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					
						int pixel = query.getRGB(x, y);
						int r = (pixel >> 16) & 0x000000FF;
						int g = (pixel >> 8) & 0x000000FF;
						int b = pixel & 0x000000FF;
						
						if (r > 255) {
							r = 255;
						}
						else if (r < 0) {
							r = 0;
						}
						if (g > 255){
							g = 255;
						}
						else if (g < 0) {
							g = 0;
						}
						if (b > 255) {
							b = 255;
						}
						else if (b < 0) {
							b = 0;
						}
						
						float[] hsv = new float[3];
						Color.RGBtoHSB(r, g, b, hsv);
						Float h_value = new Float(hsv[0]); // hue
						Float s_value = new Float(hsv[1]); // sat
						Float v_value = new Float(hsv[2]); // val
						_hsvQueryImage[x][y][0] = new Double(h_value.doubleValue());
						_hsvQueryImage[x][y][1] = new Double(s_value.doubleValue());
						_hsvQueryImage[x][y][2] = new Double(v_value.doubleValue());
						
						h.AddValue((hsv));					
					
				}
			}
			
			// Normalize the histogram
			h.Normalize();
			return h;
		}
		
	
	
	// Searches images for query image
	public int find() {	
		boolean result = false;
		int resultNum = -1;
		int frameNumber=-1;
		double resultDistance = Double.MAX_VALUE;
		
		for (int i = 0; i < numberOfFrames; i++) {
			BufferedImage image = readBytes();
			
			searchImageHistogram=createHSVForEachFrame(image);
			
			
			// Compare the search histogram for the frame histogram to see if there is a match
				double distance = queryImageHistogram.Compare(searchImageHistogram);
		//		System.out.println(i+ " "+ distance);
				if ((distance > 0) && (distance < resultDistance))
				{
					result = true;
					resultDistance = distance;
					frameNumber=i;
					System.out.println(i+" "+resultDistance);
				}
			//}
		    
		}
		if(resultDistance<THRESHOLD)
			return frameNumber;
		else
			return -1;
	}
	private BufferedImage readBytes() {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		// TODO Auto-generated method stub
		try {
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = videoFile.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			int ind = 0;
			for (int y = 0; y < height ; y++) {
				for (int x = 0; x < width; x++) {
					byte r = bytes[ind];
					byte g = bytes[ind + height * width];
					byte b = bytes[ind + height * width * 2];
					
					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					img.setRGB(x, y, pix);
				
					
					ind++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}
	
	
	


}
