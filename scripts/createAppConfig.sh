#!/bin/bash
set -ev

dir="config"
appConfig="$dir/appConfig.properties"

if [[ ! -e $dir ]]; then
    mkdir $dir
elif [[ ! -d $dir ]]; then
    echo "$dir already exists but is not a directory" 1>&2
fi

echo "threeDimensional=True" > $appConfig
echo "enableConfigSigning=False" >> $appConfig
echo "enableDataEncryption=False" >> $appConfig

cat $appConfig
