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

import me.kangarko.ui.UIDesignerAPI;
import me.kangarko.ui.menu.menues.MenuPagged;
import me.kangarko.ui.model.ItemCreator;
import net.lapismc.homespawn.HomeSpawn;
import net.lapismc.homespawn.HomeSpawnPermissions;
import net.lapismc.homespawn.util.EasyComponent;
import net.lapismc.homespawn.util.TeleportTask;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::loadHomes);
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
            teleportTask.getPlayer().sendMessage(plugin.HSConfig.getColoredMessage("Home.Cancelled"));
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
            teleportTask.getPlayer().sendMessage(plugin.HSConfig.getColoredMessage("Spawn.Teleport"));
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
        boolean delay = plugin.HSPerms.isPermitted(p.getUniqueId(), HomeSpawnPermissions.Perm.TeleportDelay);
        if (delay) {
            Integer delayTime = plugin.HSPerms.getPermissionValue(p.getUniqueId(),
                    HomeSpawnPermissions.Perm.TeleportDelay);
            p.sendMessage(plugin.HSConfig.getColoredMessage("Home.Wait").replace("%TIME%", delayTime.toString()));
            teleportTask = new TeleportTask(Bukkit.getScheduler().runTaskLater(plugin,
                    () -> {
                        p.teleport(spawn);
                        p.sendMessage(plugin.HSConfig.getColoredMessage("Spawn.Teleport"));
                    }, delayTime * 20), p, spawn);
        } else {
            p.teleport(spawn);
            p.sendMessage(plugin.HSConfig.getColoredMessage("Spawn.Teleport"));
        }
    }

    public boolean hasHome(String name) {
        for (Home home : homes) {
            if (home.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Home getHome(String name) {
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
        StringBuilder message = new StringBuilder();
        for (Home home : homes) {
            message.append(" ");
            message.append(plugin.HSConfig.secondaryColor).append(home.getName());
            message.append(" ");
        }
        sender.sendMessage(message.toString());
    }

    public void sendClickableHomesList(Player p) {
        String command = p.getUniqueId().equals(uuid) ? "/home " : "/homespawn player " + Bukkit.getOfflinePlayer(uuid).getName();
        EasyComponent component = new EasyComponent("");
        for (Home home : homes) {
            component.append(" ");
            component.append(plugin.HSConfig.secondaryColor + home.getName())
                    .onClickRunCmd(command + home.getName())
                    .onHover(plugin.HSConfig.primaryColor + "Click to teleport");
            component.append(" ");
        }
        component.send(p);
    }

    public void showHomesGUI(Player p) {
        UIDesignerAPI.setPlugin(plugin);
        new HomeListGUI().displayTo(p);
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
        String info = plugin.HSConfig.getColoredMessage("PlayerData");
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        Long time = getConfig().getLong("Time");
        String timeString = plugin.prettyTime.format(
                reduceDurationList(plugin.prettyTime.calculatePreciseDuration(new Date(time))));
        info = info.replace("%NAME%", op.getName());
        info = info.replace("%PERMISSION%", plugin.HSPerms.getPlayersPermission(uuid));
        info = info.replace("%STATE%", op.isOnline() ? "online" : "offline");
        info = info.replace("%TIME%", timeString);
        info = info.replace("%USED%", String.valueOf(homes.size()));
        info = info.replace("%TOTAL%",
                (plugin.HSPerms.getPermissionValue(uuid, HomeSpawnPermissions.Perm.Homes)) + "");
        return info;
    }

    public YamlConfiguration getConfig() {
        File file = new File(plugin.getDataFolder() + File.separator
                + "PlayerData" + File.separator + uuid.toString() + ".yml");
        if (!file.exists()) {
            try {
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

    private class HomeListGUI extends MenuPagged<Home> {

        final Random r = new Random(System.currentTimeMillis());
        final OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);

        HomeListGUI() {
            super(9 * 2, null, homes);
            setTitle(getMenuTitle());
        }

        @Override
        protected String getMenuTitle() {
            return op == null ? "Player" : op.getName() + "'s homes";
        }

        @Override
        protected ItemStack convertToItemStack(Home home) {
            return ItemCreator.of(Material.WOOL).color(DyeColor.values()[(r.nextInt(DyeColor.values().length))])
                    .name(plugin.HSConfig.primaryColor + home.getName()).build().make();
        }

        @Override
        protected void onMenuClickPaged(Player player, Home home, ClickType clickType) {
            if (clickType.isLeftClick() || clickType.isRightClick()) {
                player.closeInventory();
                home.teleportPlayer(player);
            }
        }

        @Override
        protected boolean updateButtonOnClick() {
            return false;
        }

        @Override
        protected String[] getInfo() {
            return new String[]{
                    "This is a list of your current homes", "", "Click to teleport!"
            };
        }
    }
}
