package image_char_matching;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static image_char_matching.CharConverter.convertToBoolArray;

public class CharBrightness {
	// בהירויות גולמיות: תו -> raw brightness
	private final Map<Character, Double> rawBrightness = new HashMap<>();
	// בהירויות מנורמלות: תו -> normalized brightness
	private final Map<Character, Double> normalizedBrightness = new HashMap<>();

	private double minRaw = Double.POSITIVE_INFINITY;
	private double maxRaw = Double.NEGATIVE_INFINITY;

	public CharBrightness(Map<Character, Double> initialRaw) {
		rawBrightness.putAll(initialRaw);
		recalcMinMax();
		normalize();
	}

	/** מוסיף תו חדש + מחשב לו raw + מעדכן נרמול */
	public void addChar(char c) {
		if (rawBrightness.containsKey(c)) {
			return;
		}
		double raw = computeRawBrightness(c);
		rawBrightness.put(c, raw);
		recalcMinMax();
		normalize();
	}

	/** מסיר תו מהסט ומעדכן min/max + נרמול */
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

	/** גישה למפה של raw (למשל לדיבוג / טסטים) */
	public Map<Character, Double> getRawBrightnessMap() {
		return Collections.unmodifiableMap(rawBrightness);
	}

	/** גישה למפה של normalized */
	public Map<Character, Double> getNormalizedBrightnessMap() {
		return Collections.unmodifiableMap(normalizedBrightness);
	}

	/* ============ עזר פנימי ============ */

	private void recalcMinMax() {
		Set<Character> keys = rawBrightness.keySet();
		minRaw = Double.POSITIVE_INFINITY;
		maxRaw = Double.NEGATIVE_INFINITY;
		for (char c : keys) {
			double raw = rawBrightness.get(c);
			if (raw < minRaw) minRaw = raw;
			if (raw > maxRaw) maxRaw = raw;
		}
	}

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

	/** חישוב raw חדש לתו אחד – פעם אחת בלבד לתו */
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
		int totalPixels = tab.length * tab[0].length; // 16*16=256
		return (double) countTrue / totalPixels;
	}
}
