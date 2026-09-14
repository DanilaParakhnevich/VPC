cd ../..
sudo docker compose build --no-cache music-service && sudo docker compose up --force-recreate --no-deps -d music-service