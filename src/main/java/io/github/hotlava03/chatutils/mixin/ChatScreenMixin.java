package io.github.hotlava03.chatutils.mixin;

import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.hotlava03.chatutils.events.ChatScreenInitCallback;
import io.github.hotlava03.chatutils.events.ChatScreenCloseCallback;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    protected ChatScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        ChatScreenInitCallback.EVENT.invoker().onInit((ChatScreen) (Object) this, this::addRenderableWidget);
    }

    @Inject(method = "onClose", at = @At("TAIL"))
    private void onClose(CallbackInfo ci) {
        ChatScreenCloseCallback.EVENT.invoker().onClose((ChatScreen) (Object) this);
    }
}
