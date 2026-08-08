package io.github.hotlava03.chatutils.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import io.github.hotlava03.chatutils.fileio.ChatFilter;
import org.jetbrains.annotations.NotNull;

public class ChatFiltersScreen extends Screen {
    private static final int ROW_HEIGHT = 48;
    private static final int ROW_WIDTH = 310;
    private static final int WIDGET_HEIGHT = 20;
    private static final int REMOVE_WIDTH = 54;
    private static final int BUTTON_WIDTH = 110;
    private static final int GAP = 4;
    private static final int PATTERN_MAX_LENGTH = 256;

    private final Screen parent;
    private final Consumer<List<ChatFilter>> onDone;
    private final List<ChatFilter> filters;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private FilterList list;

    public ChatFiltersScreen(Screen parent, List<ChatFilter> filters, Consumer<List<ChatFilter>> onDone) {
        super(Component.translatable("chat-utils.filters.title"));
        this.parent = parent;
        this.onDone = onDone;
        this.filters = new ArrayList<>(filters.stream().map(ChatFilter::copy).toList());
    }

    @Override
    protected void init() {
        this.layout.addTitleHeader(this.title, this.font);

        this.list = this.layout.addToContents(new FilterList());
        this.filters.forEach(this.list::add);

        var footer = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        footer.addChild(Button.builder(Component.translatable("chat-utils.filters.add"), button -> addFilter())
                .width(BUTTON_WIDTH)
                .build());
        footer.addChild(Button.builder(Component.translatable("chat-utils.filters.log.open"),
                        // Leaves this screen on the stack, so the filters being edited stay as they are.
                        button -> this.minecraft.gui.setScreen(new FilteredMessagesScreen(this)))
                .width(BUTTON_WIDTH)
                .build());
        footer.addChild(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .width(BUTTON_WIDTH)
                .build());

        this.layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        this.list.updateSize(this.width, this.layout);
    }

    @Override
    public void onClose() {
        this.onDone.accept(ChatFilter.dropBlank(this.filters));
        this.minecraft.gui.setScreen(this.parent);
    }

    private void addFilter() {
        var filter = new ChatFilter();
        this.filters.add(filter);
        this.list.add(filter);
        this.list.setScrollAmount(this.list.maxScrollAmount());
    }

    private class FilterList extends ContainerObjectSelectionList<FilterList.@NotNull FilterEntry> {
        FilterList() {
            super(
                    ChatFiltersScreen.this.minecraft,
                    ChatFiltersScreen.this.width,
                    ChatFiltersScreen.this.layout.getContentHeight(),
                    ChatFiltersScreen.this.layout.getHeaderHeight(),
                    ROW_HEIGHT
            );
        }

        @Override
        public int getRowWidth() {
            return ROW_WIDTH;
        }

        void add(ChatFilter filter) {
            addEntry(new FilterEntry(filter));
        }

        private class FilterEntry extends ContainerObjectSelectionList.Entry<@NotNull FilterEntry> {
            private final ChatFilter filter;
            private final EditBox pattern;
            private final List<Checkbox> flags;
            private final Button remove;
            private final List<AbstractWidget> widgets;

            FilterEntry(ChatFilter filter) {
                this.filter = filter;

                this.pattern = new EditBox(ChatFiltersScreen.this.font, 0, 0, ROW_WIDTH, WIDGET_HEIGHT,
                        Component.translatable("chat-utils.filters.pattern"));
                this.pattern.setMaxLength(PATTERN_MAX_LENGTH);
                this.pattern.setHint(Component.translatable("chat-utils.filters.pattern.hint"));
                this.pattern.setValue(filter.pattern());
                this.pattern.setResponder(filter::setPattern);

                this.flags = List.of(
                        checkbox("regex", filter.regex(), filter::setRegex),
                        checkbox("caseSensitive", filter.caseSensitive(), filter::setCaseSensitive),
                        checkbox("exactMatch", filter.exactMatch(), filter::setExactMatch)
                );

                this.remove = Button.builder(Component.translatable("chat-utils.filters.remove"),
                                button -> ChatFiltersScreen.this.removeFilter(this))
                        .bounds(0, 0, REMOVE_WIDTH, WIDGET_HEIGHT)
                        .build();

                var widgets = new ArrayList<AbstractWidget>();
                widgets.add(this.pattern);
                widgets.addAll(this.flags);
                widgets.add(this.remove);
                this.widgets = List.copyOf(widgets);
            }

            private Checkbox checkbox(String name, boolean value, Consumer<Boolean> setter) {
                return Checkbox.builder(Component.translatable("chat-utils.filters." + name),
                                ChatFiltersScreen.this.font)
                        .selected(value)
                        .onValueChange((checkbox, selected) -> setter.accept(selected))
                        .tooltip(Tooltip.create(Component.translatable("chat-utils.filters." + name + ".description")))
                        .build();
            }

            @Override
            public void extractContent(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
                int left = getContentX();
                int top = getContentY();

                this.remove.setPosition(getContentRight() - REMOVE_WIDTH, top);
                this.pattern.setWidth(Math.max(WIDGET_HEIGHT, getContentWidth() - REMOVE_WIDTH - GAP));
                this.pattern.setPosition(left, top);

                int columnWidth = (getContentWidth() - GAP * (this.flags.size() - 1)) / this.flags.size();
                int x = left;
                for (Checkbox flag : this.flags) {
                    flag.adjustWidth(columnWidth, ChatFiltersScreen.this.font);
                    flag.setPosition(x, top + WIDGET_HEIGHT + GAP);
                    x += columnWidth + GAP;
                }

                this.widgets.forEach(widget -> widget.extractRenderState(graphics, mouseX, mouseY, a));
            }

            @Override
            public @NotNull List<? extends GuiEventListener> children() {
                return this.widgets;
            }

            @Override
            public @NotNull List<? extends NarratableEntry> narratables() {
                return this.widgets;
            }

            ChatFilter filter() {
                return this.filter;
            }
        }

        void remove(FilterEntry entry) {
            removeEntry(entry);
        }
    }

    private void removeFilter(FilterList.FilterEntry entry) {
        this.filters.remove(entry.filter());
        this.list.remove(entry);
    }
}
