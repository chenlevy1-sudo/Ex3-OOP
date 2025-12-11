package ascii_art;

/**
 * A generic checked exception for all user-facing errors in the ASCII-art shell.
 * The message of this exception is printed directly to the user.
 */
public class AsciiArtException extends Exception {

    /**
     * Creates a new {@code AsciiArtException} with the given message.
     *
     * @param message the error message to be shown to the user
     */
	public AsciiArtException(String message) {
        super(message);
	}
}
