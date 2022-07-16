package io.github.hotlava03.chatutils.util;

import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;

import java.io.File;

public class IoUtils {
    public static File getConfigDirectory() {
        File configDir = new File(MinecraftClient.getInstance().runDirectory, "config");
        if (!configDir.isDirectory() && configDir.exists())
            LogManager.getLogger().warn("[chat-utils] A file was found in place of the config folder!");
        else if (!configDir.isDirectory()) {
            boolean created = configDir.mkdir();
            if (!created) {
                LogManager.getLogger().warn("[chat-utils] Failed to create config folder! This may cause errors!");
            }
        }
        return configDir;
    }
}
