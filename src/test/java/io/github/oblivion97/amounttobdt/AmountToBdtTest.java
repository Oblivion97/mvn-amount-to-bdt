package io.github.oblivion97.amounttobdt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AmountToBdtTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "0              | Zero Taka Only",
            "0.004          | Zero Taka Only",
            "-0.004         | Zero Taka Only",
            "0.5            | Fifty Paisa Only",
            "0.05           | Five Paisa Only",
            "1              | One Taka Only",
            "19             | Nineteen Taka Only",
            "20             | Twenty Taka Only",
            "21             | Twenty One Taka Only",
            "99.99          | Ninety Nine Taka Ninety Nine Paisa Only",
            "100            | One Hundred Taka Only",
            "101            | One Hundred One Taka Only",
            "999            | Nine Hundred Ninety Nine Taka Only",
            "1000           | One Thousand Taka Only",
            "1001           | One Thousand One Taka Only",
            "12000          | Twelve Thousand Taka Only",
            "99999          | Ninety Nine Thousand Nine Hundred Ninety Nine Taka Only",
            "100000         | One Lakh Taka Only",
            "1050000        | Ten Lakh Fifty Thousand Taka Only",
            "1234567.89     | Twelve Lakh Thirty Four Thousand Five Hundred Sixty Seven Taka Eighty Nine Paisa Only",
            "10000000       | One Crore Taka Only",
            "10001000       | One Crore One Thousand Taka Only",
            "1000000000     | One Hundred Crore Taka Only",
            "-5             | Minus Five Taka Only",
            "-0.25          | Minus Twenty Five Paisa Only",
            "-1234.5        | Minus One Thousand Two Hundred Thirty Four Taka Fifty Paisa Only",
    })
    void convertsAmounts(String amount, String expected) {
        assertEquals(expected, AmountToBdt.convert(new BigDecimal(amount)));
        assertEquals(expected, AmountToBdt.convert(Double.parseDouble(amount)));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "0.125 | Thirteen Paisa Only",
            "2.675 | Two Taka Sixty Eight Paisa Only",
            "1.005 | One Taka One Paisa Only",
            "0.995 | One Taka Only",
            "-2.675 | Minus Two Taka Sixty Eight Paisa Only",
    })
    void roundsHalfUp(double amount, String expected) {
        assertEquals(expected, AmountToBdt.convert(amount));
    }

    @Test
    void convertsWholeTaka() {
        assertEquals("Five Hundred Taka Only", AmountToBdt.convert(500L));
        assertEquals("Five Hundred Taka Only", AmountToBdt.convert(500));
    }

    @Test
    void ignoresDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            for (Locale locale : new Locale[]{Locale.GERMANY, Locale.FRANCE, new Locale("bn", "BD"), new Locale("ar", "SA"),
                    new Locale("hi", "IN")}) {
                Locale.setDefault(locale);
                assertEquals("One Thousand Two Hundred Thirty Four Taka Fifty Six Paisa Only",
                        AmountToBdt.convert(1234.56), locale.toString());
            }
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    void rejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> AmountToBdt.convert(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> AmountToBdt.convert(Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> AmountToBdt.convert(new BigDecimal("1e30")));
        assertThrows(IllegalArgumentException.class, () -> AmountToBdt.convertTaka(Long.MIN_VALUE));
        assertThrows(NullPointerException.class, () -> AmountToBdt.convert((BigDecimal) null));
    }

    @Test
    void convertsLargestLong() {
        // 9,223,372,036,854,775,807 = 92233 crore crore + 72,03,685 crore + 47,75,807
        assertEquals("Ninety Two Thousand Two Hundred Thirty Three Crore Seventy Two Lakh Three Thousand Six Hundred "
                        + "Eighty Five Crore Forty Seven Lakh Seventy Five Thousand Eight Hundred Seven Taka Only",
                AmountToBdt.convert(Long.MAX_VALUE));
    }

    @Test
    void convertsParts() {
        assertEquals("", AmountToBdt.convertTaka(0));
        assertEquals("Minus Twelve Thousand", AmountToBdt.convertTaka(-12000));
        assertEquals("", AmountToBdt.convertPaisa(0));
        assertEquals("Forty Five", AmountToBdt.convertPaisa(45));
        assertEquals("", AmountToBdt.convertPaisa(100));
    }
}
