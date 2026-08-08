package io.github.hotlava03.chatutils.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import io.github.hotlava03.chatutils.fileio.ChatMacro;
import io.github.hotlava03.chatutils.util.MacroActions;
import org.jetbrains.annotations.NotNull;

public class MacrosScreen extends Screen {
    private static final int HEADER_HEIGHT = 48;
    private static final int FOOTER_HEIGHT = 33;
    private static final int ROW_HEIGHT = 48;
    private static final int ROW_WIDTH = 310;
    private static final int WIDGET_HEIGHT = 20;
    private static final int LABEL_WIDTH = 96;
    private static final int REMOVE_WIDTH = 54;
    private static final int BUTTON_WIDTH = 110;
    private static final int GAP = 4;
    private static final int LABEL_MAX_LENGTH = 32;

    private final Screen parent;
    private final Consumer<List<ChatMacro>> onDone;
    private final List<ChatMacro> macros;
    private HeaderAndFooterLayout layout;
    private MacroList list;

    public MacrosScreen(Screen parent, List<ChatMacro> macros, Consumer<List<ChatMacro>> onDone) {
        super(Component.translatable("chat-utils.macros.title"));
        this.parent = parent;
        this.onDone = onDone;
        this.macros = new ArrayList<>(macros.stream().map(ChatMacro::copy).toList());
    }

    @Override
    protected void init() {
        this.layout = new HeaderAndFooterLayout(this, HEADER_HEIGHT, FOOTER_HEIGHT);

        var header = this.layout.addToHeader(LinearLayout.vertical().spacing(GAP));
        header.addChild(new StringWidget(this.title, this.font),
                settings -> settings.alignHorizontallyCenter());
        header.addChild(new StringWidget(Component.translatable("chat-utils.macros.shortcuts.note")
                        .withStyle(ChatFormatting.GRAY), this.font),
                settings -> settings.alignHorizontallyCenter());

        this.list = this.layout.addToContents(new MacroList());
        this.macros.forEach(macro -> this.list.add(macroRow(macro)));

        var footer = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        footer.addChild(Button.builder(Component.translatable("chat-utils.macros.macros.add"),
                        button -> add())
                .width(BUTTON_WIDTH)
                .build());
        footer.addChild(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .width(BUTTON_WIDTH)
                .build());

        this.layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        this.list.updateSize(this.width, this.layout);
    }

    @Override
    public void onClose() {
        this.onDone.accept(ChatMacro.dropBlank(this.macros));
        this.minecraft.gui.setScreen(this.parent);
    }

    private void add() {
        var macro = new ChatMacro();
        this.macros.add(macro);
        this.list.add(macroRow(macro));
        this.list.setScrollAmount(this.list.maxScrollAmount());
    }

    private Row macroRow(ChatMacro macro) {
        return new Row(
                editBox("label", LABEL_MAX_LENGTH, macro.label(), macro::setLabel),
                editBox("message", MacroActions.MAX_LENGTH, macro.message(), macro::setMessage),
                new KeyBindButton(0, 0, LABEL_WIDTH, WIDGET_HEIGHT, macro),
                row -> {
                    this.macros.remove(macro);
                    this.list.remove(row);
                });
    }

    private EditBox editBox(String name, int maxLength, String value, Consumer<String> setter) {
        var box = new EditBox(this.font, 0, 0, ROW_WIDTH, WIDGET_HEIGHT,
                Component.translatable("chat-utils.macros." + name));
        box.setMaxLength(maxLength);
        box.setHint(Component.translatable("chat-utils.macros." + name + ".hint"));
        box.setValue(value);
        box.setResponder(setter);

        return box;
    }

    /**
     * Name and message on top, the key binding plus remove underneath.
     */
    private class Row extends ContainerObjectSelectionList.Entry<@NotNull Row> {
        private final EditBox label;
        private final EditBox message;
        private final AbstractWidget key;
        private final Button remove;
        private final List<AbstractWidget> widgets;

        Row(EditBox label, EditBox message, AbstractWidget key, Consumer<Row> onRemove) {
            this.label = label;
            this.message = message;
            this.key = key;
            this.remove = Button.builder(Component.translatable("chat-utils.macros.remove"),
                            button -> onRemove.accept(this))
                    .bounds(0, 0, REMOVE_WIDTH, WIDGET_HEIGHT)
                    .build();
            this.widgets = List.of(label, message, key, this.remove);
        }

        @Override
        public void extractContent(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY,
                                   boolean hovered, float delta) {
            int left = getContentX();
            int top = getContentY();
            int bottom = top + WIDGET_HEIGHT + GAP;

            this.label.setWidth(LABEL_WIDTH);
            this.label.setPosition(left, top);
            this.message.setWidth(Math.max(WIDGET_HEIGHT, getContentWidth() - LABEL_WIDTH - GAP));
            this.message.setPosition(left + LABEL_WIDTH + GAP, top);

            this.key.setWidth(Math.max(WIDGET_HEIGHT, getContentWidth() - REMOVE_WIDTH - GAP));
            this.key.setPosition(left, bottom);
            this.remove.setPosition(getContentRight() - REMOVE_WIDTH, bottom);

            this.widgets.forEach(widget -> widget.extractRenderState(graphics, mouseX, mouseY, delta));
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return this.widgets;
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return this.widgets;
        }
    }

    private class MacroList extends ContainerObjectSelectionList<@NotNull Row> {
        MacroList() {
            super(
                    MacrosScreen.this.minecraft,
                    MacrosScreen.this.width,
                    MacrosScreen.this.layout.getContentHeight(),
                    MacrosScreen.this.layout.getHeaderHeight(),
                    ROW_HEIGHT
            );
        }

        @Override
        public int getRowWidth() {
            return ROW_WIDTH;
        }

        void add(Row row) {
            addEntry(row);
        }

        void remove(Row row) {
            removeEntry(row);
        }
    }
}
