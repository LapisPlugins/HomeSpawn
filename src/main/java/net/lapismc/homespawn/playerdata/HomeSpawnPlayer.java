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

package net.lapismc.homespawn.playerdata;

import net.lapismc.homespawn.HomeSpawn;
import net.lapismc.homespawn.HomeSpawnPermissions;
import net.lapismc.homespawn.util.EasyComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ocpsoft.prettytime.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class HomeSpawnPlayer {

    private ArrayList<Home> homes = new ArrayList<>();
    private HomeSpawn plugin;
    private UUID uuid;

    public HomeSpawnPlayer(HomeSpawn plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
    }

    public boolean hasHome(String name) {
        for (Home home : homes) {
            if (home.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Home getHome(String name) {
        for (Home home : homes) {
            if (home.getName().equalsIgnoreCase(name)) {
                return home;
            }
        }
        return null;
    }

    public List<Home> getHomes() {
        return homes;
    }

    public void sendHomesList(CommandSender sender) {
        StringBuilder message = new StringBuilder();
        for (Home home : homes) {
            message.append(" ");
            message.append(plugin.HSConfig.secondaryColor).append(home.getName());
            message.append(" ");
        }
        sender.sendMessage(message.toString());
    }

    public void sendClickableHomesList(Player p) {
        EasyComponent component = new EasyComponent("");
        for (Home home : homes) {
            component.append(" ");
            component.append(plugin.HSConfig.secondaryColor + home.getName())
                    .onClickRunCmd("/home " + home.getName())
                    .onHover(plugin.HSConfig.primaryColor + "Click to teleport");
            component.append(" ");
        }
    }

    public void deleteHome(Home home) {
        YamlConfiguration yaml = getConfig();
        yaml.set("Homes." + home.getName(), null);
        saveConfig(yaml);
        homes.remove(home);
    }

    public void addHome(Home home) {
        homes.add(home);
    }

    public String getPlayerInfo() {
        String info = plugin.HSConfig.getColoredMessage("PlayerData");
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        Long time = getConfig().getLong("Time");
        String timeString = plugin.prettyTime.format(
                reduceDurationList(plugin.prettyTime.calculatePreciseDuration(new Date(time))));
        info = info.replace("%NAME%", op.getName());
        info = info.replace("%PERMISSION%", plugin.HSPerms.getPlayersPermission(uuid));
        info = info.replace("%STATE%", op.isOnline() ? "online" : "offline");
        info = info.replace("%TIME%", timeString);
        info = info.replace("%USED%", String.valueOf(homes.size()));
        info = info.replace("%TOTAL%",
                (plugin.HSPerms.getPermissionValue(uuid, HomeSpawnPermissions.Perm.CustomHomes) + 1) + "");
        return info;
    }

    public YamlConfiguration getConfig() {
        File file = new File(plugin.getDataFolder() + File.separator
                + "PlayerData" + File.separator + uuid.toString() + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public void saveConfig(YamlConfiguration yaml) {
        File file = new File(plugin.getDataFolder() + File.separator
                + "PlayerData" + File.separator + uuid.toString() + ".yml");
        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Duration> reduceDurationList(List<Duration> durationList) {
        while (durationList.size() > 2) {
            Duration smallest = null;
            for (Duration current : durationList) {
                if (smallest == null || smallest.getUnit().getMillisPerUnit() > current.getUnit().getMillisPerUnit()) {
                    smallest = current;
                }
            }
            durationList.remove(smallest);
        }
        return durationList;
    }
}
