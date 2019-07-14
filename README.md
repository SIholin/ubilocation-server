# Bluetooth Location Server

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

Bluetooth Location Server is part of a larger project which aim is to build comprehensive implementation of indoor positioning system for ubikampus. Systems main repository can be found [here](https://github.com/ubikampus/ubi-Indoor-Positioning).

This program is however still self-sufficient as it can operate no matter how raw data is been created/collected and how the gotten information is used. It's only functionality is to collect signal strength data from MQTT bus topic and after using it to calculate location of BLE devices to publish that ifnormation into another MQTT bus topic. Server also allows controlling MQTT bus URL and topics info through properties file and BLE listeners static locations through both properties file and MQTT bus topic. That can be also configurated from properties file. 

## Table of Contents <a name="table-of-contents"/>

*  [Local Development](#local-development)
*  [Installation](#installation)
*  [Usage](#usage)
*  [License](#license)

## Local Development

### Docker

Easiest way to get a development server running is to use docker-compose.
Before running necessary properties files must be created. 
To achieve this you can use from projects root a [bash script](https://github.com/ubikampus/Bluetooth-location-server/blob/master/scripts/createProperties.sh) with command './scripts/createProperties.sh' 
to automatically create all the needed properties and give them a default value that can be used directly or changed if wanted.
Set up the development environment with `docker-compose up -d mqtt` and `docker-compose up btls`.
You should now have both the location server and an mqtt server running.
The mqtt server has port 1883 exposed, so you can also connect to it from the outside.

Src and properties files are shared to the container, so you don't have to rebuild the image when making changes. Just restart the server.

If you just need the location server, set `mqttUrl` to the mqtt server's url in the properties files, and run `docker-compose up btls`.

## Installation

*  `git clone https://github.com/ubikampus/ubi-Indoor-Positioning.git`
*  `cd ubi-Indoor-Positioning`
*  `./scripts/createProperties.sh`

## Usage

To use with localhost MQTT server

*  In project root `docker-compose up -d mqtt`
*  In project root `docker-compose up btls`

To use with external MQTT server

*  From project root open in editor `config/mqttConfig.properties`
*  Then change `mqttUrl` value to servers address and close the file
*  In project root `docker-compose up btls`

## License

Code is under the [MIT License](https://github.com/ubikampus/Bluetooth-Location-Server/blob/master/LICENSE)


