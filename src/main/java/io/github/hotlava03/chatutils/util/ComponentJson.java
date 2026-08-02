package io.github.hotlava03.chatutils.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public final class ComponentJson {
    private ComponentJson() {}

    public static String toJson(Component component) {
        return ComponentSerialization.CODEC
                .encodeStart(ops(), component)
                .result()
                .map(JsonElement::toString)
                .orElse(null);
    }

    public static Component fromJson(String json) {
        if (json == null) return null;
        try {
            JsonElement element = JsonParser.parseString(json);
            return ComponentSerialization.CODEC.parse(ops(), element).result().orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private static DynamicOps<JsonElement> ops() {
        var connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            return connection.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        }
        return JsonOps.INSTANCE;
    }
}
