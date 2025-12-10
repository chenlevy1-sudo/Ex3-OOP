package ascii_art;

import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Interactive shell for user commands and interaction with the ASCII-art
 * algorithm.
 * <p>
 * The shell:
 * <ul>
 *     <li>Maintains the current charset as a sorted set of characters.</li>
 *     <li>Holds a single {@link SubImgCharMatcher} instance and keeps it
 *     synchronized with the charset, so character brightness information
 *     is reused between runs.</li>
 *     <li>Creates a new {@link AsciiArtAlgorithm} instance on every
 *     {@code asciiArt} command, as required.</li>
 * </ul>
 */
public class Shell {

    /**
     * Current charset – always sorted by ASCII value.
     */
    private final SortedSet<Character> chars;

    /**
     * Matcher that holds brightness information for the current charset.
     * Shared between all runs of the algorithm to avoid redundant
     * recomputation.
     */
    private final SubImgCharMatcher matcher;

    /**
     * Current resolution (number of characters per row).
     */
    private int resolution = 2;

    /**
     * Current image used for ASCII-art generation.
     */
    private Image image;

    /**
     * Cached width of the image, used for resolution boundaries.
     */
    private int imgWidth;

    /**
     * Cached height of the image, used for resolution boundaries.
     */
    private int imgHeight;

    /**
     * Reverse mode flag (behavior not defined yet).
     */
    private boolean reverse = false;

    /**
     * Current output target (console / html).
     */
    private AsciiOutput output = new ConsoleAsciiOutput();

    /**
     * Constructs a new shell with a default charset of digits '0'–'9'.
     * <p>
     * Both the sorted charset and the {@link SubImgCharMatcher} are
     * initialized to contain these digits.
     */
    public Shell() {
        this.chars = new TreeSet<>();
        for (char c = '0'; c <= '9'; c++) {
            chars.add(c);
        }
        // 1) initialize matcher with default charset (digits)
        this.matcher = new SubImgCharMatcher();
    }

    /**
     * Starts the shell session on the given image file.
     * <p>
     * If loading the image fails, the method returns immediately, as
     * required by the assignment.
     *
     * @param imageName path to the image file
     */
    public void run(String imageName) {
        try {
            this.image = new Image(imageName);
            imgWidth = image.getWidth();
            imgHeight = image.getHeight();
        } catch (IOException e) {
            // If there is a problem with the image – just terminate.
            return;
        }

        userInputLoop();
    }

    /**
     * Main loop that reads and handles user commands.
     * <p>
     * The loop terminates only when the user types {@code exit}.
     */
    private void userInputLoop() {
        while (true) {
            System.out.print(">>> ");
            String input = KeyboardInput.readLine();  // already trimmed

            if (input.isEmpty()) {
                continue;
            }
            String[] parts = input.split("\\s+");
            String cmd = parts[0];

            if (cmd.equals("exit")) {
                return;  // must not print anything after this
            } else if (cmd.equals("chars")) {
                handleChars();
            } else if (cmd.equals("add")) {
                handleAdd(parts);
            } else if (cmd.equals("remove")) {
                handleRemove(parts);
            } else if (cmd.equals("res")) {
                handleRes(parts);
            } else if (cmd.equals("reverse")) {
                handleReverse(parts);
            } else if (cmd.equals("output")) {
                handleOutput(parts);
            } else if (cmd.equals("asciiArt")) {
                handleAsciiArt();
            }
        }
    }

    /**
     * Handles the {@code asciiArt} command.
     * <p>
     * Creates a <b>new</b> {@link AsciiArtAlgorithm} instance for this run,
     * but reuses the shared {@link SubImgCharMatcher} so that character
     * brightness information is reused between runs.
     */
    private void handleAsciiArt() {
        if (chars.size() < 2) {
            System.out.println("Did not execute. Charset is too small.");
            return;
        }

        // 1) create a NEW algorithm instance for this run
        AsciiArtAlgorithm algo = new AsciiArtAlgorithm(image, matcher, resolution);

        // 2) run the algorithm
        char[][] art = algo.run();

        // 3) send to the selected output (console / html)
        output.out(art);
    }

    /**
     * Handles the {@code chars} command – prints the current charset
     * as a sorted sequence of characters on a single line.
     */
    private void handleChars() {
        for (char c : chars) {
            System.out.print(c);
        }
        System.out.println();
    }

    /**
     * Handles the {@code add} command.
     * <p>
     * Supported formats:
     * <ul>
     *     <li>{@code add all}</li>
     *     <li>{@code add space}</li>
     *     <li>{@code add X}</li>
     *     <li>{@code add A-Z}</li>
     * </ul>
     * Whenever characters are added to {@link #chars}, they are also
     * added to {@link #matcher} so that brightness information stays
     * synchronized.
     *
     * @param parts tokenized user input
     */
    private void handleAdd(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Did not add due to incorrect format.");
            return;
        }

        String arg = parts[1]; // we may ignore the rest of the input

