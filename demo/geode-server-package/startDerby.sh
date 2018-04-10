#!/bin/bash

# -h 0.0.0.0 is for listening on all interfaces; see also:
# http://db.apache.org/derby/docs/10.10/adminguide/cadminnetservsecurity.html
startNetworkServer -h 0.0.0.0 > derby.log 2>&1&
echo $! > derby_pid.txt
echo "Derby Started"
