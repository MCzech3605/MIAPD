## Usage
#### Server
run `docker-compose -f server/compose.yaml up`

#### Mobile App
run the `mobile-app` project in Android Studio Code's emulator. For the app to connect to the server it has to be available under `ahpserver.local`. In case of problems it's recommended to try and adjust the server's ip at the top of `mobile-app/app/src/main/java/com/decisionmaking/ServerAndConsts.kt`.

