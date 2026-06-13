package com.github.selfcrafted.mqttsync.velocity;

import com.github.selfcrafted.mqttsync.CallbackHandler;
import com.github.selfcrafted.mqttsync.message.PlayerChatMessage;
import com.github.selfcrafted.mqttsync.message.PlayerConnectionMessage;
import com.github.selfcrafted.mqttsync.message.ServerRegistrationMessage;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.slf4j.Logger;

public class VelocityCallbackHandler implements CallbackHandler {
    private final Logger logger;
    private final ProxyServer proxyServer;
    private final BackendsManager backendsManager;

    public VelocityCallbackHandler(Logger logger, ProxyServer proxyServer, BackendsManager backendsManager) {
        this.logger = logger;
        this.proxyServer = proxyServer;
        this.backendsManager = backendsManager;
    }

    @Override
    public void handlePlayerConnection(PlayerConnectionMessage msg) {
        proxyServer.sendMessage(Component.text()
                .append(Component.text("[" + msg.getSender() + "] ", NamedTextColor.DARK_GRAY))
                .append(Component.text(msg.getPlayer().toString(), NamedTextColor.AQUA))
                .append(Component.text(" moved ", NamedTextColor.GRAY))
                .append(Component.text(msg.getLastBackend(), NamedTextColor.WHITE).decorate(TextDecoration.BOLD))
                .append(Component.text(" → ", NamedTextColor.GRAY))
                .append(Component.text(msg.getBackend(), NamedTextColor.WHITE).decorate(TextDecoration.BOLD))
                .build());
    }

    @Override
    public void handlePlayerChat(PlayerChatMessage msg) {
        proxyServer.sendMessage(Component.text()
                .append(Component.text("[" + msg.getSender() + "] ", NamedTextColor.DARK_GRAY))
                .append(Component.text(msg.getPlayer().toString(), NamedTextColor.YELLOW))
                .append(Component.text(": ", NamedTextColor.WHITE))
                .append(Component.text(msg.getMessage(), NamedTextColor.WHITE))
                .build());
    }

    @Override
    public void handleServerRegistration(ServerRegistrationMessage msg) {

    }
}
