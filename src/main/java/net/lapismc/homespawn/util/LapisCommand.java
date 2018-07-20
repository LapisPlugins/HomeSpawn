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

import net.lapismc.homespawn.HomeSpawn;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;

public abstract class LapisCommand extends BukkitCommand {

    protected HomeSpawn plugin;

    protected LapisCommand(HomeSpawn plugin, String name, String desc, ArrayList<String> aliases) {
        super(name);
        this.plugin = plugin;
        setDescription(desc);
        setAliases(aliases);
        registerCommand(name);
    }

    protected void sendMessage(CommandSender sender, String key) {
        sender.sendMessage(plugin.HSConfig.getColoredMessage(key));
    }

    protected boolean forcePlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, "Error.MustBePlayer");
            return true;
        }
        return false;
    }

    private void registerCommand(String name) {
        try {
            final Field serverCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            serverCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) serverCommandMap.get(Bukkit.getServer());
            commandMap.register(name, this);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        onCommand(sender, args);
        return true;
    }

    protected abstract void onCommand(CommandSender sender, String[] args);

}
