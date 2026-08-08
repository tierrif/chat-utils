package io.github.hotlava03.chatutils.gui;

import java.util.List;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.platform.InputConstants;

import org.apache.logging.log4j.LogManager;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;

import io.github.hotlava03.chatutils.fileio.ChatFilter;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.util.KeyUtils;

public class ConfigGui {
    public static ConfigBuilder getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("chat-utils.config_title"))
                .setSavingRunnable(() -> {
                    ChatUtilsConfig.saveToFile();
                    ChatUtilsConfig.loadFromFile();
                });
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("chat-utils.config_title"));
        addBooleanEntry(general, builder, ChatUtilsConfig.ANTI_SPAM);
        addIntEntry(general, builder, ChatUtilsConfig.ANTI_SPAM_RANGE, 1, 99);
        addBooleanEntry(general, builder, ChatUtilsConfig.ANTI_SPAM_IGNORE_COLORS);
        addBooleanEntry(general, builder, ChatUtilsConfig.CHAT_FILTER);
        addChatFilterEntry(general, ChatUtilsConfig.CHAT_FILTERS);
        addBooleanEntry(general, builder, ChatUtilsConfig.ENABLED);
        addBooleanEntry(general, builder, ChatUtilsConfig.TOOLTIP_ENABLED);
        addStringEntry(general, builder, ChatUtilsConfig.COPY_TO_CLIPBOARD_MESSAGE);
        addBooleanEntry(general, builder, ChatUtilsConfig.PREVIEW_CONTENT);
        addBooleanEntry(general, builder, ChatUtilsConfig.COPY_COLORS);
        addBooleanEntry(general, builder, ChatUtilsConfig.COPY_HEX_COLORS);
        addBooleanEntry(general, builder, ChatUtilsConfig.ENABLE_CHAT_PERSIST);
        addBooleanEntry(general, builder, ChatUtilsConfig.ENABLE_COMMAND_PERSIST);
        addBooleanEntry(general, builder, ChatUtilsConfig.ENABLE_COPY_KEY);
        addKeyCodeEntry(general, builder, ChatUtilsConfig.COPY_KEY);
        addBooleanEntry(general, builder, ChatUtilsConfig.SHOW_ALERTS);

        return builder;
    }

    private static void addStringEntry(ConfigCategory category, ConfigBuilder builder,
                                       ChatUtilsConfig.Value<String> value) {
        category.addEntry(builder.entryBuilder()
                .startStrField(label(value.name()), value.value())
                .setDefaultValue(value.defaultValue())
                .setTooltip(description(value.name()))
                .setSaveConsumer(value::setValue)
                .build());
    }

    private static void addBooleanEntry(ConfigCategory category, ConfigBuilder builder,
                                        ChatUtilsConfig.Value<Boolean> value) {
        category.addEntry(builder.entryBuilder()
                .startBooleanToggle(label(value.name()), value.value())
                .setDefaultValue(value.defaultValue())
                .setTooltip(description(value.name()))
                .setSaveConsumer(value::setValue)
                .build());
    }

    private static void addIntEntry(ConfigCategory category, ConfigBuilder builder,
                                        ChatUtilsConfig.Value<Integer> value, int min, int max) {
        category.addEntry(builder.entryBuilder()
                .startIntSlider(label(value.name()), value.value(), min, max)
                .setDefaultValue(value.defaultValue())
                .setTooltip(description(value.name()))
                .setSaveConsumer(value::setValue)
                .build());
    }

    private static void addChatFilterEntry(ConfigCategory category, ChatUtilsConfig.Value<List<ChatFilter>> value) {
        category.addEntry(new ChatFilterListEntry(
                label(value.name()),
                description(value.name()),
                value.value(),
                filters -> value.setValue(ChatFilter.dropBlank(filters))));
    }

    private static Component label(String name) {
        return Component.translatable("chat-utils.configs." + name + ".label");
    }

    private static Component description(String name) {
        return Component.translatable("chat-utils.configs." + name + ".description");
    }

    private static void addKeyCodeEntry(ConfigCategory category, ConfigBuilder builder,
                                        ChatUtilsConfig.Value<Integer> value) {
        category.addEntry(builder.entryBuilder()
                .startKeyCodeField(label(value.name()),
                        InputConstants.Type.KEYSYM.getOrCreate(value.value()))
                .setDefaultValue(InputConstants.Type.KEYSYM.getOrCreate(value.defaultValue()))
                .setTooltip(description(value.name()))
                .setKeySaveConsumer((key) -> {
                    if (key.getType() != InputConstants.Type.KEYSYM || !KeyUtils.isValidKey(key.getValue())) {
                        LogManager.getLogger().warn("[chat-utils] Ignoring unsupported copy key binding {}, "
                                + "keeping the current one.", key.getName());
                        return;
                    }

                    value.setValue(key.getValue());
                })
                .build());
    }
}
