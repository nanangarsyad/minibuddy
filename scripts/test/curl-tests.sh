#!/bin/bash

HOST=${HOST:-http://localhost:8080}
echo "Testing API: $HOST"

# Run tests
echo "1. Upload test"
curl -s "$HOST/audio/user/1/phrase/1" -F "audio_file=@test.m4a"

echo -e "\n2. Download test"
curl -s "$HOST/audio/user/1/phrase/1/m4a" > downloaded.m4a

# Show results
echo -e "\nResults:"
ls -l test.m4a downloaded.m4a
