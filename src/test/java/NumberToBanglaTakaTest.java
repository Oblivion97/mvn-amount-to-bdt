import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The legacy default-package entry point must keep its behaviour for existing callers such as Jasper reports.
 */
class NumberToBanglaTakaTest {

    @Test
    void delegatesToAmountToBdt() {
        NumberToBanglaTaka legacy = new NumberToBanglaTaka();
        assertEquals("Twelve Thousand Taka Only", NumberToBanglaTaka.convert(12000.0));
        assertEquals("One Thousand Taka Fifty Paisa Only", NumberToBanglaTaka.convert(new BigDecimal("1000.50")));
        assertEquals("Twelve Lakh", legacy.convertTaka(1200000L));
        assertEquals("Ninety Nine", legacy.convertPaisa(99));
    }

    @Test
    void keepsPublicWordTables() {
        assertEquals(20, NumberToBanglaTaka.units.length);
        assertEquals("Nineteen", NumberToBanglaTaka.units[19]);
        assertEquals(10, NumberToBanglaTaka.tens.length);
        assertEquals("Ninety", NumberToBanglaTaka.tens[9]);
    }
}
