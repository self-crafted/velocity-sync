package com.github.selfcrafted.mqttsync.message;

import java.util.UUID;

/**
 * Representation of a player connecting to a backend server.
 * If no {@link #connect_backend} is set, the player disconnected from the network
 * If no {@link #connect_last_backend} is set, the player connected to the network
 */
public class PlayerConnectionMessage extends SyncMessage {
    private UUID connect_player;
    private String connect_backend;
    private String connect_last_backend;

    public PlayerConnectionMessage() {
        super(MessageSerializer.CLASS2NAME.get(PlayerConnectionMessage.class));
    }

    public UUID getPlayer() {
        return connect_player;
    }

    public void setPlayer(UUID player) {
        this.connect_player = player;
    }

    public String getBackend() {
        return connect_backend;
    }

    public void setBackend(String backend) {
        this.connect_backend = backend;
    }

    public String getLastBackend() {
        return connect_last_backend;
    }

    public void setLastBackend(String lastBackend) {
        this.connect_last_backend = lastBackend;
    }
}
