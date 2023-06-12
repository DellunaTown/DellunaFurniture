package me.lewin.dellunafurniture.furniture;

import me.lewin.dellunafurniture.DataBase;
import me.lewin.dellunafurniture.Reference;
import me.lewin.dellunafurniture.func.Chair;
import me.lewin.dellunafurniture.func.Chest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;


public class FurnitureInteract implements Listener {
    @EventHandler
    private void onInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && Reference.isFurnitureLocation(event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5))) {

            if (event.getPlayer().isSneaking()) return;

            event.setCancelled(true);

            Player player = event.getPlayer();

            Location click_loc = event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5);

            String type = (String) DataBase.getLocationConfig(click_loc, "type");
            Boolean rotation = (Boolean) DataBase.getLocationConfig(click_loc, "rotation");
            String func = (String) DataBase.getLocationConfig(click_loc, "func");
            String model = (String) DataBase.getLocationConfig(click_loc, "model");
            String uuid = (String) DataBase.getLocationConfig(click_loc, "uuid");
            Location start_loc = (Location) DataBase.getLocationConfig(click_loc, "startLoc");
            String direction4 = (String) DataBase.getLocationConfig(click_loc, "direction4");
            String direction8 = (String) DataBase.getLocationConfig(click_loc, "direction8");
            double offsetY = (double) DataBase.getLocationConfig(click_loc, "offsetY");

            if (func.equals("chest")) {
                Chest.onInteract(player, uuid);
            }
            else if (func.equals("chair_blocked")) {
                Chair.onInteract(click_loc, offsetY, rotation, direction4, direction8, player, true);
            }
            else if (func.equals("chair")) {
                Chair.onInteract(click_loc, offsetY, rotation, direction4, direction8, player, false);
            }
            else if (func.equals("trash")) {
                player.openInventory(Bukkit.createInventory(null, 27, "쓰레기통"));
            }
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEntityEvent event) {
        if (Reference.isFurnitureEntity(event.getRightClicked().getUniqueId().toString())) {
            event.setCancelled(true);

            String uuid = event.getRightClicked().getUniqueId().toString();
            YamlConfiguration frame_config = DataBase.getFrameConfig(uuid);
            String func = frame_config.getString("func");
            int func_bool = frame_config.getInt("func_bool");
            Entity entity = event.getRightClicked();

            if (func.equals("light")) {
                if (func_bool == 3) {
                    entity.getLocation().getBlock().setType(Material.AIR);
                    frame_config.set("func_bool", 0);
                }
                else if (func_bool == 0){
                    Block b = entity.getLocation().getBlock();
                    b.setType(Material.LIGHT);
                    Levelled level = (Levelled) b.getBlockData();
                    level.setLevel(5);
                    b.setBlockData(level, true);
                    frame_config.set("func_bool", 1);
                }
                else if (func_bool == 1){
                    Block b = entity.getLocation().getBlock();
                    b.setType(Material.LIGHT);
                    Levelled level = (Levelled) b.getBlockData();
                    level.setLevel(8);
                    b.setBlockData(level, true);
                    frame_config.set("func_bool", 2);
                }
                else if (func_bool == 2){
                    Block b = entity.getLocation().getBlock();
                    b.setType(Material.LIGHT);
                    Levelled level = (Levelled) b.getBlockData();
                    level.setLevel(11);
                    b.setBlockData(level, true);
                    frame_config.set("func_bool", 3);
                }
                DataBase.saveConfig(frame_config, DataBase.getFile("entity", uuid));
            }
        }
    }

}
