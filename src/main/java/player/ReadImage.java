package player;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ReadImage {

	//private long frameCount;		//for debug purpose
	private final int width = 480;
	private final int height = 270;
	private InputStream is = null;
	private BufferedImage img = null;
	private byte[] bytes; 
	File file = null;
		
	public void load(String imageFileName, PlaySound pSound){
		try {
			img = null;
			img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			bytes = new byte[(int)width*height*3];
			
			is = null;
			file = new File(imageFileName);
			is = new FileInputStream(file);			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads in the bytes of raw RGB data for a frame.
	 */
	public  BufferedImage readBytes() {
		//frameCount++;
		try {
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
					img.setRGB(x,y,pix);
					ind++;
				} 
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("frameCount: " + frameCount);
		return img;
	}
	
	public long getImageFileLength(){
		return file.length();
	}
	
	public void resetInputStream(String fileName){
		try {
			is = new FileInputStream(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}
}