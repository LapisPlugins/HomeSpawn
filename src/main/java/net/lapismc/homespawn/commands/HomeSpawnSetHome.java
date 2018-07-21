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

import net.lapismc.homespawn.HomeSpawn;
import net.lapismc.homespawn.HomeSpawnPermissions;
import net.lapismc.homespawn.api.events.HomeMoveEvent;
import net.lapismc.homespawn.api.events.HomeSetEvent;
import net.lapismc.homespawn.playerdata.Home;
import net.lapismc.homespawn.playerdata.HomeSpawnPlayer;
import net.lapismc.homespawn.util.LapisCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class HomeSpawnSetHome extends LapisCommand {

    public HomeSpawnSetHome(HomeSpawn plugin) {
        super(plugin, "sethome", "Sets a home at your current location", new ArrayList<>());
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (forcePlayer(sender)) {
            return;
        }
        Player p = (Player) sender;
        if (isNotPermitted(p.getUniqueId(), HomeSpawnPermissions.Perm.Homes)) {
            sendMessage(sender, "Error.NotPermitted");
        }
        HomeSpawnPlayer player = plugin.getPlayer(p.getUniqueId());
        String homeName = "Home";
        //if the player is setting a custom home, check that they can and then set the name
        if (args.length == 1) {
            if (plugin.HSPerms.getPermissionValue(p.getUniqueId(), HomeSpawnPermissions.Perm.Homes) > 1) {
                //check that the player doesn't have to many custom homes to set another
                //also check if they are moving a preexisting home as they can do that even if they are at the limit
                if (player.getHomes().size() >= plugin.HSPerms.getPermissionValue(p.getUniqueId(),
                        HomeSpawnPermissions.Perm.Homes) && !player.hasHome(args[0])) {
                    sendMessage(sender, "Home.LimitReached");
                    return;
                }
                //set the home name to the custom name provided
                homeName = args[0];
            } else {
                sendMessage(sender, "Error.NotPermitted");
            }
        }
        if (player.hasHome(homeName)) {
            //if we are just moving a preexisting home then this is significantly better
            //run home move event
            Home home = player.getHome(homeName);
            HomeMoveEvent event = new HomeMoveEvent(p, home.getName(), home.getLocation(), p.getLocation());
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                p.sendMessage(plugin.HSConfig.getColoredMessage("Error.ActionCancelled") + event.getReason());
                return;
            }
            home.setLoc(p.getLocation());
            sendMessage(sender, "Home.Moved");
        } else {
            //otherwise make a new home and add it to the player
            Home home = new Home(plugin, p.getUniqueId(), homeName, p.getLocation());
            //run home set event
            HomeSetEvent event = new HomeSetEvent(p, home);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                p.sendMessage(plugin.HSConfig.getColoredMessage("Error.ActionCancelled") + event.getReason());
                return;
            }
            home.setLoc(p.getLocation());
            player.addHome(home);
            sendMessage(sender, "Home.Created");
        }
    }

}
