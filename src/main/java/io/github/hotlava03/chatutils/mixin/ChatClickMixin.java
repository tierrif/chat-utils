package io.github.hotlava03.chatutils.mixin;

import io.github.hotlava03.chatutils.events.CopyToClipboardCallback;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.util.OrderedTextAdapter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static io.github.hotlava03.chatutils.util.StringUtils.textToLegacy;

@Mixin(ChatHud.class)
public abstract class ChatClickMixin {
    @Shadow @Final
    protected abstract int getMessageLineIndex(double chatLineX, double chatLineY);

    @Shadow @Final
    protected abstract double toChatLineX(double x);

    @Shadow @Final
    protected abstract double toChatLineY(double y);

    @Shadow @Final
    private List<ChatHudLine.Visible> visibleMessages;

    @Inject(method = "mouseClicked",
            at = @At("HEAD"))
    private void onChatClick(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!(client.currentScreen instanceof ChatScreen)) return;

        int index = findIndex(mouseX, mouseY);
        if (index == -1) {
            return;
        }

        MutableText text = MutableText.of(TextContent.EMPTY);
        for (OrderedText t : walkEntry(index)) {
            if (ChatUtilsConfig.COPY_COLORS.value()) {
                text.append(OrderedTextAdapter.orderedTextToMutableText(t));
            } else {
                text.append(OrderedTextAdapter.orderedTextToString(t));
            }
        }

        CopyToClipboardCallback.EVENT.invoker().accept(text);
    }

    @Unique
    private int findIndex(double x, double y) {
        int line = getMessageLineIndex(toChatLineX(x), toChatLineY(y));
        if (line == -1) return -1;

        int chatSize = visibleMessages.size();

        int i = line;
        while (i < chatSize) {
            if (i != line && visibleMessages.get(i).endOfEntry()) {
                return i - 1;
            }
            i++;
        }

        return i - 1;
    }

    @Unique
    private List<OrderedText> walkEntry(int index) {
        List<OrderedText> text = new ArrayList<>();
        int i = index;
        while (i >= 0) {
            ChatHudLine.Visible visible = visibleMessages.get(i);
            text.add(visible.content());
            if (visible.endOfEntry()) {
                break;
            }
            i--;
        }

        return text;
    }
}
