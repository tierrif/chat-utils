package io.github.hotlava03.chatutils.events;

import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class JoinServerEvent extends Event {
    public static ListenerManager<JoinServerEvent> LISTENERS = new ListenerManager<>();

    private final ServerInfo info;

    public JoinServerEvent(CallbackInfo callbackInfo, ServerInfo info) {
        super(callbackInfo);
        this.info = info;
    }

    public ServerInfo getInfo() {
        return info;
    }
}
