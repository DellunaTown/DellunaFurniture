package me.lewin.dellunafurniture;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashMap;
import java.util.UUID;

public class Chair implements Listener {
    public static HashMap<Player, String> chairlist = new HashMap<>();

    @EventHandler
    private void onED(EntityDismountEvent e) {
        Entity E = e.getEntity();
        if(!(E instanceof Player)) return;
        Player p = (Player) E;
        if (chairlist.containsKey(p)) {
            Bukkit.getEntity(UUID.fromString(chairlist.get(p))).remove();
            chairlist.remove(p);
        }
    }

}
