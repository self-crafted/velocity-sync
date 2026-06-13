package com.github.selfcrafted.mqttsync;

import com.github.selfcrafted.mqttsync.message.PlayerChatMessage;
import com.github.selfcrafted.mqttsync.message.PlayerConnectionMessage;
import com.github.selfcrafted.mqttsync.message.ServerRegistrationMessage;

public interface CallbackHandler {
    void handlePlayerConnection(PlayerConnectionMessage msg);
    void handlePlayerChat(PlayerChatMessage msg);
    void handleServerRegistration(ServerRegistrationMessage msg);
}
