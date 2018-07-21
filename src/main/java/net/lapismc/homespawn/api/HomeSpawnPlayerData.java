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

package net.lapismc.homespawn.api;

import net.lapismc.homespawn.HomeSpawn;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.UUID;

/**
 * api Class to get Player Data Files
 *
 * @author Dart2112
 */
@SuppressWarnings("unused")
public class HomeSpawnPlayerData {

    private static HomeSpawn plugin;

    public void init(HomeSpawn p) {
        plugin = p;
    }

    /**
     * Returns the currently loaded Player Data file will the given Player name
     */
    public YamlConfiguration getHomeConfig(UUID uuid) {
        return plugin.getPlayer(uuid).getConfig();
    }

    /**
     * Saves the given YamlConfiguration as the UUID in its file name
     */
    public void saveHomesConfig(UUID uuid, YamlConfiguration HomeConfig) {
        plugin.getPlayer(uuid).saveConfig(HomeConfig);
    }

}
