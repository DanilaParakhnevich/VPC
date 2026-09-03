cd ..
sudo docker compose build --no-cache gateway-service && sudo docker compose up --force-recreate --no-deps -d gateway-service