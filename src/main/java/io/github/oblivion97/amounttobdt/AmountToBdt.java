package io.github.oblivion97.amounttobdt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * Converts numeric amounts into words in Bangladeshi Taka, using the lakh/crore numbering system.
 *
 * <pre>
 * AmountToBdt.convert(new BigDecimal("1234567.89"));
 * // "Twelve Lakh Thirty Four Thousand Five Hundred Sixty Seven Taka Eighty Nine Paisa Only"
 * </pre>
 *
 * <p>Amounts are rounded to two decimals with {@link RoundingMode#HALF_UP}. Output does not depend on the JVM's
 * default locale. All methods are thread-safe.
 */
public final class AmountToBdt {

    private static final String[] UNITS = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
    private static final String[] TENS = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty",
            "Ninety"};

    private AmountToBdt() {
    }

    /**
     * Full amount in words: Taka, then Paisa, ending in "Only".
     *
     * @throws NullPointerException     if {@code amount} is null
     * @throws IllegalArgumentException if the Taka part does not fit in a {@code long}
     */
    public static String convert(BigDecimal amount) {
        return convert(amount, WordStyle.DEFAULT);
    }

    /**
     * Full amount in words, written in the given style.
     *
     * @throws NullPointerException     if {@code amount} or {@code style} is null
     * @throws IllegalArgumentException if the Taka part does not fit in a {@code long}
     */
    public static String convert(BigDecimal amount, WordStyle style) {
        if (style == null) {
            throw new NullPointerException("style");
        }
        Amount a = Amount.of(amount);
        String taka = a.taka > 0L ? takaWords(a.taka, style.and) : a.isZero() ? "Zero" : "";
        String paisa = a.paisa > 0 ? convertPaisa(a.paisa) : "";

        StringBuilder words = new StringBuilder();
        if (a.negative) {
            words.append("Minus ");
        }
        if (style.currencyFirst) {
            if (!taka.isEmpty()) {
                words.append(style.currency).append(' ').append(taka).append(' ');
            }
            if (!paisa.isEmpty()) {
                words.append(taka.isEmpty() ? "" : "and ").append(style.paisa).append(' ').append(paisa).append(' ');
            }
        } else {
            if (!taka.isEmpty()) {
                words.append(taka).append(' ').append(style.currency).append(' ');
            }
            if (!paisa.isEmpty()) {
                words.append(paisa).append(' ').append(style.paisa).append(' ');
            }
        }
        if (style.only) {
            words.append("Only");
        }
        String result = words.toString().trim();
        return style.upperCase ? result.toUpperCase(Locale.ROOT) : result;
    }

    /**
     * Full amount in words, written in the given style, for {@code double} values.
     *
     * @throws IllegalArgumentException if {@code amount} is NaN or infinite
     */
    public static String convert(double amount, WordStyle style) {
        return convert(toBigDecimal(amount), style);
    }

    /**
     * Whole-Taka amount in words, written in the given style.
     */
    public static String convert(long taka, WordStyle style) {
        return convert(BigDecimal.valueOf(taka), style);
    }

    /**
     * Full amount in words. The value is taken as written in decimal, so {@code 2.675} rounds to 2.68.
     *
     * @throws IllegalArgumentException if {@code amount} is NaN or infinite
     */
    public static String convert(double amount) {
        return convert(toBigDecimal(amount));
    }

    /**
     * Whole-Taka amount in words, e.g. 500 → "Five Hundred Taka Only".
     */
    public static String convert(long taka) {
        return convert(BigDecimal.valueOf(taka));
    }

    /**
     * Whole-Taka part in words, e.g. 12000 → "Twelve Thousand". Zero returns an empty string.
     *
     * @throws IllegalArgumentException if {@code n} is {@link Long#MIN_VALUE}
     */
    public static String convertTaka(long n) {
        if (n == Long.MIN_VALUE) {
            throw new IllegalArgumentException("Amount out of range: " + n);
        } else if (n < 0L) {
            return "Minus " + convertTaka(-n);
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

    /**
     * Positive whole number in words, optionally with "and" before the last two digits ("One Hundred and Five").
     */
    private static String takaWords(long n, boolean and) {
        long lastTwo = n % 100L;
        if (!and || n < 100L || lastTwo == 0L) {
            return convertTaka(n);
        }
        return convertTaka(n - lastTwo) + " and " + convertTaka(lastTwo);
    }

    /**
     * Full amount in Bangla words, e.g. 1234.56 → "এক হাজার দুইশত চৌত্রিশ টাকা ছাপ্পান্ন পয়সা মাত্র".
     *
     * @throws NullPointerException     if {@code amount} is null
     * @throws IllegalArgumentException if the Taka part does not fit in a {@code long}
     */
    public static String convertBangla(BigDecimal amount) {
        return convertBangla(amount, WordStyle.DEFAULT);
    }

    /**
     * Full amount in Bangla words. Of the style settings, only {@link WordStyle#withoutOnly()} applies to Bangla.
     *
     * @throws NullPointerException     if {@code amount} or {@code style} is null
     * @throws IllegalArgumentException if the Taka part does not fit in a {@code long}
     */
    public static String convertBangla(BigDecimal amount, WordStyle style) {
        if (style == null) {
            throw new NullPointerException("style");
        }
        Amount a = Amount.of(amount);
        StringBuilder words = new StringBuilder();
        if (a.isZero()) {
            words.append(BanglaWords.ZERO).append(' ').append(BanglaWords.TAKA).append(' ');
        }
        if (a.negative) {
            words.append(BanglaWords.MINUS).append(' ');
        }
        if (a.taka > 0L) {
            words.append(BanglaWords.words(a.taka)).append(' ').append(BanglaWords.TAKA).append(' ');
        }
        if (a.paisa > 0) {
            words.append(BanglaWords.under100(a.paisa)).append(' ').append(BanglaWords.PAISA).append(' ');
        }
        if (style.only) {
            words.append(BanglaWords.ONLY);
        }
        return words.toString().trim();
    }

    /**
     * Full amount in Bangla words, in the given style, for {@code double} values.
     *
     * @throws IllegalArgumentException if {@code amount} is NaN or infinite
     */
    public static String convertBangla(double amount, WordStyle style) {
        return convertBangla(toBigDecimal(amount), style);
    }

    /**
     * Whole-Taka amount in Bangla words, in the given style.
     */
    public static String convertBangla(long taka, WordStyle style) {
        return convertBangla(BigDecimal.valueOf(taka), style);
    }

    /**
     * Full amount in Bangla words, for {@code double} values.
     *
     * @throws IllegalArgumentException if {@code amount} is NaN or infinite
     */
    public static String convertBangla(double amount) {
        return convertBangla(toBigDecimal(amount));
    }

    /**
     * Whole-Taka amount in Bangla words, e.g. 500 → "পাঁচশত টাকা মাত্র".
     */
    public static String convertBangla(long taka) {
        return convertBangla(BigDecimal.valueOf(taka));
    }

    /**
     * Amount with lakh/crore digit grouping and two decimals, e.g. 1234567.891 → "12,34,567.89".
     *
     * @throws NullPointerException if {@code amount} is null
     */
    public static String format(BigDecimal amount) {
        if (amount == null) {
            throw new NullPointerException("amount");
        }
        BigDecimal rounded = amount.setScale(2, RoundingMode.HALF_UP);
        String plain = rounded.abs().toPlainString();
        int dot = plain.indexOf('.');
        String whole = plain.substring(0, dot);

        StringBuilder grouped = new StringBuilder();
        int len = whole.length();
        if (len <= 3) {
            grouped.append(whole);
        } else {
            String head = whole.substring(0, len - 3);
            int first = head.length() % 2;
            if (first > 0) {
                grouped.append(head, 0, first).append(',');
            }
            for (int i = first; i < head.length(); i += 2) {
                grouped.append(head, i, i + 2).append(',');
            }
            grouped.append(whole.substring(len - 3));
        }
        return (rounded.signum() < 0 ? "-" : "") + grouped + plain.substring(dot);
    }

    /**
     * Lakh/crore grouping for {@code double} values.
     *
     * @throws IllegalArgumentException if {@code amount} is NaN or infinite
     */
    public static String format(double amount) {
        return format(toBigDecimal(amount));
    }

    /**
     * Lakh/crore grouping for whole-Taka amounts, e.g. 1500000 → "15,00,000.00".
     */
    public static String format(long taka) {
        return format(BigDecimal.valueOf(taka));
    }

    /**
     * Lakh/crore grouping in Bangla digits, e.g. 1234567.89 → "১২,৩৪,৫৬৭.৮৯".
     *
     * @throws NullPointerException if {@code amount} is null
     */
    public static String formatBangla(BigDecimal amount) {
        return BanglaWords.digits(format(amount));
    }

    /**
     * Bangla-digit grouping for {@code double} values.
     *
     * @throws IllegalArgumentException if {@code amount} is NaN or infinite
     */
    public static String formatBangla(double amount) {
        return BanglaWords.digits(format(amount));
    }

    /**
     * Bangla-digit grouping for whole-Taka amounts.
     */
    public static String formatBangla(long taka) {
        return BanglaWords.digits(format(taka));
    }

    static BigDecimal toBigDecimal(double amount) {
        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new IllegalArgumentException("Amount must be a finite number: " + amount);
        }
        return BigDecimal.valueOf(amount);
    }

    /**
     * An amount split into sign, whole Taka and Paisa after rounding to two decimals.
     */
    static final class Amount {
        final boolean negative;
        final long taka;
        final int paisa;

        private Amount(boolean negative, long taka, int paisa) {
            this.negative = negative;
            this.taka = taka;
            this.paisa = paisa;
        }

        static Amount of(BigDecimal amount) {
            if (amount == null) {
                throw new NullPointerException("amount");
            }
            BigDecimal abs = amount.setScale(2, RoundingMode.HALF_UP).abs();
            long taka;
            try {
                taka = abs.setScale(0, RoundingMode.DOWN).longValueExact();
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException("Amount out of range: " + amount, e);
            }
            int paisa = abs.movePointRight(2).remainder(BigDecimal.valueOf(100)).intValue();
            boolean negative = amount.signum() < 0 && (taka > 0L || paisa > 0);
            return new Amount(negative, taka, paisa);
        }

        boolean isZero() {
            return taka == 0L && paisa == 0;
        }
    }
}
