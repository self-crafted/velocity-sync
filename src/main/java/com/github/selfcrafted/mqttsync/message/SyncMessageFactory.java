package com.github.selfcrafted.mqttsync.message;

public class SyncMessageFactory {
    private final String senderId;

    public SyncMessageFactory(String senderId) {
        this.senderId = senderId;
    }
    public SyncMessage createSyncMessage(Type type) {
        SyncMessage msg = switch (type) {
            case CHAT -> new PlayerChatMessage();
            case PLAYER_CONNECTION -> new PlayerConnectionMessage();
            case SERVER_REGISTRATION -> new ServerRegistrationMessage();
        };
        msg.setSender(senderId);
        return msg;
    }

    public enum Type {
        CHAT,
        PLAYER_CONNECTION,
        SERVER_REGISTRATION
    }
}
