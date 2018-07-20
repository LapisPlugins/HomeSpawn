package net.lapismc.homespawn.playerdata;

import org.bukkit.Location;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Home {

    private UUID owner;
    private String name;
    private Location loc;

    public Home(UUID owner, String name, Location loc) {
        this.owner = owner;
        this.name = name;
        this.loc = loc;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void teleportPlayer(Player p) {
        boolean delay = true;
        //TODO check if we are delaying teleport

    }

    private void teleport(Player p) {
        if (loc == null) {
            //TODO get location if null, once implemented remove return statement
            return;
        }
        if (!loc.getChunk().isLoaded()) {
            loc.getChunk().load();
        }
        if (p.isInsideVehicle()) {
            if (p.getVehicle() instanceof Horse) {
                Horse horse = (Horse) p.getVehicle();
                horse.eject();
                horse.teleport(loc);
                p.teleport(loc);
                horse.addPassenger(p);
            }
        } else {
            p.teleport(loc);
        }
    }
}
