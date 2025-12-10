import ascii_art.AsciiArtAlgorithm;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

/**
 * Manual test for {@link AsciiArtAlgorithm} using a real image file and both
 * console and HTML outputs.
 * <p>
 * This class is only for local debugging and is not part of the submission.
 */
public class TestAsciiArtAlgorithm {

    /**
     * Entry point for manually testing the {@link AsciiArtAlgorithm}.
     *
     * @param args command-line arguments (not used)
     * @throws Exception if the input image file cannot be read
     */
    public static void main(String[] args) throws Exception {

        // 1) choose a real image file inside your project folder
        String imagePath = "C:\\Users\\adipa\\IdeaProjects\\Ex3\\cat.jpeg";

        // 2) load the image using the constructor provided by the assignment
        Image image = new Image(imagePath);            // loads the image file into an Image object

        // 3) build a charset containing A-Z
        char[] ABCcharset = new char[26];                 // array to hold characters 'A'..'Z'
        for (int i = 0; i < 26; i++) {                 // fill the charset array step by step
            ABCcharset[i] = (char) ('A' + i);             // each position gets the corresponding uppercase letter
        }
        // build a charset containing ALL
        char[] fullAsciiCharset = buildFullAsciiCharset();        // use *all* printable ASCII chars

        // build a charset containing 0123456789
        char[] numericCharset = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};


        // 4) choose a resolution (number of sub-images per row)
        int resolution = 512;                           // TODO: you can change this (must be a valid resolution)

        // 5) create the algorithm instance with the chosen configuration
        AsciiArtAlgorithm algorithm =
                new AsciiArtAlgorithm(image, fullAsciiCharset, resolution); // algorithm that converts the image to ASCII

        // 6) run the algorithm to get the ASCII-art matrix
        char[][] asciiImage = algorithm.run();         // compute the ASCII-art representation as char[][]

        // 7) create both output types
        AsciiOutput consoleOutput = new ConsoleAsciiOutput(); // outputs ASCII-art directly to the console
        AsciiOutput htmlOutput = new HtmlAsciiOutput("ascii_art.html", "Courier New");       // outputs ASCII-art into an HTML file (e.g. out.html)

        // 8) send the result to the console output
        consoleOutput.out(asciiImage);              // prints the ASCII-art so you can see it in IntelliJ console

        // 9) send the result to the HTML output
        htmlOutput.out(asciiImage);                 // writes the ASCII-art into an HTML file to view in a browser
    }

    /**
     * Builds a charset containing all printable ASCII characters.
     * <p>
     * The range is from character code 32 (' ') to 126 ('~'),
     * which includes digits, letters, punctuation and symbols.
     *
     * @return an array with all printable ASCII characters
     */
    private static char[] buildFullAsciiCharset() {
        final int firstPrintable = 32;                // ' ' (space)
        final int lastPrintable = 126;               // '~'
        int size = lastPrintable - firstPrintable + 1;

        char[] charset = new char[size];             // holds all printable characters

        for (int i = 0; i < size; i++) {             // fill the array step by step
            charset[i] = (char) (firstPrintable + i); // convert numeric code to char
        }

        return charset;
    }


}
