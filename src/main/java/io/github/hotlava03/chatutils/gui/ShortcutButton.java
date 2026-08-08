package io.github.hotlava03.chatutils.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import io.github.hotlava03.chatutils.fileio.ChatShortcut;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.util.MacroActions;
import io.github.hotlava03.chatutils.util.ShortcutGrid;
import io.github.hotlava03.chatutils.util.StringUtils;
import org.jetbrains.annotations.NotNull;

public class ShortcutButton extends AbstractWidget {
    private static final int DRAG_THRESHOLD = 2;
    private static final int LABEL_PADDING = 4;
    private static final int LABEL_COLOR = 0xFFFFFFFF;

    private final ChatShortcut shortcut;
    private final Screen screen;
    private final boolean editing;
    private final Runnable onEdit;
    private double grabX;
    private double grabY;
    private double dragged;

    public ShortcutButton(ChatShortcut shortcut, Screen screen, boolean editing, Runnable onEdit) {
        super(
                ShortcutGrid.xOf(shortcut, screen.width),
                ShortcutGrid.yOf(shortcut, screen.height),
                ShortcutGrid.WIDTH,
                ShortcutGrid.HEIGHT,
                Component.literal(shortcut.displayLabel())
        );
        this.shortcut = shortcut;
        this.screen = screen;
        this.editing = editing;
        this.onEdit = onEdit;
        setTooltip(Tooltip.create(editing
                ? Component.translatable("chat-utils.macros.shortcuts.editHint")
                : Component.literal(shortcut.message())));
    }

    @Override
    protected void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        var color = this.shortcut.color();
        graphics.fill(getX(), getY(), getRight(), getBottom(),
                isHoveredOrFocused() ? color.hoveredFill() : color.fill());
        graphics.outline(getX(), getY(), this.width, this.height, color.border());

        var font = Minecraft.getInstance().font;
        var label = StringUtils.ellipsis(font, this.shortcut.displayLabel(), this.width - LABEL_PADDING * 2);
        graphics.centeredText(font, label, getX() + this.width / 2,
                getY() + (this.height - font.lineHeight) / 2 + 1, LABEL_COLOR);
    }

    @Override
    public void onClick(@NotNull MouseButtonEvent event, boolean doubleClick) {
        this.grabX = event.x() - getX();
        this.grabY = event.y() - getY();
        this.dragged = 0;
    }

    @Override
    protected void onDrag(@NotNull MouseButtonEvent event, double dragX, double dragY) {
        if (!this.editing) {
            return;
        }

        this.dragged += Math.abs(dragX) + Math.abs(dragY);
        if (this.dragged < DRAG_THRESHOLD) {
            return;
        }

        setPosition((int) Math.round(event.x() - this.grabX), (int) Math.round(event.y() - this.grabY));
    }

    @Override
    public void onRelease(@NotNull MouseButtonEvent event) {
        if (this.editing) {
            if (this.dragged >= DRAG_THRESHOLD) {
                drop();
            } else {
                this.onEdit.run();
            }

            return;
        }

        this.screen.onClose();
        MacroActions.run(this.shortcut.message());
    }

    private void drop() {
        ShortcutGrid.snapTo(this.shortcut, getX(), getY(), this.screen.width, this.screen.height);
        setPosition(ShortcutGrid.xOf(this.shortcut, this.screen.width),
                ShortcutGrid.yOf(this.shortcut, this.screen.height));
        ChatUtilsConfig.saveToFile();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
