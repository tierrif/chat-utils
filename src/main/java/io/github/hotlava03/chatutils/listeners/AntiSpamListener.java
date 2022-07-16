package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.config.ChatUtilsConfig;
import io.github.hotlava03.chatutils.events.EventHandler;
import io.github.hotlava03.chatutils.events.types.MessageReceiveEvent;
import io.github.hotlava03.chatutils.events.types.MessageReceiveListener;
import io.github.hotlava03.chatutils.util.StringUtils;
import net.minecraft.text.Text;

public class AntiSpamListener extends MessageReceiveListener {
    private Text lastMessage;
    private int spamCounter = 1;

    @Override
    public void onMessageReceive(MessageReceiveEvent e) {
        if (ChatUtilsConfig.ANTI_SPAM.value()) {
            double prejudice = 0;
            if (this.lastMessage == null) {
                this.lastMessage = e.getText();
                this.spamCounter = 1;
                return;
            }
            if (StringUtils.getDifference(e.getText().getString(), this.lastMessage.getString()) <= prejudice) {
                this.spamCounter++;
                e.getTextAsMutable().append(StringUtils.translateAlternateColorCodes(" &8[&c" + this.spamCounter + "x&8]"));
                e.getLines().remove(0);
            } else {
                this.lastMessage = e.getText();
                this.spamCounter = 1;
            }
        }
    }

    @Override
    public EventHandler.EventType getType() {
        return EventHandler.EventType.MESSAGE_RECEIVE;
    }
}
