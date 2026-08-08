package io.github.hotlava03.chatutils.gui;

import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import io.github.hotlava03.chatutils.fileio.ChatShortcut;
import io.github.hotlava03.chatutils.fileio.ShortcutColor;
import io.github.hotlava03.chatutils.util.MacroActions;
import io.github.hotlava03.chatutils.util.ShortcutGrid;
import org.jetbrains.annotations.NotNull;

public class ShortcutEditScreen extends Screen {
    private static final int FIELD_WIDTH = 200;
    private static final int WIDGET_HEIGHT = 20;
    private static final int ROW_HEIGHT = 34;
    private static final int CAPTION_GAP = 11;
    private static final int BUTTON_WIDTH = 98;
    private static final int GAP = 6;
    private static final int LABEL_MAX_LENGTH = 32;
    private static final int TITLE_COLOR = -1;
    private static final int CAPTION_COLOR = 0xFFC0C0C0;

    private static final int BACKGROUND_COLOR = 0xE8101010;

    private final Screen parent;
    private final ChatShortcut shortcut;
    private final Runnable onDelete;
    private final Consumer<ChatShortcut> onDone;
    private int left;
    private int top;
    private Button done;

    public ShortcutEditScreen(Screen parent, ChatShortcut shortcut, Runnable onDelete,
                              Consumer<ChatShortcut> onDone) {
        super(Component.translatable("chat-utils.macros.shortcut.edit"));
        this.parent = parent;
        this.shortcut = shortcut.copy();
        this.onDelete = onDelete;
        this.onDone = onDone;
    }

    @Override
    protected void init() {
        this.left = this.width / 2 - FIELD_WIDTH / 2;
        this.top = this.height / 4;

        var label = field(0, "label", LABEL_MAX_LENGTH, this.shortcut.label(), this.shortcut::setLabel);
        addRenderableWidget(label);
        addRenderableWidget(field(1, "message", MacroActions.MAX_LENGTH, this.shortcut.message(), message -> {
            this.shortcut.setMessage(message);
            this.done.active = !message.isBlank();
        }));

        addRenderableWidget(CycleButton.<ShortcutColor>builder(ShortcutColor::label, this.shortcut.color())
                .withValues(ShortcutColor.values())
                .create(this.left, fieldY(2), FIELD_WIDTH, WIDGET_HEIGHT,
                        Component.translatable("chat-utils.macros.color"),
                        (button, color) -> this.shortcut.setColor(color)));

        if (this.onDelete != null) {
            addRenderableWidget(Button.builder(Component.translatable("chat-utils.macros.remove"), button -> {
                        this.onDelete.run();
                        onClose();
                    })
                    .bounds(this.left, fieldY(3) + GAP, FIELD_WIDTH, WIDGET_HEIGHT)
                    .build());
        }

        this.done = addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> confirm())
                .bounds(this.width / 2 - BUTTON_WIDTH - GAP / 2, this.height - 40, BUTTON_WIDTH, WIDGET_HEIGHT)
                .build());
        this.done.active = !this.shortcut.message().isBlank();
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose())
                .bounds(this.width / 2 + GAP / 2, this.height - 40, BUTTON_WIDTH, WIDGET_HEIGHT)
                .build());

        setInitialFocus(label);
    }

    private EditBox field(int row, String name, int maxLength, String value, Consumer<String> setter) {
        var box = new EditBox(this.font, this.left, fieldY(row), FIELD_WIDTH, WIDGET_HEIGHT,
                Component.translatable("chat-utils.macros." + name));
        box.setMaxLength(maxLength);
        box.setHint(Component.translatable("chat-utils.macros." + name + ".hint"));
        box.setValue(value);
        box.setResponder(setter);

        return box;
    }

    private int captionY(int row) {
        return this.top + row * ROW_HEIGHT;
    }

    private int fieldY(int row) {
        return captionY(row) + CAPTION_GAP;
    }

    private void confirm() {
        this.onDone.accept(this.shortcut);
        onClose();
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, this.width, this.height, BACKGROUND_COLOR);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.centeredText(this.font, this.title, this.width / 2, this.top - ROW_HEIGHT, TITLE_COLOR);
        caption(graphics, 0, "label");
        caption(graphics, 1, "message");
        caption(graphics, 2, "color");

        // The colour reads better as the rectangle it will actually be drawn as than as its name.
        int previewX = this.left + FIELD_WIDTH + GAP;
        int previewY = fieldY(2);
        var color = this.shortcut.color();

        graphics.fill(previewX, previewY, previewX + ShortcutGrid.HEIGHT, previewY + WIDGET_HEIGHT, color.fill());
        graphics.outline(previewX, previewY, ShortcutGrid.HEIGHT, WIDGET_HEIGHT, color.border());
    }

    private void caption(GuiGraphicsExtractor graphics, int row, String name) {
        graphics.text(this.font, Component.translatable("chat-utils.macros." + name), this.left, captionY(row),
                CAPTION_COLOR);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }
}
