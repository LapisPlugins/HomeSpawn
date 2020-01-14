/*
 * Copyright 2020 Benjamin Martin
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

@SuppressWarnings("unused")
public class HomeMoveEvent extends LapisCoreCancellableEvent {

    private final String name;
    private final Location oldHome;
    private final Location newHome;
    private final Player p;

    public HomeMoveEvent(Player p, String name, Location oldHome, Location newHome) {
        this.name = name;
        this.oldHome = oldHome;
        this.newHome = newHome;
        this.p = p;
    }

    public String getName() {
        return name;
    }

    public Location getOldHome() {
        return oldHome;
    }

    public Location getNewHome() {
        return newHome;
    }

    public Player getPlayer() {
        return p;
    }

}
