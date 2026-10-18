package io.github.oblivion97.amounttobdt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BanglaTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "0          | শূন্য টাকা মাত্র",
            "0.004      | শূন্য টাকা মাত্র",
            "1          | এক টাকা মাত্র",
            "0.5        | পঞ্চাশ পয়সা মাত্র",
            "16         | ষোলো টাকা মাত্র",
            "29         | ঊনত্রিশ টাকা মাত্র",
            "99.99      | নিরানব্বই টাকা নিরানব্বই পয়সা মাত্র",
            "100        | একশত টাকা মাত্র",
            "105        | একশত পাঁচ টাকা মাত্র",
            "500        | পাঁচশত টাকা মাত্র",
            "1000       | এক হাজার টাকা মাত্র",
            "1234.56    | এক হাজার দুইশত চৌত্রিশ টাকা ছাপ্পান্ন পয়সা মাত্র",
            "12000      | বারো হাজার টাকা মাত্র",
            "100000     | এক লক্ষ টাকা মাত্র",
            "1234567.89 | বারো লক্ষ চৌত্রিশ হাজার পাঁচশত সাতষট্টি টাকা ঊননব্বই পয়সা মাত্র",
            "10000000   | এক কোটি টাকা মাত্র",
            "52500000   | পাঁচ কোটি পঁচিশ লক্ষ টাকা মাত্র",
            "1000000000 | একশত কোটি টাকা মাত্র",
            "-5         | ঋণাত্মক পাঁচ টাকা মাত্র",
            "-0.25      | ঋণাত্মক পঁচিশ পয়সা মাত্র",
            "2.675      | দুই টাকা আটষট্টি পয়সা মাত্র",
    })
    void convertsToBanglaWords(String amount, String expected) {
        assertEquals(expected, AmountToBdt.convertBangla(new BigDecimal(amount)));
        assertEquals(expected, AmountToBdt.convertBangla(Double.parseDouble(amount)));
    }

    @Test
    void hasDistinctWordForEveryNumberUnder100() {
        Set<String> seen = new HashSet<>();
        for (int n = 1; n < 100; n++) {
            String word = BanglaWords.under100(n);
            assertFalse(word.isEmpty(), "missing word for " + n);
            assertFalse(word.contains(" "), "word for " + n + " should be a single word");
            assertTrue(seen.add(word), "duplicate word for " + n + ": " + word);
        }
    }

    @Test
    void convertsWholeTaka() {
        assertEquals("পাঁচশত টাকা মাত্র", AmountToBdt.convertBangla(500L));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "0            | 0.00",
            "5            | 5.00",
            "999          | 999.00",
            "1000         | 1,000.00",
            "12345        | 12,345.00",
            "100000       | 1,00,000.00",
            "1234567.891  | 12,34,567.89",
            "10000000     | 1,00,00,000.00",
            "123456789.5  | 12,34,56,789.50",
            "-1234567.5   | -12,34,567.50",
            "-0.001       | 0.00",
            "0.125        | 0.13",
    })
    void formatsWithLakhGrouping(String amount, String expected) {
        assertEquals(expected, AmountToBdt.format(new BigDecimal(amount)));
        assertEquals(expected, AmountToBdt.format(Double.parseDouble(amount)));
    }

    @Test
    void formatsWithBanglaDigits() {
        assertEquals("১২,৩৪,৫৬৭.৮৯", AmountToBdt.formatBangla(new BigDecimal("1234567.89")));
        assertEquals("১৫,০০,০০০.০০", AmountToBdt.formatBangla(1500000L));
        assertEquals("-১,০০০.৫০", AmountToBdt.formatBangla(-1000.5));
        assertEquals("15,00,000.00", AmountToBdt.format(1500000L));
    }

    @Test
    void rejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> AmountToBdt.convertBangla(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> AmountToBdt.format(Double.NEGATIVE_INFINITY));
        assertThrows(NullPointerException.class, () -> AmountToBdt.format((BigDecimal) null));
        assertThrows(NullPointerException.class, () -> AmountToBdt.convertBangla((BigDecimal) null));
    }
}
