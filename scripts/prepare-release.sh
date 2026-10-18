#!/usr/bin/env bash
# Prepares a release on the current branch: sets the version, builds and tests, and adds the artifacts to this
# repository's Maven layout (the GitHub-hosted repository).
#
# Usage:   scripts/prepare-release.sh 1.4.1
# Maven:   uses `mvn` from PATH, or set MVN=/path/to/mvn
set -euo pipefail

VERSION="${1:-}"
MVN="${MVN:-mvn}"
ARTIFACT=amount-to-bdt
LAYOUT="io/github/oblivion97/$ARTIFACT"

cd "$(dirname "$0")/.."

if [[ ! "$VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "Usage: scripts/prepare-release.sh <major.minor.patch>, e.g. 1.4.1" >&2
  exit 1
fi
if [ "$(git branch --show-current)" = "main" ]; then
  echo "You're on main, which is protected. Create a branch first: git switch -c release/$VERSION" >&2
  exit 1
fi
if [ -d "$LAYOUT/$VERSION" ]; then
  echo "$VERSION is already released ($LAYOUT/$VERSION exists). Pick a new version." >&2
  exit 1
fi

# Set the project version: the first <version> after <artifactId>amount-to-bdt</artifactId>
awk -v v="$VERSION" '
  !done && found && /<version>/ { sub(/<version>[^<]*<\/version>/, "<version>" v "</version>"); done = 1 }
  /<artifactId>amount-to-bdt<\/artifactId>/ { found = 1 }
  { print }
' pom.xml > pom.xml.tmp && mv pom.xml.tmp pom.xml
echo "pom.xml version set to $VERSION"

"$MVN" -B -ntp clean verify

# On Git Bash (Windows), turn /c/Users/... into C:/Users/... so Maven gets a valid file URL
if command -v cygpath > /dev/null; then REPO_URL="file:///$(cygpath -m "$(pwd)")"; else REPO_URL="file://$(pwd)"; fi

"$MVN" -B -ntp -q deploy:deploy-file -DpomFile=pom.xml \
  -Dfile="target/$ARTIFACT-$VERSION.jar" \
  -Dsources="target/$ARTIFACT-$VERSION-sources.jar" \
  -Durl="$REPO_URL"
rm -rf target
echo "Added $LAYOUT/$VERSION to the GitHub-hosted Maven repository"

if ! grep -q "^| $VERSION |" README.md; then
  echo
  echo "Reminder: add a '| $VERSION | ... |' row to the changelog in README.md and update the dependency example."
fi

cat <<EOF

Next:
  git add -A && git commit -m "Release $VERSION" && git push -u origin $(git branch --show-current)
  Open a PR, wait for CI, merge it. Then publish to Maven Central:
  git switch main && git pull && git tag v$VERSION && git push origin v$VERSION
EOF
