package io.github.hotlava03.chatutils.util;

import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import static org.apache.commons.lang3.StringUtils.getLevenshteinDistance;

public class StringUtils {

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
}
