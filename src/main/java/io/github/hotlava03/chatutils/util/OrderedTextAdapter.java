package io.github.hotlava03.chatutils.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;

public class OrderedTextAdapter {
    public static String orderedTextToString(FormattedCharSequence text) {
        var visitor = new TextVisitor();
        text.accept(visitor);
        return visitor.toString();
    }

    public static MutableComponent orderedTextToMutable(FormattedCharSequence text) {
        var visitor = new TextVisitor();
        text.accept(visitor);
        List<MutableComponent> list = visitor.getTextList();
        return list.stream().reduce(MutableComponent::append).orElse(null);
    }

    private static class TextVisitor implements FormattedCharSink {
        StringBuilder sb = new StringBuilder();
        List<MutableComponent> textList = new ArrayList<>();

        @Override
        public String toString() {
            return sb.toString();
        }

        public List<MutableComponent> getTextList() {
            return textList;
        }

        @Override
        public boolean accept(int index, Style style, int codePoint) {
            sb.appendCodePoint(codePoint);
            MutableComponent text = MutableComponent.create(
                    PlainTextContents.create(new String(Character.toChars(codePoint))));
            text.setStyle(style);
            textList.add(text);
            return true;
        }
    }
}
