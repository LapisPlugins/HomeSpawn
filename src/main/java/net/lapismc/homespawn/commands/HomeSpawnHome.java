/*
 * Copyright 2020 Benjamin Martin
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
import net.lapismc.homespawn.api.events.HomeTeleportEvent;
import net.lapismc.homespawn.playerdata.HomeSpawnPlayer;
import net.lapismc.homespawn.playerdata.Permission;
import net.lapismc.homespawn.util.HomeSpawnCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class HomeSpawnHome extends HomeSpawnCommand {

    public HomeSpawnHome(HomeSpawn plugin, HomesTabCompleter tabCompleter) {
        super(plugin, "home", "Teleport to one of your homes", new ArrayList<>());
        registerTabCompleter(tabCompleter);
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (forcePlayer(sender)) {
            return;
        }
        Player p = (Player) sender;
        HomeSpawnPlayer player = plugin.getPlayer(p.getUniqueId());
        String homeName = "Home";
        if (args.length >= 1) {
            homeName = args[0];
        }
        //check if the player has more homes then allowed and lock them out if they have exceeded the limit
        if (player.getHomes().size() > plugin.perms.getPermissionValue(p.getUniqueId(), Permission.Homes.getPermission())) {
            sendMessage(sender, "Home.LimitExceeded");
            return;
        }
        //check if home exists
        if (!player.hasHome(homeName)) {
            sendMessage(sender, "Error.HomeDoesNotExist");
            return;
        }
        //run home teleport event
        HomeTeleportEvent event = new HomeTeleportEvent(p, player.getHome(homeName));
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            p.sendMessage(plugin.config.getMessage("Error.ActionCancelled") + event.getReason());
            return;
        }
        player.getHome(homeName).teleportPlayer(p);
    }
}
