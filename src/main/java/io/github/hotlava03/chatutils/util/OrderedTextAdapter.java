package io.github.hotlava03.chatutils.util;

import net.minecraft.text.*;

public class OrderedTextAdapter {
    public static String orderedTextToString(OrderedText text) {
        var visitor = new TextVisitor();
        text.accept(visitor);
        return visitor.toString();
    }

    private static class TextVisitor implements CharacterVisitor {
        StringBuilder sb = new StringBuilder();

        @Override
        public String toString() {
            return sb.toString();
        }

        @Override
        public boolean accept(int index, Style style, int codePoint) {
            sb.appendCodePoint(codePoint);
            return true;
        }
    }
}
