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
import net.lapismc.homespawn.playerdata.Permission;
import net.lapismc.homespawn.util.HomeSpawnCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class HomeSpawnSetSpawn extends HomeSpawnCommand {

    public HomeSpawnSetSpawn(HomeSpawn plugin) {
        super(plugin, "setspawn", "Set the location for /spawn", new ArrayList<>());
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (forcePlayer(sender)) {
            return;
        }
        Player p = (Player) sender;
        if (isNotPermitted(p.getUniqueId(), Permission.SetSpawn)) {
            sendMessage(sender, "Error.NotPermitted");
            return;
        }
        //check if the command has an argument of new
        setSpawnLocation(p.getLocation(), args.length >= 1 && args[0].equalsIgnoreCase("new"));
        sendMessage(sender, "Spawn.Created");
    }
}
