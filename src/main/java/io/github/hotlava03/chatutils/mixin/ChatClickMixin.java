package io.github.hotlava03.chatutils.mixin;

import io.github.hotlava03.chatutils.events.CopyToClipboardCallback;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.util.ChatHudUtils;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatHud.class)
public abstract class ChatClickMixin {

    @Inject(method = "mouseClicked",
            at = @At("HEAD"))
    private void onChatClick(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!(client.currentScreen instanceof ChatScreen)) return;

        if (ChatUtilsConfig.ENABLE_COPY_KEY.value()) {
            if (!InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(),
                    ChatUtilsConfig.COPY_KEY.value())) {
                return;
            }
        }

        var message = ChatHudUtils.getMessageAt(mouseX, mouseY);
        if (message != null) {
            // asComponent doesn't realise Mojang is insane and parses colours inside `content,`
            // so we have to take matters into our own hands.
            // TODO: file a bug report with adventure upstream
            var component = ((TextComponent) message.content().asComponent()).toBuilder()
                    .mapChildrenDeep(anyChild -> {
                        if (anyChild instanceof TextComponent c &&
                                c.content().indexOf(LegacyComponentSerializer.SECTION_CHAR) != -1) {
                            return LegacyComponentSerializer.legacySection()
                                    .deserialize(c.content())
                                    .style(newStyle -> newStyle.merge(c.style(),
                                            Style.Merge.Strategy.IF_ABSENT_ON_TARGET));
                        } else return anyChild;
                    })
                    .build();
            CopyToClipboardCallback.EVENT.invoker().accept(component, message.creationTick());
        }
    }
}
