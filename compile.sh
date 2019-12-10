#!/bin/bash
export PROJ=`pwd`
export AI=/android-sdk/platforms/android-19/android.jar

aapt package -f -m -J $PROJ/src -M $PROJ/AndroidManifest.xml -S $PROJ/res -I $AI
aapt package -f -m -F $PROJ/bin/AFTV-XM.unaligned.apk -M $PROJ/AndroidManifest.xml -S $PROJ/res -A $PROJ/assets -I $AI
mkdir -p obj
javac -d obj -classpath "src:provided/XposedBridgeApi-54.jar" -bootclasspath $AI src/tsynik/xposed/mod/aftv/*.java
mkdir -p bin
dx --dex --output=$PROJ/bin/classes.dex $PROJ/obj
cp $PROJ/bin/classes.dex .
aapt add $PROJ/bin/AFTV-XM.unaligned.apk classes.dex
zipalign -f 4 $PROJ/bin/AFTV-XM.unaligned.apk $PROJ/bin/AFTV-XM.133t.apk

### SIGN
