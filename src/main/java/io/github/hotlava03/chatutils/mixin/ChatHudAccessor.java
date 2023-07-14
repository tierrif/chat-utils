package io.github.hotlava03.chatutils.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChatHud.class)
public interface ChatHudAccessor {
    @Accessor
    List<String> getMessageHistory();

    @Accessor
    List<ChatHudLine.Visible> getVisibleMessages();

    @Accessor
    List<ChatHudLine> getMessages();

    @Invoker("getMessageLineIndex")
    int invokeGetMessageLineIndex(double chatLineX, double chatLineY);

    @Invoker("toChatLineX")
    double invokeToChatLineX(double x);

    @Invoker("toChatLineY")
    double invokeToChatLineY(double y);
}
