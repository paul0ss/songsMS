#! /bin/sh


if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters"
    echo "Usage: ./deleteSong.sh http://host:port/songsWS-TEAMNAME token songIdToBeDeleted"
    echo "Example: ./deleteSong.sh http://host:port/songsWS-teames 4c87dubofnfrheesom6t0833qa 17"
    exit 1
fi

echo "--- DELETING SONG $2 WITH TOKEN--------"
curl -X DELETE \
     -H "Authorization: $1" \
     -v "http://localhost:8080/songs/$2"
echo " "
echo "-------------------------------------------------------------------------------------------------"

