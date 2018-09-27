# TOMCAT_HOME/bin/catalina.sh jpda start
export JPDA_OPTS="-agentlib:jdwp=transport=dt_socket,address=48000,server=y,suspend=n"
