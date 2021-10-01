#! /bin/sh
#

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    echo "Usage: ./POSTsongListTester.sh Auth-token"
    echo "Example: ./POSTsongListTester.sh som6t0833qa"
    exit 1
fi

echo "--- POSTING A JSON SONGLIST   ---------------------"
curl -X POST \
     -H "Authorization: $1" \
     -H "Content-Type: application/json" \
     -d "@aSongList.json" \
     -v "http://localhost:8080/songs/playlists"
echo "---------------------------------------------------"

echo "--- POSTING A JSON SONGLIST WITH NON-EXISTING SONG -"
curl -X POST \
     -H "Authorization: $1" \
     -H "Content-Type: application/json" \
     -d "@aSongListBad.json" \
     -v "http://localhost:8080/songs/playlists"
echo " "


