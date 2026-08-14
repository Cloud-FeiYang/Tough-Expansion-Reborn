package p455w0rd.tanaddons.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Embedded utility from p455w0rdslib to format numbers in a clean readable form.
 * E.g., 1000 -> 1K, 1500000 -> 1.5M, etc.
 */
public class ReadableNumberConverter {

    public static final ReadableNumberConverter INSTANCE = new ReadableNumberConverter();

    private static final DecimalFormat FORMAT = new DecimalFormat("0.#");
    private static final String[] SUFFIXES = { "", "K", "M", "B", "T", "P", "E" };

    static {
        FORMAT.setRoundingMode(RoundingMode.DOWN);
    }

    public String toReadableForm(long number) {
        if (number < 1000) {
            return String.valueOf(number);
        }
        int exp = (int) (Math.log10(number) / 3);
        if (exp >= SUFFIXES.length) {
            exp = SUFFIXES.length - 1;
        }
        double val = number / Math.pow(10, exp * 3);
        return FORMAT.format(val) + SUFFIXES[exp];
    }

    public String toWideReadableForm(long number) {
        return toReadableForm(number);
    }

    public String toReadableForm(double number) {
        return toReadableForm((long) number);
    }

    public String toWideReadableForm(double number) {
        return toWideReadableForm((long) number);
    }
}
