package net.lapismc.homespawn;

import net.lapismc.homespawn.playerdata.HomeSpawnPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class HomeSpawn extends JavaPlugin {

    private HashMap<UUID, HomeSpawnPlayer> players = new HashMap<>();

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public HomeSpawnPlayer getPlayer(UUID uuid) {
        if (!players.containsKey(uuid)) {
            players.put(uuid, new HomeSpawnPlayer(this, uuid));
        }
        return players.get(uuid);
    }
}
