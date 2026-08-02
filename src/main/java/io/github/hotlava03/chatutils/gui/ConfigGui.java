package io.github.hotlava03.chatutils.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.platform.InputConstants;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;

import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;

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
                .startStrField(Component.translatable("chat-utils.configs." + value.name() + ".label"), value.value())
                .setDefaultValue(value.defaultValue())
                .setTooltip(Component.translatable("chat-utils.configs." + value.name() + ".description"))
                .setSaveConsumer(value::setValue)
                .build());
    }

    private static void addBooleanEntry(ConfigCategory category, ConfigBuilder builder,
                                        ChatUtilsConfig.Value<Boolean> value) {
        category.addEntry(builder.entryBuilder()
                .startBooleanToggle(Component.translatable("chat-utils.configs." + value.name() + ".label"), value.value())
                .setDefaultValue(value.defaultValue())
                .setTooltip(Component.translatable("chat-utils.configs." + value.name() + ".description"))
                .setSaveConsumer(value::setValue)
                .build());
    }

    private static void addIntEntry(ConfigCategory category, ConfigBuilder builder,
                                        ChatUtilsConfig.Value<Integer> value, int min, int max) {
        category.addEntry(builder.entryBuilder()
                .startIntSlider(Component.translatable("chat-utils.configs." + value.name() + ".label"), value.value(), min, max)
                .setDefaultValue(value.defaultValue())
                .setTooltip(Component.translatable("chat-utils.configs." + value.name() + ".description"))
                .setSaveConsumer(value::setValue)
                .build());
    }

    private static void addKeyCodeEntry(ConfigCategory category, ConfigBuilder builder,
                                        ChatUtilsConfig.Value<Integer> value) {
        category.addEntry(builder.entryBuilder()
                .startKeyCodeField(Component.translatable("chat-utils.configs." + value.name() + ".label"),
                        InputConstants.Type.KEYSYM.getOrCreate(value.value()))
                .setDefaultValue(InputConstants.Type.KEYSYM.getOrCreate(value.defaultValue()))
                .setTooltip(Component.translatable("chat-utils.configs." + value.name() + ".description"))
                .setKeySaveConsumer((key) -> value.setValue(key.getValue()))
                .build());
    }
}
