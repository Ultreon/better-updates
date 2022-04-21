package com.ultreon.mods.betterupdates.client.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class BetterButton extends Button {
    public BetterButton(int x, int y, int width, Component title, OnPress pressedAction) {
        super(x, y, width, 20, title, pressedAction);
    }

    public BetterButton(int x, int y, int width, Component title, OnPress pressedAction, OnTooltip onTooltip) {
        super(x, y, width, 20, title, pressedAction, onTooltip);
    }
}
