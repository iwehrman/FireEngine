# Sets and exports the java path and java executable.
JAVA=${JAVA_HOME}/bin/java
export JAVA
JAVA_RUNTIME_PATH=${JAVA_HOME}/jre/lib/rt.jar
export JAVA_RUNTIME_PATH
JAVA_TOOLS_PATH=${JAVA_HOME}/lib/tools.jar
export JAVA_TOOLS_PATH

# Sets and exports the ant path.
ANT_PATH=lib/ant.jar
export ANT_PATH

# Sets and exports the class path.
CLASSPATH=${CLASSPATH}:${JAVA_TOOLS_PATH}:${JAVA_RUNTIME_PATH}:${ANT_PATH}:
export CLASSPATH

# Starts the build
${JAVA} -mx64m -classpath ${CLASSPATH} -Dant.home=$ANT_HOME org.apache.tools.ant.Main "$@" -buildfile build.xml 