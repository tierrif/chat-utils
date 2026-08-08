package io.github.hotlava03.chatutils.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import io.github.hotlava03.chatutils.util.ShortcutGrid;
import org.jetbrains.annotations.NotNull;

public class ShortcutGridOverlay extends AbstractWidget {
    private static final int CELL_COLOR = 0x26FFFFFF;

    public ShortcutGridOverlay(int screenWidth, int screenHeight) {
        super(0, 0, screenWidth, screenHeight, Component.empty());
        this.active = false;
    }

    @Override
    protected void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int columns = ShortcutGrid.columns(this.width);
        int rows = ShortcutGrid.rows(this.height);

        for (int column = 0; column < columns; column++) {
            int x = ShortcutGrid.xOfColumn(column, this.width);
            int cellWidth = ShortcutGrid.widthOfColumn(column, this.width);

            for (int row = 0; row < rows; row++) {
                // The add button owns that cell; nothing can be dropped into it.
                if (ShortcutGrid.isAddCell(column, row)) {
                    continue;
                }

                int y = ShortcutGrid.yOfRow(row, this.height);
                graphics.fill(x, y, x + cellWidth, y + ShortcutGrid.heightOfRow(row, this.height), CELL_COLOR);
            }
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }
}
