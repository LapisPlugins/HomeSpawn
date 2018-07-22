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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class TeleportTask {

    private final BukkitTask task;
    private final Player player;
    private Location loc;

    public TeleportTask(BukkitTask task, Player player) {
        this.task = task;
        this.player = player;
    }

    public TeleportTask(BukkitTask task, Player player, Location loc) {
        this.task = task;
        this.player = player;
        this.loc = loc;
    }

    public boolean isNotCancelled() {
        return !task.isCancelled();
    }

    public void cancelTask() {
        task.cancel();
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return loc;
    }
}