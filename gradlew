#!/usr/bin/env sh
if command -v gradle >/dev/null 2>&1; then
  gradle "$@"
else
  GRADLE_DIR="$HOME/.gradle-dist/gradle-7.5.1"
  if [ ! -x "$GRADLE_DIR/bin/gradle" ]; then
    mkdir -p "$HOME/.gradle-dist"
    wget -q https://services.gradle.org/distributions/gradle-7.5.1-bin.zip -O /tmp/gradle.zip
    unzip -q /tmp/gradle.zip -d "$HOME/.gradle-dist"
  fi
  PATH="$GRADLE_DIR/bin:$PATH"
  gradle "$@"
fi
