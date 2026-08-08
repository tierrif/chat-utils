package io.github.hotlava03.chatutils.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;

import io.github.hotlava03.chatutils.events.ChatScreenInitCallback;
import io.github.hotlava03.chatutils.fileio.ChatShortcut;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.gui.EditModeButton;
import io.github.hotlava03.chatutils.gui.ShortcutButton;
import io.github.hotlava03.chatutils.gui.ShortcutEditScreen;
import io.github.hotlava03.chatutils.gui.ShortcutGridOverlay;
import io.github.hotlava03.chatutils.mixin.ChatScreenAccessor;
import io.github.hotlava03.chatutils.util.ShortcutGrid;
import io.github.hotlava03.chatutils.events.ChatScreenCloseCallback;

public class ShortcutOverlayListener implements ChatScreenInitCallback, ChatScreenCloseCallback {
    private static final int INPUT_STRIP_INSET = 2;
    private static final int INPUT_STRIP_BOTTOM_OFFSET = 14;

    private static List<AbstractWidget> overlay = List.of();
    private static boolean editing;

    @Override
    public void onInit(ChatScreen screen, Consumer<AbstractWidget> addWidget) {
        overlay = List.of();
        if (!ChatUtilsConfig.SHORTCUTS_ENABLED.value()) {
            return;
        }

        var widgets = new ArrayList<AbstractWidget>();
        if (editing) {
            // Added first so the cells are drawn under the shortcuts rather than over them.
            widgets.add(new ShortcutGridOverlay(screen.width, screen.height));
            widgets.add(addShortcutButton(screen));
        }

        ChatUtilsConfig.SHORTCUTS.value().forEach(shortcut -> widgets.add(
                new ShortcutButton(shortcut, screen, editing, () -> editShortcut(screen, shortcut))));
        widgets.add(editModeButton(screen));

        widgets.forEach(addWidget);
        overlay = List.copyOf(widgets);
    }

    private EditModeButton editModeButton(ChatScreen screen) {
        int x = screen.width - INPUT_STRIP_INSET - EditModeButton.SIZE;
        var button = new EditModeButton(x, screen.height - INPUT_STRIP_BOTTOM_OFFSET, editing,
                () -> toggleEditing(screen));

        var input = ((ChatScreenAccessor) screen).getInput();
        input.setWidth(Math.max(EditModeButton.SIZE, x - ShortcutGrid.GAP - input.getX()));

        return button;
    }

    private void toggleEditing(ChatScreen screen) {
        editing = !editing;
        screen.resize(screen.width, screen.height);
    }

    private Button addShortcutButton(ChatScreen screen) {
        return Button.builder(Component.translatable("chat-utils.macros.shortcuts.add"),
                        button -> createShortcut(screen))
                .bounds(ShortcutGrid.xOfColumn(0, screen.width), ShortcutGrid.yOfRow(0, screen.height),
                        ShortcutGrid.widthOfColumn(0, screen.width), ShortcutGrid.heightOfRow(0, screen.height))
                .build();
    }

    private void createShortcut(ChatScreen screen) {
        var shortcut = new ChatShortcut();
        ShortcutGrid.placeFree(shortcut, ChatUtilsConfig.SHORTCUTS.value(),
                ChatUtilsConfig.SHORTCUTS_NEW_TOP_RIGHT.value(), screen.width, screen.height);

        Minecraft.getInstance().gui.setScreen(new ShortcutEditScreen(screen, shortcut, null, created -> {
            var shortcuts = new ArrayList<>(ChatUtilsConfig.SHORTCUTS.value());
            shortcuts.add(created);
            store(shortcuts);
        }));
    }

    private void editShortcut(ChatScreen screen, ChatShortcut shortcut) {
        Minecraft.getInstance().gui.setScreen(new ShortcutEditScreen(screen, shortcut,
                () -> replace(shortcut, null),
                updated -> replace(shortcut, updated)));
    }

    private void replace(ChatShortcut shortcut, ChatShortcut updated) {
        var shortcuts = new ArrayList<>(ChatUtilsConfig.SHORTCUTS.value());
        for (int index = 0; index < shortcuts.size(); index++) {
            if (shortcuts.get(index) != shortcut) {
                continue;
            }

            if (updated == null) {
                shortcuts.remove(index);
            } else {
                shortcuts.set(index, updated);
            }

            store(shortcuts);
            return;
        }
    }

    @Override
    public void onClose(ChatScreen screen) {
        editing = false;
    }

    private void store(List<ChatShortcut> shortcuts) {
        ChatUtilsConfig.SHORTCUTS.setValue(ChatShortcut.dropBlank(shortcuts));
        ChatUtilsConfig.saveToFile();
    }

    public static boolean isOverOverlay(double x, double y) {
        return overlay.stream().anyMatch(widget -> widget.visible && widget.isMouseOver(x, y));
    }
}
