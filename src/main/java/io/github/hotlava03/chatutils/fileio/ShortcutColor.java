package io.github.hotlava03.chatutils.fileio;

import java.util.Locale;

import net.minecraft.network.chat.Component;

public enum ShortcutColor {
    RED(0xE05A5A),
    ORANGE(0xE08A3C),
    YELLOW(0xE0C84A),
    GREEN(0x5FBF5F),
    AQUA(0x4FC3C3),
    BLUE(0x5A8AE0),
    PURPLE(0x9B5FD0),
    PINK(0xE06AB0),
    GRAY(0x9A9A9A),
    WHITE(0xE8E8E8);

    public static final ShortcutColor DEFAULT = BLUE;

    private static final int FILL_ALPHA = 0x55;
    private static final int HOVERED_FILL_ALPHA = 0xAA;

    private final int rgb;

    ShortcutColor(int rgb) {
        this.rgb = rgb;
    }

    public int border() {
        return 0xFF000000 | this.rgb;
    }

    public int fill() {
        return (FILL_ALPHA << 24) | this.rgb;
    }

    public int hoveredFill() {
        return (HOVERED_FILL_ALPHA << 24) | this.rgb;
    }

    public Component label() {
        return Component.translatable("chat-utils.macros.color." + name().toLowerCase(Locale.ROOT));
    }

    public static ShortcutColor byName(String name) {
        for (ShortcutColor color : values()) {
            if (color.name().equalsIgnoreCase(name)) {
                return color;
            }
        }

        return DEFAULT;
    }
}
