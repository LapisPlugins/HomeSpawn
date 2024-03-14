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

package net.lapismc.homespawn.api.events;

import net.lapismc.lapiscore.events.LapisCoreCancellableEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * A cancelable event triggered when a player attempts to teleport to spawn
 */
public class SpawnTeleportEvent extends LapisCoreCancellableEvent {

    private final Location location;
    private final Player p;

    /**
     * Setup a SpawnTeleport event
     *
     * @param p The player teleporting to spawn
     * @param l The location they are attempting to teleport too
     */
    public SpawnTeleportEvent(Player p, Location l) {
        this.location = l;
        this.p = p;
    }

    /**
     * @return the spawn location that the player is attempting to teleport to
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @return get the player who is attempting to teleport to spawn
     */
    public Player getPlayer() {
        return p;
    }

}
