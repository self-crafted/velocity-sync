package com.github.selfcrafted.mqttsync.message;

import java.util.UUID;

public class SyncMessage {
    private final UUID msg_id;
    private String msg_sender;
    private final String msg_type;

    public SyncMessage(String type) {
        this.msg_id = UUID.randomUUID();
        this.msg_type = type;
    }

    public UUID getID() {
        return msg_id;
    }

    public String getSender() {
        return msg_sender;
    }

    public String getType() {
        return msg_type;
    }

    protected void setSender(String senderId) {
        this.msg_sender = senderId;
    }
}
