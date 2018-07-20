package net.lapismc.homespawn.playerdata;

import net.lapismc.homespawn.HomeSpawn;

import java.util.UUID;

public class HomeSpawnPlayer {

    private HomeSpawn plugin;
    private UUID uuid;

    public HomeSpawnPlayer(HomeSpawn plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
    }


}
