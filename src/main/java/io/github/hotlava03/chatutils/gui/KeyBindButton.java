package io.github.hotlava03.chatutils.gui;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

import org.lwjgl.glfw.GLFW;

import io.github.hotlava03.chatutils.fileio.ChatMacro;
import org.jetbrains.annotations.NotNull;

public class KeyBindButton extends Button.Plain {
    private final ChatMacro macro;
    private boolean listening;

    public KeyBindButton(int x, int y, int width, int height, ChatMacro macro) {
        super(x, y, width, height, Component.empty(), button -> {
        }, DEFAULT_NARRATION);
        this.macro = macro;
        updateMessage();
    }

    @Override
    public void onPress(@NotNull InputWithModifiers input) {
        this.listening = true;
        updateMessage();
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        if (!this.listening) {
            return super.keyPressed(event);
        }

        this.macro.setKey(event.key() == GLFW.GLFW_KEY_ESCAPE ? GLFW.GLFW_KEY_UNKNOWN : event.key());
        this.listening = false;
        updateMessage();

        return true;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);

        // Clicking elsewhere should cancel the capture rather than leave it armed off-screen.
        if (!focused && this.listening) {
            this.listening = false;
            updateMessage();
        }
    }

    private void updateMessage() {
        if (this.listening) {
            setMessage(Component.translatable("chat-utils.macros.key.listening"));
            return;
        }

        setMessage(Component.translatable("chat-utils.macros.key",
                this.macro.isBound()
                        ? InputConstants.Type.KEYSYM.getOrCreate(this.macro.key()).getDisplayName()
                        : Component.translatable("chat-utils.macros.key.unbound")));
    }
}
