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

import net.lapismc.homespawn.api.HomeSpawnPlayerData;
import net.lapismc.homespawn.playerdata.HomeSpawnPlayer;
import net.lapismc.homespawn.util.HomeSpawnDataConverter;
import net.lapismc.homespawn.util.LapisUpdater;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Millisecond;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public final class HomeSpawn extends JavaPlugin {

    public HomeSpawnConfiguration HSConfig;
    public HomeSpawnPermissions HSPerms;
    public PrettyTime prettyTime;
    public LapisUpdater lapisUpdater;
    private HashMap<UUID, HomeSpawnPlayer> players = new HashMap<>();

    //TODO check API with extensions

    @Override
    public void onEnable() {
        //checkUpdates();
        HSConfig = new HomeSpawnConfiguration(this);
        HSPerms = new HomeSpawnPermissions(this);
        prettyTime = new PrettyTime();
        prettyTime.setLocale(Locale.ENGLISH);
        prettyTime.removeUnit(JustNow.class);
        prettyTime.removeUnit(Millisecond.class);
        new HomeSpawnFileWatcher(this);
        new HomeSpawnListeners(this);
        new HomeSpawnCommands(this);
        new HomeSpawnDataConverter(this);
        new HomeSpawnPlayerData().init(this);
    }

    @Override
    public void onDisable() {

    }

    private void checkUpdates() {
        //TODO update these values when we make our new config
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            lapisUpdater = new LapisUpdater(this);
            //check for an update
            if (lapisUpdater.checkUpdate()) {
                //if there in an update but download is disabled and notification is enabled then notify in console
                if (getConfig().getBoolean("UpdateNotification") && !getConfig()
                        .getBoolean("DownloadUpdates")) {
                    getLogger().info("An update for HomeSpawn is available and can be" +
                            " downloaded and installed by running /homespawn update");
                } else if (getConfig().getBoolean("DownloadUpdates")) {
                    //if downloading updates is enabled then download it and notify console
                    lapisUpdater.downloadUpdate();
                    getLogger().info("Downloading Homespawn update, it will be installed " +
                            "on next restart!");
                }
            } else {
                //if there is no update and notify is enabled then notify console that there was no update
                if (getConfig().getBoolean("UpdateNotification")) {
                    getLogger().info("No Update Available");
                }
            }
        });
    }

    public HomeSpawnPlayer getPlayer(UUID uuid) {
        if (!players.containsKey(uuid)) {
            players.put(uuid, new HomeSpawnPlayer(this, uuid));
        }
        return players.get(uuid);
    }

    public String parseLocationToString(Location loc) {
        return loc.getWorld() + "," + loc.getX() + "," + loc.getY() + ","
                + loc.getZ() + "," + loc.getPitch() + "," + loc.getYaw();
    }

    public Location parseStringToLocation(String s) {
        if (s == null) {
            return null;
        }
        Location loc;
        String[] args = s.split(",");
        String worldName = args[0];
        if (Bukkit.getServer().getWorld(worldName) == null) {
            return null;
        }
        World world = getServer().getWorld(worldName);
        try {
            Float pitch = Float.valueOf(args[4]);
            Float yaw = Float.valueOf(args[5]);
            Double x = Double.valueOf(args[1]);
            Double y = Double.valueOf(args[2]);
            Double z = Double.valueOf(args[3]);
            loc = new Location(world, x, y, z, yaw, pitch);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
        return loc;
    }
}
