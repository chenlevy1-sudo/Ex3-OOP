package image_char_matching;

import java.util.HashMap;
import java.util.Map;
import static image_char_matching.CharConverter.convertToBoolArray;

// אחראית על חישוב בהירות גולמית (raw) לכל תו בסט
public class CharRawBrightness {
	private static final int TOTAL_PIXELS =
			CharConverter.DEFAULT_PIXEL_RESOLUTION * CharConverter.DEFAULT_PIXEL_RESOLUTION;
	public static final char[] DEFAULT_TABS = {'0','1','2','3','4','5','6','7','8','9'};
	private final char[] tabs;

	public CharRawBrightness() {
		this(DEFAULT_TABS);
	}
	public CharRawBrightness(char[] tabs) {
		this.tabs = tabs;
	}

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

	// נותנת למטריצה (תו) ציון בהירות גולמי בין 0 ל-1
	public double getBrightnessScore(boolean[][] tab) {
		double whiteCells = (double) howManyTrue(tab);
		return whiteCells / TOTAL_PIXELS;
	}

	// מחזירה מילון: תו -> בהירות גולמית
	public Map<Character, Double> getRawBrightnessMap() {
		Map<Character, Double> res = new HashMap<>();
		for (char c : tabs) {
			boolean[][] tab = convertToBoolArray(c);
			double tabBright = getBrightnessScore(tab);
			res.put(c, tabBright);
		}
		return res;
	}

	// עדכון/הוספה של תו אחד למפה קיימת
	public Map<Character, Double> updateRawBrightnessMap(Map<Character, Double> myDic, char a) {
		double tabBright = getBrightnessScore(convertToBoolArray(a));
		myDic.put(a, tabBright);
		return myDic;
	}
}
