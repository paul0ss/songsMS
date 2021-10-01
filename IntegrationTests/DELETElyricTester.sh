#! /bin/sh

if [ "$#" -ne 3 ]; then
    echo "$#"
    echo "Illegal number of parameters"
    echo "Usage: ./DELETEsongListTester.sh token artist songname"
    echo "Example: ./DELETEsongListTester.sh 4c87dubo Drake Elevate"
    exit 1
fi


echo "--- DELETING LYRIC"
curl -X DELETE \
    -H "Authorization: $1" \
    -v "http://localhost:8080/lyrics/$2/$3"
echo "-----------------------------------------------------"

echo "--- DELETING LYRIC WITHOUT AUTHORIZATION: 401"
curl -X DELETE \
    -H "Authorization: q" \
    -v "http://localhost:8080/lyrics/$2/$3"
echo "-----------------------------------------------------"

echo "--- DELETING LYRIC THAT DOSNT EXIST: 404"
curl -X DELETE \
    -H "Authorization: $1" \
    -v "http://localhost:8080/lyrics/$2/$3"
echo "-----------------------------------------------------"