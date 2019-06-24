# Bluetooth Location Server

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

<!-- TODO: Description -->

## Table of Contents <a name="table-of-contents"/>
*  [Local Development](#local-development)
*  [Installation](#installation)
*  [Usage](#usage)

## Local Development

### Docker

Easiest way to get a development server running is to use docker-compose.
Before running necessary properties files must be created. 
To achieve this you can use from projects root a [bash script](https://github.com/ubikampus/Bluetooth-location-server/blob/master/scripts/createProperties.sh) with command './scripts/createProperties.sh' 
to automatically create all the needed properties and give them a default value that can be used directly or changed if wanted.
Set up the development environment with `docker-compose up -d mqtt` and `docker-compose up btls`.
You should now have both the location server and an mqtt server running.
The mqtt server has port 1883 exposed, so you can also connect to it from the outside.

src and properties flies are shared to the container, so you don't have to rebuild the image when making changes. Just restart the server.

If you just need the location server, set `mqttUrl` to the mqtt server's url in the properties files, and run `docker-compose up btls`.

### Installation

## Usage

