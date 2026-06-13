package com.github.selfcrafted.mqttsync.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.viaversion.viaversion.VelocityPlugin;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.velocity.platform.VelocityViaInjector;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.IntStream;

import io.netty.channel.ChannelHandler;

import java.util.Arrays;


public class BackendsManager implements VersionProvider {
    private final Map<String, ProtocolVersion> serverProtocolVersions = new HashMap<>();
    private final Logger logger;
    private final ProxyServer proxyServer;

    private static final Method GET_ASSOCIATION = getAssociationMethod();

    private static @Nullable Method getAssociationMethod() {
        try {
            return Class.forName("com.velocitypowered.proxy.connection.MinecraftConnection").getMethod("getAssociation");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            Via.getPlatform().getLogger().log(Level.SEVERE, "Failed to get association method from Velocity, please report this issue on our GitHub.", e);
            return null;
        }
    }

    @Override
    public ProtocolVersion getClosestServerProtocol(UserConnection user) throws Exception {
        return user.isClientSide() ? getBackProtocol(user) : getFrontProtocol(user);
    }

    private ProtocolVersion getBackProtocol(UserConnection user) throws Exception {
        //TODO use newly added Velocity netty event
        ChannelHandler mcHandler = user.getChannel().pipeline().get("handler");
        ServerConnection serverConnection = (ServerConnection) GET_ASSOCIATION.invoke(mcHandler);
        return serverProtocolVersions.get(serverConnection.getServerInfo().getName());
    }

    private ProtocolVersion getFrontProtocol(UserConnection user) throws Exception {
        ProtocolVersion playerVersion = user.getProtocolInfo().protocolVersion();

        IntStream versions = com.velocitypowered.api.network.ProtocolVersion.SUPPORTED_VERSIONS.stream()
                .mapToInt(com.velocitypowered.api.network.ProtocolVersion::getProtocol);

        // Modern forwarding mode needs 1.13 Login plugin message
        if (VelocityViaInjector.GET_PLAYER_INFO_FORWARDING_MODE != null
                && ((Enum<?>) VelocityViaInjector.GET_PLAYER_INFO_FORWARDING_MODE.invoke(VelocityPlugin.PROXY.getConfiguration()))
                .name().equals("MODERN")) {
            versions = versions.filter(ver -> ver >= ProtocolVersion.v1_13.getVersion());
        }
        int[] compatibleProtocols = versions.toArray();

        if (Arrays.binarySearch(compatibleProtocols, playerVersion.getVersion()) >= 0) {
            // Velocity supports it
            return playerVersion;
        }

        if (playerVersion.getVersion() < compatibleProtocols[0]) {
            // Older than Velocity supports, get the lowest version
            return ProtocolVersion.getProtocol(compatibleProtocols[0]);
        }

        // Loop through all protocols to get the closest protocol id that Velocity supports (and that Via does too)

        // TODO: This needs a better fix, i.e checking ProtocolRegistry to see if it would work.
        // This is more of a workaround for snapshot support
        for (int i = compatibleProtocols.length - 1; i >= 0; i--) {
            int protocol = compatibleProtocols[i];
            if (playerVersion.getVersion() > protocol && ProtocolVersion.isRegistered(protocol)) {
                return ProtocolVersion.getProtocol(protocol);
            }
        }

        Via.getPlatform().getLogger().severe("Panic, no protocol id found for " + playerVersion);
        return playerVersion;
    }

    public BackendsManager(Logger logger, ProxyServer proxyServer) {
        this.logger = logger;
        this.proxyServer = proxyServer;
        Via.getManager().getProviders().use(BackendsManager.class, this);
    }

    public void register(String serverName, InetSocketAddress address, Integer protocolVersion) {
        ProtocolVersion version = ProtocolVersion.getProtocol(protocolVersion);
        Optional<RegisteredServer> server = proxyServer.getServer(serverName);
        ServerInfo serverInfo = new ServerInfo(serverName, address);
        if (server.isPresent()) {
            logger.info("Server {} already registered.", serverName);
            if (version.equals(serverProtocolVersions.get(serverName))) {
                logger.info("Server {} already at protocol version {}", serverName, protocolVersion);
            } else {
                serverProtocolVersions.put(serverName, version);
                logger.info("Server {} updated to protocol version {}", serverName, protocolVersion);
            }
            var existingServerInfo = server.get().getServerInfo();
            if (existingServerInfo.equals(serverInfo)) {
                logger.info("Server {} already at address {}", serverName, address);
            } else {
                proxyServer.unregisterServer(existingServerInfo);
                proxyServer.registerServer(serverInfo);
                logger.info("Server {} updated to address {}", serverName, address);
            }
        } else {
            proxyServer.registerServer(serverInfo);
            serverProtocolVersions.put(serverName, version);
        }
        logger.info("Registered server {} with version {} at address {}", serverName, version, address);
    }

    public void unregister(String serverName) {
        Optional<RegisteredServer> server = proxyServer.getServer(serverName);
        if (server.isPresent()) {
            ServerInfo serverInfo = server.get().getServerInfo();
            proxyServer.unregisterServer(serverInfo);
        }
        serverProtocolVersions.remove(serverName);
        logger.info("Unregistered server {}", serverName);
    }
}
