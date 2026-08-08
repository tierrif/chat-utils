package io.github.hotlava03.chatutils.listeners;

import java.time.Instant;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;

import com.mojang.authlib.GameProfile;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

import io.github.hotlava03.chatutils.fileio.ChatFilter;
import io.github.hotlava03.chatutils.util.FilteredMessageLog;
import org.jetbrains.annotations.NotNull;

public class ChatFilterListener implements ClientReceiveMessageEvents.AllowChat, ClientReceiveMessageEvents.AllowGame {
    @Override
    public boolean allowReceiveChatMessage(
            @NotNull Component message,
            PlayerChatMessage signedMessage,
            GameProfile sender,
            ChatType.@NotNull Bound params,
            @NotNull Instant receptionTimestamp
    ) {
        return allow(message);
    }

    @Override
    public boolean allowReceiveGameMessage(@NotNull Component message, boolean overlay) {
        // Overlay messages are the action bar, not chat.
        return overlay || allow(message);
    }

    private boolean allow(Component message) {
        var filter = ChatFilter.firstMatch(message);
        if (filter == null) {
            return true;
        }

        FilteredMessageLog.getInstance().record(message, filter.pattern());

        return false;
    }
}
