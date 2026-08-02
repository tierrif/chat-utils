package io.github.hotlava03.chatutils.util;

import java.util.regex.Pattern;

import net.minecraft.network.chat.Component;

import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;

public final class AntiSpamCounter {
    private static final String CODE = "§[0-9a-fk-orxA-FK-ORX]";

    private static final String CODES = "(?:" + CODE + ")*";

    private static final Pattern PATTERN = Pattern.compile(
            "^(.+?)" + CODES + " ?" + CODES + "\\[" + CODES + "x(\\d{1,9})" + CODES + "]$");

    private AntiSpamCounter() {
    }

    public static String strip(String message) {
        var matcher = PATTERN.matcher(message);
        return matcher.matches() ? matcher.group(1) : message;
    }

    public static int countOf(String message) {
        var matcher = PATTERN.matcher(message);
        return matcher.matches() ? Integer.parseInt(matcher.group(2)) : 1;
    }

    public static boolean hasCounter(String message) {
        return PATTERN.matcher(message).matches();
    }

    public static String suffix(int count) {
        return " §8[§cx" + count + "§8]";
    }

    public static String key(Component text) {
        var adventure = StringUtils.asAdventure(text);
        if (!ChatUtilsConfig.ANTI_SPAM_IGNORE_COLORS.value()) {
            return StringUtils.componentToLegacySection(adventure);
        }

        // Style-based colours are gone once the component is flattened to plain text, but servers
        // routinely embed § codes in the text content itself, and those survive.
        return stripFormatting(StringUtils.componentToPlainText(adventure));
    }

    public static String stripFormatting(String message) {
        return message.replaceAll(CODE, "");
    }
}
