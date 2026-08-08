package io.github.hotlava03.chatutils.fileio;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.logging.log4j.LogManager;

import io.github.hotlava03.chatutils.util.AntiSpamCounter;
import io.github.hotlava03.chatutils.util.StringUtils;

public class ChatFilter {
    private String pattern;
    private boolean regex;
    private boolean caseSensitive;
    private boolean exactMatch;

    private Pattern compiled;
    private boolean compiledUpToDate;

    public ChatFilter() {
        this("", false, false, false);
    }

    public ChatFilter(String pattern, boolean regex, boolean caseSensitive, boolean exactMatch) {
        this.pattern = pattern;
        this.regex = regex;
        this.caseSensitive = caseSensitive;
        this.exactMatch = exactMatch;
    }

    public static boolean matchesAny(net.minecraft.network.chat.Component text) {
        return firstMatch(text) != null;
    }

    public static ChatFilter firstMatch(net.minecraft.network.chat.Component text) {
        if (!ChatUtilsConfig.CHAT_FILTER.value()) return null;

        var filters = ChatUtilsConfig.CHAT_FILTERS.value();
        if (filters.isEmpty()) {
            return null;
        }

        var plain = StringUtils.plainText(text);
        var stripped = AntiSpamCounter.strip(plain);
        var counted = !stripped.equals(plain);

        for (ChatFilter filter : filters) {
            if (filter.matches(plain) || (counted && filter.matches(stripped))) {
                return filter;
            }
        }

        return null;
    }

    public boolean matches(String plainMessage) {
        if (pattern.isEmpty()) return false;

        if (regex) {
            var compiledPattern = compiled();
            if (compiledPattern == null) return false;

            var matcher = compiledPattern.matcher(plainMessage);

            return exactMatch
                    ? matcher.matches()
                    : matcher.find();
        }

        if (exactMatch) {
            return caseSensitive
                    ? plainMessage.equals(pattern)
                    : plainMessage.equalsIgnoreCase(pattern);
        }

        return caseSensitive
                ? plainMessage.contains(pattern)
                : plainMessage.toLowerCase(Locale.ROOT).contains(pattern.toLowerCase(Locale.ROOT));
    }

    private Pattern compiled() {
        if (compiledUpToDate) {
            return compiled;
        }

        compiledUpToDate = true;
        try {
            compiled = Pattern.compile(pattern, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        } catch (PatternSyntaxException exception) {
            // A typo in the config must not take chat down with it, so the filter just never matches.
            compiled = null;
            LogManager.getLogger().warn("[chat-utils] Ignoring chat filter with invalid regex \"{}\": {}",
                    pattern, exception.getDescription());
        }

        return compiled;
    }

    public String pattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
        this.compiledUpToDate = false;
    }

    public boolean regex() {
        return regex;
    }

    public void setRegex(boolean regex) {
        this.regex = regex;
    }

    public boolean caseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        this.compiledUpToDate = false;
    }

    public boolean exactMatch() {
        return exactMatch;
    }

    public void setExactMatch(boolean exactMatch) {
        this.exactMatch = exactMatch;
    }

    public ChatFilter copy() {
        return new ChatFilter(pattern, regex, caseSensitive, exactMatch);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ChatFilter filter
                && regex == filter.regex
                && caseSensitive == filter.caseSensitive
                && exactMatch == filter.exactMatch
                && pattern.equals(filter.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, regex, caseSensitive, exactMatch);
    }

    public JsonObject toJson() {
        var object = new JsonObject();
        object.addProperty("pattern", pattern);
        object.addProperty("regex", regex);
        object.addProperty("caseSensitive", caseSensitive);
        object.addProperty("exactMatch", exactMatch);

        return object;
    }

    public static ChatFilter fromJson(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return null;
        }

        var object = element.getAsJsonObject();
        var pattern = object.get("pattern");
        if (pattern == null || !pattern.isJsonPrimitive()) {
            return null;
        }

        return new ChatFilter(
                pattern.getAsString(),
                readFlag(object, "regex"),
                readFlag(object, "caseSensitive"),
                readFlag(object, "exactMatch"));
    }

    private static boolean readFlag(JsonObject object, String name) {
        var flag = object.get(name);

        return flag != null && flag.isJsonPrimitive() && flag.getAsBoolean();
    }

    public static List<ChatFilter> dropBlank(List<ChatFilter> filters) {
        return filters.stream().filter(filter -> !filter.pattern().isBlank()).toList();
    }
}
