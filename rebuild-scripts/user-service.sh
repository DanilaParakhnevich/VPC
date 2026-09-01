cd ../
sudo docker compose build --no-cache user-service && docker compose up --force-recreate --no-deps -d user-service