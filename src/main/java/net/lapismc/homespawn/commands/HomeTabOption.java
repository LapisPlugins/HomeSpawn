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
import net.lapismc.homespawn.playerdata.HomeSpawnPlayer;
import net.lapismc.lapiscore.commands.tabcomplete.LapisTabOption;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeTabOption implements LapisTabOption {

    HomeSpawn plugin;

    public HomeTabOption(HomeSpawn plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> getOptions(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }
        HomeSpawnPlayer player = plugin.getPlayer(((Player) sender).getUniqueId());
        List<String> homeNames = new ArrayList<>();
        player.getHomes().forEach(home -> homeNames.add(home.getName()));
        return homeNames;
    }

    @Override
    public List<LapisTabOption> getChildren(CommandSender sender) {
        return new ArrayList<>();
    }
}
