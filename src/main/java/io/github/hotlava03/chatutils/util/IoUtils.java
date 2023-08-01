package io.github.hotlava03.chatutils.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class IoUtils {
    public static File getConfigDirectory() {
        File configDir = new File(MinecraftClient.getInstance().runDirectory, "config/chatutils");
        synchronized (IoUtils.class) { // Synchronise this in case multiple threads try this.
            if (!configDir.isDirectory() && configDir.exists())
                LogManager.getLogger().warn("[chat-utils] A file was found in place of the config folder!");
            else if (!configDir.isDirectory()) {
                boolean created = configDir.mkdirs();
                if (!created) {
                    LogManager.getLogger().warn("[chat-utils] Failed to create config folder! This may cause errors!");
                }
            }
        }
        return configDir;
    }

    public static void writeJsonToFile(String name, JsonObject object, Gson gson) throws IOException {
        // Write to temporary file.
        File dir = IoUtils.getConfigDirectory();
        try(PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dir, name + "~")), StandardCharsets.UTF_8)))){
            gson.toJson(object, writer);
        }
        // Overwrite proper file atomically with temporary file after write has finished.
        Files.move(
                dir.toPath().resolve(name + "~"),
                dir.toPath().resolve(name),
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING
        );
    }
}
