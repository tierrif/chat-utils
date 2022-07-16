package io.github.hotlava03.chatutils.events;

import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class JoinServerEvent extends Event {
    public static ListenerManager<JoinServerEvent> LISTENERS = new ListenerManager<>();
    private final GameJoinS2CPacket packet;

    public JoinServerEvent(CallbackInfo callbackInfo, GameJoinS2CPacket packet) {
        super(callbackInfo);
        this.packet = packet;
    }

    public GameJoinS2CPacket getPacket() {
        return packet;
    }
}
