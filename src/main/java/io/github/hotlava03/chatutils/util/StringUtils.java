package io.github.hotlava03.chatutils.util;

import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.text.Text;

import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.getLevenshteinDistance;

public class StringUtils {

    // Taken from https://github.com/SpigotMC/BungeeCord
    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§([0-9A-FK-OR]|#[a-f0-9]{6})");
    public static String translateAlternateColorCodes(String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; ++i) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = '§';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    /*
     * This method does not belong to me.
     * Original source:
     * https://github.com/killjoy1221/TabbyChat-2/blob/master/src/main/java/mnm/mods/tabbychat/extra/ChatAddonAntiSpam.java
     */
    public static double getDifference(String s1, String s2) {
        double avgLen = (s1.length() + s2.length()) / 2D;
        if (avgLen == 0) {
            return 0;
        }
        return getLevenshteinDistance(s1.toLowerCase(), s2.toLowerCase()) / avgLen;
    }

    public static String textToLegacy(Text text, boolean useHexCodes) {
        var builder = LegacyComponentSerializer.builder()
                .character(LegacyComponentSerializer.SECTION_CHAR)
                .flattener(FabricClientAudiences.of().flattener());
        if (useHexCodes) builder.hexColors();
        return builder.build().serialize(text.asComponent());
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
