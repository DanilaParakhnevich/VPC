cd ../
sudo docker compose build --no-cache music-service && docker compose up --force-recreate --no-deps -d music-service