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
import net.lapismc.homespawn.util.LapisCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class HomeSpawnSpawn extends LapisCommand {

    public HomeSpawnSpawn(HomeSpawn plugin) {
        super(plugin, "spawn", "Teleport to the preset spawn location", new ArrayList<>());
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (forcePlayer(sender)) {
            return;
        }
        Player p = (Player) sender;
        if (isNotPermitted(p.getUniqueId(), HomeSpawnPermissions.Perm.Spawn)) {
            sendMessage(sender, "Error.NotPermitted");
            return;
        }
        Location spawn = getSpawnLocation(false);
        if (spawn == null) {
            sendMessage(sender, "Spawn.NotSet");
            return;
        }
        p.teleport(spawn);
        sendMessage(sender, "Spawn.Teleport");
    }
}
