#!/bin/bash

export DERBY_INSTALL=/huge/r/bin/db-derby-10.14.1.0-bin
export CLASSPATH=$DERBY_INSTALL/lib/derbytools.jar:$DERBY_INSTALL/lib/derbynet.jar:.

# -h 0.0.0.0 is for listening on all interfaces; see also:
# http://db.apache.org/derby/docs/10.10/adminguide/cadminnetservsecurity.html
java -jar $DERBY_INSTALL/lib/derbyrun.jar server start -h 0.0.0.0 > derby.log 2>&1&
echo $! > derby_pid.txt
echo "Derby Started"
