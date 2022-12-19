package io.github.hotlava03.chatutils.mixin;

import io.github.hotlava03.chatutils.events.JoinServerEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class JoinServerMixin {
    @Inject(at = @At("RETURN"),
            method = "connect(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;Lnet/minecraft/client/network/ServerInfo;)V")
    public void onServerJoin(MinecraftClient client, ServerAddress address, ServerInfo info, CallbackInfo ci) {
        JoinServerEvent.LISTENERS.fire(new JoinServerEvent(ci, info));
    }
}
