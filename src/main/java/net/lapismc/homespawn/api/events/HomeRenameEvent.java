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

package net.lapismc.homespawn.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@SuppressWarnings({"unused", "WeakerAccess", "CanBeFinal"})
public class HomeRenameEvent extends Event implements Cancellable {

    public static HandlerList handlers = new HandlerList();
    private final String oldHome;
    private final String newHome;
    private final Player p;
    private String reason;
    private boolean cancelled;

    public HomeRenameEvent(Player p, String oldHome, String newHome) {
        this.oldHome = oldHome;
        this.newHome = newHome;
        this.p = p;
        this.cancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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

    public String getReason() {
        return reason;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    @Deprecated
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public void setCancelled(boolean cancel, String reason) {
        cancelled = cancel;
        this.reason = reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
