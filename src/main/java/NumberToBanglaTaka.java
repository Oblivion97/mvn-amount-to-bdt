import io.github.oblivion97.amounttobdt.AmountToBdt;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Legacy entry point in the default package, kept so existing callers (such as compiled Jasper reports) keep working.
 * Java code in a named package cannot import this class; use {@link AmountToBdt} instead.
 */
public class NumberToBanglaTaka {
   public static final String[] units = new String[]{"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
   public static final String[] tens = new String[]{"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

   public static String convert(double n) {
      return AmountToBdt.convert(n);
   }

   public static String convert(BigDecimal n) {
      return AmountToBdt.convert(n);
   }

   public String convertTaka(Long n) {
      return AmountToBdt.convertTaka(n);
   }

   public String convertPaisa(int n) {
      return AmountToBdt.convertPaisa(n);
   }

   public static void main(String[] args) {
      while(true) {
         Scanner scr = new Scanner(System.in);
         double l = scr.nextDouble();
         System.out.println(convert(l));
      }
   }
}
