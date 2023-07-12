package io.github.hotlava03.chatutils.mixin;

import io.github.hotlava03.chatutils.events.CopyToClipboardCallback;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.util.OrderedTextAdapter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.MutableText;
import net.minecraft.text.TextContent;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.IntStream;

@Mixin(ChatHud.class)
public abstract class ChatClickMixin {

    @Shadow
    @Final
    protected abstract int getMessageLineIndex(double chatLineX, double chatLineY);

    @Shadow
    @Final
    protected abstract double toChatLineX(double x);

    @Shadow
    @Final
    protected abstract double toChatLineY(double y);

    @Shadow
    @Final
    private List<ChatHudLine.Visible> visibleMessages;

    @Shadow
    @Final
    private List<ChatHudLine> messages;

    @Inject(method = "mouseClicked",
            at = @At("HEAD"))
    private void onChatClick(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!(client.currentScreen instanceof ChatScreen)) return;

        int lineSelected = getMessageLineIndex(toChatLineX(mouseX), toChatLineY(mouseY));

        // User clicked in the middle of nowhere
        if (lineSelected == -1) return;

        // This is a list containing all endOfEntry lines' indices
        var indexesOfEntryEnds = IntStream.range(0, visibleMessages.size())
                .filter(index -> visibleMessages.get(index).endOfEntry())
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

        // This is where we transform the index of the visibleMessage into an index we can use against message
        // We have the entry end index, a list containing ONLY entry ends, and `messages` only contains whole entries
        int indexOfMessage = indexesOfEntryEnds.indexOf(indexOfMessageEntryEnd);
        var message = messages.get(indexOfMessage).content().asOrderedText();

        var newText = MutableText.of(TextContent.EMPTY);

        if (ChatUtilsConfig.COPY_COLORS.value()) {
            newText.append(OrderedTextAdapter.orderedTextToMutableText(message));
        } else {
            newText.append(OrderedTextAdapter.orderedTextToString(message));
        }

        CopyToClipboardCallback.EVENT.invoker().accept(newText);
    }
}
