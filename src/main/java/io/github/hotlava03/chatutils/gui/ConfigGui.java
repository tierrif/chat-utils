package io.github.hotlava03.chatutils.gui;

import io.github.hotlava03.chatutils.config.ChatUtilsConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;

public class ConfigGui {
    public static ConfigBuilder getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("chat-utils.config_title"))
                .setSavingRunnable(() -> {
                    ChatUtilsConfig.saveToFile();
                    ChatUtilsConfig.loadFromFile();
                });
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("chat-utils.config_title"));
        addBooleanEntry(general, builder, ChatUtilsConfig.ANTI_SPAM);
        addBooleanEntry(general, builder, ChatUtilsConfig.ENABLED);
        addBooleanEntry(general, builder, ChatUtilsConfig.TOOLTIP_ENABLED);
        addStringEntry(general, builder, ChatUtilsConfig.COPY_TO_CLIPBOARD_MESSAGE);
        addBooleanEntry(general, builder, ChatUtilsConfig.PREVIEW_CONTENT);
        addBooleanEntry(general, builder, ChatUtilsConfig.COPY_COLORS);
        addBooleanEntry(general, builder, ChatUtilsConfig.COPY_HEX_COLORS);
        return builder;
    }

    private static void addStringEntry(ConfigCategory category, ConfigBuilder builder,
                                       ChatUtilsConfig.Value<String> value) {
        category.addEntry(builder.entryBuilder()
                .startStrField(Text.translatable("chat-utils.configs." + value.name() + ".label"), value.value())
                .setDefaultValue(value.defaultValue())
                .setTooltip(Text.translatable("chat-utils.configs." + value.name() + ".description"))
                .setSaveConsumer(value::setValue)
                .build());
    }

    private static void addBooleanEntry(ConfigCategory category, ConfigBuilder builder,
                                        ChatUtilsConfig.Value<Boolean> value) {
        category.addEntry(builder.entryBuilder()
                .startBooleanToggle(Text.translatable("chat-utils.configs." + value.name() + ".label"), value.value())
                .setDefaultValue(value.defaultValue())
                .setTooltip(Text.translatable("chat-utils.configs." + value.name() + ".description"))
                .setSaveConsumer(value::setValue)
                .build());
    }
}
