package com.github.selfcrafted.mqttsync;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private final ObjectMapper mapper;
    private final Logger logger;
    private final Path configPath;
    private Config config;

    public ConfigManager(Logger logger, Path configPath) {
        this.mapper = new ObjectMapper(new YAMLFactory());
        this.logger = logger;
        this.configPath = configPath;
        mapper.findAndRegisterModules();
        read();
    }

    public Config config() {
        return this.config;
    }

    public void read() {
        try {
            try {
                String configText = Files.readString(configPath.toAbsolutePath());
                this.config = mapper.readValue(configText, Config.class);
            } catch (FileNotFoundException e) {
                this.config = new Config();
                save();
            } catch (JsonProcessingException e) {
                logger.error("Error parsing configuration. Saving backup and recreating with defaults", e);
                Files.move(configPath, Path.of(configPath + ".bak"));
                this.config = new Config();
                save();
            }
        } catch (IOException e) {
            logger.error("Error reading config file", e);
        }
    }

    private void save() throws IOException {
        try {
            Files.writeString(configPath, this.mapper.writeValueAsString(config));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Config {
        public final String proxy_id;
        public final boolean format_chat;
        public final String broker_addr;
        public final String broker_username;
        public final String broker_password;
        public final byte broker_qos;

        public Config() {
            this.proxy_id = "proxy-1";
            this.format_chat = true;
            this.broker_addr = "tcp://localhost:1883";
            this.broker_username = "";
            this.broker_password = "";
            this.broker_qos = 1;
        }

        @JsonCreator
        public Config(String proxy_id,
                      boolean format_chat,
                      String broker_addr,
                      String broker_username,
                      String broker_password,
                      byte broker_qos) {
            this.proxy_id = proxy_id;
            this.format_chat = format_chat;
            this.broker_addr = broker_addr;
            this.broker_username = broker_username;
            this.broker_password = broker_password;
            this.broker_qos = broker_qos;
        }
    }
}
