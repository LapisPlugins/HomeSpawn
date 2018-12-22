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

package net.lapismc.homespawn.commands;

import net.lapismc.homespawn.playerdata.HomeSpawnPlayer;
import net.lapismc.homespawn.playerdata.Permission;
import net.lapismc.homespawn.util.HomeSpawnCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class HomeSpawn extends HomeSpawnCommand {

    private final HomeSpawnPlayerCommand HSPlayer;

    public HomeSpawn(net.lapismc.homespawn.HomeSpawn plugin) {
        super(plugin, "homespawn", "Plugin information, help and player information", new ArrayList<>());
        HSPlayer = new HomeSpawnPlayerCommand();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            displayPluginInfo(sender);
        } else {
            if (args[0].equalsIgnoreCase("update")) {
                update(sender);
            } else if (args[0].equalsIgnoreCase("reload")) {
                reload(sender);
            } else if (args[0].equalsIgnoreCase("help")) {
                displayHelp(sender);
            } else if (args[0].equalsIgnoreCase("player")) {
                HSPlayer.onCommand(sender, args);
            } else {
                sendMessage(sender, "Help.Homespawn");
            }
        }
    }

    private void update(CommandSender sender) {
        if (sender instanceof Player) {
            if (isNotPermitted(((Player) sender).getUniqueId(), Permission.CanUpdate)) {
                sendMessage(sender, "Error.NotPermitted");
            }
        }
        if (plugin.lapisUpdater.checkUpdate()) {
            plugin.lapisUpdater.downloadUpdate();
            sendMessage(sender, "Update.Downloading");
        } else {
            sendMessage(sender, "Update.NotAvailable");
        }
    }

    private void reload(CommandSender sender) {
        if (sender instanceof Player) {
            if (isNotPermitted(((Player) sender).getUniqueId(), Permission.CanReload)) {
                sendMessage(sender, "Error.NotPermitted");
            }
        }
        plugin.getLogger().info("Reloading...");
        plugin.config.reloadMessages(null);
        plugin.reloadConfig();
        plugin.perms.loadPermissions();
        sendMessage(sender, "Reload");
    }

    private void displayHelp(CommandSender sender) {
        YamlConfiguration messages = plugin.config.getMessages();
        sender.sendMessage(plugin.config.getMessage("Help.Help"));
        for (String key : messages.getConfigurationSection("Help").getKeys(false)) {
            if (!key.equalsIgnoreCase("help")) {
                sendMessage(sender, "Help." + key);
            }
        }
    }

    private void displayPluginInfo(CommandSender sender) {
        String primary = plugin.primaryColor;
        String secondary = plugin.secondaryColor;
        String bars = secondary + "-------------";
        sender.sendMessage(bars + primary + "  HomeSpawn  " + bars);
        sender.sendMessage(primary + "Version: " + secondary + plugin.getDescription().getVersion());
        sender.sendMessage(primary + "Author: " + secondary + plugin.getDescription().getAuthors().get(0));
        sender.sendMessage(primary + "Spigot: " + secondary + "https://goo.gl/aWby6W");
        sender.sendMessage(primary + "If you need help use " + secondary + "/homespawn help");
        sender.sendMessage(bars + bars + bars);
    }

    private class HomeSpawnPlayerCommand {

        private void onCommand(CommandSender sender, String[] args) {
            if (sender instanceof Player) {
                if (isNotPermitted(((Player) sender).getUniqueId(), Permission.CanViewPlayerStats)) {
                    sendMessage(sender, "Error.NotPermitted");
                    return;
                }
            }
            if (args.length == 1 || args.length > 3) {
                sendMessage(sender, "Help.HomespawnPlayer");
            } else if (args.length == 2) {
                displayPlayerInformation(sender, args[1]);
            } else if (args.length == 3) {
                teleportToHome(sender, args[1], args[2]);
            }
        }

        private void displayPlayerInformation(CommandSender sender, String target) {
            //noinspection deprecation
            OfflinePlayer op = Bukkit.getOfflinePlayer(target);
            if (!op.hasPlayedBefore()) {
                sendMessage(sender, "Error.PlayerNotFound");
                return;
            }
            HomeSpawnPlayer player = plugin.getPlayer(op.getUniqueId());
            sender.sendMessage(player.getPlayerInfo());
            if (sender instanceof Player) {
                player.sendClickableHomesList((Player) sender);
            } else {
                player.sendHomesList(sender);
            }
        }

        private void teleportToHome(CommandSender sender, String targetPlayer, String homeName) {
            if (forcePlayer(sender)) {
                return;
            }
            Player p = (Player) sender;
            //noinspection deprecation
            OfflinePlayer op = Bukkit.getOfflinePlayer(targetPlayer);
            if (!op.hasPlayedBefore()) {
                sendMessage(sender, "Error.PlayerNotFound");
                return;
            }
            HomeSpawnPlayer player = plugin.getPlayer(op.getUniqueId());
            if (!player.hasHome(homeName)) {
                sendMessage(sender, "Error.HomeDoesNotExist");
                return;
            }
            player.getHome(homeName).teleportPlayer(p);
        }

    }
}
