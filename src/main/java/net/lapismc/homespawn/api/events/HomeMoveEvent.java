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
 * A cancelable event triggered when a player attempts to move a home location
 */
public class HomeMoveEvent extends LapisCoreCancellableEvent {

    private final String name;
    private final Location oldHome;
    private final Location newHome;
    private final Player p;

    /**
     * Set up a HomeMoveEvent
     *
     * @param p       The player who is trying to move their home
     * @param name    The name of the home they are trying to move
     * @param oldHome The old home location
     * @param newHome The new home location
     */
    public HomeMoveEvent(Player p, String name, Location oldHome, Location newHome) {
        this.name = name;
        this.oldHome = oldHome;
        this.newHome = newHome;
        this.p = p;
    }

    /**
     * @return name of the home involved in the event
     */
    public String getName() {
        return name;
    }

    /**
     * @return the location of the old home
     */
    public Location getOldHome() {
        return oldHome;
    }

    /**
     * @return the location of the new home
     */
    public Location getNewHome() {
        return newHome;
    }

    /**
     * @return the player attempting to move their home
     */
    public Player getPlayer() {
        return p;
    }

}
