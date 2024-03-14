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

import net.lapismc.homespawn.playerdata.Home;
import net.lapismc.lapiscore.events.LapisCoreCancellableEvent;
import org.bukkit.entity.Player;

/**
 * A cancellable event triggered when a player attempts to teleport to one of their homes
 */
public class HomeTeleportEvent extends LapisCoreCancellableEvent {

    private final Home home;
    private final Player p;

    /**
     * Set up a HomeTeleportEvent
     *
     * @param p    the player attempting to teleport to one of their homes
     * @param home the home that the player will teleport to
     */
    public HomeTeleportEvent(Player p, Home home) {
        this.home = home;
        this.p = p;
    }

    /**
     * @return the home that the player is trying to teleport to
     */
    public Home getHome() {
        return home;
    }

    /**
     * @return the player attempting to teleport to one of their homes
     */
    public Player getPlayer() {
        return p;
    }

}
