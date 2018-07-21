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

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

@SuppressWarnings("FieldCanBeLocal")
public class HomeSpawnConfiguration {

    private final int configVersion = 1;
    private final int messagesVersion = 1;
    private final HomeSpawn plugin;
    public String primaryColor = ChatColor.WHITE.toString();
    public String secondaryColor = ChatColor.BLUE.toString();
    private File messagesFile;
    private YamlConfiguration messages;

    HomeSpawnConfiguration(HomeSpawn p) {
        plugin = p;
        messagesFile = new File(plugin.getDataFolder() + File.separator + "messages.yml");
        generateConfigs();
        checkConfigVersions();
    }

    void generateConfigs() {
        plugin.saveDefaultConfig();
        //if the messages file doesn't exist, extract it from the jar file and place it in the plugin dir
        if (!messagesFile.exists()) {
            try (InputStream is = plugin.getResource("messages.yml");
                 OutputStream os = new FileOutputStream(messagesFile)) {
                int readBytes;
                byte[] buffer = new byte[4096];
                while ((readBytes = is.read(buffer)) > 0) {
                    os.write(buffer, 0, readBytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        primaryColor = ChatColor.translateAlternateColorCodes('&', messages.getString("primaryColor", ChatColor.GOLD.toString()));
        secondaryColor = ChatColor.translateAlternateColorCodes('&', messages.getString("secondaryColor", ChatColor.RED.toString()));
    }

    public void reloadMessages(File f) {
        if (f != null) {
            messagesFile = f;
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        primaryColor = ChatColor.translateAlternateColorCodes('&', messages.getString("primaryColor", ChatColor.GOLD.toString()));
        secondaryColor = ChatColor.translateAlternateColorCodes('&', messages.getString("secondaryColor", ChatColor.RED.toString()));
    }

    public YamlConfiguration getMessages() {
        return messages;
    }

    private void checkConfigVersions() {
        if (plugin.getConfig().getInt("ConfigVersion") != configVersion) {
            File oldConfig = new File(plugin.getDataFolder() + File.separator + "config_OLD.yml");
            File config = new File(plugin.getDataFolder() + File.separator + "config.yml");
            config.renameTo(oldConfig);
            plugin.saveDefaultConfig();
            plugin.getLogger().info("The config.yml file has been updated, it is now called config_OLD.yml," +
                    " please transfer any values into the new config.yml");
        }
        if (messages.getInt("ConfigVersion") != messagesVersion) {
            File oldMessages = new File(plugin.getDataFolder() + File.separator + "messages_OLD.yml");
            messagesFile.renameTo(oldMessages);
            generateConfigs();
            plugin.getLogger().info("The messages.yml file has been updated, it is now called messages_OLD.yml," +
                    " please transfer any values into the new messages.yml");
        }
    }

    public String getColoredMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString(key).replace("&p", primaryColor).replace("&s", secondaryColor));
    }

}
