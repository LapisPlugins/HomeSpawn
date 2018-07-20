/*
 * Copyright 2018 Benjamin Martin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.lapismc.homespawn.util;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "unused"})
public final class EasyComponent {

    private final List<BaseComponent> parts = new ArrayList<>();
    private BaseComponent current;

    public EasyComponent(EasyComponent original) {
        current = original.current.duplicate();

        for (final BaseComponent baseComponent : original.parts)
            parts.add(baseComponent.duplicate());
    }

    public EasyComponent(String text) {
        current = new TextComponent(TextComponent.fromLegacyText(colorize(text)));
    }

    public EasyComponent(BaseComponent component) {
        current = component.duplicate();
    }

    public EasyComponent append(BaseComponent component) {
        return append(component, FormatRetention.ALL);
    }

    public EasyComponent append(BaseComponent component, FormatRetention retention) {
        parts.add(current);

        final BaseComponent previous = current;
        current = component.duplicate();
        current.copyFormatting(previous, retention, false);
        return this;
    }

    public EasyComponent append(BaseComponent[] components) {
        return append(components, FormatRetention.ALL);
    }

    public EasyComponent append(BaseComponent[] components, FormatRetention retention) {
        Preconditions.checkArgument(components.length != 0, "No components to append");

        final BaseComponent previous = current;
        for (final BaseComponent component : components) {
            parts.add(current);

            current = component.duplicate();
            current.copyFormatting(previous, retention, false);
        }

        return this;
    }

    public EasyComponent append(String text) {
        return append(text, FormatRetention.FORMATTING);
    }

    public EasyComponent append(String text, FormatRetention retention) {
        parts.add(current);

        final BaseComponent old = current;
        current = new TextComponent(TextComponent.fromLegacyText(colorize(text)));
        current.copyFormatting(old, retention, false);

        return this;
    }

    public EasyComponent onClickRunCmd(String text) {
        return onClick(Action.RUN_COMMAND, text);
    }

    public EasyComponent onClickSuggestCmd(String text) {
        return onClick(Action.SUGGEST_COMMAND, text);
    }

    public EasyComponent onClick(Action action, String text) {
        current.setClickEvent(new ClickEvent(action, colorize(text)));
        return this;
    }

    public EasyComponent onHover(String text) {
        return onHover(HoverEvent.Action.SHOW_TEXT, text);
    }

    public EasyComponent onHover(HoverEvent.Action action, String text) {
        current.setHoverEvent(new HoverEvent(action, TextComponent.fromLegacyText(colorize(text))));

        return this;
    }

    public EasyComponent retain(FormatRetention retention) {
        current.retain(retention);
        return this;
    }

    public BaseComponent[] create() {
        final BaseComponent[] result = parts.toArray(new BaseComponent[parts.size() + 1]);
        result[parts.size()] = current;

        return result;
    }

    public void send(Player... players) {
        final BaseComponent[] comp = create();

        for (final Player player : players)
            player.spigot().sendMessage(comp);
    }

    public final EasyComponent builder(String text) {
        return new EasyComponent(text);
    }

    private String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}