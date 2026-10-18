package io.github.oblivion97.amounttobdt;

/**
 * Bangla number words (Bangla Academy spelling). Numbers 0–99 are irregular in Bangla, so each has its own word.
 */
final class BanglaWords {

    static final String ZERO = "শূন্য";
    static final String HUNDRED = "শত";
    static final String THOUSAND = "হাজার";
    static final String LAKH = "লক্ষ";
    static final String CRORE = "কোটি";
    static final String TAKA = "টাকা";
    static final String PAISA = "পয়সা";
    static final String ONLY = "মাত্র";
    static final String MINUS = "ঋণাত্মক";

    private static final String[] UNDER_100 = {
            "", "এক", "দুই", "তিন", "চার", "পাঁচ", "ছয়", "সাত", "আট", "নয়",
            "দশ", "এগারো", "বারো", "তেরো", "চৌদ্দ", "পনেরো", "ষোলো", "সতেরো", "আঠারো", "উনিশ",
            "বিশ", "একুশ", "বাইশ", "তেইশ", "চব্বিশ", "পঁচিশ", "ছাব্বিশ", "সাতাশ", "আটাশ", "ঊনত্রিশ",
            "ত্রিশ", "একত্রিশ", "বত্রিশ", "তেত্রিশ", "চৌত্রিশ", "পঁয়ত্রিশ", "ছত্রিশ", "সাঁইত্রিশ", "আটত্রিশ", "ঊনচল্লিশ",
            "চল্লিশ", "একচল্লিশ", "বিয়াল্লিশ", "তেতাল্লিশ", "চুয়াল্লিশ", "পঁয়তাল্লিশ", "ছেচল্লিশ", "সাতচল্লিশ", "আটচল্লিশ", "ঊনপঞ্চাশ",
            "পঞ্চাশ", "একান্ন", "বায়ান্ন", "তিপ্পান্ন", "চুয়ান্ন", "পঞ্চান্ন", "ছাপ্পান্ন", "সাতান্ন", "আটান্ন", "ঊনষাট",
            "ষাট", "একষট্টি", "বাষট্টি", "তেষট্টি", "চৌষট্টি", "পঁয়ষট্টি", "ছেষট্টি", "সাতষট্টি", "আটষট্টি", "ঊনসত্তর",
            "সত্তর", "একাত্তর", "বাহাত্তর", "তিয়াত্তর", "চুয়াত্তর", "পঁচাত্তর", "ছিয়াত্তর", "সাতাত্তর", "আটাত্তর", "ঊনআশি",
            "আশি", "একাশি", "বিরাশি", "তিরাশি", "চুরাশি", "পঁচাশি", "ছিয়াশি", "সাতাশি", "আটাশি", "ঊননব্বই",
            "নব্বই", "একানব্বই", "বিরানব্বই", "তিরানব্বই", "চুরানব্বই", "পঁচানব্বই", "ছিয়ানব্বই", "সাতানব্বই", "আটানব্বই", "নিরানব্বই",
    };

    private static final char[] DIGITS = {'০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯'};

    private BanglaWords() {
    }

    /**
     * Words for 0–99. Zero returns an empty string.
     */
    static String under100(int n) {
        return UNDER_100[n];
    }

    /**
     * Whole number in Bangla words, lakh/crore system. Zero returns an empty string.
     */
    static String words(long n) {
        if (n == Long.MIN_VALUE) {
            throw new IllegalArgumentException("Amount out of range: " + n);
        } else if (n < 0L) {
            return MINUS + " " + words(-n);
        } else if (n < 100L) {
            return UNDER_100[(int) n];
        } else if (n < 1000L) {
            return join(UNDER_100[(int) n / 100] + HUNDRED, words(n % 100L));
        } else if (n < 100000L) {
            return join(words(n / 1000L) + " " + THOUSAND, words(n % 1000L));
        } else if (n < 10000000L) {
            return join(words(n / 100000L) + " " + LAKH, words(n % 100000L));
        } else {
            return join(words(n / 10000000L) + " " + CRORE, words(n % 10000000L));
        }
    }

    /**
     * Replaces ASCII digits with Bangla digits; other characters are kept.
     */
    static String digits(String s) {
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            out.append(c >= '0' && c <= '9' ? DIGITS[c - '0'] : c);
        }
        return out.toString();
    }

    private static String join(String head, String tail) {
        return tail.isEmpty() ? head : head + " " + tail;
    }
}
