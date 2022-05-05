package me.lewin.dellunafurniture.func;

import me.lewin.dellunafurniture.DataBase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Chest implements Listener {
    private static HashMap<String, Inventory> invMap = new HashMap<>();
    public static HashMap<Player, String> clickMap = new HashMap<>();

    public static void onInteract(Player player, String uuid) {
        if (!clickMap.containsKey(player)) {
            clickMap.put(player, uuid);

            if (!invMap.containsKey(uuid)) {
                Inventory inv = Bukkit.createInventory(null, 27, "§l§7" + uuid);
                YamlConfiguration c = DataBase.getChestConfig(uuid);
                ArrayList<ItemStack> list = (ArrayList<ItemStack>) c.get("contents");
                if (list != null)  {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i) != null) {
                            inv.setItem(i, list.get(i));
                        }
                    }
                }
                invMap.put(uuid, inv);
            }
            player.openInventory(invMap.get(uuid));

            return;
        }
    }

    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        if (clickMap.containsKey(event.getPlayer())) {
            clickMap.remove(event.getPlayer());
            YamlConfiguration c = DataBase.getChestConfig(event.getView().getTitle().substring(4));
            c.set("contents", event.getInventory().getContents());
            DataBase.saveConfig(c, DataBase.getFile("chest", event.getView().getTitle().substring(4)));
            if (event.getViewers().size() == 1 && event.getViewers().get(0).equals(event.getPlayer())) {
                invMap.remove(event.getView().getTitle().substring(4));
            }
        }
    }

    public static void onRemove(String uuid, Location location) {
        if (invMap.containsKey(uuid)) {
            Inventory inv = invMap.get(uuid);
            List<HumanEntity> list = new ArrayList<>();
            list.addAll(inv.getViewers());

            for (int i = 0; i < list.size(); i++) {
                System.out.println("humanEntity: " + list.get(i));
                list.get(i).closeInventory();
            }
        }

        YamlConfiguration c = DataBase.getChestConfig(uuid);
        if (c.get("contents") != null) {
            for (ItemStack i : (ArrayList<ItemStack>) c.get("contents")) {
                if (i == null) continue;
                location.getWorld().dropItem(location, i);
            }
        }
        DataBase.removeFile("chest", uuid);
    }
}
