#! /bin/sh

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    echo "Usage: ./deleteSong.sh http://host:port/songsWS-TEAMNAME token"
    echo "Example: ./deleteSong.sh http://host:port/songsWS-teames 4c87dub2s"
    exit 1
fi

echo "--- REQUESTING LYRIC THAT IS PRESENT IN DB IN JSON----------"
curl -X GET \
     -H "Authorization: $1" \
     -H "Accept: application/json" \
     -v "http://localhost:8080/lyrics/Eminem/Mockingbird"
echo "-------------------------------------------------------"

echo "--- REQUESTING LYRIC THAT IS PRESENT IN DB IN XML----------"
curl -X GET \
     -H "Authorization: $1" \
     -H "Accept: application/xml" \
     -v "http://localhost:8080/lyrics/Eminem/Mockingbird"
echo "-------------------------------------------------------"

echo "--- REQUESTING LYRIC THAT IS NOT PRESENT IN DB: 404----------"
curl -X GET \
     -H "Authorization: $1" \
     -H "Accept: application/json" \
     -v "http://localhost:8080/lyrics/Drake/abs"
echo "-------------------------------------------------------"

echo "--- REQUESTING ALL LYRICS OF EMINEM IN JSON----------"
curl -X GET \
     -H "Authorization: $1" \
     -H "Accept: application/json" \
     -v "http://localhost:8080/lyrics/Eminem"
echo "-------------------------------------------------------"

echo "--- REQUESTING ALL LYRICS OF EMINEM IN XML----------"
curl -X GET \
     -H "Authorization: $1" \
     -H "Accept: application/xml" \
     -v "http://localhost:8080/lyrics/Eminem"
echo "-------------------------------------------------------"

echo "--- REQUESTING ALL LYRICS OF NOT EXISTIN ARTIST: 404----------"
curl -X GET \
     -H "Authorization: $1" \
     -H "Accept: application/xml" \
     -v "http://localhost:8080/lyrics/andriylubar"
echo "-------------------------------------------------------"

echo "--- REQUESTING ALL LYRICS WITH NOT ACCEPTABLE FORMAT: 406---------"
curl -X GET \
     -H "Authorization: $1" \
     -H "Accept: application/txt" \
     -v "http://localhost:8080/lyrics/andriylubar"
echo "-------------------------------------------------------"