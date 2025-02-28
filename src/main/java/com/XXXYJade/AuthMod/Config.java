package com.XXXYJade.AuthMod;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private Config() {
    }

    private static Config instance;
    private static String passwordDataFilePath;
    private static int passwordMaxLength;
    private static int passwordMinLength;
    private static final Logger LOGGER = LogManager.getLogger("authmod");
    private static final Gson gson = new Gson();
    private static JsonObject config;

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
            instance.loadConfig();
        }
        return instance;
    }
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
    }

    private void loadConfig() {
        try {
//            Path configDir = Path.of("config/authmod");
//            Files.createDirectories(configDir);

//            Path configPath = configDir.resolve("config.json");
//            if (Files.notExists(configPath)) {
//                try (InputStream in = Config.class.getResourceAsStream("/config/authmod/config.json")) {
//                    if (in != null) {
//                        Files.copy(in, configPath);
//                        LOGGER.info( "配置文件创建成功");
//                    } else {
//                        LOGGER.error("配置文件丢失!");
//                        return;
//                    }
//                }
//            }
            Path configPath = Path.of("/config/authmod/config.json");
            try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
                config = new Gson().fromJson(reader, JsonObject.class);
                JsonObject password = config.getAsJsonObject("password");
                passwordMaxLength = password.get("max_length").getAsInt();
                passwordMinLength = password.get("min_length").getAsInt();
                passwordDataFilePath = password.get("file_path").getAsString();
            } catch (IOException e) {
                LOGGER.error("加载配置文件时出现异常:", e);
            }
        }catch (Exception e){
            LOGGER.error("加载配置文件时出现异常:", e);
        }
//        } catch (IOException e) {
//            LOGGER.error("创建配置目录时出现异常:", e);
//        }
    }

    public int getPasswordMaxLenth() {
        return passwordMaxLength;
    }

    public int getPasswordMinLenth() {
        return passwordMinLength;
    }

    public String getPasswordDataFilePath() {
        return passwordDataFilePath;
    }
}
