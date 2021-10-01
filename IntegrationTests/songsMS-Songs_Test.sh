#! /bin/sh
 

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    echo "Usage: ./songsWSTester.sh Authorization-token"
    echo "Example: ./songsWSTester.sh 8WPjhGQt8p"
    exit 1
fi

echo "--- POSTING A JSON SONG ------------------------"
curl -X POST \
     -H "Content-Type: application/json" \
     -H "Authorization: $1" \
     -d '{"title" : "777 title", "artist" : "artist 777", "label" : "label 777", "released" : 2017}' \
     -v "http://localhost:8080/songs"
echo "------------------------------------------------"

echo "--- POSTING A JSON SONG WITHOUT A TITLE: 400----"
curl -X POST \
     -H "Content-Type: application/json" \
     -H "Authorization: $1" \
     -d '{"artist" : "artist 777", "label" : "label 777", "released" : 2017}' \
     -v "http://localhost:8080/songs"
echo "------------------------------------------------"

echo "--- REQUESTING ALL SONGS IN JSON ---------------"
curl -X GET \
     -H "Accept: application/json" \
     -H "Authorization: $1" \
     -v "http://localhost:8080/songs"
echo "------------------------------------------------"

echo "--- REQUESTING SONG 6 IN JSON ------------------"
curl -X GET \
     -H "Accept: application/json" \
     -H "Authorization: $1" \
     -v "http://localhost:8080/songs/6"
echo "------------------------------------------------"

echo "--- REQUESTING ALL XML SONGS--------------------"
curl -X GET \
     -H "Accept: application/xml" \
     -H "Authorization: $1" \
     -v "http://localhost:8080/songs"
echo "------------------------------------------------"

echo "--- REQUESTING SONG 6 IN XML--------------------"
curl -X GET \
     -H "Accept: application/xml" \
     -H "Authorization: $1" \
     -v "http://localhost:8080/songs/6"
echo "------------------------------------------------"

echo "--- REQUESTING NON-EXISTING SONG 2222: ---------"
curl -X GET \
     -H "Accept: application/json" \
     -H "Authorization: $1" \
     -v "http://localhost:8080/songs/2222"
echo "------------------------------------------------"

echo "--- UPDATING JSON-SONG 6 -----------------------"
curl -X PUT \
     -H "Content-Type: application/json" \
     -H "Authorization: $1" \
     -d '{"id": 6,"title": "Wrecking Ball","artist": "MILEY CYRUS","label": "RCA","released": 2013}' \
     -v "http://localhost:8080/songs/6"
echo "------------------------------------------------"

echo "--- REQUESTING UPDATED SONG 6 ------------------"
curl -X GET \
     -H "Accept: application/json" \
     -H "Authorization: $1" \
     -v "http://localhost:8080/songs/6"
echo "------------------------------------------------"

echo "--- UPDATING NON-EXISTING SONG 2222 WITH PAYLOAD SONG 6 -"
echo "--- SHOULD RETURN 404 or 400 -------------------"
curl -X PUT \
     -H "Content-Type: application/json" \
     -H "Authorization: $1" \
     -d '{"id": 6,"title": "Wrecking Ball","artist": "MILEY CYRUS","label": "RCA","released": 2013}' \
     -v "http://localhost:8080/songs/2222"
echo "------------------------------------------------"

echo "--- UPDATING SONG 7 WITH PAYLOAD SONG 6: 400 ---"
curl -X PUT \
     -H "Content-Type: application/json" \
     -H "Authorization: $1" \
     -d '{"id": 6,"title": "Wrecking Ball","artist": "MILEY CYRUS","label": "RCA","released": 2013}' \
     -v "http://localhost:8080/songs/7"
echo "------------------------------------------------"

echo "--- DELETING SONG 2 ----------------------------"
curl -X DELETE \
     -H "Authorization: $1" \
     -v "http://localhost:8080/songs/2"
echo "------------------------------------------------"

echo "--- DELETING SONG 2 AGAIN: SHOULD PRODUCE 404 --"
curl -X DELETE \
     -H "Authorization: $1" \
     -v "http://localhost:8080/songs/2"
echo "------------------------------------------------"

