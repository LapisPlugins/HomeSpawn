/*
 * Copyright 2024 Benjamin Martin
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

import net.lapismc.homespawn.commands.*;

import java.util.List;

class HomeSpawnCommands {

    HomeSpawnCommands(HomeSpawn plugin) {
        List<String> disabledCommands = plugin.getConfig().getStringList("DisabledCommands");
        if (!disabledCommands.contains("homespawn"))
            new net.lapismc.homespawn.commands.HomeSpawn(plugin);
        if (!disabledCommands.contains("delhome"))
            new HomeSpawnDelHome(plugin);
        if (!disabledCommands.contains("delspawn"))
            new HomeSpawnDelSpawn(plugin);
        if (!disabledCommands.contains("home"))
            new HomeSpawnHome(plugin);
        if (!disabledCommands.contains("homelist"))
            new HomeSpawnHomeList(plugin);
        if (!disabledCommands.contains("renamehome"))
            new HomeSpawnRenameHome(plugin);
        if (!disabledCommands.contains("sethome"))
            new HomeSpawnSetHome(plugin);
        if (!disabledCommands.contains("setspawn"))
            new HomeSpawnSetSpawn(plugin);
        if (!disabledCommands.contains("spawn"))
            new HomeSpawnSpawn(plugin);
    }

}
