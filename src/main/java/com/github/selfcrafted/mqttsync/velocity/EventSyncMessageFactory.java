package com.github.selfcrafted.mqttsync.velocity;

import com.github.selfcrafted.mqttsync.message.PlayerChatMessage;
import com.github.selfcrafted.mqttsync.message.PlayerConnectionMessage;
import com.github.selfcrafted.mqttsync.message.SyncMessage;
import com.github.selfcrafted.mqttsync.message.SyncMessageFactory;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;

public class EventSyncMessageFactory extends SyncMessageFactory {
    public EventSyncMessageFactory(String senderId) {
        super(senderId);
    }

    public SyncMessage createFromEvent(Object event) {
        if (event instanceof PlayerChatEvent chatEvent) {
            PlayerChatMessage msg = (PlayerChatMessage) createSyncMessage(Type.CHAT);
            msg.setMessage(chatEvent.getMessage());
            msg.setPlayer(chatEvent.getPlayer().getUniqueId());
            return msg;
        }

        if (event instanceof ServerConnectedEvent serverConnectedEvent) {
            PlayerConnectionMessage msg = (PlayerConnectionMessage) createSyncMessage(Type.PLAYER_CONNECTION);
            msg.setPlayer(serverConnectedEvent.getPlayer().getUniqueId());
            msg.setBackend(serverConnectedEvent.getServer().getServerInfo().getName());
            msg.setLastBackend(serverConnectedEvent.getPreviousServer().isPresent() ?
                    serverConnectedEvent.getPreviousServer().get().getServerInfo().getName() : null);
            return msg;
        }

        if (event instanceof DisconnectEvent disconnectEvent) {
            PlayerConnectionMessage msg = (PlayerConnectionMessage) createSyncMessage(Type.PLAYER_CONNECTION);
            msg.setPlayer(disconnectEvent.getPlayer().getUniqueId());
            msg.setBackend(null);
            msg.setLastBackend(null);
            return msg;
        }

        throw new IllegalArgumentException("Invalid event type: " + event.getClass());
    }
}
