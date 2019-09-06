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
import net.lapismc.homespawn.util.TeleportTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Home {

    private final UUID owner;
    private final HomeSpawn plugin;
    private String name;
    private Location loc;
    private TeleportTask teleportTask;

    public Home(HomeSpawn plugin, UUID owner, String name, Location loc) {
        this.plugin = plugin;
        this.owner = owner;
        this.name = name;
        this.loc = loc;
    }

    Home(HomeSpawn plugin, UUID owner, String name, String loc) {
        this.plugin = plugin;
        this.owner = owner;
        this.name = name;
        this.loc = parseStringToLocation(loc);
    }

    boolean isValid() {
        return loc != null;
    }

    public void rename(String newName) {
        HomeSpawnPlayer player = plugin.getPlayer(owner);
        player.deleteHome(this);
        name = newName;
        setLoc(loc);
        player.addHome(this);
    }

    boolean isWaiting() {
        return teleportTask != null;
    }

    void cancelTeleport() {
        if (teleportTask != null && teleportTask.isNotCancelled()) {
            teleportTask.getPlayer().sendMessage(plugin.config.getMessage("Home.Cancelled"));
            teleportTask.cancelTask();
            teleportTask = null;
        }
    }

    void skipTeleportTimer() {
        if (teleportTask != null && teleportTask.isNotCancelled()) {
            teleport(teleportTask.getPlayer());
        }
    }

    public void setLoc(Location loc) {
        HomeSpawnPlayer player = plugin.getPlayer(owner);
        YamlConfiguration yaml = player.getConfig();
        yaml.set("Homes." + name, parseLocationToString(loc));
        player.saveConfig(yaml);
        this.loc = loc;
    }

    public Location getLocation() {
        if (loc == null) {
            loadLocation();
        }
        return loc;
    }

    private void loadLocation() {
        HomeSpawnPlayer player = plugin.getPlayer(owner);
        YamlConfiguration yaml = player.getConfig();
        loc = parseStringToLocation(yaml.getString("Homes." + name));
    }

    public String getName() {
        return name;
    }

    public void teleportPlayer(Player p) {
        if (teleportTask != null) {
            teleportTask.cancelTask();
            teleportTask = null;
        }
        boolean delay = plugin.perms.isPermitted(p.getUniqueId(), Permission.TeleportDelay.getPermission());
        if (delay) {
            Integer delayTime = plugin.perms.getPermissionValue(p.getUniqueId(),
                    Permission.TeleportDelay.getPermission());
            p.sendMessage(plugin.config.getMessage("Home.Wait").replace("%TIME%", delayTime.toString()));
            teleportTask = new TeleportTask(Bukkit.getScheduler().runTaskLater(plugin,
                    () -> teleport(p), delayTime * 20), p);
        } else {
            this.teleport(p);
        }
    }

    private void teleport(Player p) {
        if (teleportTask != null) {
            teleportTask.cancelTask();
            teleportTask = null;
        }
        teleportNow(p);
        p.sendMessage(plugin.config.getMessage("Home.Teleport"));
    }

    private void teleportNow(Player p) {
        if (loc == null) {
            loadLocation();
        }
        if (!loc.getChunk().isLoaded()) {
            loc.getChunk().load();
        }
        if (p.isInsideVehicle()) {
            if (p.getVehicle() instanceof Horse) {
                Horse horse = (Horse) p.getVehicle();
                horse.eject();
                horse.teleport(loc);
                p.teleport(loc);
                horse.addPassenger(p);
            }
        } else {
            p.teleport(loc);
        }
    }

    public String getLocationString() {
        return parseLocationToString(loc);
    }

    private String parseLocationToString(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + ","
                + loc.getZ() + "," + loc.getPitch() + "," + loc.getYaw();
    }

    private Location parseStringToLocation(String s) {
        Location loc;
        String[] args = s.split(",");
        String worldName = args[0];
        if (Bukkit.getServer().getWorld(worldName) == null) {
            //This has been delayed by one tick since this code is called when the player
            //is being initialized which can lead to an infinite loop
            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.getPlayer(owner).deleteHome(this);
                plugin.getLogger().warning(plugin.config.getMessage("Error.WorldDoesNotExist")
                        .replace("%WORLD%", worldName));
            });
            return null;
        }
        World world = plugin.getServer().getWorld(worldName);
        try {
            double x = Double.parseDouble(args[1]);
            double y = Double.parseDouble(args[2]);
            double z = Double.parseDouble(args[3]);
            float pitch = Float.parseFloat(args[4]);
            float yaw = Float.parseFloat(args[5]);
            loc = new Location(world, x, y, z, yaw, pitch);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            plugin.getPlayer(owner).deleteHome(this);
            plugin.getLogger().warning(plugin.config.getMessage("Error.NumberFormatError"));
            return null;
        }
        return loc;
    }
}
