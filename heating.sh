#!/usr/bin/env bash
java \
-cp "/home/pi/lightboard/lib/*:/home/pi/heatingsystem/lib/*:/home/pi/heatingsystem/target/classes" \
-Djava.library.path=/home/pi/lightboard/native/libpi4j.so \
-client \
-Xms1g \
-Xmx1g \
net.amarantha.heating.HeatingControlApplication $*