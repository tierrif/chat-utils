package io.github.hotlava03.chatutils.gui;

import io.github.hotlava03.chatutils.config.ChatUtilsConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;

public class ConfigGui {
    public static ConfigBuilder getConfigScreen(Screen parent) {
        // TODO: use translatable? lang/en_us.json
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Chat Utils Configuration"))
                .setSavingRunnable(() -> {
                    ChatUtilsConfig.saveToFile();
                    ChatUtilsConfig.loadFromFile();
                    LogManager.getLogger().info("[chat-utils] Saved settings.");
                });
        ConfigCategory general = builder.getOrCreateCategory(Text.literal("Chat Utils Configuration"));
        addStringEntry(general, builder, ChatUtilsConfig.COPY_TO_CLIPBOARD_MESSAGE);
        addBooleanEntry(general, builder, ChatUtilsConfig.PREVIEW_CONTENT);
        addBooleanEntry(general, builder, ChatUtilsConfig.COPY_COLORS);
        addBooleanEntry(general, builder, ChatUtilsConfig.ANTI_SPAM);
        addBooleanEntry(general, builder, ChatUtilsConfig.TOOLTIP_ENABLED);
        addBooleanEntry(general, builder, ChatUtilsConfig.ENABLED);
        return builder;
    }

    private static void addStringEntry(ConfigCategory category, ConfigBuilder builder,
                                       ChatUtilsConfig.Value<String> value) {
        category.addEntry(builder.entryBuilder()
                .startStrField(Text.literal(value.name()), value.value())
                .setDefaultValue(value.defaultValue())
                .setTooltip(Text.literal(value.description()))
                .setSaveConsumer(value::setValue)
                .build());
    }

    private static void addBooleanEntry(ConfigCategory category, ConfigBuilder builder,
                                        ChatUtilsConfig.Value<Boolean> value) {
        category.addEntry(builder.entryBuilder()
                .startBooleanToggle(Text.literal(value.name()), value.value())
                .setDefaultValue(value.defaultValue())
                .setTooltip(Text.literal(value.description()))
                .setSaveConsumer(value::setValue)
                .build());
    }
}
