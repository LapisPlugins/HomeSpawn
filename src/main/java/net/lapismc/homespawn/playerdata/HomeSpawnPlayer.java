/*
 * Copyright 2019 Benjamin Martin
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
import net.lapismc.homespawn.util.EasyComponent;
import net.lapismc.homespawn.util.TeleportTask;
import net.lapismc.lapiscore.utils.CompatibleMaterial;
import net.lapismc.lapiscore.utils.LapisItemBuilder;
import net.lapismc.lapisui.menu.SinglePage;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ocpsoft.prettytime.Duration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomeSpawnPlayer {

    private final HomeSpawn plugin;
    private final UUID uuid;
    private ArrayList<Home> homes = new ArrayList<>();
    private TeleportTask teleportTask;

    public HomeSpawnPlayer(HomeSpawn plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
        loadHomes();
    }

    private void loadHomes() {
        homes = new ArrayList<>();
        YamlConfiguration yaml = getConfig();
        if (!yaml.contains("Homes")) {
            return;
        }
        ConfigurationSection permsSection = yaml.getConfigurationSection("Homes");
        Set<String> homes = permsSection.getKeys(false);
        for (String homeString : homes) {
            String location = yaml.getString("Homes." + homeString);
            Home home = new Home(plugin, uuid, homeString, location);
            if (home.isValid())
                addHome(home);
        }
    }

    private void loadHomesIfEmpty() {
        if (homes.isEmpty()) {
            loadHomes();
        }
    }

    public boolean isNotWaitingForTeleport() {
        if (teleportTask != null && teleportTask.isNotCancelled()) {
            return false;
        }
        for (Home home : homes) {
            if (home.isWaiting())
                return false;
        }
        return true;
    }

    public void cancelTeleport() {
        if (teleportTask != null && teleportTask.isNotCancelled()) {
            teleportTask.cancelTask();
            teleportTask.getPlayer().sendMessage(plugin.config.getMessage("Home.Cancelled"));
            teleportTask = null;
        }
        for (Home home : homes) {
            if (home.isWaiting())
                home.cancelTeleport();
        }
    }

    public void skipTeleportTimer() {
        if (teleportTask != null && teleportTask.isNotCancelled()) {
            teleportTask.getPlayer().teleport(teleportTask.getLocation());
            teleportTask.getPlayer().sendMessage(plugin.config.getMessage("Spawn.Teleport"));
            teleportTask.cancelTask();
            teleportTask = null;
        }
        for (Home home : homes) {
            if (home.isWaiting())
                home.skipTeleportTimer();
        }
    }

    public void teleportToSpawn(Location spawn) {
        if (teleportTask != null) {
            teleportTask.cancelTask();
            teleportTask = null;
        }
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) {
            return;
        }
        boolean delay = plugin.perms.isPermitted(p.getUniqueId(), Permission.TeleportDelay.getPermission());
        if (delay) {
            Integer delayTime = plugin.perms.getPermissionValue(p.getUniqueId(),
                    Permission.TeleportDelay.getPermission());
            p.sendMessage(plugin.config.getMessage("Home.Wait").replace("%TIME%", delayTime.toString()));
            teleportTask = new TeleportTask(Bukkit.getScheduler().runTaskLater(plugin,
                    () -> {
                        p.teleport(spawn);
                        p.sendMessage(plugin.config.getMessage("Spawn.Teleport"));
                    }, delayTime * 20), p, spawn);
        } else {
            p.teleport(spawn);
            p.sendMessage(plugin.config.getMessage("Spawn.Teleport"));
        }
    }

    public boolean hasHome(String name) {
        loadHomesIfEmpty();
        for (Home home : homes) {
            if (home.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Home getHome(String name) {
        loadHomesIfEmpty();
        for (Home home : homes) {
            if (home.getName().equalsIgnoreCase(name)) {
                return home;
            }
        }
        return null;
    }

    public List<Home> getHomes() {
        return homes;
    }

    public void sendHomesList(CommandSender sender) {
        loadHomesIfEmpty();
        StringBuilder message = new StringBuilder();
        for (Home home : homes) {
            message.append(" ");
            message.append(plugin.secondaryColor).append(home.getName());
            message.append(" ");
        }
        sender.sendMessage(message.toString());
    }

    public void sendClickableHomesList(Player p) {
        loadHomesIfEmpty();
        String command = p.getUniqueId().equals(uuid) ? "/home " : "/homespawn player " + Bukkit.getOfflinePlayer(uuid).getName();
        EasyComponent component = new EasyComponent("");
        for (Home home : homes) {
            component.append(" ");
            component.append(plugin.secondaryColor + ChatColor.UNDERLINE + home.getName() + ChatColor.RESET)
                    .onClickRunCmd(command + home.getName())
                    .onHover(plugin.primaryColor + "Click to teleport");
            component.append(" ");
        }
        component.send(p);
    }

    public void showHomesGUI(Player p) {
        loadHomesIfEmpty();
        new HomeListGUI().showTo(p);
    }

    public void deleteHome(Home home) {
        YamlConfiguration yaml = getConfig();
        yaml.set("Homes." + home.getName(), null);
        if (yaml.getConfigurationSection("Homes").getKeys(false).isEmpty()) {
            yaml.set("Homes", null);
        }
        saveConfig(yaml);
        homes.remove(home);
    }

    public void addHome(Home home) {
        homes.add(home);
    }

    public String getPlayerInfo() {
        loadHomesIfEmpty();
        String info = plugin.config.getMessage("PlayerData");
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        long time = getConfig().getLong("Time");
        String timeString = plugin.prettyTime.format(
                reduceDurationList(plugin.prettyTime.calculatePreciseDuration(new Date(time))));
        info = info.replace("%NAME%", op.getName());
        org.bukkit.permissions.Permission assignedPermission = plugin.perms.getAssignedPermission(op.getUniqueId());
        info = info.replace("%PERMISSION%", assignedPermission == null ? "Unknown" : assignedPermission.getName());
        info = info.replace("%STATE%", op.isOnline() ? "online" : "offline");
        info = info.replace("%TIME%", timeString);
        info = info.replace("%USED%", String.valueOf(homes.size()));
        info = info.replace("%TOTAL%",
                (plugin.perms.getPermissionValue(uuid, Permission.Homes.getPermission())) + "");
        return info;
    }

    public YamlConfiguration getConfig() {
        File file = new File(plugin.getDataFolder() + File.separator
                + "PlayerData" + File.separator + uuid.toString() + ".yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdir();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public void reloadConfig() {
        loadHomes();
    }

    public void saveConfig(YamlConfiguration yaml) {
        File file = new File(plugin.getDataFolder() + File.separator
                + "PlayerData" + File.separator + uuid.toString() + ".yml");
        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Duration> reduceDurationList(List<Duration> durationList) {
        while (durationList.size() > 2) {
            Duration smallest = null;
            for (Duration current : durationList) {
                if (smallest == null || smallest.getUnit().getMillisPerUnit() > current.getUnit().getMillisPerUnit()) {
                    smallest = current;
                }
            }
            durationList.remove(smallest);
        }
        return durationList;
    }

    private class HomeListGUI extends SinglePage<Home> {

        Random r = new Random(System.currentTimeMillis());
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);

        HomeListGUI() {
            super(homes);
            setTitle(getTitlePrefix());
        }

        protected String getTitlePrefix() {
            if (op == null) {
                return "Your Homes";
            }
            return op.getName() + "'s homes";
        }

        @Override
        protected ItemStack toItemStack(Home home) {
            return new LapisItemBuilder(CompatibleMaterial.WHITE_WOOL.parseMaterial())
                    .woolColor(LapisItemBuilder.WoolColor.values()[r.nextInt(DyeColor.values().length)])
                    .setName(plugin.primaryColor + home.getName())
                    .build();
        }

        @Override
        protected void onItemClick(Player player, Home home) {
            player.closeInventory();
            home.teleportPlayer(player);
        }
    }
}
