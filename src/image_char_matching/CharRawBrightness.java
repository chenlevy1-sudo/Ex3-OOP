package image_char_matching;

import java.util.HashMap;
import java.util.Map;

import static image_char_matching.CharConverter.convertToBoolArray;

/**
 * Computes raw brightness scores for characters based on their boolean
 * bitmap representation.
 * <p>
 * The raw brightness of a character is defined as the fraction of
 * {@code true} cells in its boolean matrix, in the range {@code [0.0, 1.0]}.
 * The bitmap for each character is obtained via
 * {@link CharConverter#convertToBoolArray(char)} and is assumed to have a
 * resolution of {@link CharConverter#DEFAULT_PIXEL_RESOLUTION}
 * by {@link CharConverter#DEFAULT_PIXEL_RESOLUTION}.
 */
public class CharRawBrightness {

    /**
     * Total number of pixels in a character bitmap
     * ({@code DEFAULT_PIXEL_RESOLUTION * DEFAULT_PIXEL_RESOLUTION}).
     */
    private static final int TOTAL_PIXELS =
            CharConverter.DEFAULT_PIXEL_RESOLUTION * CharConverter.DEFAULT_PIXEL_RESOLUTION;

    /**
     * Default set of characters whose brightness is computed: digits {@code '0'–'9'}.
     */
    public static final char[] DEFAULT_TABS = {'0','1','2','3','4','5','6','7','8','9'};

    /**
     * Global cache of raw brightness values for characters that were already
     * analyzed in the program.
     */
    private static final Map<Character, Double> GLOBAL_RAW_CACHE = new HashMap<>();

    /**
     * The set of characters for which raw brightness values will be computed.
     */
    private final char[] tabs;

    /**
     * Creates a {@code CharRawBrightness} instance with the default set
     * of characters ({@link #DEFAULT_TABS}).
     */
    public CharRawBrightness() {
        this(DEFAULT_TABS);
    }

    /**
     * Creates a {@code CharRawBrightness} instance for the given set of characters.
     *
     * @param tabs the characters whose raw brightness values will be computed
     */
    public CharRawBrightness(char[] tabs) {
        this.tabs = tabs;
    }

    /**
     * Counts how many cells in the given boolean matrix are {@code true}.
     *
     * @param tab a 2D boolean array representing a character bitmap
     * @return the number of {@code true} cells in {@code tab}
     */
    private int howManyTrue(boolean[][] tab) {
        int res = 0;
        for (int row = 0; row < tab.length; row++) {
            for (int col = 0; col < tab[row].length; col++) {
                if (tab[row][col]) {
                    res++;
                }
            }
        }
        return res;
    }

    /**
     * Computes a raw brightness score for the given boolean bitmap.
     * <p>
     * The score is defined as:
     * <pre>
     *   brightness = (# of true cells) / TOTAL_PIXELS
     * </pre>
     *
     * @param tab a 2D boolean array representing a character bitmap
     * @return the raw brightness score in the range {@code [0.0, 1.0]}
     */
    public double getBrightnessScore(boolean[][] tab) {
        double whiteCells = (double) howManyTrue(tab);
        return whiteCells / TOTAL_PIXELS;
    }

    /**
     * Builds a new map from characters to their raw brightness values.
     * <p>
     * This method uses a global static cache so that the bitmap of a given
     * character is converted and analyzed at most once during the lifetime of
     * the program. If a brightness value for some character was already
     * computed, it is reused instead of recomputing it.
     *
     * @return a map from characters to their raw brightness values
     */
    public Map<Character, Double> getRawBrightnessMap() {
        Map<Character, Double> res = new HashMap<>();
        for (char c : tabs) {
            Double cached = GLOBAL_RAW_CACHE.get(c);
            if (cached == null) {
                boolean[][] tab = convertToBoolArray(c);
                cached = getBrightnessScore(tab);
                GLOBAL_RAW_CACHE.put(c, cached);
            }
            res.put(c, cached);
        }
        return res;
    }

    /**
     * Updates (or inserts) the raw brightness value of a single character
     * in an existing map.
     * <p>
     * The character's bitmap is obtained via
     * {@link CharConverter#convertToBoolArray(char)}, and its brightness is
     * computed with {@link #getBrightnessScore(boolean[][])}. The entry in
     * {@code myDic} for the given character is then set to this value.
     *
     * @param myDic the map to update (modified in-place)
     * @param a     the character whose brightness value should be (re)computed
     * @return the same map instance {@code myDic}, after the update
     */
    public Map<Character, Double> updateRawBrightnessMap(Map<Character, Double> myDic, char a) {
        double tabBright = getBrightnessScore(convertToBoolArray(a));
        myDic.put(a, tabBright);
        return myDic;
    }
}
