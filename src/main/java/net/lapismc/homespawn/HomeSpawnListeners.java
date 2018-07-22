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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.lapismc.homespawn.playerdata.HomeSpawnPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

class HomeSpawnListeners implements Listener {

    private final HomeSpawn plugin;
    private final Cache<UUID, String> attackedPlayers = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS).build();

    HomeSpawnListeners(HomeSpawn plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Deal with new players and checking data from reoccurring players
     */

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        HomeSpawnPlayer player = plugin.getPlayer(p.getUniqueId());
        YamlConfiguration yaml = player.getConfig();
        //if there is no time it means the player is new
        if (!yaml.contains("Time")) {
            spawn(p);
        }
        yaml.set("Time", System.currentTimeMillis());
        yaml.set("Permission", plugin.HSPerms.getPlayersPermission(p.getUniqueId()));
        player.saveConfig(yaml);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        HomeSpawnPlayer player = plugin.getPlayer(p.getUniqueId());
        YamlConfiguration yaml = player.getConfig();
        yaml.set("Time", System.currentTimeMillis());
        player.saveConfig(yaml);
    }

    private void spawn(Player p) {
        File file = new File(plugin.getDataFolder() + File.separator + "spawn.yml");
        if (!file.exists()) {
            //if the file doesn't exist then a new spawn certainly wont so we just stop here
            return;
        }
        YamlConfiguration spawn = YamlConfiguration.loadConfiguration(file);
        String locationString = spawn.getString("SpawnNew");
        Location loc = plugin.parseStringToLocation(locationString);
        if (loc != null) {
            p.teleport(loc);
        }
    }

    /**
     * Deal with teleport cancellations
     */

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent e) {
        if (attackedPlayers.getIfPresent(e.getPlayer().getUniqueId()) != null) {
            return;
        }
        boolean cancel = false;
        if (plugin.getConfig().getInt("TeleportCancel.Move") == 1) {
            return;
        } else if (plugin.getConfig().getInt("TeleportCancel.Move") == 0) {
            cancel = true;
        }
        if (plugin.getPlayer(e.getPlayer().getUniqueId()).isNotWaitingForTeleport()) {
            return;
        }
        //check if the player has moved or just looked around
        boolean x = e.getFrom().getBlockX() == e.getTo().getBlockX();
        boolean y = e.getFrom().getBlockY() == e.getTo().getBlockY();
        boolean z = e.getFrom().getBlockZ() == e.getTo().getBlockZ();
        boolean moved = !(x && y && z);
        if (moved) {
            if (cancel) {
                plugin.getPlayer(e.getPlayer().getUniqueId()).cancelTeleport();
            } else {
                plugin.getPlayer(e.getPlayer().getUniqueId()).skipTeleportTimer();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerPvP(EntityDamageByEntityEvent e) {
        //if the attacked entity isn't a player we don't care
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getEntity();
        boolean cancel = false;
        if (plugin.getConfig().getInt("TeleportCancel.PvP") == 1) {
            attackedPlayers.put(p.getUniqueId(), "PvP");
            return;
        } else if (plugin.getConfig().getInt("TeleportCancel.PvP") == 0) {
            cancel = true;
        }
        if (plugin.getPlayer(p.getUniqueId()).isNotWaitingForTeleport()) {
            return;
        }
        //if the attacker isn't a player it might not be PvP
        if (!(e.getDamager() instanceof Player)) {
            // if its an arrow make sure a player shot it, if not we don't care
            if (e.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getDamager();
                if (!(arrow.getShooter() instanceof Player)) {
                    return;
                }
            } else if (e.getDamager() instanceof Wolf) {
                //if its a wolf and it isn't tamed we don't care
                Wolf wolf = (Wolf) e.getDamager();
                if (!wolf.isTamed()) {
                    return;
                }
            } else {
                return;
            }
        }
        //if we get to here its a player or a players wolf who has attacked a player and is probably PvP
        if (cancel) {
            plugin.getPlayer(p.getUniqueId()).cancelTeleport();
        } else {
            plugin.getPlayer(p.getUniqueId()).skipTeleportTimer();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerAttacked(EntityDamageByEntityEvent e) {
        //if the attacked entity isn't a player we don't care
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getEntity();
        boolean cancel = false;
        if (plugin.getConfig().getInt("TeleportCancel.Attacked") == 1) {
            attackedPlayers.put(p.getUniqueId(), "Attacked");
            return;
        } else if (plugin.getConfig().getInt("TeleportCancel.Attacked") == 0) {
            cancel = true;
        }
        if (plugin.getPlayer(p.getUniqueId()).isNotWaitingForTeleport()) {
            return;
        }
        //if the attacker wasn't a mob we don't care
        if (!(e.getDamager() instanceof Monster)) {
            return;
        }
        if (cancel) {
            plugin.getPlayer(p.getUniqueId()).cancelTeleport();
        } else {
            attackedPlayers.put(p.getUniqueId(), "Attacked");
            plugin.getPlayer(p.getUniqueId()).skipTeleportTimer();
        }
    }

}
