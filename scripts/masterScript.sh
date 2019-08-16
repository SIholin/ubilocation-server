#!/bin/bash
set -ev

# Calls 3 different bash scripts that have their own jobs to create needed files.
/bin/bash ./scripts/createProperties.sh
/bin/bash ./scripts/createSignConfig.sh
/bin/bash ./scripts/createAppConfig.sh
