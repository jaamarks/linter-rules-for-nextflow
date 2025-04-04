#! /bin/bash

RULESET="$1"
RUNTYPE="$2"
echo "Checking with ruleset: ${RULESET:=healthomics}"
echo "Running on type: ${RUNTYPE:=script}"

# Check if RUNTYPE is set to "script" or "config"
if [ "$RUNTYPE" == "script" ]; then
  INCLUDES="**/*.nf"
elif [ "$RUNTYPE" == "config" ]; then
  INCLUDES="**/*.config"
else
  # If RUN_TYPE is not "script" or "config", exit and show an error message
  echo "Error: You need to specify either 'script' or 'config' as the argument."
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
