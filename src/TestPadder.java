import image.Image;
import image.ImagePadder;

public class TestPadder {
    public static void main(String[] args) {
        try {
            // Load the original image
            Image img = new Image("C:\\Users\\adipa\\IdeaProjects\\Ex3\\cat.jpeg");

            System.out.println("Original size: " + img.getWidth() + "x" + img.getHeight());

            // Apply padding
            ImagePadder padder = new ImagePadder();
            Image padded = padder.pad(img);

            System.out.println("Padded size:   " + padded.getWidth() + "x" + padded.getHeight());

            // Save the padded image
            padded.saveImage("padded_output");

            System.out.println("Saved padded_output.jpeg");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
