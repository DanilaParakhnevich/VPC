cd ../..
sudo docker compose build --no-cache user-service && sudo docker compose up --force-recreate --no-deps -d user-service