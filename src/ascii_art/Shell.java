package ascii_art;

import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.io.IOException;
import java.util.*;


/**
 * TODO: Shell for user commands and interaction.
 */
public class Shell {
	// מאגר התווים – תמיד ממויין לפי ASCII
	private final SortedSet<Character> chars;

	// רזולוציה נוכחית (סעיף 2.6.5)
	private int resolution = 2;
	private Image image;

	// נתוני התמונה – לצורך חישוב גבולות הרזולוציה
	private int imgWidth;
	private int imgHeight;
	private boolean reverse = false;     // 2.7 – מצב reverse
	private AsciiOutput output = new ConsoleAsciiOutput(); // 2.8 – ברירת מחדל console

	public Shell() {
		this.chars = new TreeSet<>();
		// מאתחלים ברירת מחדל 0–9 (סעיף 2.3)
		for (char c = '0'; c <= '9'; c++) {
			chars.add(c);
		}
	}


	/* ====== לוגיקה ראשית ====== */

	public void run(String imageName) {
		// מנסים לטעון תמונה – אם יש בעיה, פשוט מסיימים (כמו בהוראות)
		try {
			this.image = new Image(imageName);
			imgWidth = image.getWidth();
			imgHeight = image.getHeight();
		} catch (IOException e) {
			// אם יש בעיה עם התמונה – פשוט מסיימים
			return;
		}

		userInputLoop();
	}

	private void printChars(){
		for (char c: chars){
			System.out.print(c+" ");
		}
	}

	private void userInputLoop() {
		while (true) {
			System.out.print(">>> ");
			String input = KeyboardInput.readLine();  // כבר עם trim

			if (input.isEmpty()) {
				continue;
			}
			String[] parts = input.split("\\s+");
			String cmd = parts[0];

			if (cmd.equals("exit")) {
				return;  // אסור להדפיס כלום אחרי
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
			}
			else if (cmd.equals("asciiArt")) {
				handleAsciiArt();
			}
			}
		}

	private void handleAsciiArt() {
		if (chars.size() < 2) {
			System.out.println("Did not execute. Charset is too small.");
			return;
		}

		// 2. ממירים את SortedSet<Character> למערך char[]
		char[] charset = new char[chars.size()];
		int i = 0;
		for (char c : chars) {
			charset[i++] = c;
		}

		// 3. יוצרים את האלגוריתם עם התמונה, המאפיינים והרזולוציה הנוכחית
		AsciiArtAlgorithm algo = new AsciiArtAlgorithm(image, charset, resolution);
		//algo.setReverse(reverse);   // מצב reverse מה-Shell

		// 4. מריצים את האלגוריתם
		char[][] art = algo.run();

		// 5. שולחים לפלט שנבחר (console / html)
		output.out(art);
	}
	/* ====== 2.3 – chars ====== */

	private void handleChars() {
		for (char c : chars) {
			System.out.print(c);
		}
		System.out.println();
	}

	/* ====== 2.4 – add ====== */

	private void handleAdd(String[] parts) {
		if (parts.length < 2) {
			System.out.println("Did not add due to incorrect format.");
			return;
		}

		String arg = parts[1]; // לפי ההוראות מותר להתעלם משאר הקלט אחרי זה

		if (arg.equals("all")) {
			addRange((char) 32, (char) 126);
		} else if (arg.equals("space")) {
			chars.add(' ');
		} else if (arg.length() == 1) {
			char c = arg.charAt(0);
			if (isLegalAscii(c)) {
				chars.add(c);
			} else {
				System.out.println("Did not add due to incorrect format.");
			}
		} else if (arg.length() == 3 && arg.charAt(1) == '-') {
			char c1 = arg.charAt(0);
			char c2 = arg.charAt(2);

			// קודם בודקים שהם בתחום ה־ASCII המותר
			if (!isLegalAscii(c1) || !isLegalAscii(c2)) {
				System.out.println("Did not add due to incorrect format.");
				return;
			}

			// דואגים שהקטן יהיה start והגדול end – כך שגם "p-m" יעבוד
			char start = (char) Math.min(c1, c2);
			char end   = (char) Math.max(c1, c2);

			addRange(start, end);
		} else {
			System.out.println("Did not add due to incorrect format.");
		}
	}


	private boolean isLegalAscii(char c) {
		int v = (int) c;
		return v >= 32 && v <= 126;
	}

	private void addRange(char from, char to) {
		for (char c = from; c <= to; c++) {
			chars.add(c);
		}
	}

	/* ====== 2.5 – remove ====== */

	private void handleRemove(String[] parts) {
		if (parts.length < 2) {
			System.out.println("Did not remove due to incorrect format.");
			return;
		}

		String arg = parts[1];

		if (arg.equals("all")) {
			chars.clear();
		} else if (arg.equals("space")) {
			chars.remove(' ');
		} else if (arg.length() == 1) {
			char c = arg.charAt(0);
			if (isLegalAscii(c)) {
				chars.remove(c);
			} else {
				System.out.println("Did not remove due to incorrect format.");
			}
		} else if (arg.length() == 3 && arg.charAt(1) == '-') {
			char from = arg.charAt(0);
			char to = arg.charAt(2);
			if (isLegalAscii(from) && isLegalAscii(to) && from <= to) {
				for (char c = from; c <= to; c++) {
					chars.remove(c);
				}
			} else {
				System.out.println("Did not remove due to incorrect format.");
			}
		} else {
			System.out.println("Did not remove due to incorrect format.");
		}
	}
	/* ====== 2.6 – res / res up / res down ====== */

	private void handleRes(String[] parts) {
		if (parts.length == 1) {
			// 2.6.1 – רק מדפיסים, לא משנים
			System.out.println("Resolution set to " + resolution + ".");
			return;
		}

		String arg = parts[1];  // מתעלמים משאר הקלט אחרי זה

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
	private void handleReverse (String[] parts){
		reverse = !reverse;   // פשוט הופכים מצב: false→true, true→false
	}
	private void handleOutput(String[] parts) {
		if (parts.length < 2) {
			System.out.println("Did not change output method due to incorrect format.");
			return;
		}

		String arg = parts[1];   // מותר להתעלם משאר הקלט לפי 2.8.4

		if (arg.equals("console")) {
			output = new ConsoleAsciiOutput();
		} else if (arg.equals("html")) {
			output = new HtmlAsciiOutput("out.html", "Courier New");
		} else {
			System.out.println("Did not change output method due to incorrect format.");
		}
	}



	public static void main(String[] args) {
        // TODO: implement according to instructions
		Shell shell = new Shell();
		shell.run(args[0]);
    }
}

