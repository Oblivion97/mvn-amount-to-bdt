package io.github.oblivion97.amounttobdt;

/**
 * How {@link AmountToBdt#convert(java.math.BigDecimal, WordStyle)} writes an amount. Immutable and thread-safe: each
 * {@code with...} method returns a new style.
 *
 * <pre>
 * WordStyle cheque = WordStyle.DEFAULT.withCurrencyFirst().withAnd().withUpperCase();
 * AmountToBdt.convert(new BigDecimal("1250.50"), cheque);
 * // "TAKA ONE THOUSAND TWO HUNDRED AND FIFTY AND PAISA FIFTY ONLY"
 * </pre>
 */
public final class WordStyle {

    /**
     * The library's standard output: "One Thousand Two Hundred Fifty Taka Fifty Paisa Only".
     */
    public static final WordStyle DEFAULT = new WordStyle(false, true, "Taka", "Paisa", false, false);

    final boolean upperCase;
    final boolean only;
    final String currency;
    final String paisa;
    final boolean and;
    final boolean currencyFirst;

    private WordStyle(boolean upperCase, boolean only, String currency, String paisa, boolean and,
                      boolean currencyFirst) {
        this.upperCase = upperCase;
        this.only = only;
        this.currency = currency;
        this.paisa = paisa;
        this.and = and;
        this.currencyFirst = currencyFirst;
    }

    /**
     * ALL CAPS, as often required on cheques.
     */
    public WordStyle withUpperCase() {
        return new WordStyle(true, only, currency, paisa, and, currencyFirst);
    }

    /**
     * Leaves out the trailing "Only" (or "মাত্র" in Bangla).
     */
    public WordStyle withoutOnly() {
        return new WordStyle(upperCase, false, currency, paisa, and, currencyFirst);
    }

    /**
     * Currency word instead of "Taka", e.g. "BDT" or "Tk.".
     */
    public WordStyle withCurrency(String currency) {
        return new WordStyle(upperCase, only, requireWord(currency, "currency"), paisa, and, currencyFirst);
    }

    /**
     * Word for the fractional unit instead of "Paisa", e.g. "Poisha".
     */
    public WordStyle withPaisaWord(String paisa) {
        return new WordStyle(upperCase, only, currency, requireWord(paisa, "paisa"), and, currencyFirst);
    }

    /**
     * Inserts "and" before the last two digits: "One Hundred and Five", "One Thousand and Fifty".
     */
    public WordStyle withAnd() {
        return new WordStyle(upperCase, only, currency, paisa, true, currencyFirst);
    }

    /**
     * Currency words before the numbers: "Taka One Thousand and Paisa Fifty Only".
     */
    public WordStyle withCurrencyFirst() {
        return new WordStyle(upperCase, only, currency, paisa, and, true);
    }

    private static String requireWord(String word, String name) {
        if (word == null || word.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return word.trim();
    }
}
