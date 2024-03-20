# Use the official Mosquitto image as the base image
FROM eclipse-mosquitto

# Expose MQTT and WebSocket ports
EXPOSE 1883 9001

# Copy the local configuration file into the container
COPY mosquitto.conf /mosquitto/config/mosquitto.conf
