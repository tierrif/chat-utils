package io.github.hotlava03.chatutils.util;

import net.minecraft.text.*;

import java.util.ArrayList;
import java.util.List;

public class OrderedTextAdapter {
    public static String orderedTextToString(OrderedText text) {
        var visitor = new TextVisitor();
        text.accept(visitor);
        return visitor.toString();
    }

    public static MutableText orderedTextToMutable(OrderedText text) {
        var visitor = new TextVisitor();
        text.accept(visitor);
        List<MutableText> list = visitor.getTextList();
        return list.stream().reduce(MutableText::append).orElse(null);
    }

    private static class TextVisitor implements CharacterVisitor {
        StringBuilder sb = new StringBuilder();
        List<MutableText> textList = new ArrayList<>();

        @Override
        public String toString() {
            return sb.toString();
        }

        public List<MutableText> getTextList() {
            return textList;
        }

        @Override
        public boolean accept(int index, Style style, int codePoint) {
            sb.appendCodePoint(codePoint);
            MutableText text = MutableText.of(new PlainTextContent.Literal(new String(Character.toChars(codePoint))));
            text.setStyle(style);
            textList.add(text);
            return true;
        }
    }
}
