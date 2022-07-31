package io.github.hotlava03.chatutils.mixin;

import io.github.hotlava03.chatutils.events.JoinServerEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ConnectScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ConnectScreen.class)
public class JoinServerMixin {
    @Mixin(ChatHud.class)
    public interface MessageHistoryAccessor {
        @Accessor
        List<String> getMessageHistory();
    }

    @Inject(at = @At("RETURN"), method = "connect(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;)V")
    public void onServerJoin(CallbackInfo info) {
        var chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
        JoinServerEvent.LISTENERS.fire(new JoinServerEvent(info, ((MessageHistoryAccessor) chatHud).getMessageHistory()));
    }
}
