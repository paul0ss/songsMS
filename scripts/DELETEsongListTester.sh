#! /bin/sh
#


if [ "$#" -ne 2 ]; then
    echo "$#"
    echo "Illegal number of parameters"
    echo "Usage: ./DELETEsongListTester.sh token songListId"
    echo "Example: ./DELETEsongListTester.sh 4c87dubofnfrheesom6t0833qa 22"
    exit 1
fi

echo "--- DELETING SONGLIST WITHOUT AUTHORIZATION HEADER SHOULD RETURN 400"
curl -X DELETE \
     -v "http://localhost:8080/songs/playlists/$2"
echo "-----------------------------------------------------"

echo "--- DELETING SONGLIST WITH A WRONG TOKEN SHOULD RETURN 401"
curl -X DELETE \
     -H "Authorization: q" \
     -v "http://localhost:8080/songs/playlists/$2"
echo "-----------------------------------------------------"

echo "--- DELETING SONGLIST WITH TOKEN--------"
curl -X DELETE \
     -H "Authorization: $1" \
     -v "http://localhost:8080/songs/playlists/$2"
echo "-----------------------------------------------------"

echo "--- REQUESTING DELETED SONGLIST, SHOULD RETURN 404 --"
curl -X GET \
     -H "Authorization: $1" \
     -H "Accept: application/json" \
     -v "http://localhost:8080/songs/playlists/$2"
echo " "


