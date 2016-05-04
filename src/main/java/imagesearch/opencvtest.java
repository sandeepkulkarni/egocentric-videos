package imagesearch;

import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class opencvtest {
	
	public static void main(String args[]){
	  //System.load(new File("/Users/rrgirish/Downloads/opencv-3.1.0/build/liblibopencv_java310.so").getAbsolutePath());
	  System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	  System.out.println(System.getProperty("java.library.path"));
      Mat mat = Mat.eye( 3, 3, CvType.CV_8UC1 );
      System.out.println( "mat = " + mat.dump() );
	}
}
	