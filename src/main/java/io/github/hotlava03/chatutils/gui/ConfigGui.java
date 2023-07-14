package io.github.hotlava03.chatutils.gui;

import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

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
        addIntEntry(general, builder, ChatUtilsConfig.ANTI_SPAM_RANGE, 1, 99);
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

    private static void addIntEntry(ConfigCategory category, ConfigBuilder builder,
                                        ChatUtilsConfig.Value<Integer> value, int min, int max) {
        category.addEntry(builder.entryBuilder()
                .startIntSlider(Text.translatable("chat-utils.configs." + value.name() + ".label"), value.value(), min, max)
                .setDefaultValue(value.defaultValue())
                .setTooltip(Text.translatable("chat-utils.configs." + value.name() + ".description"))
                .setSaveConsumer(value::setValue)
                .build());
    }

    private static void addKeyCodeEntry(ConfigCategory category, ConfigBuilder builder,
                                        ChatUtilsConfig.Value<Integer> value) {
        category.addEntry(builder.entryBuilder()
                .startKeyCodeField(Text.translatable("chat-utils.configs." + value.name() + ".label"),
                        InputUtil.fromKeyCode(value.value(), GLFW.glfwGetKeyScancode(value.value())))
                .setDefaultValue(InputUtil.fromKeyCode(value.defaultValue(), GLFW.glfwGetKeyScancode(value.value())))
                .setTooltip(Text.translatable("chat-utils.configs." + value.name() + ".description"))
                .setKeySaveConsumer((key) -> value.setValue(key.getCode()))
                .build());
    }
}
