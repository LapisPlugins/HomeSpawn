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

import net.lapismc.homespawn.playerdata.HomeSpawnPlayer;
import net.lapismc.homespawn.playerdata.Permission;
import net.lapismc.lapiscore.LapisCorePermissions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

public class HomeSpawnPermissions extends LapisCorePermissions {

    private HomeSpawn plugin;

    HomeSpawnPermissions(HomeSpawn core) {
        super(core);
        this.plugin = core;
        registerPermissions();
        loadPermissions();
    }

    private void registerPermissions() {
        for (Permission permission : Permission.values()) {
            registerPermissions(permission.getPermission());
        }
    }

    @Override
    public org.bukkit.permissions.Permission getOfflinePlayerPermission(OfflinePlayer op) {
        HomeSpawnPlayer player = plugin.getPlayer(op.getUniqueId());
        YamlConfiguration yaml = player.getConfig();
        if (yaml.contains("Permission") && !yaml.getString("Permission").equals("")) {
            return Bukkit.getPluginManager().getPermission(yaml.getString("Permission"));
        }
        return null;
    }

    @Override
    protected void savePlayersPermission(OfflinePlayer op, org.bukkit.permissions.Permission perm) {
        HomeSpawnPlayer player = plugin.getPlayer(op.getUniqueId());
        YamlConfiguration yaml = player.getConfig();
        yaml.set("Permission", perm.getName());
        player.saveConfig(yaml);
    }

}
