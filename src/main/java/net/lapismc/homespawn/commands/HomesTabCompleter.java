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
import net.lapismc.homespawn.playerdata.Home;
import net.lapismc.homespawn.playerdata.HomeSpawnPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomesTabCompleter implements TabCompleter {

    private final net.lapismc.homespawn.HomeSpawn plugin;

    public HomesTabCompleter(HomeSpawn plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (!(sender instanceof Player) || args.length > 1) {
            return suggestions;
        }
        Player player = (Player) sender;
        HomeSpawnPlayer p = plugin.getPlayer(player.getUniqueId());
        String startsWith = args.length == 0 ? "" : args[args.length - 1];
        for (Home h : p.getHomes()) {
            if (h.getName().toLowerCase().startsWith(startsWith.toLowerCase()))
                suggestions.add(h.getName());
        }
        return suggestions;
    }
}
