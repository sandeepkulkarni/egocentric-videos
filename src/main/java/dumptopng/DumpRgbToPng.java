package dumptopng;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

public class DumpRgbToPng {

	private String inputVideoPath;	// = "D:\\576project\\Alin_Day1_002\\Alin_Day1_002.rgb";
	private String pngDataSetPath;	// = "D:\\576project\\pngfiles1sec\\";
	private String queryImagePath;
	private String pngQueryPath;
	private final static int width = 480;
	private final static int height = 270;
	private static InputStream is;
	private static BufferedImage img;
	private static byte[] bytes  = new byte[(int)width*height*3];

	public DumpRgbToPng(String inputVideoPath, String pngDataSetPath, String queryImagePath, String pngQueryPath) {
		this.inputVideoPath = inputVideoPath;
		this.pngDataSetPath = pngDataSetPath;
		this.queryImagePath = queryImagePath;
		this.pngQueryPath = pngQueryPath;
	}

	public void writeRgbVideoToPng() {
		File file = new File(inputVideoPath);
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		try {
			is = new FileInputStream(file);
			System.out.println("Clean png dataset directory...");
			FileUtils.cleanDirectory(new File(pngDataSetPath)); 

			long len = width*height*3;
			long numFrames = file.length()/len;

			for(int i = 0; i < numFrames; i++) {

				int offset = 0;
				int numRead = 0;
				while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
					offset += numRead;
				}
				int ind = 0;
				for(int y = 0; y < height; y++){
					for(int x = 0; x < width; x++){
						byte r = bytes[ind];
						byte g = bytes[ind+height*width];
						byte b = bytes[ind+height*width*2]; 

						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
						//int pix = 0xff000000 | (r << 16) | (g << 8) | b;
						img.setRGB(x,y,pix);
						ind++;
					} 
				}

				if(i % 15 == 0){
					ImageIO.write(img, "png", new File(pngDataSetPath+"\\"+i+".png"));
					System.out.println("Writing Img :"+i+".png");
				}
			}
			System.out.println("----Write Complete---");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void writeRgbToPng(){
		File file = new File(queryImagePath);
		//override default dimensions as query image has different dimensions
		int width = 1280;
		int height = 960;//720;
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		try {
			is = new FileInputStream(file);
			System.out.println("Clean query png directory...");
			FileUtils.cleanDirectory(new File(pngQueryPath)); 

			bytes  = new byte[(int)width*height*3];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
				offset += numRead;
			}
			int ind = 0;
			for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = 0xff000000 | (r << 16) | (g << 8) | b;
					img.setRGB(x,y,pix);
					ind++;
				} 
			}

			ImageIO.write(img, "png", new File(pngQueryPath+"\\"+"queryImg.png"));
			System.out.println("----Writing Query Img : queryImg.png---");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*public static void main(String[] args){
		File file = new File("D:\\576project\\Yin_Snack\\4321.rgb");
		int width = 1280;
		int height = 720;
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		try {
			is = new FileInputStream(file);
			System.out.println("Clean query png directory...");
			FileUtils.cleanDirectory(new File("C:\\Python27\\vacation-image-search-engine\\queries")); 

			bytes  = new byte[(int)width*height*3];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
				offset += numRead;
			}
			int ind = 0;
			for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = 0xff000000 | (r << 16) | (g << 8) | b;
					img.setRGB(x,y,pix);
					ind++;
				} 
			}

			ImageIO.write(img, "png", new File("C:\\Python27\\vacation-image-search-engine\\queries"+"\\"+"queryImg.png"));
			System.out.println("----Writing Query Img : queryImg.png---");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}*/


}
