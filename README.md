# mvn-amount-to-bdt

A small Java library that converts a numeric amount into words in Bangladeshi Taka, using the
lakh/crore numbering system. It's free for anyone to use in any project.

```java
NumberToBanglaTaka.convert(1234567.89);
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
    <version>1.0.1</version>
</dependency>
```

## Changelog

| Version | Changes |
|---|---|
| 1.0.1 | Zero now returns "Zero Taka Only" (was "Only"). Fixed a double space after "Thousand" for round thousands, e.g. 12000 → "Twelve Thousand Taka Only". |
| 1.0.0 | Initial release. |

## API

`NumberToBanglaTaka` is in the default package. Requires Java 8 or newer.

| Method | Description |
|---|---|
| `static String convert(double amount)` | Full amount in words, Taka and Paisa, ending in "Only" |
| `String convertTaka(Long taka)` | Whole-Taka part in words |
| `String convertPaisa(int paisa)` | Paisa part in words |

## Repository layout

| Path | Contents |
|---|---|
| `src/main/java/` | Library source |
| `pom.xml` | Builds the library (`mvn package` → `target/`) |
| `io/github/oblivion97/amount-to-bdt/` | Published Maven artifacts (jar, sources jar, pom, checksums) |

## Contributing

`main` is protected: changes land only through pull requests. Create a branch, push it, and open a PR.

## Publishing a new version

1. Bump `<version>` in `pom.xml` (for example to `1.0.2`) and build with `mvn package`.
2. Deploy the build into this repository's Maven layout:
   ```bash
   mvn deploy:deploy-file -DpomFile=pom.xml -Dfile=target/amount-to-bdt-1.0.2.jar \
     -Dsources=target/amount-to-bdt-1.0.2-sources.jar "-Durl=file://$(pwd)"
   ```
3. Commit on a branch, open a PR, and merge it into `main`.

Never overwrite a released version. Bump the version instead, because Maven caches downloaded releases forever.
