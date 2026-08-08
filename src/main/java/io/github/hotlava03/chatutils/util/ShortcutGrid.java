package io.github.hotlava03.chatutils.util;

import java.util.List;

import net.minecraft.util.Mth;

import io.github.hotlava03.chatutils.fileio.ChatShortcut;

public final class ShortcutGrid {
    public static final int WIDTH = 110;
    public static final int HEIGHT = 22;
    public static final int GAP = 3;
    public static final int MARGIN = 4;

    private static final int CELL_WIDTH = WIDTH + GAP;
    private static final int CELL_HEIGHT = HEIGHT + GAP;

    private static final int BOTTOM_RESERVED = 44;

    private ShortcutGrid() {
    }

    public static int columns(int screenWidth) {
        return Math.max(1, (screenWidth - 2 * MARGIN + GAP) / CELL_WIDTH);
    }

    public static int rows(int screenHeight) {
        return Math.max(1, (screenHeight - MARGIN - BOTTOM_RESERVED + GAP) / CELL_HEIGHT);
    }

    private static int originX(int screenWidth) {
        int gridWidth = columns(screenWidth) * CELL_WIDTH - GAP;

        return Math.max(MARGIN, (screenWidth - gridWidth) / 2);
    }

    public static int columnAt(int column, boolean anchorRight, int screenWidth) {
        int columns = columns(screenWidth);
        int clamped = Mth.clamp(column, 0, columns - 1);

        return anchorRight ? columns - 1 - clamped : clamped;
    }

    public static int xOfColumn(int column, int screenWidth) {
        return originX(screenWidth) + column * CELL_WIDTH;
    }

    public static int xAt(int column, boolean anchorRight, int screenWidth) {
        return xOfColumn(columnAt(column, anchorRight, screenWidth), screenWidth);
    }

    public static int yAt(int row) {
        return MARGIN + row * CELL_HEIGHT;
    }

    public static int xOf(ChatShortcut shortcut, int screenWidth) {
        return xAt(shortcut.gridX(), shortcut.anchorRight(), screenWidth);
    }

    public static int yOf(ChatShortcut shortcut, int screenHeight) {
        return yAt(Mth.clamp(shortcut.gridY(), 0, rows(screenHeight) - 1));
    }

    public static boolean isAddCell(int column, int row) {
        return column == 0 && row == 0;
    }

    public static void snapTo(ChatShortcut shortcut, int x, int y, int screenWidth, int screenHeight) {
        int columns = columns(screenWidth);
        int rows = rows(screenHeight);

        int column = Mth.clamp(Math.round((x - originX(screenWidth)) / (float) CELL_WIDTH), 0, columns - 1);
        int row = Mth.clamp(Math.round((y - MARGIN) / (float) CELL_HEIGHT), 0, rows - 1);

        // Dropping onto the add button bumps the shortcut clear of it instead of hiding under it.
        if (isAddCell(column, row)) {
            if (rows > 1) {
                row = 1;
            } else if (columns > 1) {
                column = 1;
            }
        }

        boolean anchorRight = x + WIDTH / 2 > screenWidth / 2;
        shortcut.setAnchorRight(anchorRight);
        shortcut.setGridX(anchorRight ? columns - 1 - column : column);
        shortcut.setGridY(row);
    }

    public static void placeFree(ChatShortcut shortcut, List<ChatShortcut> existing, boolean anchorRight,
                                 int screenWidth, int screenHeight) {
        shortcut.setAnchorRight(anchorRight);

        int columns = columns(screenWidth);
        int rows = rows(screenHeight);
        for (int index = 0; index < columns; index++) {
            for (int row = 0; row < rows; row++) {
                if (isFree(existing, columnAt(index, anchorRight, screenWidth), row, screenWidth)) {
                    shortcut.setGridX(index);
                    shortcut.setGridY(row);
                    return;
                }
            }
        }

        // Every cell is taken; stack it on the first one rather than refusing to create it.
        shortcut.setGridX(0);
        shortcut.setGridY(0);
    }

    private static boolean isFree(List<ChatShortcut> existing, int column, int row, int screenWidth) {
        if (isAddCell(column, row)) {
            return false;
        }

        // Compared as resolved columns: the same cell can be reached from either anchor.
        return existing.stream().noneMatch(shortcut -> shortcut.gridY() == row
                && columnAt(shortcut.gridX(), shortcut.anchorRight(), screenWidth) == column);
    }
}
