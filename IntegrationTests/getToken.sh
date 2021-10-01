#! /bin/sh

# if [ "$#" -ne 1 ]; then
#     echo "Illegal number of parameters"
#     echo "Usage: ./getToken.sh http://host:port/songsWS-TEAMNAME"
#     echo "Example: ./getToken.sh http://localhost:8080/songsWS-teames" 
#     exit 1
# fi

echo "--- REQUESTING A TOKEN WITH ---"
curl -X POST \
     --silent \
	-H "Content-Type: application/json" \
     -d '{"userId":"mmuster","password":"pass1234"}' \
     -v "http://localhost:8080/auth"
echo ""
