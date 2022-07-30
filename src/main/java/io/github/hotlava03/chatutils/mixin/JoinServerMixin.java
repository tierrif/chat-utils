package io.github.hotlava03.chatutils.mixin;

import io.github.hotlava03.chatutils.events.JoinServerEvent;
import net.minecraft.client.MinecraftClientGame;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class JoinServerMixin {
    @Inject(at = @At("RETURN"), method = "connect(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;)V")
    public void onServerJoin(CallbackInfo info) {
        JoinServerEvent.LISTENERS.fire(new JoinServerEvent(info));
    }
}
