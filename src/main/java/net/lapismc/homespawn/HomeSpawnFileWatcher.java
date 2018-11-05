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

package net.lapismc.homespawn;

import net.lapismc.lapiscore.LapisCoreFileWatcher;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.UUID;

class HomeSpawnFileWatcher extends LapisCoreFileWatcher {

    private final HomeSpawn plugin;

    HomeSpawnFileWatcher(HomeSpawn p) {
        super(p);
        plugin = p;
    }

    @Override
    public void checkOtherFile(File f) {
        UUID uuid = null;
        try {
            uuid = UUID.fromString(f.getName().replace(".yml", ""));
        } catch (IllegalArgumentException ignored) {
        }
        if (uuid != null && Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
            plugin.getPlayer(uuid).reloadConfig();
            plugin.getLogger().info("Changes made to " + Bukkit.getOfflinePlayer(uuid).getName() + "'s config have been loaded");
        }
    }

}
