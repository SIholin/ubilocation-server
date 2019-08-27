# Bluetooth Location Server

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

Bluetooth Location Server is part of a larger project which aim is to build comprehensive implementation of indoor positioning system for ubikampus. Systems main repository can be found [here](https://github.com/ubikampus/ubi-Indoor-Positioning).

This program is however still self-sufficient as it can operate no matter how raw data is been created/collected and how the gotten information is used. It's only functionality is to collect signal strength data from MQTT bus topic and after using it to calculate location of BLE devices to publish that information into another MQTT bus topic. Server also allows controlling MQTT bus URL and topics info through properties file and BLE listeners static locations through both properties file and MQTT bus topic. That can be also configurated from properties file.

## Table of Contents <a name="table-of-contents"/>

*  [Local Development](#local-development)
*  [Production](#production)
*  [Configurations](#configurations)
*  [Data specs](#data-specs)
*  [License](#license)

## Local Development

### Installation

*  `git clone https://github.com/ubikampus/ubi-Indoor-Positioning.git`
*  `cd ubi-Indoor-Positioning`
*  `./scripts/masterScript.sh`

### Usage

*  Check [configurations](#configurations) are correct from `config` folder.

After that

To use with localhost MQTT server

*  In project root `docker-compose up -d mqtt`
*  In project root `docker-compose up btls`

To use with external MQTT server

*  From project root open in editor `config/mqttConfig.properties`
*  Then change `mqttUrl` value to servers address and close the file
*  In project root `docker-compose up btls`

## Production



### Locally

#### Installation

*  `git clone https://github.com/ubikampus/ubi-Indoor-Positioning.git`
*  `cd ubi-Indoor-Positioning`
*  `./scripts/masterScript.sh`

#### Usage

*  Check [configurations](#configurations) are correct from `config` folder.

Then

*  From project root open in editor `config/mqttConfig.properties`
*  Then change `mqttUrl` value to servers address and close the file

And last

*  `docker-compose -f docker-compose.prod.yml up -d`

By default, journald is used for storing logs. For example all logs can be
viewed with `sudo journalctl CONTAINER_NAME=bluetooth-location-server`

## Configurations

After properties files have been created by the three [bash scripts](https://github.com/ubikampus/Bluetooth-location-server/blob/master/scripts/) which master script called then you will find a folder called `config` if not yet existed and from there four properties files with their default values.

### mqttConfig.properties

```
mqttUrl=mqtt
subscribeTopic=ohtu/test/observations
publishTopic=ohtu/test/locations
observerConfigTopic=ohtu/test/observerConfig
observerConfigStatusTopic=ohtu/test/observerConfigStatus
debug=true
```

First row in the file indicates the address of the MQTT server which if locally hosted is generally mqtt. Otherwise it must be full url without starting protocol name so without "://" beginning. So for example "iot.ubikampus.net". Second and fourth indicates the topics in the MQTT bus which need to be listened for new messages. First of these tells where all the data BLE listeners have collected should be published and second where all the configuration changes should be published related to BLE listeners locations. third and fifth row instead are about the topics where calculated location data should be published and where to give response everytime BLE listeners configurations where attempted to changed. And last row indicates if debug mode should be true or false. If its true then program will use runtime generated observation data about fake people for location calculations. And if false then actual data from topic that is defined by second row.

NOTE: All the topics above must be different so they can't be the same.

### rasps.properties

```
#Rasps locations in format of x/y/z
rasp-1=0/0/0
rasp-2=0/10000/3333
rasp-3=10000/0/6666
rasp-4=10000/10000/10000
```

This file contains only the information about the static BLE listeners. As comment indicates the format of these configurations are following `(name of the BLE listener)=(the x-coordinate of listener)/(the y-coordinate of listener)/(the z-coordinate of listener)`. These configurations however unlike others can be also modified during runtime using MQTT bus.

### keys.properties

```
signingConfigPublicKey=config/PublicKeyForObserverConfig.txt
encryptionPrivateKey=config/PrivateKeyForEncryption.txt
encryptionPublicKey=config/PublicKeyForEncryption.txt
```

Property `signingConfigPublicKey` tells the relative path of text-file that contains public key and only the key for reading signed messages in MQTT bus. The relative path is taken from the aspect of projects root but it must begin with `config/` if configurations are used to  create production version. Signed reading from MQTT topic to configure BLE listeners is not active by default.

Property `encryptionPrivateKey` tells the relative path of text-file that contains private key and only the key for decrypting messages in MQTT bus that contain observation data. The relative path is taken from the aspect of projects root but it must begin with `config/` if configurations are used to  create production version. Decrypting messages that are gotten from MQTT bus is not active by default.

Property `encryptionPublicKey` tells the relative path of text-file that contains public key and only the key for encrypting messages that are send to MQTT bus. The relative path is taken from the aspect of projects root but it must begin with `config/` if configurations are used to  create production version. Encrypting published messages is not active by default.

### appConfig.properties

```
threeDimensional=True
enableConfigSigning=False
enableDataEncryption=False
enableDataDecryption=False
```

Property `threeDimensional` tells if applications should create three dimensional data or not. If false it will by default create two dimensional data.

Properties `enableConfigSigning`, `enableDataDecryption`  and `enableDataEncryption` indicate if signing, decryption and/or encryption should be used or not respectively. 

If signing is enabled then application only registers the bluetooth listener (observer) configuration messages that can be verified with `signingConfigPublicKey` property.

If encryption is enabled then application uses `encryptionPublicKey` property to encrypt published messages that contain location data and if decryption is enabled then it uses `encryptionPrivateKey` property to decrypt observaton data that is listened from subscribed topic.

## Data Specs

### Raw signal strength data

```
{
  "observerId": string,
  "beaconId": string,
  "rssi": decimal number
}
```

### Observer configuration data

Json array of

```
{
  "observerId": string,
  "position": json array of decimal numbers
}
```

### Location data

Either json array of

```
{
  "z": decimal number,
  "zr": decimal number,
  "beaconId": string,
  "x": decimal number,
  "y": decimal number,
  "xr": decimal number,
  "yr": decimal number
}
```

or

```
{
  "alignment": decimal number,
  "beaconId": string,
  "x": decimal number,
  "y": decimal number,
  "xr": decimal number,
  "yr": decimal number
}
```

### Status response for observer configuration

Either `success` or `error`.

## License

Code is under the [MIT License](https://github.com/ubikampus/Bluetooth-Location-Server/blob/master/LICENSE)
