#! /bin/bash

RULESET="$1"
echo "Checking with ruleset: ${RULESET:=healthomics}"

if [ "$RULESET" == "config" ]; then
  INCLUDES="**/*.config"
elif [ "$RULESET" == "general" ] || [ "$RULESET" == "healthomics" ]; then
  INCLUDES="**/*.nf"
else
  # If RULESET is not "config", "healthomics", or "general" then exit and show an error message
  echo "Error: You need to specify 'general', 'healthomics', or 'config' as the ruleset argument."
  exit 1
fi

# Run the Java command with the appropriate INCLUDES
java -Dorg.slf4j.simpleLogger.defaultLogLevel=error \
  -classpath linter-rules.jar:CodeNarc-3.3.0-all.jar:slf4j-api-1.7.36.jar:slf4j-simple-1.7.36.jar \
  org.codenarc.CodeNarc \
  -report=text:stdout \
  -rulesetfiles=rulesets/"${RULESET}".xml \
  -basedir=/data \
  -includes="${INCLUDES}"
