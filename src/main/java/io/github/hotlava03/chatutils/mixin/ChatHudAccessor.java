package io.github.hotlava03.chatutils.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChatHud.class)
public interface ChatHudAccessor {
    @Accessor
    List<String> getMessageHistory();

    // @Accessor List<ChatHudLine.Visible> getVisibleMessages();
}
