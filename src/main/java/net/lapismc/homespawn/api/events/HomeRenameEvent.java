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
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class HomeRenameEvent extends LapisCoreCancellableEvent {

    private final String oldHome;
    private final String newHome;
    private final Player p;

    public HomeRenameEvent(Player p, String oldHome, String newHome) {
        this.oldHome = oldHome;
        this.newHome = newHome;
        this.p = p;
    }

    public String getOldHome() {
        return oldHome;
    }

    public String getNewHome() {
        return newHome;
    }

    public Player getPlayer() {
        return p;
    }

}
