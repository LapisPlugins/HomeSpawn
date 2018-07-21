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

package net.lapismc.homespawn.util;

import net.lapismc.homespawn.HomeSpawn;
import net.lapismc.homespawn.HomeSpawnPermissions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

public abstract class LapisCommand extends BukkitCommand {

    protected final HomeSpawn plugin;

    protected LapisCommand(HomeSpawn plugin, String name, String desc, ArrayList<String> aliases) {
        super(name);
        this.plugin = plugin;
        setDescription(desc);
        setAliases(aliases);
        registerCommand(name);
    }

    protected void sendMessage(CommandSender sender, String key) {
        sender.sendMessage(plugin.HSConfig.getColoredMessage(key));
    }

    protected boolean forcePlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, "Error.MustBePlayer");
            return true;
        }
        return false;
    }

    protected boolean isNotPermitted(UUID uuid, HomeSpawnPermissions.Perm perm) {
        return !plugin.HSPerms.isPermitted(uuid, perm);
    }

    protected Location getSpawnLocation(boolean isNew) {
        File file = new File(plugin.getDataFolder() + File.separator + "spawn.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration spawn = YamlConfiguration.loadConfiguration(file);
        String locationString = spawn.getString(isNew ? "SpawnNew" : "Spawn");
        return parseStringToLocation(locationString);
    }

    protected void setSpawnLocation(Location loc, boolean isNew) {
        File file = new File(plugin.getDataFolder() + File.separator + "spawn.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration spawn = YamlConfiguration.loadConfiguration(file);
        spawn.set(isNew ? "SpawnNew" : "Spawn", parseLocationToString(loc));
        try {
            spawn.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void deleteSpawnLocation(boolean isNew) {
        File file = new File(plugin.getDataFolder() + File.separator + "spawn.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration spawn = YamlConfiguration.loadConfiguration(file);
        spawn.set(isNew ? "SpawnNew" : "Spawn", null);
        try {
            spawn.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String parseLocationToString(Location loc) {
        return loc.getWorld() + "," + loc.getX() + "," + loc.getY() + ","
                + loc.getZ() + "," + loc.getPitch() + "," + loc.getYaw();
    }

    private Location parseStringToLocation(String s) {
        Location loc;
        String[] args = s.split(",");
        String worldName = args[0];
        if (Bukkit.getServer().getWorld(worldName) == null) {
            return null;
        }
        World world = plugin.getServer().getWorld(worldName);
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

    private void registerCommand(String name) {
        try {
            final Field serverCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            serverCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) serverCommandMap.get(Bukkit.getServer());
            commandMap.register(name, this);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        onCommand(sender, args);
        return true;
    }

    protected abstract void onCommand(CommandSender sender, String[] args);

}
