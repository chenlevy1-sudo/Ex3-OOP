package image_char_matching;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Maps sub-image brightness values to characters from a configurable charset.
 * <p>
 * This matcher maintains a set of candidate characters and precomputes a
 * normalized brightness value for each one (using {@link CharRawBrightness}
 * and {@link CharBrightness}). Given a brightness value in {@code [0,1]},
 * it returns the character whose normalized brightness is closest to that
 * value. The charset can be dynamically updated by adding or removing
 * characters, in which case the internal brightness cache is refreshed.
 */
public class SubImgCharMatcher {

	private final Set<Character> charSet;
	private Map<Character, Double> normalizedBrightnessMap;
	private final CharBrightness charBrightness;
    // Default charset used by the shell when no explicit charset is provided
    private static final char[] DEFAULT_CHARSET =
            new char[]{'0','1','2','3','4','5','6','7','8','9'};


    /**
     * Creates a matcher for the given charset and precomputes brightness
     * information for all characters.
     *
     * @param charset array of characters that can be used in the ASCII-art
     *                representation; brightness is calculated and normalized
     *                for each character in this array
     */
	public SubImgCharMatcher(char [] charset) {
		charSet = new HashSet<Character>();
		for (char ch : charset) {
			charSet.add(ch);
		}
		// raw brightness
		CharRawBrightness rawCalc = new CharRawBrightness(charset);
		Map<Character, Double> rawMap = rawCalc.getRawBrightnessMap();

		// create object to normalize values
		charBrightness = new CharBrightness(rawMap);

		// cache normalized brightness for later calculations
		normalizedBrightnessMap = charBrightness.getNormalizedBrightnessMap();

	}

    /**
     * Default constructor – initializes the matcher with digits '0'–'9'.
     * <p>
     * This is used by the shell when creating a matcher with the default
     * charset.
     */
    public SubImgCharMatcher() {
        this(DEFAULT_CHARSET);
    }

    /**
     * Returns the character whose normalized brightness is closest to the
     * given brightness value.
     *
     * @param brightness desired brightness in the range {@code [0.0, 1.0]}
     * @return the character from the current charset whose normalized
     *         brightness best matches {@code brightness}
     */
    public char getCharByImageBrightness(double brightness) {
        char best = 0;
        double smallestDiff = Double.MAX_VALUE;

        for (Map.Entry<Character, Double> entry : normalizedBrightnessMap.entrySet()) {
            double diff = Math.abs(entry.getValue() - brightness);
            if (diff < smallestDiff) {
                smallestDiff = diff;
                best = entry.getKey();
            }
        }
        return best;
    }


    /**
     * Adds a new character to the matcher and updates the internal
     * brightness cache if the character was not already present.
     *
     * @param c the character to add to the charset
     */
    public void addChar(char c) {
        if (charSet.add(c)) {
            charBrightness.addChar(c);
            normalizedBrightnessMap = charBrightness.getNormalizedBrightnessMap();
        }
    }

    /**
     * Removes a character from the matcher and updates the internal
     * brightness cache if the character was present.
     *
     * @param c the character to remove from the charset
     */
    public void removeChar(char c) {
        if (charSet.remove(c)) {
            charBrightness.removeChar(c);
            normalizedBrightnessMap = charBrightness.getNormalizedBrightnessMap();
        }
    }
}
