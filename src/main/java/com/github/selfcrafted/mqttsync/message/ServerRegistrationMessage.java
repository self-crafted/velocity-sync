package com.github.selfcrafted.mqttsync.message;

/**
 * Representation of a server protocol announcement
 */
public class ServerRegistrationMessage extends SyncMessage {
    private String protocol_server;
    private int protocol_version;

    public ServerRegistrationMessage() {
        super(MessageSerializer.CLASS2NAME.get(ServerRegistrationMessage.class));
    }

    public String getServer() {
        return protocol_server;
    }

    public void setServer(String server) {
        this.protocol_server = server;
    }

    public int getVersion() {
        return protocol_version;
    }

    public void setVersion(int version) {
        this.protocol_version =  version;
    }
}
