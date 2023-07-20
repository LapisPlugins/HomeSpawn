/*
 * Copyright 2023 Benjamin Martin
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
import net.lapismc.homespawn.playerdata.Home;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class HomeSpawnDataConverter {

    //TODO move to only command after the next few updates

    private final HomeSpawn plugin;

    public HomeSpawnDataConverter(HomeSpawn plugin) {
        this.plugin = plugin;
        runConverter();
    }

    private void runConverter() {
        File spawnFile = new File(plugin.getDataFolder() + File.separator + "Spawn.yml");
        if (spawnFile.exists()) {
            YamlConfiguration spawn = YamlConfiguration.loadConfiguration(spawnFile);
            if (spawn.contains("spawn")) {
                try {
                    Location l = (Location) spawn.get("spawn");
                    String loc = plugin.parseLocationToString(l);
                    spawn.set("spawn", loc);
                } catch (ClassCastException e) {
                    //already converted
                }
            }
            if (spawn.contains("spawnnew")) {
                try {
                    Location l = (Location) spawn.get("spawnnew");
                    String loc = plugin.parseLocationToString(l);
                    spawn.set("spawnnew", loc);
                } catch (ClassCastException e) {
                    //already converted
                }
            }
            try {
                spawn.save(spawnFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            spawnFile.renameTo(new File(plugin.getDataFolder() + File.separator + "spawn.yml"));
        }
        File playerDataFolder = new File(plugin.getDataFolder() + File.separator + "PlayerData");
        if (!playerDataFolder.exists() || !playerDataFolder.isDirectory()) {
            return;
        }
        for (File f : Objects.requireNonNull(playerDataFolder.listFiles())) {
            if (f.getName().endsWith(".yml")) {
                if (f.getName().contains("Passwords")) {
                    f.delete();
                } else {
                    YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
                    yaml.set("login", null);
                    yaml.set("logout", null);
                    yaml.set("UUID", null);
                    yaml.set("UserName", null);
                    if (yaml.contains("Homes") && yaml.contains("Homes.list")) {
                        yaml.set("Homes.list", null);
                        UUID uuid = UUID.fromString(f.getName().replace(".yml", ""));
                        ConfigurationSection permsSection = yaml.getConfigurationSection("Homes");
                        Set<String> homes = permsSection.getKeys(false);
                        for (String home : homes) {
                            if (!home.equalsIgnoreCase("list")) {
                                try {
                                    Location location = (Location) yaml.get("Homes." + home);
                                    yaml.set("Homes." + home, new Home(plugin, uuid, home, location).getLocationString());
                                } catch (ClassCastException ignored) {
                                }
                            }
                        }
                    }
                    try {
                        yaml.save(f);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
