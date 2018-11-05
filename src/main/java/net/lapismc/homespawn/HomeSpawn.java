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
import net.lapismc.homespawn.api.HomeSpawnPlayerData;
import net.lapismc.homespawn.playerdata.HomeSpawnPlayer;
import net.lapismc.homespawn.util.HomeSpawnDataConverter;
import net.lapismc.lapiscore.LapisCoreConfiguration;
import net.lapismc.lapiscore.LapisCorePlugin;
import net.lapismc.lapiscore.LapisUpdater;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Millisecond;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class HomeSpawn extends LapisCorePlugin {

    public PrettyTime prettyTime;
    public HomeSpawnPermissions perms;
    public LapisUpdater lapisUpdater;
    private final Cache<UUID, HomeSpawnPlayer> players = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES).build();

    //TODO add help messages to messages.yml and commands

    @Override
    public void onEnable() {
        registerConfiguration(new LapisCoreConfiguration(this, 1, 1));
        this.perms = new HomeSpawnPermissions(this);
        registerPermissions(this.perms);
        prettyTime = new PrettyTime();
        prettyTime.setLocale(Locale.ENGLISH);
        prettyTime.removeUnit(JustNow.class);
        prettyTime.removeUnit(Millisecond.class);
        new HomeSpawnFileWatcher(this);
        new HomeSpawnListeners(this);
        new HomeSpawnCommands(this);
        new HomeSpawnDataConverter(this);
        new HomeSpawnPlayerData().init(this);
        checkUpdates();
        getLogger().info(getDescription().getName() + " v" + getDescription().getVersion() + " has been enabled");
    }

    private void checkUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            lapisUpdater = new LapisUpdater(this, "HomeSpawn", "LapisPlugins", "HomeSpawn", "master");
            //check for an update
            if (lapisUpdater.checkUpdate()) {
                //if there in an update but download is disabled and notification is enabled then notify in console
                if (getConfig().getBoolean("Update.NotifyConsole") && !getConfig().getBoolean("Update.Download")) {
                    getLogger().info(config.getMessage("Update.Available"));
                } else if (getConfig().getBoolean("Update.Download")) {
                    //if downloading updates is enabled then download it and notify console
                    lapisUpdater.downloadUpdate();
                    getLogger().info(config.getMessage("Update.Downloading"));
                }
            } else {
                //if there is no update and notify is enabled then notify console that there was no update
                if (getConfig().getBoolean("UpdateNotification")) {
                    getLogger().info(config.getMessage("Update.NotAvailable"));
                }
            }
        });
    }

    public HomeSpawnPlayer getPlayer(UUID uuid) {
        if (players.getIfPresent(uuid) == null) {
            players.put(uuid, new HomeSpawnPlayer(this, uuid));
        }
        return players.getIfPresent(uuid);
    }

    public String parseLocationToString(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + ","
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
            float pitch = Float.parseFloat(args[4]);
            float yaw = Float.parseFloat(args[5]);
            double x = Double.parseDouble(args[1]);
            double y = Double.parseDouble(args[2]);
            double z = Double.parseDouble(args[3]);
            loc = new Location(world, x, y, z, yaw, pitch);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
        return loc;
    }
}
