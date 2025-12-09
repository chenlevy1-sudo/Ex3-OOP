import image.Image;
import image.ImagePadder;
import image.ImageSplitter;

public class TestSplitter {
    public static void main(String[] args) throws Exception {
        // 1. Load original image
        Image original = new Image("C:\\Users\\adipa\\IdeaProjects\\Ex3\\dogs.jpeg");

        // 2. Pad image to powers of two
        ImagePadder padder = new ImagePadder();
        Image padded = padder.pad(original);

        // 3. Choose resolution (number of sub-images / characters per row)
        int resolution = 64; // adjust as you like

        // 4. Split padded image
        ImageSplitter splitter = new ImageSplitter();
        Image[][] blocks = splitter.split(resolution, padded);

        // 5. Compute block size in pixels
        int blockSize = padded.getWidth() / resolution;

        // 6. Print sanity info
        System.out.println("Padded size:      " + padded.getWidth() + "x" + padded.getHeight());
        System.out.println("Resolution:       " + resolution + " sub-images per row");
        System.out.println("Block size (px):  " + blockSize + "x" + blockSize);
        System.out.println("Blocks rows:      " + blocks.length);
        System.out.println("Blocks cols:      " + blocks[0].length);
        System.out.println("First block size: "
                + blocks[0][0].getWidth() + "x" + blocks[0][0].getHeight());

        // 7. Optional: save one block to visually inspect it
        blocks[20][20].saveImage("block_20_20");
        System.out.println("Saved block_20_20.jpeg");
    }
}
