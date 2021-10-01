#! /bin/sh
#


if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    echo "Usage: ./GETsongListTester.sh Auth-Token"
    echo "Example: ./GETsongListTester.sh 4c87dubofnf"
    exit 1
fi

echo "--- REQUESTING JSON-SONGLIST 3 WITH TOKEN: ----------"
curl -X GET \
     -H "Authorization: $1" \
     -H "Accept: application/json" \
     -v "http://localhost:8080/songs/playlists/3"
echo "-------------------------------------------------------"

echo "--- REQUESTING XML-SONGLIST 3 WITH TOKEN: ------------"
curl -X GET \
     -H "Authorization: $1" \
     -H "Accept: application/xml" \
     -v "http://localhost:8080/songs/playlists/3"
echo "-------------------------------------------------------"
