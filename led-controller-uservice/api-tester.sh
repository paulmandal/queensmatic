#!/bin/bash

if [ "$#" -ne 7 ]
then
	echo "Usage ${0} <apiUrl> <ledRangeStart> <ledRangeEnd> <red> <green> <blue> <brightness>"
	exit 1
fi

LED_API_URL=${1}
LED_RANGE_START=${2}
LED_RANGE_END=${3}
LED_RED=${4}
LED_GREEN=${5}
LED_BLUE=${6}
LED_BRIGHTNESS=${7}

for ledNum in `seq $LED_RANGE_START $LED_RANGE_END`
do
	curl -H 'Content-Type: application/json' -H 'Accept: application/json' -X PUT -d '{ "ledNumber": '${ledNum}', "red": '${LED_RED}', "green": '${LED_GREEN}', "blue": '${LED_BLUE}', "brightness": '${LED_BRIGHTNESS}' }' ${LED_API_URL}
done
