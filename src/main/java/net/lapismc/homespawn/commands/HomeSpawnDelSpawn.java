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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class HomeSpawnDelSpawn extends LapisCommand {

    public HomeSpawnDelSpawn(HomeSpawn plugin) {
        super(plugin, "delspawn", "Deletes a spawn point", new ArrayList<>());
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player && isNotPermitted(((Player) sender).getUniqueId(),
                HomeSpawnPermissions.Perm.DeleteSpawn)) {
            sendMessage(sender, "Error.NotPermitted");
            return;
        }
        boolean isNew = args.length == 1 && args[0].equalsIgnoreCase("new");
        if (getSpawnLocation(isNew) == null) {
            sendMessage(sender, "Spawn.NotSet");
            return;
        }
        deleteSpawnLocation(isNew);
    }
}
