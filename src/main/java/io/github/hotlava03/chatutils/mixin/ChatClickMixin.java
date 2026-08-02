package io.github.hotlava03.chatutils.mixin;

import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.MouseButtonEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.hotlava03.chatutils.events.CopyToClipboardCallback;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.util.ChatHudUtils;
import io.github.hotlava03.chatutils.util.KeyUtils;
import io.github.hotlava03.chatutils.util.StringUtils;

@Mixin(ChatScreen.class)
public abstract class ChatClickMixin {

    @Inject(method = "mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z",
            at = @At("HEAD"))
    private void onChatClick(MouseButtonEvent event, boolean doubleClick,
                             CallbackInfoReturnable<Boolean> cir) {
        if (ChatUtilsConfig.ENABLE_COPY_KEY.value()) {
            if (!KeyUtils.isKeyDown(ChatUtilsConfig.COPY_KEY.value())) {
                return;
            }
        }

        var message = ChatHudUtils.getMessageAt(event.x(), event.y());
        if (message == null) {
            return;
        }

        var adventure = StringUtils.asAdventure(message.content());
        CopyToClipboardCallback.EVENT.invoker()
                .accept(StringUtils.unpackLegacyCodes(adventure), message.addedTime());
    }
}
