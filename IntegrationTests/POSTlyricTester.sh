#! /bin/sh

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    echo "Usage: ./deleteSong.sh http://host:port/songsWS-TEAMNAME token"
    echo "Example: ./deleteSong.sh http://host:port/songsWS-teames 4c87dub2s"
    exit 1
fi

echo "--- POST LYRIC----------"
curl -X POST \
     -H "Authorization: $1" \
     -H "Content-Type: application/json" \
     -d "@lyric.json" \
     -v "http://localhost:8080/lyrics"
echo " "

echo "--- POST LYRIC UNATHORIZED: 401----------"
curl -X POST \
     -H "Authorization: q" \
     -H "Content-Type: application/json" \
     -d "@lyric.json" \
     -v "http://localhost:8080/lyrics"
echo " "

echo "--- POST LYRIC WITH XML: 415----------"
curl -X POST \
     -H "Authorization: $1" \
     -H "Content-Type: application/xml" \
     -d "@lyric.json" \
     -v "http://localhost:8080/lyrics"
echo " "