package image;

import java.awt.*;

//אחראית על המרה מתת תמונה צבעונית לשחור לבן
public class ImageBrightnessCalculator {
	private final Image image;
	private static final double MAX_RGB=255;
	private static final double CALCULATE_RED =0.2126;
	private static final double CALCULATE_GREEN =0.7152;
	private static final double CALCULATE_BLUE =0.0722;

	public ImageBrightnessCalculator(Image image) {
		this.image= image;
	}

	//עובר על התמונה ומחזיר מערך מקביל עם ערך לבהירות בגוון אפור של כל פיקסל
	public double[][] getBrightGreyPixels (){
		int height= image.getHeight();
		int width= image.getWidth();
		double[][] pixels = new double[height][width];
		for(int row=0;row<height;row++){
			for(int col=0;col<width;col++) {
				pixels[row][col] = getGreyPixel(image.getPixel(row,col));
			}
		}
		return pixels;
	}

	// מחשב את הערך של הבהירות הפיקסל בהמרה לצבע אפור מבוסס על הפונקציה הנתונה
	private double getGreyPixel (Color pixel){
		double greyPixel= pixel.getRed()*CALCULATE_RED+
				pixel.getGreen()*CALCULATE_GREEN+pixel.getBlue()*CALCULATE_BLUE;
		return greyPixel/MAX_RGB;
	}


}
