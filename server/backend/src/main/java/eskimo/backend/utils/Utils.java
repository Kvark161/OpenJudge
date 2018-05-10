package eskimo.backend.utils;

import java.util.Locale;
import java.util.Random;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

public class Utils {
    private Utils() {
    }

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 12;
    private static final String PASSWORD_ABC;

    static {
        String abcBig = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String abcSmall = abcBig.toLowerCase(Locale.ROOT);
        String digits = "0123456789";
        PASSWORD_ABC = abcBig + abcSmall + digits;
    }

    /**
     * Generates password from latin letters and digits
     */
    public static String generatePassword() {
        int passwordLength = new Random().nextInt();
        if (passwordLength < 0) {
            passwordLength = -passwordLength;
        }
        passwordLength  = passwordLength % (MAX_PASSWORD_LENGTH - MIN_PASSWORD_LENGTH + 1) + MIN_PASSWORD_LENGTH;
        Random charRandom = new Random();
        return IntStream.range(0, passwordLength)
                .boxed()
                .map(i -> {
                    int index = Math.abs(charRandom.nextInt()) % PASSWORD_ABC.length();
                    return PASSWORD_ABC.charAt(index);
                })
                .map(Object::toString)
                .collect(joining(""));
    }
}
