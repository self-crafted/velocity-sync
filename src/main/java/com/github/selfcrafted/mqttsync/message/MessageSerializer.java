package com.github.selfcrafted.mqttsync.message;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.stream.Collectors;

public class MessageSerializer {
    private final ObjectMapper mapper;

    public static final Map<Class<?>, String> CLASS2NAME = Map.of(
            PlayerChatMessage.class, "chat",
            PlayerConnectionMessage.class, "player_connection",
            ServerRegistrationMessage.class, "server_registration"
    );

    public static final Map<String, Class<?>> NAME2CLASS = CLASS2NAME.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    public MessageSerializer() {
        this.mapper = new ObjectMapper(new JsonFactory());
        mapper.findAndRegisterModules();
        mapper.registerSubtypes(NAME2CLASS.values());
    }

    public String toJson(SyncMessage message) throws JsonProcessingException {
        return mapper.writeValueAsString(message);
    }

    public SyncMessage fromJson(String json) throws JsonProcessingException {
        return mapper.readValue(json, SyncMessage.class);
    }
}
