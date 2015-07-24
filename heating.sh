#!/usr/bin/env bash
java \
-Djava.library.path=/home/pi/lightboard/native/libpi4j.so \
-client \
-Xms1g \
-Xmx1g \
-cp "/home/pi/lightboard/lib/*:/home/pi/heatingsystem/target/classes" \
net.amarantha.heating.Main $*