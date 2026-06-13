package com.github.selfcrafted.mqttsync;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.selfcrafted.mqttsync.message.*;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;

public class MqttManager {
    private final Logger logger;
    private final MessageSerializer serializer;
    private final MqttClient client;
    private final String clientId;
    private final ConfigManager configManager;
    private final CallbackHandler callbackHandler;

    private static final String TOPIC_ROOT     = "mqttsync";

    public MqttManager(Logger logger, MessageSerializer serializer, ConfigManager configManager, CallbackHandler handler) {
        this.configManager = configManager;
        this.logger = logger;
        this.serializer = serializer;
        this.callbackHandler = handler;
        ConfigManager.Config config = configManager.config();
        this.clientId = "mqttsync-" + config.proxy_id;
        try {
            this.client = new MqttClient(config.broker_addr, clientId, new MemoryPersistence());
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect() {
        ConfigManager.Config config = configManager.config();
        try {
            MqttConnectOptions opts = new MqttConnectOptions();
            opts.setCleanSession(true);
            opts.setAutomaticReconnect(true);
            opts.setConnectionTimeout(10);
            opts.setKeepAliveInterval(30);
            if (!config.broker_username.isBlank()) {
                opts.setUserName(config.broker_username);
                opts.setPassword(config.broker_password.toCharArray());
            }

            client.setCallback(new MqttCallback() {
                @Override public void connectionLost(Throwable cause) {
                    logger.warn("[MQTTSync] Connection lost: {}", cause.getMessage());
                }
                @Override public void messageArrived(String topic, MqttMessage message) {
                    handleIncoming(topic, new String(message.getPayload(), StandardCharsets.UTF_8));
                }
                @Override public void deliveryComplete(IMqttDeliveryToken token) {}
            });

            client.connect(opts);
            client.subscribe(TOPIC_ROOT+"/#", config.broker_qos);
            logger.info("[MQTTSync] Connected to MQTT broker {} as '{}'", config.broker_addr, clientId);

        } catch (MqttException e) {
            logger.error("[MQTTSync] Failed to connect to MQTT: {}", e.getMessage());
        }
    }

    public void disconnect() {
        if (client.isConnected()) {
            try { client.disconnect(); } catch (MqttException ignored) {}
        }
    }

    public void publish(SyncMessage message) {
        if (!client.isConnected()) logger.warn("MQTT manager not ready");
        String topic = TOPIC_ROOT + "/" + message.getType();

        try {
            String json = serializer.toJson(message);
            MqttMessage msg = new MqttMessage(json.getBytes(StandardCharsets.UTF_8));
            msg.setQos(1);
            client.publish(topic, msg);
        } catch (MqttException e) {
            logger.warn("[MQTTSync] Publish failed on {}: {}", topic, e.getMessage());
        } catch (JsonProcessingException e) {
            logger.warn("[MQTTSync] Could not serialize message: {}", e.getMessage());
        }
    }

    private void handleIncoming(String ignore, String json) {
        try {
            SyncMessage raw = serializer.fromJson(json);

            if (raw instanceof ServerRegistrationMessage msg) {
                callbackHandler.handleServerRegistration(msg);
                return;
            }

            if (raw instanceof PlayerChatMessage msg) {
                callbackHandler.handlePlayerChat(msg);
                return;
            }

            if (raw instanceof PlayerConnectionMessage msg) {
                callbackHandler.handlePlayerConnection(msg);
                return;
            }

        } catch (Exception e) {
            logger.warn("[MQTTSync] Failed to handle message {}: {}", json, e.getMessage());
        }
    }
}
