package io.github.hotlava03.chatutils.util;

import java.util.List;

import net.minecraft.util.Mth;

import io.github.hotlava03.chatutils.fileio.ChatShortcut;

public final class ShortcutGrid {
    public static final int WIDTH = 110;
    public static final int HEIGHT = 22;
    public static final int GAP = 3;
    public static final int MARGIN = 4;

    private static final int BOTTOM_RESERVED = 44;

    private ShortcutGrid() {
    }

    private static int usableWidth(int screenWidth) {
        return Math.max(1, screenWidth - 2 * MARGIN + GAP);
    }

    private static int usableHeight(int screenHeight) {
        return Math.max(1, screenHeight - MARGIN - BOTTOM_RESERVED + GAP);
    }

    public static int columns(int screenWidth) {
        return fit(usableWidth(screenWidth), WIDTH + GAP);
    }

    public static int rows(int screenHeight) {
        return fit(usableHeight(screenHeight), HEIGHT + GAP);
    }

    private static int fit(int usable, int nominalCell) {
        return Math.max(1, (int) Math.round((double) usable / nominalCell));
    }

    public static int xOfColumn(int column, int screenWidth) {
        int columns = columns(screenWidth);

        return MARGIN + edge(Mth.clamp(column, 0, columns - 1), columns, usableWidth(screenWidth));
    }

    public static int yOfRow(int row, int screenHeight) {
        int rows = rows(screenHeight);

        return MARGIN + edge(Mth.clamp(row, 0, rows - 1), rows, usableHeight(screenHeight));
    }

    public static int widthOfColumn(int column, int screenWidth) {
        return span(column, columns(screenWidth), usableWidth(screenWidth));
    }

    public static int heightOfRow(int row, int screenHeight) {
        return span(row, rows(screenHeight), usableHeight(screenHeight));
    }

    private static int edge(int index, int count, int usable) {
        return (int) Math.round((double) Mth.clamp(index, 0, count) * usable / count);
    }

    private static int span(int index, int count, int usable) {
        int clamped = Mth.clamp(index, 0, count - 1);

        return Math.max(1, edge(clamped + 1, count, usable) - edge(clamped, count, usable) - GAP);
    }

    public static int columnAt(int column, boolean anchorRight, int screenWidth) {
        int columns = columns(screenWidth);
        int clamped = Mth.clamp(column, 0, columns - 1);

        return anchorRight ? columns - 1 - clamped : clamped;
    }

    public static int xOf(ChatShortcut shortcut, int screenWidth) {
        return xOfColumn(columnAt(shortcut.gridX(), shortcut.anchorRight(), screenWidth), screenWidth);
    }

    public static int widthOf(ChatShortcut shortcut, int screenWidth) {
        return widthOfColumn(columnAt(shortcut.gridX(), shortcut.anchorRight(), screenWidth), screenWidth);
    }

    public static int yOf(ChatShortcut shortcut, int screenHeight) {
        return yOfRow(shortcut.gridY(), screenHeight);
    }

    public static int heightOf(ChatShortcut shortcut, int screenHeight) {
        return heightOfRow(shortcut.gridY(), screenHeight);
    }

    public static boolean isAddCell(int column, int row) {
        return column == 0 && row == 0;
    }

    public static void snapTo(ChatShortcut shortcut, int x, int y, int screenWidth, int screenHeight) {
        int columns = columns(screenWidth);
        int rows = rows(screenHeight);

        int column = nearest(x, columns, usableWidth(screenWidth));
        int row = nearest(y, rows, usableHeight(screenHeight));

        // Dropping onto the add button bumps the shortcut clear of it instead of hiding under it.
        if (isAddCell(column, row)) {
            if (rows > 1) {
                row = 1;
            } else if (columns > 1) {
                column = 1;
            }
        }

        boolean anchorRight = column >= (columns + 1) / 2;
        shortcut.setAnchorRight(anchorRight);
        shortcut.setGridX(anchorRight ? columns - 1 - column : column);
        shortcut.setGridY(row);
    }

    private static int nearest(int position, int count, int usable) {
        return Mth.clamp((int) Math.round((double) (position - MARGIN) * count / usable), 0, count - 1);
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
