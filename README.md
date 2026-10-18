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
    <version>1.0.0</version>
</dependency>
```

## API

`NumberToBanglaTaka` is in the default package. Requires Java 8 or newer.

| Method | Description |
|---|---|
| `static String convert(double amount)` | Full amount in words, Taka and Paisa, ending in "Only" |
| `String convertTaka(Long taka)` | Whole-Taka part in words |
| `String convertPaisa(int paisa)` | Paisa part in words |

## Publishing a new version

From a local clone of this repository:

```bash
mvn deploy:deploy-file -DgroupId=io.github.oblivion97 -DartifactId=amount-to-bdt -Dversion=1.0.1 -Dpackaging=jar \
  -Dfile=path/to/amount-to-bdt-1.0.1.jar -DgeneratePom=true "-Durl=file:///path/to/mvn-amount-to-bdt"
git add . && git commit -m "Release 1.0.1" && git push
```

Never overwrite a released version. Bump the version instead, because Maven caches downloaded releases forever.
