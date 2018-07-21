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

import net.lapismc.homespawn.commands.*;

class HomeSpawnCommands {

    HomeSpawnCommands(HomeSpawn plugin) {
        new HomeSpawnDelHome(plugin);
        new HomeSpawnDelSpawn(plugin);
        new HomeSpawnHome(plugin);
        new HomeSpawnRenameHome(plugin);
        new HomeSpawnSetHome(plugin);
        new HomeSpawnSetSpawn(plugin);
        new HomeSpawnSpawn(plugin);
    }

}
