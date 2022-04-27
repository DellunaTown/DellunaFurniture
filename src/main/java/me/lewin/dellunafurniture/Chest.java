package me.lewin.dellunafurniture;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class Chest implements Listener {
    private static HashMap<String, Inventory> invMap = new HashMap<>();
    public static HashMap<Player, Boolean> clickMap = new HashMap<>();

    public static void getInventory(Player player, String uuid) {
        if (!invMap.containsKey(uuid)) {
            Inventory inv = Bukkit.createInventory(null, 27, "§l§7" + uuid);
            YamlConfiguration c = DataBase.getChestConfig(uuid);
            DataBase.saveConfig(c, DataBase.getFile("chest", uuid));

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

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().length() != 40) return;
        if (DataBase.getChestNames().contains(event.getView().getTitle().substring(4))) {
            clickMap.remove(event.getPlayer());
            if (event.getViewers().size() == 1) {
                YamlConfiguration c = DataBase.getChestConfig(event.getView().getTitle().substring(4));
                c.set("contents", event.getInventory().getContents());
                DataBase.saveConfig(c, DataBase.getFile("chest", event.getView().getTitle().substring(4)));
                invMap.remove(event.getView().getTitle().substring(4));
            }
        }
    }
}
