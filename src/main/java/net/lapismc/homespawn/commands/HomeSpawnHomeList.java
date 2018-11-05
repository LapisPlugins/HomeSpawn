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
import net.lapismc.homespawn.playerdata.HomeSpawnPlayer;
import net.lapismc.homespawn.util.HomeSpawnCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class HomeSpawnHomeList extends HomeSpawnCommand {

    public HomeSpawnHomeList(HomeSpawn plugin) {
        super(plugin, "homelist", "Shows the players current homes", new ArrayList<>(Arrays.asList("homeslist", "listhomes", "listhome")));
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (forcePlayer(sender)) {
            return;
        }
        Player p = (Player) sender;
        HomeSpawnPlayer player = plugin.getPlayer(p.getUniqueId());
        if (player.getHomes().isEmpty()) {
            sendMessage(sender, "Home.NoHomes");
            return;
        }
        if (plugin.getConfig().getBoolean("HomeListGUI")) {
            player.showHomesGUI(p);
        } else {
            player.sendClickableHomesList(p);
        }
    }
}
