package io.github.hotlava03.chatutils.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import net.minecraft.network.chat.Style;

import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class StringUtils {
    public static Component asAdventure(net.minecraft.network.chat.Component text) {
        var builder = Component.text();
        text.visit((style, content) -> {
            builder.append(Component.text(content, asAdventureStyle(style)));
            return Optional.empty();
        }, Style.EMPTY);
        return builder.build();
    }

    private static net.kyori.adventure.text.format.Style asAdventureStyle(Style style) {
        var builder = net.kyori.adventure.text.format.Style.style();

        var color = style.getColor();
        if (color != null) builder.color(TextColor.color(color.getValue()));
        if (style.isBold()) builder.decorate(TextDecoration.BOLD);
        if (style.isItalic()) builder.decorate(TextDecoration.ITALIC);
        if (style.isUnderlined()) builder.decorate(TextDecoration.UNDERLINED);
        if (style.isStrikethrough()) builder.decorate(TextDecoration.STRIKETHROUGH);
        if (style.isObfuscated()) builder.decorate(TextDecoration.OBFUSCATED);

        return builder.build();
    }

    public static Component unpackLegacyCodes(Component component) {
        var children = component.children();
        if (!children.isEmpty()) {
            component = component.children(children.stream()
                    .map(StringUtils::unpackLegacyCodes)
                    .toList());
        }

        if (!(component instanceof TextComponent text)
                || text.content().indexOf(LegacyComponentSerializer.SECTION_CHAR) == -1
        ) {
            return component;
        }

        // The parsed runs replace the content, so they have to come ahead of the component's own
        // children. Its style stays as the fallback for whatever the codes leave unset.
        var parsed = LegacyComponentSerializer.legacySection().deserialize(text.content());

        return parsed
                .applyFallbackStyle(text.style())
                .children(Stream.concat(
                        parsed.children().stream(), text.children().stream()).toList());
    }

    public static String componentToLegacy(Component component, boolean useHexCodes) {
        var builder = LegacyComponentSerializer.builder()
                .character(LegacyComponentSerializer.AMPERSAND_CHAR)
                .flattener(MinecraftClientAudiences.of().flattener());
        if (useHexCodes) builder.hexColors();
        return builder.build().serialize(component);
    }

    public static String componentToLegacySection(Component component) {
        return LegacyComponentSerializer.builder()
                .character(LegacyComponentSerializer.SECTION_CHAR)
                .flattener(MinecraftClientAudiences.of().flattener())
                .build()
                .serialize(component);
    }

    public static String componentToPlainText(Component component) {
        return PlainTextComponentSerializer.builder()
                .flattener(MinecraftClientAudiences.of().flattener())
                .build()
                .serialize(component);
    }

    public static String wrap(String str, int wrapLength) {
        return wrap(str, wrapLength, null, false);
    }

    public static String wrap(String str, int wrapLength, String newLineStr, boolean wrapLongWords) {
        return wrap(str, wrapLength, newLineStr, wrapLongWords, " ");
    }

    public static String wrap(String str, int wrapLength, String newLineStr, boolean wrapLongWords, String wrapOn) {
        if (str == null) {
            return null;
        } else {
            if (newLineStr == null) {
                newLineStr = System.lineSeparator();
            }

            if (wrapLength < 1) {
                wrapLength = 1;
            }

            if (org.apache.commons.lang3.StringUtils.isBlank(wrapOn)) {
                wrapOn = " ";
            }

            Pattern patternToWrapOn = Pattern.compile(wrapOn);
            int inputLineLength = str.length();
            int offset = 0;
            StringBuilder wrappedLine = new StringBuilder(inputLineLength + 32);

            while (offset < inputLineLength) {
                int spaceToWrapAt = -1;
                Matcher matcher = patternToWrapOn.matcher(str.substring(offset,
                        Math.min((int) Math.min(2147483647L, (long) (offset + wrapLength) + 1L), inputLineLength)));

                if (matcher.find()) {
                    if (matcher.start() == 0) {
                        offset += matcher.end();
                        continue;
                    }

                    spaceToWrapAt = matcher.start() + offset;
                }

                if (inputLineLength - offset <= wrapLength) {
                    break;
                }

                while (matcher.find()) {
                    spaceToWrapAt = matcher.start() + offset;
                }

                if (spaceToWrapAt >= offset) {
                    wrappedLine.append(str, offset, spaceToWrapAt);
                    wrappedLine.append(newLineStr);
                    offset = spaceToWrapAt + 1;
                } else if (wrapLongWords) {
                    wrappedLine.append(str, offset, wrapLength + offset);
                    wrappedLine.append(newLineStr);
                    offset += wrapLength;
                } else {
                    matcher = patternToWrapOn.matcher(str.substring(offset + wrapLength));
                    if (matcher.find()) {
                        spaceToWrapAt = matcher.start() + offset + wrapLength;
                    }

                    if (spaceToWrapAt >= 0) {
                        wrappedLine.append(str, offset, spaceToWrapAt);
                        wrappedLine.append(newLineStr);
                        offset = spaceToWrapAt + 1;
                    } else {
                        wrappedLine.append(str, offset, str.length());
                        offset = inputLineLength;
                    }
                }
            }

            wrappedLine.append(str, offset, str.length());

            return wrappedLine.toString();
        }
    }
}
