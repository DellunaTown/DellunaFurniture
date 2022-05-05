package me.lewin.dellunafurniture.func;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import java.lang.reflect.InvocationTargetException;

public class Bed implements Listener {
    private static ProtocolManager protocolManager;

    public static void onInteract(Location click_loc, double offsetY, Boolean rotation, String direction4, String direction8, Player player) {
        System.out.println("click");

        PacketContainer useBed = protocolManager.createPacket(PacketType.Play.Server.BED);

        useBed.getIntegers()
                .write(0, player.getEntityId())
                .write(1, click_loc.getBlockX())
                .write(2, click_loc.getBlockY())
                .write(3, click_loc.getBlockZ());

        try {
            protocolManager.sendServerPacket(player, useBed);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(
                    "Cannot send packet " + useBed, e);
        }
    }

    @EventHandler
    private void onSleep(PlayerBedEnterEvent event) {
        //System.out.println(event.getPlayer().getPose());
    }
}
