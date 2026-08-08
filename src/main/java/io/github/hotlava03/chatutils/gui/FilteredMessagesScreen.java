package io.github.hotlava03.chatutils.gui;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import io.github.hotlava03.chatutils.util.FilteredMessageLog;
import io.github.hotlava03.chatutils.util.StringUtils;
import org.jetbrains.annotations.NotNull;

public class FilteredMessagesScreen extends Screen {
    private static final int ROW_HEIGHT = 36;
    private static final int ROW_WIDTH = 310;
    private static final int BUTTON_WIDTH = 110;
    private static final int BUTTON_HEIGHT = 20;
    private static final int COPY_WIDTH = 44;
    private static final int LINE_SPACING = 11;
    private static final int GAP = 4;
    private static final int TEXT_COLOR = -1;
    private static final int SUBTLE_TEXT_COLOR = 0xFFA0A0A0;

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    private final Screen parent;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private MessageList list;
    private Button clearButton;

    public FilteredMessagesScreen(Screen parent) {
        super(Component.translatable("chat-utils.filters.log.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.layout.addTitleHeader(this.title, this.font);
        this.list = this.layout.addToContents(new MessageList());

        var footer = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        this.clearButton = footer.addChild(Button.builder(
                        Component.translatable("chat-utils.filters.log.clear"),
                        button -> {
                            FilteredMessageLog.getInstance().clear();
                            refresh();
                        })
                .width(BUTTON_WIDTH)
                .build());
        footer.addChild(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .width(BUTTON_WIDTH)
                .build());

        refresh();
        this.layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    private void refresh() {
        var entries = FilteredMessageLog.getInstance().entries();
        this.list.replaceWith(entries);
        this.clearButton.active = !entries.isEmpty();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        this.list.updateSize(this.width, this.layout);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        if (this.list.children().isEmpty()) {
            graphics.centeredText(this.font, Component.translatable("chat-utils.filters.log.empty"),
                    this.width / 2, this.height / 2 - this.font.lineHeight / 2, SUBTLE_TEXT_COLOR);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }

    private class MessageList extends ContainerObjectSelectionList<MessageList.@NotNull MessageEntry> {
        MessageList() {
            super(
                    FilteredMessagesScreen.this.minecraft,
                    FilteredMessagesScreen.this.width,
                    FilteredMessagesScreen.this.layout.getContentHeight(),
                    FilteredMessagesScreen.this.layout.getHeaderHeight(),
                    ROW_HEIGHT
            );
        }

        @Override
        public int getRowWidth() {
            return ROW_WIDTH;
        }

        void replaceWith(List<FilteredMessageLog.Entry> entries) {
            clearEntries();
            entries.forEach(entry -> addEntry(new MessageEntry(entry)));
        }

        private class MessageEntry extends ContainerObjectSelectionList.Entry<@NotNull MessageEntry> {
            private final FilteredMessageLog.Entry entry;
            private final String heading;
            private final String message;
            private final Button copy;

            MessageEntry(FilteredMessageLog.Entry entry) {
                this.entry = entry;
                this.heading = Component.translatable("chat-utils.filters.log.entry",
                        TIME.format(Instant.ofEpochMilli(entry.timestamp())), entry.filter()).getString();
                this.message = StringUtils.plainText(entry.message());
                this.copy = Button.builder(Component.translatable("chat-utils.filters.log.copy"),
                                button -> copyToClipboard())
                        .bounds(0, 0, COPY_WIDTH, BUTTON_HEIGHT)
                        .build();
            }

            private void copyToClipboard() {
                var adventure = StringUtils.unpackLegacyCodes(StringUtils.asAdventure(this.entry.message()));
                Minecraft.getInstance().keyboardHandler.setClipboard(StringUtils.forClipboard(adventure));
            }

            @Override
            public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
                var font = FilteredMessagesScreen.this.font;
                int left = getContentX();
                int top = getContentY();

                this.copy.setPosition(getContentRight() - COPY_WIDTH, getContentYMiddle() - BUTTON_HEIGHT / 2);
                this.copy.extractRenderState(graphics, mouseX, mouseY, a);

                int textWidth = Math.max(0, getContentWidth() - COPY_WIDTH - GAP);
                graphics.text(font, font.plainSubstrByWidth(this.heading, textWidth), left, top,
                        SUBTLE_TEXT_COLOR);
                graphics.text(font, font.plainSubstrByWidth(this.message, textWidth), left, top + LINE_SPACING,
                        TEXT_COLOR);
            }

            @Override
            public @NotNull List<? extends GuiEventListener> children() {
                return List.of(this.copy);
            }

            @Override
            public @NotNull List<? extends NarratableEntry> narratables() {
                return List.of(this.copy);
            }
        }
    }
}
