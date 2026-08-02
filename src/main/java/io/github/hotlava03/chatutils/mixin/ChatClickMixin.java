package io.github.hotlava03.chatutils.mixin;

import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.MouseButtonEvent;

import com.mojang.blaze3d.platform.InputConstants;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import io.github.hotlava03.chatutils.events.CopyToClipboardCallback;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.util.ChatHudUtils;

@Mixin(ChatScreen.class)
public abstract class ChatClickMixin {

    @Inject(method = "mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z",
            at = @At("HEAD"))
    private void onChatClick(MouseButtonEvent event, boolean doubleClick,
                             CallbackInfoReturnable<Boolean> cir) {
        Minecraft client = Minecraft.getInstance();

        if (ChatUtilsConfig.ENABLE_COPY_KEY.value()) {
            if (!InputConstants.isKeyDown(client.getWindow(), ChatUtilsConfig.COPY_KEY.value())) {
                return;
            }
        }

        var message = ChatHudUtils.getMessageAt(event.x(), event.y());
        if (message != null) {
            // asAdventure doesn't realise Mojang is insane and parses colours inside `content,`
            // so we have to take matters into our own hands.
            // TODO: file a bug report with adventure upstream
            var adventure = MinecraftClientAudiences.of().asAdventure(message.content());
            var component = mapComponentChildren(adventure);
            if (component != null) {
                CopyToClipboardCallback.EVENT.invoker().accept(component, message.addedTime());
            }
        }
    }

    @Unique
    private Component mapComponentChildren(Component component) {
        Function<Component, ? extends Component> func = anyChild -> {
            if (anyChild instanceof TextComponent c &&
                    c.content().indexOf(LegacyComponentSerializer.SECTION_CHAR) != -1) {
                return LegacyComponentSerializer.legacySection()
                        .deserialize(c.content())
                        .style(newStyle -> newStyle.merge(c.style(),
                                Style.Merge.Strategy.IF_ABSENT_ON_TARGET));
            } else return anyChild;
        };

        Component mapped;
        if (component instanceof TextComponent c) {
            mapped = c.toBuilder().mapChildrenDeep(func).build();
        } else if (component instanceof TranslatableComponent c) {
            mapped = c.toBuilder().mapChildrenDeep(func).build();
        } else {
            mapped = null;
        }

        return mapped;
    }
}
