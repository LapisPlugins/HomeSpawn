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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;


public class HomeSpawnPermissions {

    private final HashMap<Permission, HashMap<Perm, Integer>> pluginPerms = new HashMap<>();
    private final HomeSpawn plugin;
    private HashMap<UUID, Permission> playerPerms = new HashMap<>();

    HomeSpawnPermissions(HomeSpawn p) {
        plugin = p;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> playerPerms.clear(),
                20 * 60 * 5, 20 * 60 * 5);
        loadPermissions();
    }

    public void loadPermissions() {
        pluginPerms.clear();
        playerPerms.clear();
        ConfigurationSection permsSection = plugin.getConfig().getConfigurationSection("Permissions");
        Set<String> perms = permsSection.getKeys(false);
        for (String perm : perms) {
            HashMap<Perm, Integer> permMap = new HashMap<>();
            String permName = perm.replace(",", ".");
            for (Perm permission : Perm.values()) {
                int i = plugin.getConfig().getInt("Permissions." + perm + "." + permission.name());
                permMap.put(permission, i);
            }
            PermissionDefault permissionDefault;
            switch (permMap.get(Perm.Default)) {
                case 1:
                    permissionDefault = PermissionDefault.TRUE;
                    break;
                case 2:
                    permissionDefault = PermissionDefault.OP;
                    break;
                case 0:
                default:
                    permissionDefault = PermissionDefault.FALSE;
                    break;
            }
            Permission permission = new Permission(permName, permissionDefault);
            if (Bukkit.getPluginManager().getPermission(permName) == null) {
                Bukkit.getPluginManager().addPermission(permission);
            }
            pluginPerms.put(permission, permMap);
        }
    }

    private Permission getPlayerPermission(UUID uuid) {
        Permission p = null;
        if (playerPerms.get(uuid) != null) {
            return playerPerms.get(uuid);
        }
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        if (op.isOnline()) {
            Player player = op.getPlayer();
            Integer priority = -1;
            for (Permission perm : pluginPerms.keySet()) {
                if (player.hasPermission(perm) &&
                        (pluginPerms.get(perm).get(Perm.Priority) > priority)) {
                    p = perm;
                    priority = pluginPerms.get(perm).get(Perm.Priority);
                }
            }
            if (p == null) {
                return null;
            } else {
                playerPerms.put(uuid, p);
                HomeSpawnPlayer homeSpawnPlayer = plugin.getPlayer(uuid);
                YamlConfiguration yaml = homeSpawnPlayer.getConfig();
                yaml.set("Permission", p.getName());
                homeSpawnPlayer.saveConfig(yaml);
            }
            return p;
        } else if (op.hasPlayedBefore()) {
            HomeSpawnPlayer homeSpawnPlayer = plugin.getPlayer(uuid);
            YamlConfiguration yaml = homeSpawnPlayer.getConfig();
            String permission = yaml.getString("Permission");
            if (permission == null) {
                return null;
            } else {
                p = Bukkit.getPluginManager().getPermission(permission);
                playerPerms.put(uuid, p);
                return p;
            }
        }
        return null;
    }

    public String getPlayersPermission(UUID uuid) {
        Permission p = getPlayerPermission(uuid);
        if (p == null) {
            return plugin.HSConfig.getColoredMessage("Error.PermissionNotFound");
        } else {
            return p.getName();
        }
    }

    public Boolean isPermitted(UUID uuid, Perm perm) {
        HashMap<Perm, Integer> permMap;
        Permission p = getPlayerPermission(uuid);
        if (!pluginPerms.containsKey(p) || pluginPerms.get(p) == null) {
            loadPermissions();
            permMap = pluginPerms.get(p);
        } else {
            permMap = pluginPerms.get(p);
        }
        return permMap != null && permMap.get(perm) != null && permMap.get(perm) >= 1;
    }

    public Integer getPermissionValue(UUID uuid, Perm perm) {
        HashMap<Perm, Integer> permMap;
        Permission p = getPlayerPermission(uuid);
        if (!pluginPerms.containsKey(p) || pluginPerms.get(p) == null) {
            loadPermissions();
            permMap = pluginPerms.get(p);
        } else {
            permMap = pluginPerms.get(p);
        }
        return permMap.get(perm);
    }

    public enum Perm {
        //TODO add these to config
        Default, Priority, CanUpdate, CanReload, CanViewPlayerStats, Homes, TeleportDelay, SetSpawn, Spawn, DeleteSpawn;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

}
