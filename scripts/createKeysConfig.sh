#!/bin/bash
set -ev

dir="config"
signConfig="$dir/keys.properties"

if [[ ! -e $dir ]]; then
    mkdir $dir
elif [[ ! -d $dir ]]; then
    echo "$dir already exists but is not a directory" 1>&2
fi

echo "signingConfigPublicKey=config/PublicKeyForObserverConfig.txt" > $signConfig
echo "encryptionPrivateKey=config/PrivateKeyForEncryption.txt" >> $signConfig
echo "encryptionPublicKey=config/PublicKeyForEncryption.txt" >> $signConfig

cat $signConfig
