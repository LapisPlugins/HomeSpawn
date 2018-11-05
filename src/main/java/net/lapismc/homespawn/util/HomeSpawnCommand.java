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

package net.lapismc.homespawn.util;

import net.lapismc.homespawn.HomeSpawn;
import net.lapismc.homespawn.playerdata.Permission;
import net.lapismc.lapiscore.LapisCoreCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public abstract class HomeSpawnCommand extends LapisCoreCommand {

    protected HomeSpawn plugin;

    protected HomeSpawnCommand(HomeSpawn plugin, String name, String desc, ArrayList<String> aliases) {
        super(plugin, name, desc, aliases, true);
        this.plugin = plugin;
    }

    protected void sendMessage(CommandSender sender, String key) {
        sender.sendMessage(plugin.config.getMessage(key));
    }

    protected boolean forcePlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, "Error.MustBePlayer");
            return true;
        }
        return false;
    }

    protected boolean isNotPermitted(UUID uuid, Permission perm) {
        return !plugin.perms.isPermitted(uuid, perm.getPermission());
    }

    protected Location getSpawnLocation(boolean isNew) {
        File file = new File(plugin.getDataFolder() + File.separator + "spawn.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration spawn = YamlConfiguration.loadConfiguration(file);
        String locationString = spawn.getString(isNew ? "SpawnNew" : "Spawn");
        return parseStringToLocation(locationString);
    }

    protected void setSpawnLocation(Location loc, boolean isNew) {
        File file = new File(plugin.getDataFolder() + File.separator + "spawn.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration spawn = YamlConfiguration.loadConfiguration(file);
        spawn.set(isNew ? "SpawnNew" : "Spawn", parseLocationToString(loc));
        try {
            spawn.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!isNew && plugin.getConfig().getBoolean("SetWorldSpawn")) {
            loc.getWorld().setSpawnLocation(loc);
        }
    }

    protected void deleteSpawnLocation(boolean isNew) {
        File file = new File(plugin.getDataFolder() + File.separator + "spawn.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration spawn = YamlConfiguration.loadConfiguration(file);
        spawn.set(isNew ? "SpawnNew" : "Spawn", null);
        try {
            spawn.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String parseLocationToString(Location loc) {
        return plugin.parseLocationToString(loc);
    }

    private Location parseStringToLocation(String s) {
        return plugin.parseStringToLocation(s);
    }

}
