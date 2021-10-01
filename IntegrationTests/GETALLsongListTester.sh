#! /bin/sh

# if [ "$#" -ne 3 ]; then
#     echo "Illegal number of parameters"
#     echo "Usage: ./GETALLsongListTester.sh http://host:port/songsWS-TEAMNAME token userId"
#     echo "Example: ./GETALLsongListTester.sh http://host:port/songsWS-teames 4c87dubofnfrheesom6t0833qa mmuster"
#     exit 1
# fi

echo "--- TESTING songLists-endpoint WITHOUT TOKEN SHOULD RETURN 401:"
curl -X GET \
     -H "Accept: application/xml" \
     -v "http://localhost:8080/songs/playlist"
echo "--------------------------------------------------"


echo "--- REQUESTING ALL XML SONGLISTs for $3: ---------"
curl -X GET \
     -H "Authorization: $2" \
     -H "Accept: application/xml" \
     -v "http://localhost:8080/songs/playlist?userId=$3"
echo "---------------------------------------------------"

echo "--- REQUESTING ALL JSON SONGLISTs for $3: ---------"
curl -X GET \
     -H "Authorization: $2" \
     -H "Accept: application/json" \
     -v "http://localhost:8080/songs/playlist?userId=$3"
echo "----------------------------------------------------"
