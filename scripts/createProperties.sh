#!/bin/bash
set -ev

dir="config"
mqtt="$dir/mqttConfig.properties"
rasp="$dir/rasps.properties"

if [[ ! -e $dir ]]; then
    mkdir $dir
elif [[ ! -d $dir ]]; then
    echo "$dir already exists but is not a directory" 1>&2
fi

echo "mqttUrl=mqtt" > $mqtt
echo "subscribeTopic=ohtu/test/observations" >> $mqtt
echo "publishTopic=ohtu/test/locations" >> $mqtt
echo "observerConfigTopic=ohtu/test/observerConfig" >> $mqtt
echo "observerConfigStatusTopic=ohtu/test/observerConfigStatus" >> $mqtt
echo "debug=true" >> $mqtt

echo "# rasps locations in format of x/y/z" > $rasp
echo "rasp-1=0/0/0" >> $rasp
echo "rasp-2=0/10000/3333" >> $rasp
echo "rasp-3=10000/0/6666" >> $rasp
echo "rasp-4=10000/10000/10000" >> $rasp

cat $mqtt
cat $rasp