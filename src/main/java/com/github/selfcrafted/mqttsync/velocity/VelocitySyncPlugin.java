package com.github.selfcrafted.mqttsync.velocity;

import com.github.selfcrafted.mqttsync.ConfigManager;
import com.github.selfcrafted.mqttsync.message.MessageSerializer;
import com.github.selfcrafted.mqttsync.MqttManager;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.nio.file.Path;

/**
 * VelocitySyncPlugin — Velocity proxy plugin
 * <p>
 * Syncs chat messages, joins, leaves, and server hops across multiple proxy
 * instances using an MQTT broker as the message bus.
 */
@Plugin(
        id = Versions.ID,
        name = Versions.NAME,
        version = Versions.VERSION,
        description = "Sync chat, joins, leaves and server hops across proxy instances via MQTT",
        authors = {"offby0point5"},
        dependencies = {
                @Dependency(id = "viaversion")
        }
)
public class VelocitySyncPlugin {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    private MqttManager mqttManager;
    private EventSyncMessageFactory msgFactory;

    @Inject
    public VelocitySyncPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent event) {
        ConfigManager configManager = new ConfigManager(logger, dataDirectory.resolve("mqtt-sync.yml"));
        this.mqttManager   = new MqttManager(logger, new MessageSerializer(), configManager,
                new VelocityCallbackHandler(logger, server, new BackendsManager(logger, server)));
        this.msgFactory = new EventSyncMessageFactory(configManager.config().proxy_id);
        mqttManager.connect();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        mqttManager.publish(msgFactory.createFromEvent(event));
        mqttManager.disconnect();
    }

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        mqttManager.publish(msgFactory.createFromEvent(event));
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        mqttManager.publish(msgFactory.createFromEvent(event));
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        mqttManager.publish(msgFactory.createFromEvent(event));
    }
}
