# MiniBuddy Audio Storage / Retrieval Service

## Overview
This is a backend service that lets users upload, convert, store, and retrieve audio files.

## Endpoints
- **POST /audio/user/{user_id}/phrase/{phrase_id}**  
  Upload an audio file using multipart/form-data (key: audio_file).

- **GET /audio/user/{user_id}/phrase/{phrase_id}/{audio_format}**  
  Retrieve and, if necessary, convert the stored audio file.

## Running Locally
1. Build and run using docker-compose:
   ```
   docker-compose up --build
   ```
2. Test endpoints with curl, for example:
   ```
   curl --request POST 'http://localhost/audio/user/1/phrase/1' --form 'audio_file=@"./test_audio_file_1.m4a"'
   curl --request GET 'http://localhost/audio/user/1/phrase/1/m4a' -o './test_response_file_1_1.m4a'
   ```

## Design Trade-offs and Known Limitations

Had to cut a few corners to deliver this prototype. Here’s a quick rundown of our decisions:

- **No Security & Authentication:**  
  Didn’t add any authentication or authorization. This means the API is open so it works for demo purposes but isn’t secure for production.

- **Simple File Storage:**  
  Audio files are stored locally without encryption or advanced backup strategies. Also just delete the temporary files after conversion.

- **Basic Error Handling:**  
  The implementation logs errors at a minimal level. There’s not much advanced error recovery—if FFmpeg fails or a file I/O issue happens, it’s a simple exception.

- **Limited Testing:**  
  Only set up minimal unit tests and API tests to cover the basic flow. Full integration tests and extra coverage were skipped to save time.

- **Straightforward Audio Conversion:**  
  Audio conversion uses FFmpeg via system calls. Didn’t add detailed error parsing or performance tuning, keeping things simple for now.

- **Minimal Database Validation:**  
  User and phrase IDs are checked against pre-populated tables. In a real scenario, would need more comprehensive validation.

- **Scalability:**  
  Opted for simplicity and speed over scalability and performance optimizations. This is fine for a demo, but a production system would need more robust solutions.

