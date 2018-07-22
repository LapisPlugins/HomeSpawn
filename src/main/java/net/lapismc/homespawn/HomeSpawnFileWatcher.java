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

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

class HomeSpawnFileWatcher {

    private final HomeSpawn plugin;

    HomeSpawnFileWatcher(HomeSpawn p) {
        plugin = p;
        start();
    }

    private void start() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                watcher();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void watcher() throws IOException, InterruptedException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        Path dir = Paths.get(plugin.getDataFolder().getAbsolutePath());
        dir.register(watcher, ENTRY_DELETE, ENTRY_MODIFY);
        plugin.getLogger().info("HomeSpawn file watcher started!");
        WatchKey key = watcher.take();
        while (key != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();
                File f = fileName.toFile();
                if (kind == ENTRY_DELETE) {
                    if (f.getName().endsWith(".yml")) {
                        String name = f.getName().replace(".yml", "");
                        switch (name) {
                            case "config":
                                plugin.saveDefaultConfig();
                                plugin.reloadConfig();
                                break;
                            case "Messages":
                                plugin.HSConfig.generateConfigs();
                                break;
                        }
                    }
                } else if (kind == ENTRY_MODIFY) {
                    checkPlayerData(f);
                    checkConfig(f);
                }
            }
            key.reset();
            key = watcher.take();
        }
        plugin.getLogger().severe("HomeSpawn file watcher has stopped, please report any errors to dart2112 if this was not intended");
    }

    private void checkPlayerData(File f) {
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

    private void checkConfig(File f) {
        String name = f.getName().replace(".yml", "");
        switch (name) {
            case "config":
                plugin.reloadConfig();
                plugin.HSPerms.loadPermissions();
                plugin.getLogger().info("Changes made to LapisBans config have been loaded");
                break;
            case "Messages":
                plugin.HSConfig.reloadMessages(f);
                plugin.getLogger().info("Changes made to LapisBans Messages.yml have been loaded");
                break;
            default:
                break;
        }
    }

}
