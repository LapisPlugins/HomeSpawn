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
import net.lapismc.homespawn.api.events.SpawnTeleportEvent;
import net.lapismc.homespawn.playerdata.HomeSpawnPlayer;
import net.lapismc.homespawn.playerdata.Permission;
import net.lapismc.homespawn.util.HomeSpawnCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class HomeSpawnSpawn extends HomeSpawnCommand {

    public HomeSpawnSpawn(HomeSpawn plugin) {
        super(plugin, "spawn", "Teleport to the preset spawn location", new ArrayList<>());
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (forcePlayer(sender)) {
            return;
        }
        Player p = (Player) sender;
        HomeSpawnPlayer player = plugin.getPlayer(p.getUniqueId());
        if (isNotPermitted(p.getUniqueId(), Permission.Spawn)) {
            sendMessage(sender, "Error.NotPermitted");
            return;
        }
        Location spawn = getSpawnLocation(false);
        if (spawn == null) {
            sendMessage(sender, "Spawn.NotSet");
            return;
        }
        //run spawn teleport event
        SpawnTeleportEvent event = new SpawnTeleportEvent(p, spawn);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            p.sendMessage(plugin.config.getMessage("Error.ActionCancelled") + event.getReason());
            return;
        }
        player.teleportToSpawn(spawn);
    }
}
