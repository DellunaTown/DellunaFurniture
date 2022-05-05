package me.lewin.dellunafurniture.func;

import me.lewin.dellunafurniture.Main;
import me.lewin.dellunafurniture.Reference;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashMap;
import java.util.UUID;

public class Chair implements Listener {
    public static HashMap<Player, String> chairlist = new HashMap<>();
    public static HashMap<String, Player> chairInfo = new HashMap<>();

    public static void onInteract(Location clicked_loc, double offset, Boolean rotation, String direction4, String direction8, Player player, Boolean blocked) {
        Location clickLoc_chair = clicked_loc.add(0, offset-0.5, 0);
        clickLoc_chair.setPitch(0);

        if (chairInfo.containsKey(clickLoc_chair.getX() + ";" + clickLoc_chair.getY() + ";" + clickLoc_chair.getZ())) {
            player.sendMessage(Reference.FAIL + "누군가가 이미 앉아있습니다.");
            return;
        }

        if (rotation) {
            clickLoc_chair.setYaw(Reference.getYawBy8(Reference.getOppositeDirBy8(direction8)));
        }
        else {
            clickLoc_chair.setYaw(Reference.getYawBy4(Reference.getOppositeDirBy4(direction4)));
        }

        ArmorStand seatArmor = player.getWorld().spawn(clickLoc_chair, ArmorStand.class, b -> {
            try { b.setInvisible(true); } catch(Exception err) { }
            try { b.setSmall(true); } catch(Exception err) { }
            try { b.setGravity(false); } catch(Exception err) { }
            try { b.setMarker(true); } catch(Exception err) { }
            try { b.setBasePlate(false); } catch(Exception err) { }
            try { b.setInvulnerable(true); } catch(Exception err) { }
        });
        seatArmor.addPassenger(player);
        chairlist.put(player, seatArmor.getUniqueId().toString());
        String loc = clickLoc_chair.getX() + ";" + clickLoc_chair.getY() + ";" + clickLoc_chair.getZ();
        chairInfo.put(loc, player);

        if (!blocked) {
            startRotation(seatArmor, loc);
        }
    }

    private static void startRotation(ArmorStand a, String loc) {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {

                if(!chairInfo.containsKey(loc) || a.getPassengers().isEmpty()) {
                    cancel();
                    return;
                }

                Location l = a.getPassengers().get(0).getLocation();
                a.setRotation(l.getYaw(), l.getPitch());
            }
        };

        r.runTaskTimer(JavaPlugin.getPlugin(Main.class), 0, 2);
    }

    public static void onRemove(Location click_loc, double offset) {
        Location l = click_loc.add(0, offset - 0.5, 0);
        String s = l.getX() + ";" + l.getY() + ";" + l.getZ();
        if (chairInfo.containsKey(s)) {
            Player player = chairInfo.get(click_loc.getX() + ";" + click_loc.getY() + ";" + click_loc.getZ());
            String chairUUID = chairlist.get(player);
            Entity e = Bukkit.getEntity(UUID.fromString(chairUUID));
            Location locaiton = e.getLocation();
            locaiton.setYaw(player.getLocation().getYaw());
            locaiton.setPitch(player.getLocation().getPitch());
            player.teleport(locaiton);
            e.remove();
            chairlist.remove(player);
            chairInfo.remove(locaiton.getX() + ";" + locaiton.getY() + ";" + locaiton.getZ());
        }
    }

    @EventHandler
    private void onED(EntityDismountEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (chairlist.containsKey(p)) {
            Location l = e.getDismounted().getLocation().add(0, 1, 0);
            l.setYaw(p.getLocation().getYaw());
            l.setPitch(p.getLocation().getPitch());
            p.teleport(l);
            Bukkit.getEntity(UUID.fromString(chairlist.get(p))).remove();
            chairlist.remove(p);
            chairInfo.remove(e.getDismounted().getLocation().getX() + ";" + e.getDismounted().getLocation().getY() + ";" +e.getDismounted().getLocation().getZ());
        }
    }

    @EventHandler
    private void onQT(PlayerQuitEvent e) {
        if (chairlist.containsKey(e.getPlayer())) {
            Location l = e.getPlayer().getLocation().add(0, 1, 0);
            l.setYaw(e.getPlayer().getLocation().getYaw());
            l.setPitch(e.getPlayer().getLocation().getPitch());
            e.getPlayer().teleport(l);
        }
    }
}
