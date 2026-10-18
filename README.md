# mvn-amount-to-bdt

[![CI](https://github.com/Oblivion97/mvn-amount-to-bdt/actions/workflows/ci.yml/badge.svg)](https://github.com/Oblivion97/mvn-amount-to-bdt/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.oblivion97/amount-to-bdt)](https://central.sonatype.com/artifact/io.github.oblivion97/amount-to-bdt)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A small Java library that converts a numeric amount into words in Bangladeshi Taka, using the
lakh/crore numbering system. Free to use in any project under the [MIT License](LICENSE).

```java
import io.github.oblivion97.amounttobdt.AmountToBdt;

BigDecimal amount = new BigDecimal("1234567.89");

AmountToBdt.convert(amount);
// "Twelve Lakh Thirty Four Thousand Five Hundred Sixty Seven Taka Eighty Nine Paisa Only"

AmountToBdt.convertBangla(amount);
// "বারো লক্ষ চৌত্রিশ হাজার পাঁচশত সাতষট্টি টাকা ঊননব্বই পয়সা মাত্র"

AmountToBdt.format(amount);       // "12,34,567.89"
AmountToBdt.formatBangla(amount); // "১২,৩৪,৫৬৭.৮৯"
```

## Use it

The library is on [Maven Central](https://central.sonatype.com/artifact/io.github.oblivion97/amount-to-bdt). Add the
dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.oblivion97</groupId>
    <artifactId>amount-to-bdt</artifactId>
    <version>1.4.0</version>
</dependency>
```

Gradle: `implementation 'io.github.oblivion97:amount-to-bdt:1.4.0'`

**Alternative:** this repository is also a Maven repository, served from GitHub raw URLs. It has every version,
including 1.0.0–1.3.0, which aren't on Maven Central. To use it, add:

```xml
<repositories>
    <repository>
        <id>oblivion-github</id>
        <url>https://raw.githubusercontent.com/Oblivion97/mvn-amount-to-bdt/main/</url>
        <snapshots><enabled>false</enabled></snapshots>
    </repository>
</repositories>
```

## API

Requires Java 8 or newer. Use `io.github.oblivion97.amounttobdt.AmountToBdt`; all methods are static and thread-safe.

| Method | Description |
|---|---|
| `convert(BigDecimal amount)` | Full amount in words, Taka and Paisa, ending in "Only". Preferred for money. |
| `convert(double amount)` | Same, for `double` values |
| `convert(long taka)` | Same, for whole-Taka amounts |
| `convertTaka(long taka)` | Whole-Taka part in words |
| `convertPaisa(int paisa)` | Paisa part (0–99) in words |
| `convertBangla(BigDecimal \| double \| long)` | Full amount in Bangla words, ending in "মাত্র" |
| `format(BigDecimal \| double \| long)` | Lakh/crore digit grouping with two decimals: `12,34,567.89` |
| `formatBangla(BigDecimal \| double \| long)` | Same grouping in Bangla digits: `১২,৩৪,৫৬৭.৮৯` |

- Bangla words use Bangla Academy spelling (e.g. ষোলো, ঊনত্রিশ), "শত" for hundreds (দুইশত), and "ঋণাত্মক" for
  negative amounts.
- Amounts are rounded to two decimals, **half up**: `2.675` → "Two Taka Sixty Eight Paisa Only".
- Output doesn't depend on the server's default locale.
- `NaN`, infinity and amounts beyond about 9.2 × 10¹⁸ Taka throw `IllegalArgumentException`, and `null` throws
  `NullPointerException`.

`NumberToBanglaTaka` (default package) has the same methods and is kept for existing callers, such as compiled
Jasper reports. Java code in a named package can't import a default-package class, so new code should use `AmountToBdt`.

## Styles

Pass a `WordStyle` to `convert` (or `convertBangla`) to change how the words are written. Styles are immutable;
combine them by chaining.

```java
BigDecimal amount = new BigDecimal("1250.50");

AmountToBdt.convert(amount);
// "One Thousand Two Hundred Fifty Taka Fifty Paisa Only"

WordStyle cheque = WordStyle.DEFAULT.withCurrencyFirst().withAnd().withUpperCase();
AmountToBdt.convert(amount, cheque);
// "TAKA ONE THOUSAND TWO HUNDRED AND FIFTY AND PAISA FIFTY ONLY"
```

| Method | Effect | Example (1250.50) |
|---|---|---|
| `withUpperCase()` | ALL CAPS | `ONE THOUSAND TWO HUNDRED FIFTY TAKA FIFTY PAISA ONLY` |
| `withoutOnly()` | No trailing "Only" / "মাত্র" | `One Thousand Two Hundred Fifty Taka Fifty Paisa` |
| `withCurrency("BDT")` | Currency word | `One Thousand Two Hundred Fifty BDT Fifty Paisa Only` |
| `withPaisaWord("Poisha")` | Fractional unit word | `One Thousand Two Hundred Fifty Taka Fifty Poisha Only` |
| `withAnd()` | "and" before the last two digits | `One Thousand Two Hundred and Fifty Taka Fifty Paisa Only` |
| `withCurrencyFirst()` | Currency words before the numbers | `Taka One Thousand Two Hundred Fifty and Paisa Fifty Only` |

For Bangla output, only `withoutOnly()` applies.

## Changelog

| Version | Changes |
|---|---|
| 1.4.0 | `WordStyle` for upper case, no "Only", custom currency and paisa words, "and", and currency-first (cheque) wording. Default output is unchanged. The pom now has the metadata Maven Central requires, plus a `central-release` profile (see [RELEASING.md](RELEASING.md)). A javadoc jar is published. |
| 1.3.0 | Bangla script output with `convertBangla`. Lakh/crore number formatting with `format` (`12,34,567.89`) and `formatBangla` (`১২,৩৪,৫৬৭.৮৯`). English output is unchanged. |
| 1.2.0 | Fixed a crash on servers whose default locale uses a comma decimal separator (e.g. German, French). Rounding is now half up (`0.125` → 13 paisa, was 12). Added `convert(BigDecimal)` and `convert(long)`. Invalid input (`NaN`, infinity, out of range) throws `IllegalArgumentException`. MIT license, unit tests, CI, and an `Automatic-Module-Name` for Java modules. |
| 1.1.0 | New packaged API `io.github.oblivion97.amounttobdt.AmountToBdt`, usable from any package. `NumberToBanglaTaka` stays for existing callers and delegates to it. Output is unchanged from 1.0.1. |
| 1.0.1 | Zero now returns "Zero Taka Only" (was "Only"). Fixed a double space after "Thousand" for round thousands, e.g. 12000 → "Twelve Thousand Taka Only". |
| 1.0.0 | Initial release. |

## Repository layout

| Path | Contents |
|---|---|
| `src/main/java/`, `src/test/java/` | Library source and unit tests |
| `pom.xml` | Builds and tests the library (`mvn verify` → `target/`) |
| `io/github/oblivion97/amount-to-bdt/` | Published Maven artifacts (jar, sources, javadoc, pom, checksums) |

## Contributing

`main` is protected: changes land only through pull requests, and CI (Java 8, 11, 17 and 21) must pass. Create a
branch, push it, and open a PR. See [RELEASING.md](RELEASING.md) for publishing a new version, including to Maven
Central.
