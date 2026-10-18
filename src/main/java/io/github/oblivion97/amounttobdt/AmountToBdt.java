package io.github.oblivion97.amounttobdt;

import java.text.DecimalFormat;

/**
 * Converts numeric amounts into words in Bangladeshi Taka, using the lakh/crore numbering system.
 *
 * <pre>
 * AmountToBdt.convert(1234567.89);
 * // "Twelve Lakh Thirty Four Thousand Five Hundred Sixty Seven Taka Eighty Nine Paisa Only"
 * </pre>
 */
public final class AmountToBdt {

    private static final String[] UNITS = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
    private static final String[] TENS = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty",
            "Ninety"};

    private AmountToBdt() {
    }

    /**
     * Full amount in words, rounded to two decimals: Taka, then Paisa, ending in "Only".
     */
    public static String convert(double amount) {
        String[] parts = new DecimalFormat("#0.00").format(amount).split("\\.");
        long taka = Long.parseLong(parts[0]);
        int paisa = Integer.parseInt(parts[1]);
        if (taka == 0L && paisa == 0) {
            return "Zero Taka Only";
        }

        String words = "";
        if (taka != 0L) {
            words = convertTaka(taka) + " Taka ";
        }
        if (taka == 0L && amount < 0) {
            words = "Minus ";
        }
        if (paisa > 0) {
            words = words + convertPaisa(paisa) + " Paisa ";
        }
        return words + "Only";
    }

    /**
     * Whole-Taka part in words, e.g. 12000 → "Twelve Thousand". Zero returns an empty string.
     */
    public static String convertTaka(long n) {
        if (n < 0L) {
            return "Minus " + convertTaka(Math.abs(n));
        } else if (n < 20L) {
            return UNITS[(int) n];
        } else if (n < 100L) {
            return TENS[(int) n / 10] + (n % 10L != 0L ? " " : "") + UNITS[(int) n % 10];
        } else if (n < 1000L) {
            return UNITS[(int) n / 100] + " Hundred" + (n % 100L != 0L ? " " : "") + convertTaka(n % 100L);
        } else if (n < 100000L) {
            return convertTaka(n / 1000L) + " Thousand" + (n % 1000L != 0L ? " " : "") + convertTaka(n % 1000L);
        } else if (n < 10000000L) {
            return convertTaka(n / 100000L) + " Lakh" + (n % 100000L != 0L ? " " : "") + convertTaka(n % 100000L);
        } else {
            return convertTaka(n / 10000000L) + " Crore" + (n % 10000000L != 0L ? " " : "") + convertTaka(n % 10000000L);
        }
    }

    /**
     * Paisa part (0–99) in words. Zero, or 100 and above, returns an empty string.
     */
    public static String convertPaisa(int n) {
        if (n < 20) {
            return UNITS[n];
        } else if (n < 100) {
            return TENS[n / 10] + (n % 10 != 0 ? " " : "") + UNITS[n % 10];
        } else {
            return "";
        }
    }
}
