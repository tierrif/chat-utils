package io.github.hotlava03.chatutils.util;

import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.getLevenshteinDistance;

public class StringUtils {
    public static String componentToLegacy(Component component, boolean useHexCodes) {
        var builder = LegacyComponentSerializer.builder()
                .character(LegacyComponentSerializer.AMPERSAND_CHAR)
                .flattener(FabricClientAudiences.of().flattener());
        if (useHexCodes) builder.hexColors();
        return builder.build().serialize(component);
    }

    public static String componentToPlainText(Component component) {
        return PlainTextComponentSerializer.builder()
                .flattener(FabricClientAudiences.of().flattener())
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