        if (arg.equals("all")) {
            addRange((char) 32, (char) 126);
        } else if (arg.equals("space")) {
            chars.add(' ');
            matcher.addChar(' ');
        } else if (arg.length() == 1) {
            char c = arg.charAt(0);
            if (isLegalAscii(c)) {
                chars.add(c);
                matcher.addChar(c);
            } else {
                System.out.println("Did not add due to incorrect format.");
            }
        } else if (arg.length() == 3 && arg.charAt(1) == '-') {
            char c1 = arg.charAt(0);
            char c2 = arg.charAt(2);

            if (!isLegalAscii(c1) || !isLegalAscii(c2)) {
                System.out.println("Did not add due to incorrect format.");
                return;
            }

            char start = (char) Math.min(c1, c2);
            char end   = (char) Math.max(c1, c2);

            addRange(start, end);
        } else {
            System.out.println("Did not add due to incorrect format.");
        }
    }

    /**
     * Checks whether a character is in the legal ASCII range [32, 126].
     *
     * @param c the character to check
     * @return {@code true} if the character is legal, {@code false} otherwise
     */
    private boolean isLegalAscii(char c) {
        int v = (int) c;
        return v >= 32 && v <= 126;
    }

    /**
     * Adds all characters in the inclusive range [from, to] both to the
     * sorted charset and to the matcher.
     *
     * @param from start of the range (inclusive)
     * @param to   end of the range (inclusive)
     */
    private void addRange(char from, char to) {
        for (char c = from; c <= to; c++) {
            chars.add(c);
            matcher.addChar(c);
        }
    }

    /**
     * Handles the {@code remove} command.
     * <p>
     * Supported formats:
     * <ul>
     *     <li>{@code remove all}</li>
     *     <li>{@code remove space}</li>
     *     <li>{@code remove X}</li>
     *     <li>{@code remove A-Z}</li>
     * </ul>
     * Whenever characters are removed from {@link #chars}, they are also
     * removed from {@link #matcher}.
     *
     * @param parts tokenized user input
     */
    private void handleRemove(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Did not remove due to incorrect format.");
            return;
        }

        String arg = parts[1];

        if (arg.equals("all")) {
            for (char c : chars) {
                matcher.removeChar(c);
            }
            chars.clear();
        } else if (arg.equals("space")) {
            chars.remove(' ');
            matcher.removeChar(' ');
        } else if (arg.length() == 1) {
            char c = arg.charAt(0);
            if (isLegalAscii(c)) {
                chars.remove(c);
                matcher.removeChar(c);
            } else {
                System.out.println("Did not remove due to incorrect format.");
            }
        } else if (arg.length() == 3 && arg.charAt(1) == '-') {
            char from = arg.charAt(0);
            char to = arg.charAt(2);
            if (isLegalAscii(from) && isLegalAscii(to) && from <= to) {
                for (char c = from; c <= to; c++) {
                    chars.remove(c);
                    matcher.removeChar(c);
                }
            } else {
                System.out.println("Did not remove due to incorrect format.");
            }
        } else {
            System.out.println("Did not remove due to incorrect format.");
        }
    }

    /**
     * Handles the {@code res} command.
     * <p>
     * Supported formats:
     * <ul>
     *     <li>{@code res}</li>
     *     <li>{@code res up}</li>
     *     <li>{@code res down}</li>
     * </ul>
     * The resolution is kept within the boundaries derived from the
     * current image size.
     *
     * @param parts tokenized user input
     */
    private void handleRes(String[] parts) {
        if (parts.length == 1) {
            System.out.println("Resolution set to " + resolution + ".");
            return;
        }

        String arg = parts[1];

        int newRes;
        if (arg.equals("up")) {
            newRes = resolution * 2;
        } else if (arg.equals("down")) {
            newRes = resolution / 2;
        } else {
            System.out.println("Did not change resolution due to incorrect format.");
            return;
        }

        int minCharsInRow = Math.max(1, imgWidth / imgHeight);
        int maxCharsInRow = imgWidth;

        if (newRes < minCharsInRow || newRes > maxCharsInRow) {
            System.out.println("Did not change resolution due to exceeding boundaries.");
            return;
        }
        resolution = newRes;
        System.out.println("Resolution set to " + resolution + ".");
    }

    /**
     * Handles the {@code reverse} command – currently only toggles
     * the internal flag, without affecting the algorithm.
     *
     * @param parts tokenized user input (ignored)
     */
    private void handleReverse(String[] parts) {
        reverse = !reverse;   // toggle: false→true, true→false
    }

    /**
     * Handles the {@code output} command – selects the output target:
     * console or HTML.
     *
     * @param parts tokenized user input
     */
    private void handleOutput(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Did not change output method due to incorrect format.");
            return;
        }

        String arg = parts[1];

        if (arg.equals("console")) {
            output = new ConsoleAsciiOutput();
        } else if (arg.equals("html")) {
            output = new HtmlAsciiOutput("out.html", "Courier New");
        } else {
            System.out.println("Did not change output method due to incorrect format.");
        }
    }

    /**
     * Program entry point. Expects a single argument: the path to the
     * image file.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }
        Shell shell = new Shell();
        shell.run(args[0]);
    }
}
