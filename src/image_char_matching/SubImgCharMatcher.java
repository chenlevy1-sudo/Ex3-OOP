package image_char_matching;

import image.ImagePadder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SubImgCharMatcher {
	private final Set<Character> charSet;
	private Map<Character, Double> normalizedBrightnessMap;
	private CharBrightness charBrightness;

	public SubImgCharMatcher(char [] charset) {
		charSet = new HashSet<Character>();
		for (char ch : charset) {
			charSet.add(ch);
		}
		// חישוב raw brightness
		CharRawBrightness rawCalc = new CharRawBrightness(charset);
		Map<Character, Double> rawMap = rawCalc.getRawBrightnessMap();

		// יצירת אובייקט שמנרמל את הערכים
		charBrightness = new CharBrightness(rawMap);

		// שמירה של normalized brightness לצורך חישובים מהירים
		normalizedBrightnessMap = charBrightness.getNormalizedBrightnessMap();

	}
    
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

public void addChar(char c) {
	if (charSet.add(c)) {
		charBrightness.addChar(c);
		normalizedBrightnessMap = charBrightness.getNormalizedBrightnessMap();
	}
}
public void removeChar(char c) {
	if (charSet.remove(c)) {
		charBrightness.removeChar(c);
		normalizedBrightnessMap = charBrightness.getNormalizedBrightnessMap();
	}
}
}
