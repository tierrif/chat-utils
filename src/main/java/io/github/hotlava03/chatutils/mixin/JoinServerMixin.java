package io.github.hotlava03.chatutils.mixin;

import io.github.hotlava03.chatutils.events.JoinServerEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class JoinServerMixin {
    @Inject(at = @At("RETURN"), method = "onGameJoin")
    public void onServerJoin(GameJoinS2CPacket packet, CallbackInfo info) {
        JoinServerEvent.LISTENERS.fire(new JoinServerEvent(info, packet));
    }
}
