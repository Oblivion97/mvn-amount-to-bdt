package io.github.oblivion97.amounttobdt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WordStyleTest {

    private static final BigDecimal AMOUNT = new BigDecimal("1250.50");

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.5", "1", "105", "1250.50", "12000", "-5", "-0.25", "1234567.89"})
    void defaultStyleMatchesPlainConvert(String amount) {
        BigDecimal value = new BigDecimal(amount);
        assertEquals(AmountToBdt.convert(value), AmountToBdt.convert(value, WordStyle.DEFAULT));
        assertEquals(AmountToBdt.convertBangla(value), AmountToBdt.convertBangla(value, WordStyle.DEFAULT));
    }

    @Test
    void upperCase() {
        assertEquals("ONE THOUSAND TWO HUNDRED FIFTY TAKA FIFTY PAISA ONLY",
                AmountToBdt.convert(AMOUNT, WordStyle.DEFAULT.withUpperCase()));
    }

    @Test
    void upperCaseIgnoresDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("tr", "TR")); // Turkish upper-cases "i" to a dotted "İ"
            assertEquals("FIFTY PAISA ONLY", AmountToBdt.convert(0.5, WordStyle.DEFAULT.withUpperCase()));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    void withoutOnly() {
        assertEquals("One Thousand Two Hundred Fifty Taka Fifty Paisa",
                AmountToBdt.convert(AMOUNT, WordStyle.DEFAULT.withoutOnly()));
        assertEquals("এক হাজার দুইশত পঞ্চাশ টাকা পঞ্চাশ পয়সা",
                AmountToBdt.convertBangla(AMOUNT, WordStyle.DEFAULT.withoutOnly()));
        assertEquals("শূন্য টাকা", AmountToBdt.convertBangla(0L, WordStyle.DEFAULT.withoutOnly()));
    }

    @Test
    void customWords() {
        assertEquals("One Thousand Two Hundred Fifty BDT Fifty Poisha Only",
                AmountToBdt.convert(AMOUNT, WordStyle.DEFAULT.withCurrency("BDT").withPaisaWord("Poisha")));
    }

    @Test
    void withAnd() {
        WordStyle and = WordStyle.DEFAULT.withAnd();
        assertEquals("One Hundred and Five Taka Only", AmountToBdt.convert(105L, and));
        assertEquals("One Thousand and Fifty Taka Only", AmountToBdt.convert(1050L, and));
        assertEquals("One Lakh Twenty Thousand Five Hundred and Fifty Taka Only", AmountToBdt.convert(120550L, and));
        assertEquals("One Thousand Two Hundred Taka Only", AmountToBdt.convert(1200L, and));
        assertEquals("Ninety Nine Taka Only", AmountToBdt.convert(99L, and));
    }

    @Test
    void currencyFirst() {
        WordStyle first = WordStyle.DEFAULT.withCurrencyFirst();
        assertEquals("Taka One Thousand Two Hundred Fifty and Paisa Fifty Only", AmountToBdt.convert(AMOUNT, first));
        assertEquals("Taka Five Hundred Only", AmountToBdt.convert(500L, first));
        assertEquals("Paisa Fifty Only", AmountToBdt.convert(0.5, first));
        assertEquals("Taka Zero Only", AmountToBdt.convert(0L, first));
        assertEquals("Minus Taka Five Only", AmountToBdt.convert(-5L, first));
    }

    @Test
    void chequeStyle() {
        WordStyle cheque = WordStyle.DEFAULT.withCurrencyFirst().withAnd().withUpperCase();
        assertEquals("TAKA ONE THOUSAND TWO HUNDRED AND FIFTY AND PAISA FIFTY ONLY", AmountToBdt.convert(AMOUNT, cheque));
    }

    @Test
    void stylesAreImmutable() {
        WordStyle base = WordStyle.DEFAULT;
        base.withUpperCase().withoutOnly().withAnd().withCurrencyFirst().withCurrency("BDT");
        assertEquals("Five Hundred Taka Only", AmountToBdt.convert(500L, base));
    }

    @Test
    void rejectsInvalidStyle() {
        assertThrows(IllegalArgumentException.class, () -> WordStyle.DEFAULT.withCurrency(" "));
        assertThrows(IllegalArgumentException.class, () -> WordStyle.DEFAULT.withPaisaWord(null));
        assertThrows(NullPointerException.class, () -> AmountToBdt.convert(AMOUNT, null));
        assertThrows(NullPointerException.class, () -> AmountToBdt.convertBangla(AMOUNT, null));
    }
}
