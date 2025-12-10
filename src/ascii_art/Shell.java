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

    private static final int DEFAULT_RESOLUTION = 2;
    private static final char DEFAULT_CHARSET_START = '0';
    private static final char DEFAULT_CHARSET_END = '9';
    private static final char MIN_PRINTABLE_ASCII = 32;
    private static final char MAX_PRINTABLE_ASCII = 126;
    private static final int MIN_CHARS_IN_ROW = 1;
    private static final String CMD_EXIT = "exit";
    private static final String CMD_CHARS = "chars";
    private static final String CMD_ADD = "add";
    private static final String CMD_REMOVE = "remove";
    private static final String CMD_RES = "res";
    private static final String CMD_REVERSE = "reverse";
    private static final String CMD_OUTPUT = "output";
    private static final String CMD_ASCII_ART = "asciiArt";
    private static final String ARG_ALL = "all";
    private static final String ARG_SPACE = "space";
    private static final String ARG_CONSOLE = "console";
    private static final String ARG_HTML = "html";
    private static final String DEFAULT_FONT = "Courier New";
    private static final String DEFAULT_OUT_FILE_NAME = "out.html";
    private static final Character CHAR_SPACE = ' ';
    private static final int RANGE_EXPRESSION_LENGTH = 3;
    private static final int RANGE_START_INDEX = 0;
    private static final int RANGE_SEPARATOR_INDEX = 1;
    private static final int RANGE_END_INDEX = 2;
    private static final char RANGE_SEPARATOR = '-';



    private final SortedSet<Character> chars;
    private final SubImgCharMatcher matcher;

    private int resolution = DEFAULT_RESOLUTION;
    private Image image;
    private int imgWidth;
    private int imgHeight;

    private boolean reverse = false;
    private AsciiOutput output = new ConsoleAsciiOutput();

    /**
     * Constructs a new shell with a default charset of digits '0'–'9'.
     */
    public Shell() {
        this.chars = new TreeSet<>();
        for (char c = DEFAULT_CHARSET_START; c <= DEFAULT_CHARSET_END; c++) {
            chars.add(c);
        }
        // 1) initialize matcher with default charset (digits)
        this.matcher = new SubImgCharMatcher();
    }

    /**
     * Starts the shell session on the given image file.
     *
     * @param imageName path to the image file
     */
    public void run(String imageName) {
        try {
            this.image = new Image(imageName);
            imgWidth = image.getWidth();
            imgHeight = image.getHeight();
        } catch (IOException e) {
            return;
        }

        userInputLoop();
    }

    /**
     * Main loop that reads and handles user commands.
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

            switch (cmd) {
                case CMD_EXIT:
                    return;

                case CMD_CHARS:
                    handleChars();
                    break;

                case CMD_ADD:
                    handleAdd(parts);
                    break;

                case CMD_REMOVE:
                    handleRemove(parts);
                    break;

                case CMD_RES:
                    handleRes(parts);
                    break;

                case CMD_REVERSE:
                    handleReverse(parts);
                    break;

                case CMD_OUTPUT:
                    handleOutput(parts);
                    break;

                case CMD_ASCII_ART:
                    handleAsciiArt();
                    break;

                default:
                    // unknown command – do nothing (same behavior as before)
                    break;
            }
        }
    }


    /**
     * Handles the {@code asciiArt} command.
     * <p>
     * Creates a new {@link AsciiArtAlgorithm} instance for this run,
     * passes the shared matcher and the current reverse flag.
     */
    private void handleAsciiArt() {
        if (chars.size() < 2) {
            System.out.println("Did not execute. Charset is too small.");
            return;
        }

        // 1) create a NEW algorithm instance for this run, with shared matcher and reverse flag
        AsciiArtAlgorithm algo =
                new AsciiArtAlgorithm(image, matcher, resolution, reverse);

        // 2) run the algorithm
        char[][] art = algo.run();

        // 3) send to the selected output (console / html)
        output.out(art);
    }

    /**
     * Handles the {@code chars} command – prints the current charset.
     */
    private void handleChars() {
        for (char c : chars) {
            System.out.print(c);
        }
        System.out.println();
    }

    /**
     * Handles the {@code add} command.
     *
     * @param parts tokenized user input
     */
    private void handleAdd(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Did not add due to incorrect format.");
            return;
        }

        String arg = parts[1];

        if (arg.equals(ARG_ALL)) {
            addRange((char) MIN_PRINTABLE_ASCII, (char) MAX_PRINTABLE_ASCII);
        } else if (arg.equals(ARG_SPACE)) {
            chars.add(CHAR_SPACE);
            matcher.addChar(CHAR_SPACE);
        } else if (arg.length() == 1) {
            char c = arg.charAt(0);
            if (isLegalAscii(c)) {
                chars.add(c);
                matcher.addChar(c);
            } else {
                System.out.println("Did not add due to incorrect format.");
            }
        } else if (arg.length() == RANGE_EXPRESSION_LENGTH && arg.charAt(1) == '-') {
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
     */
    private boolean isLegalAscii(char c) {
        int v = (int) c;
        return v >= MIN_PRINTABLE_ASCII && v <= MAX_PRINTABLE_ASCII;
    }

    /**
     * Adds all characters in the inclusive range [from, to] both to the
     * sorted charset and to the matcher.
     */
    private void addRange(char from, char to) {
        for (char c = from; c <= to; c++) {
            chars.add(c);
            matcher.addChar(c);
        }
    }

    /**
     * Handles the {@code remove} command.
     *
     * @param parts tokenized user input
     */
    private void handleRemove(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Did not remove due to incorrect format.");
            return;
        }

        String arg = parts[1];

        if (arg.equals(ARG_ALL)) {
            for (char c : chars) {
                matcher.removeChar(c);
            }
            chars.clear();
        } else if (arg.equals(ARG_SPACE)) {
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

        int minCharsInRow = Math.max(MIN_CHARS_IN_ROW, imgWidth / imgHeight);
        int maxCharsInRow = imgWidth;

        if (newRes < minCharsInRow || newRes > maxCharsInRow) {
            System.out.println("Did not change resolution due to exceeding boundaries.");
            return;
        }
        resolution = newRes;
        System.out.println("Resolution set to " + resolution + ".");
    }

    /**
     * Handles the {@code reverse} command – toggles the reverse mode flag.
     * The flag will be used on the next {@code asciiArt} run.
     */
    private void handleReverse(String[] parts) {
        reverse = !reverse;
    }

    /**
     * Handles the {@code output} command – selects the output target.
     *
     * @param parts tokenized user input
     */
    private void handleOutput(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Did not change output method due to incorrect format.");
            return;
        }

        String arg = parts[1];

        if (arg.equals(ARG_CONSOLE)) {
            output = new ConsoleAsciiOutput();
        } else if (arg.equals(ARG_HTML)) {
            output = new HtmlAsciiOutput(DEFAULT_OUT_FILE_NAME, DEFAULT_FONT);
        } else {
            System.out.println("Did not change output method due to incorrect format.");
        }
    }

    /**
     * Program entry point. Expects a single argument: the path to the
     * image file.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }
        Shell shell = new Shell();
        shell.run(args[0]);
    }
}
