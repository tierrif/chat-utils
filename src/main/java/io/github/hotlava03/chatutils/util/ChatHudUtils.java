package io.github.hotlava03.chatutils.util;

import io.github.hotlava03.chatutils.mixin.ChatHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.apache.logging.log4j.LogManager;

import java.util.stream.IntStream;

public class ChatHudUtils {
    public static ChatHudLine getMessageAt(double x, double y) {
        var accessor = (ChatHudAccessor) MinecraftClient.getInstance().inGameHud.getChatHud();
        int lineSelected = accessor.invokeGetMessageLineIndex(
                accessor.invokeToChatLineX(x), accessor.invokeToChatLineY(y));

        // User clicked in the middle of nowhere
        if (lineSelected == -1) return null;

        // This is a list containing all endOfEntry lines' indices
        var indexesOfEntryEnds = IntStream.range(0, accessor.getVisibleMessages().size())
                .filter(index -> accessor.getVisibleMessages().get(index).endOfEntry())
                .boxed()
                .toList();

        // Get the index of the final entry belonging to the message of this line
        int indexOfMessageEntryEnd = indexesOfEntryEnds
                .stream()
                .filter(index -> index <= lineSelected)
                .reduce((a, b) -> b) // Improvised findLast()
                .orElse(-1);

        if (indexOfMessageEntryEnd == -1) {
            LogManager.getLogger().warn("Something cursed happened (indexOfMessageEntryEnd == -1)");
        }

        int indexOfMessage = indexesOfEntryEnds.indexOf(indexOfMessageEntryEnd);
        return accessor.getMessages().get(indexOfMessage);
    }
}
