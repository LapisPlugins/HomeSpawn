/*
 * Copyright 2020 Benjamin Martin
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
import net.lapismc.lapiscore.utils.LapisUpdater;
import net.lapismc.lapiscore.utils.LocationUtils;
import net.lapismc.lapiscore.utils.Metrics;
import net.lapismc.lapisui.LapisUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Millisecond;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class HomeSpawn extends LapisCorePlugin {

    private final Cache<UUID, HomeSpawnPlayer> players = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS).build();
    public PrettyTime prettyTime;
    public LapisUpdater lapisUpdater;
    private HomeSpawnFileWatcher fileWatcher;

    @Override
    public void onEnable() {
        registerConfiguration(new LapisCoreConfiguration(this, 2, 2));
        registerPermissions(new HomeSpawnPermissions(this));
        prettyTime = new PrettyTime();
        prettyTime.setLocale(Locale.ENGLISH);
        prettyTime.removeUnit(JustNow.class);
        prettyTime.removeUnit(Millisecond.class);
        if (getConfig().getBoolean("FileWatcher"))
            fileWatcher = new HomeSpawnFileWatcher(this);
        new LapisUI().registerPlugin(this);
        new HomeSpawnListeners(this);
        new HomeSpawnCommands(this);
        new HomeSpawnDataConverter(this);
        new HomeSpawnPlayerData().init(this);
        checkUpdates();
        new Metrics(this);
        getLogger().info(getDescription().getName() + " v" + getDescription().getVersion() + " has been enabled");
    }

    @Override
    public void onDisable() {
        //Stop the file watcher task for reloads
        if (fileWatcher != null) {
            fileWatcher.stop();
        }
        //Attempt to stop all teleport tasks so that there are no tasks waiting on reload
        for (HomeSpawnPlayer player : players.asMap().values()) {
            player.cancelTeleport();
        }
        getLogger().info(getDescription().getName() + " has been disabled");
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

    public Location getSpawn(boolean isNew) {
        File file = new File(getDataFolder() + File.separator + "spawn.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration spawn = YamlConfiguration.loadConfiguration(file);
        String locationString = spawn.getString(isNew ? "SpawnNew" : "Spawn", "");
        return parseStringToLocation(locationString);
    }

    public String parseLocationToString(Location loc) {
        return new LocationUtils().parseLocationToString(loc);
    }

    public Location parseStringToLocation(String s) {
        return new LocationUtils().parseStringToLocation(s);
    }
}
