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
import net.lapismc.homespawn.api.events.HomeDeleteEvent;
import net.lapismc.homespawn.playerdata.HomeSpawnPlayer;
import net.lapismc.homespawn.util.HomeSpawnCommand;
import net.lapismc.lapiscore.commands.tabcomplete.LapisCoreTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

public class HomeSpawnDelHome extends HomeSpawnCommand {

    public HomeSpawnDelHome(HomeSpawn plugin) {
        super(plugin, "delhome", "Delete a home", new ArrayList<>());
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
        String homeName = "Home";
        if (args.length >= 1) {
            homeName = args[0];
        }
        //check that the home exists
        if (!player.hasHome(homeName)) {
            sendMessage(sender, "Error.HomeDoesNotExist");
            return;
        }
        //run home delete event
        HomeDeleteEvent event = new HomeDeleteEvent(p, player.getHome(homeName));
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            p.sendMessage(plugin.config.getMessage("Error.ActionCancelled") + event.getReason());
            return;
        }
        //delete the home
        player.deleteHome(player.getHome(homeName));
        sendMessage(sender, "Home.Deleted");
    }
}
