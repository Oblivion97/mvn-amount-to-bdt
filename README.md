# mvn-amount-to-bdt

[![CI](https://github.com/Oblivion97/mvn-amount-to-bdt/actions/workflows/ci.yml/badge.svg)](https://github.com/Oblivion97/mvn-amount-to-bdt/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A small Java library that converts a numeric amount into words in Bangladeshi Taka, using the
lakh/crore numbering system. Free to use in any project under the [MIT License](LICENSE).

```java
import io.github.oblivion97.amounttobdt.AmountToBdt;

AmountToBdt.convert(new BigDecimal("1234567.89"));
// "Twelve Lakh Thirty Four Thousand Five Hundred Sixty Seven Taka Eighty Nine Paisa Only"
```

This repository is also a Maven repository. The jar is served straight from GitHub raw URLs, so Maven downloads it
like any other dependency, with no token or manual install.

## Use it

Add the repository and the dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>oblivion-github</id>
        <url>https://raw.githubusercontent.com/Oblivion97/mvn-amount-to-bdt/main/</url>
        <snapshots><enabled>false</enabled></snapshots>
    </repository>
</repositories>

<dependency>
    <groupId>io.github.oblivion97</groupId>
    <artifactId>amount-to-bdt</artifactId>
    <version>1.2.0</version>
</dependency>
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

- Amounts are rounded to two decimals, **half up**: `2.675` → "Two Taka Sixty Eight Paisa Only".
- Output doesn't depend on the server's default locale.
- `NaN`, infinity and amounts beyond about 9.2 × 10¹⁸ Taka throw `IllegalArgumentException`, and `null` throws
  `NullPointerException`.

`NumberToBanglaTaka` (default package) has the same methods and is kept for existing callers, such as compiled
Jasper reports. Java code in a named package can't import a default-package class, so new code should use `AmountToBdt`.

## Changelog

| Version | Changes |
|---|---|
| 1.2.0 | Fixed a crash on servers whose default locale uses a comma decimal separator (e.g. German, French). Rounding is now half up (`0.125` → 13 paisa, was 12). Added `convert(BigDecimal)` and `convert(long)`. Invalid input (`NaN`, infinity, out of range) throws `IllegalArgumentException`. MIT license, unit tests, CI, and an `Automatic-Module-Name` for Java modules. |
| 1.1.0 | New packaged API `io.github.oblivion97.amounttobdt.AmountToBdt`, usable from any package. `NumberToBanglaTaka` stays for existing callers and delegates to it. Output is unchanged from 1.0.1. |
| 1.0.1 | Zero now returns "Zero Taka Only" (was "Only"). Fixed a double space after "Thousand" for round thousands, e.g. 12000 → "Twelve Thousand Taka Only". |
| 1.0.0 | Initial release. |

## Repository layout

| Path | Contents |
|---|---|
| `src/main/java/`, `src/test/java/` | Library source and unit tests |
| `pom.xml` | Builds and tests the library (`mvn verify` → `target/`) |
| `io/github/oblivion97/amount-to-bdt/` | Published Maven artifacts (jar, sources jar, pom, checksums) |

## Contributing

`main` is protected: changes land only through pull requests, and CI (Java 8, 11, 17 and 21) must pass. Create a
branch, push it, and open a PR.

## Publishing a new version

1. Bump `<version>` in `pom.xml` (for example to `1.2.1`) and run `mvn verify`.
2. Deploy the build into this repository's Maven layout:
   ```bash
   mvn deploy:deploy-file -DpomFile=pom.xml -Dfile=target/amount-to-bdt-1.2.1.jar \
     -Dsources=target/amount-to-bdt-1.2.1-sources.jar "-Durl=file://$(pwd)"
   ```
3. Commit on a branch, open a PR, and merge it into `main` once CI passes.

Never overwrite a released version. Bump the version instead, because Maven caches downloaded releases forever.
