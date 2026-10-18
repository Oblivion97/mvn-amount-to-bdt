# Releasing

A release has two parts:

1. **A pull request** that sets the version and adds the artifacts to this repository's Maven layout (the
   GitHub-hosted repository). `main` is protected, and CI must pass.
2. **A tag** (`v1.4.1`). Pushing it makes the [Release workflow](.github/workflows/release.yml) build, test, sign and
   publish to Maven Central, then create a GitHub Release with the jars.

Never reuse a version: Maven caches releases forever, and Maven Central versions can't be changed or deleted.

| Change | Version |
|---|---|
| Bug fix, same API | 1.4.0 → 1.4.1 |
| New feature, existing code still works | 1.4.1 → 1.5.0 |
| Breaking change (removed or renamed method) | 1.5.0 → 2.0.0 |

## Each release

In Git Bash:

```bash
git switch main && git pull
git switch -c release/1.4.1

# ...fix the bug, add a test...

# Sets the version in pom.xml, runs the tests, and adds io/github/oblivion97/amount-to-bdt/1.4.1/
scripts/prepare-release.sh 1.4.1
```

Then:

1. In `README.md`, add a `1.4.1` row to the changelog and update the version in the dependency example.
2. Commit, push, open a PR, wait for CI, and merge:
   ```bash
   git add -A && git commit -m "Release 1.4.1" && git push -u origin release/1.4.1
   ```
3. Tag the merged commit to publish to Maven Central:
   ```bash
   git switch main && git pull
   git tag v1.4.1 && git push origin v1.4.1
   ```
4. Watch the **Release** run in the *Actions* tab. It checks that the tag matches the pom version and that the
   GitHub-hosted files exist, publishes to Central and waits until Central reports it as published (usually a few
   minutes), then creates the GitHub Release. The version is searchable on
   [central.sonatype.com](https://central.sonatype.com/artifact/io.github.oblivion97/amount-to-bdt) shortly after.

If the workflow fails before "Publish to Maven Central", nothing was published. Fix the problem, delete the tag
(`git push origin :v1.4.1 && git tag -d v1.4.1`), and tag again.

`prepare-release.sh` uses `mvn` from PATH. To use another Maven, set `MVN`, for example IntelliJ's bundled one:

```bash
export JAVA_HOME="/c/Program Files/JetBrains/IntelliJ IDEA 2026.1.2/jbr"
export MVN="/c/Program Files/JetBrains/IntelliJ IDEA 2026.1.2/plugins/maven-plugin/lib/maven3/bin/mvn"
```

## One-time setup for the Release workflow

Add four secrets under **Settings → Secrets and variables → Actions → New repository secret**:

| Secret | Value |
|---|---|
| `CENTRAL_USERNAME` | Central token username (from *central.sonatype.com → View Account → Generate User Token*) |
| `CENTRAL_PASSWORD` | Central token password |
| `GPG_PRIVATE_KEY` | The whole exported private key, including the `-----BEGIN PGP PRIVATE KEY BLOCK-----` and `-----END ...-----` lines |
| `GPG_PASSPHRASE` | The key's passphrase |

Export the private key in Git Bash with
`gpg --export-secret-keys --armor KEY_ID > signing-key.asc`, paste the file's contents into the secret, then delete
the file. Secrets are encrypted, and GitHub hides them in logs.

**Test the setup:** in *Actions → Release → Run workflow*, run it on `main`. A manual run builds, tests and signs
everything with the secrets but uploads nothing. If it passes, tag releases will work.

The signing key must stay published on a keyserver (`gpg --keyserver hkps://keyserver.ubuntu.com --send-keys KEY_ID`)
so Central can verify the signatures.

## Publishing from your own machine (fallback)

If GitHub Actions is unavailable, you can publish locally. You need the Central token in `~/.m2/settings.xml` (the
`<settings><servers>` wrapper is required, otherwise you get 401 Unauthorized):

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>TOKEN_USERNAME</username>
      <password>TOKEN_PASSWORD</password>
    </server>
  </servers>
</settings>
```

Then, in Git Bash, on the merged `main`:

```bash
echo test | gpg --local-user KEY_ID --sign > /dev/null && echo cached   # caches the passphrase for ~10 minutes
mvn -P central-release clean deploy
```

Finally, on [central.sonatype.com](https://central.sonatype.com) go to *Publish → Deployments* and click **Publish**
once the deployment is **VALIDATED**. Local uploads aren't published automatically, so you can check them first.
