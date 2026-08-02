package io.github.hotlava03.chatutils.util;

import net.minecraft.client.Minecraft;

import com.mojang.blaze3d.platform.InputConstants;

import org.lwjgl.glfw.GLFW;

public class KeyUtils {
    /**
     * GLFW rejects anything outside this range with GLFW_INVALID_ENUM ("Invalid key N"), and Minecraft's
     * error callback logs that once per call — which is once per frame when the query happens while
     * rendering. Anything that came from disk or from a key binding widget has to be checked first.
     */
    public static boolean isValidKey(int key) {
        return key >= GLFW.GLFW_KEY_SPACE && key <= GLFW.GLFW_KEY_LAST;
    }

    public static boolean isKeyDown(int key) {
        if (!isValidKey(key)) return false;
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), key);
    }
}
