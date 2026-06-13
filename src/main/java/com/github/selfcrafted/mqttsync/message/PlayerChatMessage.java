package com.github.selfcrafted.mqttsync.message;

import java.util.UUID;

/**
 * Representation of a chat message
 */
public class PlayerChatMessage extends SyncMessage {
    private UUID chat_sender;
    private String chat_message;

    public PlayerChatMessage() {
        super(MessageSerializer.CLASS2NAME.get(PlayerChatMessage.class));
    }

    public UUID getPlayer() {
        return chat_sender;
    }

    public void setPlayer(UUID player) {
        this.chat_sender = player;
    }

    public String getMessage() {
        return chat_message;
    }

    public void setMessage(String message) {
        this.chat_message = message;
    }
}
