package io.github.hotlava03.chatutils.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;

public class EditModeButton extends AbstractWidget {
    public static final int SIZE = 12;

    private static final String GEAR = "⚙";
    private static final int BACKGROUND = 0x80000000;
    private static final int IDLE_COLOR = 0xFFAAAAAA;
    private static final int HOVERED_COLOR = 0xFFFFFFFF;
    private static final int EDITING_COLOR = 0xFF7FDC7F;

    private final boolean editing;
    private final Runnable onToggle;

    public EditModeButton(int x, int y, boolean editing, Runnable onToggle) {
        super(x, y, SIZE, SIZE, Component.translatable("chat-utils.macros.shortcuts.edit"));
        this.editing = editing;
        this.onToggle = onToggle;
        setTooltip(Tooltip.create(Component.translatable(editing
                ? "chat-utils.macros.shortcuts.edit.done"
                : "chat-utils.macros.shortcuts.edit")));
    }

    @Override
    protected void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int color = this.editing
                ? EDITING_COLOR
                : isHoveredOrFocused() ? HOVERED_COLOR : IDLE_COLOR;

        graphics.fill(getX(), getY(), getRight(), getBottom(), BACKGROUND);
        graphics.outline(getX(), getY(), this.width, this.height, color);

        // Both measurements carry a trailing pixel of advance/leading that is not part of the
        // glyph, so centring on them directly leaves the icon sitting low and to the right.
        var font = Minecraft.getInstance().font;
        int nudge = 1; // Hack to manually position the gear, which doesn't center well.
        graphics.text(font, GEAR,
                getX() + (this.width - (font.width(GEAR) - 1)) / 2 + nudge,
                getY() + (this.height - (font.lineHeight - 1)) / 2,
                color,
                false
        );
    }

    @Override
    public void onClick(@NotNull MouseButtonEvent event, boolean doubleClick) {
        this.onToggle.run();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
