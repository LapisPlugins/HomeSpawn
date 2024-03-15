/*
 * Copyright 2024 Benjamin Martin
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

import net.lapismc.homespawn.HomeSpawn;
import net.lapismc.homespawn.api.events.HomeRenameEvent;
import net.lapismc.homespawn.playerdata.Home;
import net.lapismc.homespawn.playerdata.HomeSpawnPlayer;
import net.lapismc.homespawn.playerdata.Permission;
import net.lapismc.homespawn.util.HomeSpawnCommand;
import net.lapismc.lapiscore.commands.tabcomplete.LapisCoreTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

public class HomeSpawnRenameHome extends HomeSpawnCommand {

    public HomeSpawnRenameHome(HomeSpawn plugin) {
        super(plugin, "renamehome", "Rename a home", new ArrayList<>());
        LapisCoreTabCompleter tabCompleter = new LapisCoreTabCompleter();
        tabCompleter.registerTopLevelOptions(this, Collections.singletonList(new HomeTabOption(plugin)));
        registerTabCompleter(tabCompleter);
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (forcePlayer(sender)) {
            return;
        }
        Player p = (Player) sender;
        HomeSpawnPlayer player = plugin.getPlayer(p.getUniqueId());
        if (plugin.perms.getPermissionValue(p.getUniqueId(), Permission.Homes.getPermission()) <= 1) {
            sendMessage(sender, "Error.NotPermitted");
            return;
        }
        if (args.length == 2) {
            String oldName = args[0];
            String newName = args[1];
            if (!player.hasHome(oldName)) {
                sendMessage(sender, "Error.HomeDoesNotExist");
                return;
            }
            Home home = player.getHome(oldName);
            //run home rename event
            HomeRenameEvent event = new HomeRenameEvent(p, oldName, newName);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                p.sendMessage(plugin.config.getMessage("Error.ActionCancelled") + event.getReason());
                return;
            }
            home.rename(newName);
            sendMessage(sender, "Home.Renamed");
        } else {
            sendMessage(sender, "Help.RenameHome");
        }
    }
}
