/*
 * Copyright 2019 Benjamin Martin
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

package net.lapismc.homespawn.playerdata;

import net.lapismc.lapiscore.permissions.LapisPermission;

public enum Permission {

    Homes(new Homes()), TeleportDelay(new TeleportDelay()), Spawn(new Spawn()), SetSpawn(new SetSpawn()),
    DeleteSpawn(new DeleteSpawn()), CanUpdate(new CanUpdate()), CanReload(new CanReload()), CanViewPlayerStats(new CanViewPlayerStats());

    private final LapisPermission permission;

    Permission(LapisPermission permission) {
        this.permission = permission;
    }

    public LapisPermission getPermission() {
        return this.permission;
    }

    private static class Homes extends LapisPermission {
        //The number of homes a player can set
        Homes() {
            super("Homes");
        }
    }

    private static class TeleportDelay extends LapisPermission {
        //The delay applied to teleports
        TeleportDelay() {
            super("TeleportDelay");
        }
    }

    private static class Spawn extends LapisPermission {
        //Allows the player to teleport to spawn
        Spawn() {
            super("Spawn");
        }
    }

    private static class SetSpawn extends LapisPermission {
        //Allows the player to set spawn
        SetSpawn() {
            super("SetSpawn");
        }
    }

    private static class DeleteSpawn extends LapisPermission {
        //Allows the player to delete spawn
        DeleteSpawn() {
            super("DeleteSpawn");
        }
    }

    private static class CanUpdate extends LapisPermission {
        //Allows the player to update the plugin
        CanUpdate() {
            super("CanUpdate");
        }
    }

    private static class CanReload extends LapisPermission {
        //Allows the player to reload the plugin
        CanReload() {
            super("CanReload");
        }
    }

    private static class CanViewPlayerStats extends LapisPermission {
        //Allows the player to view other players statistics
        CanViewPlayerStats() {
            super("CanViewPlayerStats");
        }
    }

}
