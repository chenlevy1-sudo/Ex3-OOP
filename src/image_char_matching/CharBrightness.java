package image_char_matching;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static image_char_matching.CharConverter.convertToBoolArray;
/**
 * Maintains raw and normalized brightness values for a set of characters.
 * <p>
 * For each character this class stores:
 * <ul>
 *     <li>a <b>raw brightness</b> value – the fraction of {@code true} pixels in its
 *     boolean bitmap representation, in the range {@code [0.0, 1.0]}, and</li>
 *     <li>a <b>normalized brightness</b> value – a linear rescaling of all raw
 *     values so that the minimum becomes {@code 0.0} and the maximum becomes
 *     {@code 1.0} (with special handling for degenerate cases).</li>
 * </ul>
 * Whenever characters are added or removed, the internal minimum and maximum
 * raw values are recomputed and all normalized values are updated accordingly.
 */
public class CharBrightness {

	/**
	 * Raw brightness values: character → raw brightness in {@code [0.0, 1.0]}.
	 */
	private final Map<Character, Double> rawBrightness = new HashMap<>();

	/**
	 * Normalized brightness values: character → normalized brightness
	 * in {@code [0.0, 1.0]}.
	 */
	private final Map<Character, Double> normalizedBrightness = new HashMap<>();

	/**
	 * Current minimum of all raw brightness values.
	 */
	private double minRaw = Double.POSITIVE_INFINITY;

	/**
	 * Current maximum of all raw brightness values.
	 */
	private double maxRaw = Double.NEGATIVE_INFINITY;

	/**
	 * Creates a new {@code CharBrightness} instance initialized with the given
	 * raw brightness values.
	 * <p>
	 * The provided map is copied into an internal map. After initialization the
	 * minimum and maximum raw values are computed and the normalized brightness
	 * map is populated.
	 *
	 * @param initialRaw a map from characters to their raw brightness values
	 *                   (values are expected to be in {@code [0.0, 1.0]})
	 */
	public CharBrightness(Map<Character, Double> initialRaw) {
		rawBrightness.putAll(initialRaw);
		recalcMinMax();
		normalize();
	}

	/**
	 * Adds a new character to the set and computes its raw brightness.
	 * <p>
	 * If the character is already present, the method does nothing. Otherwise,
	 * the raw brightness is computed using
	 * {@link CharConverter#convertToBoolArray(char)}, the global minimum and
	 * maximum raw values are updated, and all normalized brightness values are
	 * recomputed.
	 *
	 * @param c the character to add
	 */
	public void addChar(char c) {
		if (rawBrightness.containsKey(c)) {
			return;
		}
		double raw = computeRawBrightness(c);
		rawBrightness.put(c, raw);
		recalcMinMax();
		normalize();
	}

	/**
	 * Removes a character from the set and updates the internal statistics.
	 * <p>
	 * If the character is not present, the method does nothing. After removal
	 * the minimum and maximum raw values are recomputed and all normalized
	 * brightness values are updated. If the last character is removed, both
	 * maps are cleared and the min/max values are reset to their initial
	 * extremes.
	 *
	 * @param c the character to remove
	 */
	public void removeChar(char c) {
		if (!rawBrightness.containsKey(c)) {
			return;
		}
		rawBrightness.remove(c);
		if (rawBrightness.isEmpty()) {
			minRaw = Double.POSITIVE_INFINITY;
			maxRaw = Double.NEGATIVE_INFINITY;
			normalizedBrightness.clear();
			return;
		}
		recalcMinMax();
		normalize();
	}

	/**
	 * Returns an unmodifiable view of the raw brightness map.
	 * <p>
	 * The returned map reflects the current internal state but cannot be
	 * modified by the caller.
	 *
	 * @return an unmodifiable map from characters to raw brightness values
	 */
	public Map<Character, Double> getRawBrightnessMap() {
		return Collections.unmodifiableMap(rawBrightness);
	}

	/**
	 * Returns an unmodifiable view of the normalized brightness map.
	 * <p>
	 * The returned map reflects the current internal state but cannot be
	 * modified by the caller.
	 *
	 * @return an unmodifiable map from characters to normalized brightness values
	 */
	public Map<Character, Double> getNormalizedBrightnessMap() {
		return Collections.unmodifiableMap(normalizedBrightness);
	}

	/* ===================== Internal helpers ===================== */

	/**
	 * Recalculates the {@link #minRaw} and {@link #maxRaw} values based on the
	 * current contents of {@link #rawBrightness}.
	 */
	private void recalcMinMax() {
		Set<Character> keys = rawBrightness.keySet();
		minRaw = Double.POSITIVE_INFINITY;
		maxRaw = Double.NEGATIVE_INFINITY;
		for (char c : keys) {
			double raw = rawBrightness.get(c);
			if (raw < minRaw) {
				minRaw = raw;
			}
			if (raw > maxRaw) {
				maxRaw = raw;
			}
		}
	}

	/**
	 * Rebuilds the {@link #normalizedBrightness} map from the current
	 * {@link #rawBrightness} values and the {@link #minRaw}/{@link #maxRaw}
	 * statistics.
	 * <p>
	 * <ul>
	 *     <li>If there are no characters, the method clears the map.</li>
	 *     <li>If there is only one character or all raw values are equal,
	 *     every character receives a normalized brightness of {@code 0.5}.</li>
	 *     <li>Otherwise, each raw value {@code r} is mapped linearly to
	 *     {@code (r - minRaw) / (maxRaw - minRaw)}.</li>
	 * </ul>
	 */
	private void normalize() {
		normalizedBrightness.clear();
		if (rawBrightness.isEmpty()) {
			return;
		}
		if (rawBrightness.size() == 1 || maxRaw == minRaw) {
			for (char c : rawBrightness.keySet()) {
				normalizedBrightness.put(c, 0.5);
			}
			return;
		}
		double range = maxRaw - minRaw;
		for (char c : rawBrightness.keySet()) {
			double raw = rawBrightness.get(c);
			double norm = (raw - minRaw) / range;
			normalizedBrightness.put(c, norm);
		}
	}

	/**
	 * Computes the raw brightness value for a single character.
	 * <p>
	 * The character is first converted to a 2D boolean array using
	 * {@link CharConverter#convertToBoolArray(char)}. The raw brightness is
	 * defined as the ratio between the number of {@code true} cells and the
	 * total number of cells in the array.
	 *
	 * @param c the character whose raw brightness is to be computed
	 * @return the raw brightness value in the range {@code [0.0, 1.0]}
	 */
	private double computeRawBrightness(char c) {
		boolean[][] tab = convertToBoolArray(c);
		int countTrue = 0;
		for (int row = 0; row < tab.length; row++) {
			for (int col = 0; col < tab[row].length; col++) {
				if (tab[row][col]) {
					countTrue++;
				}
			}
		}
		int totalPixels = tab.length * tab[0].length; // e.g. 16*16 = 256
		return (double) countTrue / totalPixels;
	}
}
