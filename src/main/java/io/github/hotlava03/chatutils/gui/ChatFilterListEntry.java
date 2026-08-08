package io.github.hotlava03.chatutils.gui;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;

import io.github.hotlava03.chatutils.fileio.ChatFilter;
import org.jetbrains.annotations.NotNull;

public class ChatFilterListEntry extends AbstractConfigListEntry<List<ChatFilter>> {
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;

    private final List<ChatFilter> original;
    private final Consumer<List<ChatFilter>> saveConsumer;
    private final Button openButton;
    private List<ChatFilter> value;

    public ChatFilterListEntry(Component fieldName, Component description, List<ChatFilter> value,
                               Consumer<List<ChatFilter>> saveConsumer) {
        super(fieldName, false);
        this.original = List.copyOf(value);
        this.value = List.copyOf(value);
        this.saveConsumer = saveConsumer;
        this.openButton = Button.builder(Component.empty(), button -> openScreen())
                .bounds(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT)
                .tooltip(Tooltip.create(description))
                .build();
    }

    private void openScreen() {
        Minecraft.getInstance().gui.setScreen(
                new ChatFiltersScreen(
                        getConfigScreen(),
                        this.value,
                        updated -> this.value = List.copyOf(updated)
                )
        );
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int index,
            int y,
            int x,
            int entryWidth,
            int entryHeight,
            int mouseX,
            int mouseY,
            boolean hovered,
            float delta
    ) {
        super.extractRenderState(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, delta);

        var font = Minecraft.getInstance().font;
        this.openButton.active = isEditable();
        this.openButton.setMessage(Component.translatable("chat-utils.filters.count", this.value.size()));
        this.openButton.setX(x + entryWidth - BUTTON_WIDTH);
        this.openButton.setY(y);

        graphics.text(font, getDisplayedFieldName(), x, y + entryHeight / 2 - font.lineHeight / 2,
                getPreferredTextColor());

        this.openButton.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    public List<ChatFilter> getValue() {
        return this.value;
    }

    @Override
    public Optional<List<ChatFilter>> getDefaultValue() {
        return Optional.empty();
    }

    @Override
    public boolean isEdited() {
        return !this.value.equals(this.original);
    }

    @Override
    public void save() {
        this.saveConsumer.accept(this.value);
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return List.of(this.openButton);
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return List.of(this.openButton);
    }
}
