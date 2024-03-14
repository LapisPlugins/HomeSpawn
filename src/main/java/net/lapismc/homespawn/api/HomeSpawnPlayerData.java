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

package net.lapismc.homespawn.api;

import net.lapismc.homespawn.HomeSpawn;
import net.lapismc.homespawn.playerdata.HomeSpawnPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.UUID;

/**
 * api Class to get Player Data Files
 *
 * @author Dart2112
 */
public class HomeSpawnPlayerData {

    private static HomeSpawn plugin;

    /**
     * DO NOT USE, this is an internal use method to store the main class within this class!
     *
     * @param p the HomeSpawn main class to be stored in this API
     */
    public void init(HomeSpawn p) {
        plugin = p;
    }

    /**
     * Get a HomeSpawnPlayer object
     *
     * @param op The player you wish to retrieve
     * @return a {@link HomeSpawnPlayer} object representing the player you provided
     */
    public HomeSpawnPlayer getPlayer(OfflinePlayer op) {
        return plugin.getPlayer(op.getUniqueId());
    }

    /**
     * Returns the currently loaded Player Data file will the given Player name
     *
     * @param uuid The UUID of the player you wish to fetch the data file for
     * @return The Yaml config currently associated with the given UUID
     */
    public YamlConfiguration getHomeConfig(UUID uuid) {
        return plugin.getPlayer(uuid).getConfig();
    }

    /**
     * Saves the given YamlConfiguration as the UUID in its file name
     *
     * @param uuid       The UUID of the player you wish to save the config too
     * @param HomeConfig The Yaml data to save to disk
     */
    public void saveHomesConfig(UUID uuid, YamlConfiguration HomeConfig) {
        plugin.getPlayer(uuid).saveConfig(HomeConfig);
    }

}
