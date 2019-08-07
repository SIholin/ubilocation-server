# Bluetooth Location Server

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

Bluetooth Location Server is part of a larger project which aim is to build comprehensive implementation of indoor positioning system for ubikampus. Systems main repository can be found [here](https://github.com/ubikampus/ubi-Indoor-Positioning).

This program is however still self-sufficient as it can operate no matter how raw data is been created/collected and how the gotten information is used. It's only functionality is to collect signal strength data from MQTT bus topic and after using it to calculate location of BLE devices to publish that information into another MQTT bus topic. Server also allows controlling MQTT bus URL and topics info through properties file and BLE listeners static locations through both properties file and MQTT bus topic. That can be also configurated from properties file. 

## Table of Contents <a name="table-of-contents"/>

*  [Local Development](#local-development)
*  [Production](#production)
*  [Configurations](#configurations)
*  [License](#license)

## Local Development

Easiest way to get a development server running is to use docker-compose.
Before running necessary properties files must be created. 

To achieve this you can use from projects root a [bash script](https://github.com/ubikampus/Bluetooth-location-server/blob/master/scripts/createProperties.sh) with command `./scripts/createProperties.sh` and another [bash script](https://github.com/ubikampus/Bluetooth-Location-Server/blob/master/scripts/createSignConfig.sh) with command `./scripts/createSignConfig.sh`
to automatically create all the needed properties and give them a default value that can be used directly or changed if wanted. 

Last you must create or copy the file and maybe rename the file that contains the public key. Because in default it is excepted to be inside `config` folder as `PublicKeyForObserverConfig.txt`.

Set up the development environment with `docker-compose up -d mqtt` and `docker-compose up btls`.
You should now have both the location server and an mqtt server running.
The mqtt server has port 1883 exposed, so you can also connect to it from the outside.

Src and properties files are shared to the container, so you don't have to rebuild the image when making changes. Just restart the server.

If you just need the location server, set `mqttUrl` to the mqtt server's url in the properties files, and run `docker-compose up btls`. 

So to sum it all up

### Installation

*  `git clone https://github.com/ubikampus/ubi-Indoor-Positioning.git`
*  `cd ubi-Indoor-Positioning`
*  `./scripts/createProperties.sh`
*  `./scripts/createSignConfig.sh`

### Usage

If using the default file path of the public key

*  Create a file called `PublicKeyForObserverConfig.txt` inside `config` folder or copy and rename existing text file.
*  Make sure it contains valid public key.

Otherwise

*  From project root open in editor `config/keys.properties`
*  Then change `configPublicKey` value to relative path of the file which contains the key and close the file
*  Copy existing text file or create new one and make sure it contains valid public key.

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

To locally create production version of the server application then after getting project loaded with properties files all the needed properties values must be first checked and set if needed. Then just build the docker image.

#### Installation

*  `git clone https://github.com/ubikampus/ubi-Indoor-Positioning.git`
*  `cd ubi-Indoor-Positioning`
*  `./scripts/createProperties.sh`
*  `./scripts/createSignConfig.sh`

#### Usage

If using the default file path of the public key

*  Create a file called `PublicKeyForObserverConfig.txt` inside `config` folder or copy and rename existing text file.
*  Make sure it contains valid public key.

Otherwise

*  From project root open in editor `config/keys.properties`
*  Then change `configPublicKey` value to relative path of the file which contains the key and close the file
*  Copy existing text file or create new one and make sure it contains valid public key.

NOTE: text-file must be inside `config` folder.

Then

*  From project root open in editor `config/mqttConfig.properties`
*  Then change `mqttUrl` value to servers address and close the file

And last

*  `docker build -t ubikampus-positioning-server -f Dockerfile.prod .`
*  `docker run -v $(pwd)/config:/config ubikampus-positioning-server:latest`

## Configurations

After properties files have been created by the two [bash script](https://github.com/ubikampus/Bluetooth-location-server/blob/master/scripts/)s then you will find a folder called `config` if not yet existed and from there three properties files with their default values.  

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
configPublicKey=config/PublicKeyForObserverConfig.txt
```

Property `configPublicKey` tells the relative path of text-file that contains public key and only the key for reading signed messages in MQTT bus. Signed reading from MQTT topic is default only active for configurating BLE listeners.

## License

Code is under the [MIT License](https://github.com/ubikampus/Bluetooth-Location-Server/blob/master/LICENSE)


