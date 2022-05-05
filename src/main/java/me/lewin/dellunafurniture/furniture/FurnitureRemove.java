package me.lewin.dellunafurniture.furniture;

import me.lewin.dellunafurniture.CoreProtectSet;
import me.lewin.dellunafurniture.DataBase;
import me.lewin.dellunafurniture.Reference;
import me.lewin.dellunafurniture.func.Chair;
import me.lewin.dellunafurniture.func.Chest;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class FurnitureRemove implements Listener {
    @EventHandler
    private void onInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) && Reference.isFurnitureLocation(event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5))) {
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
            Double offset2 = (Double) DataBase.getLocationConfig(click_loc, "offsetY");

            YamlConfiguration model_config = DataBase.getModelConfig(model);
            Integer model_area_count = model_config.getInt("area_count");
            ArrayList<ArrayList<Integer>> model_area = (ArrayList<ArrayList<Integer>>) model_config.get("area_" + direction4);

            ArmorStand entity = (ArmorStand) Bukkit.getEntity(UUID.fromString(uuid));
            event.getPlayer().getWorld().dropItem(click_loc, entity.getHelmet());
            entity.remove();

            for (int i = 0; i < model_area_count; i++) {
                ArrayList<Integer> offset = model_area.get(i);
                Location l = start_loc.clone().add(offset.get(0), offset.get(1), offset.get(2));
                Block b = l.getBlock();
                CoreProtectAPI api = CoreProtectSet.getCoreProtect();
                api.logRemoval(event.getPlayer().getDisplayName(), b.getLocation(), b.getType(), b.getBlockData());
                b.breakNaturally();

                YamlConfiguration loc_config = DataBase.getLocationConfig(l);
                loc_config.set(Reference.getLocFileName(l) + ".type", null);
                loc_config.set(Reference.getLocFileName(l) + ".rotation", null);
                loc_config.set(Reference.getLocFileName(l) + ".func", null);
                loc_config.set(Reference.getLocFileName(l) + ".model", null);
                loc_config.set(Reference.getLocFileName(l) + ".uuid", null);
                loc_config.set(Reference.getLocFileName(l) + ".startLoc", null);
                loc_config.set(Reference.getLocFileName(l) + ".direction4", null);
                loc_config.set(Reference.getLocFileName(l) + ".direction8", null);
                loc_config.set(Reference.getLocFileName(l) + ".offsetY", null);
                loc_config.set(Reference.getLocFileName(l), null);
                DataBase.saveConfig(loc_config, DataBase.getLocationFile(l));
            }

            if (func.equals("chest")) {
                Chest.onRemove(uuid, click_loc);
            }
            else if (func.equals("chair_blocked") || func.equals("chair")) {
                for (int i = 0; i < model_area_count; i++) {
                    ArrayList<Integer> offset = model_area.get(i);
                    Location l = start_loc.clone().add(offset.get(0), offset.get(1), offset.get(2));
                    Chair.onRemove(l, offset2);
                }
            }

            return;
        }
    }

    @EventHandler
    private void onInteract(EntityDamageByEntityEvent event) {
        if (Reference.isFurnitureEntity(event.getEntity().getUniqueId().toString())) {
            event.setCancelled(true);

            YamlConfiguration config = DataBase.getFrameConfig(event.getEntity().getUniqueId().toString());
            String type = config.getString("type");
            Boolean light = config.getBoolean("func_bool");
            if (type.equals("wall_frame")) {
                ItemFrame e = (ItemFrame) event.getEntity();
                ItemStack i = e.getItem();

                Location location = e.getLocation();
                location.getWorld().dropItem(location, i);

                String uuid = e.getUniqueId().toString();

                DataBase.removeFile("entity", uuid);

                CoreProtectAPI api = CoreProtectSet.getCoreProtect();
                api.logRemoval(((Player)event.getDamager()).getDisplayName(), e.getLocation(), Material.ITEM_FRAME, null);

                e.setItem(null);
                e.remove();

                if (light) {
                    e.getLocation().getBlock().setType(Material.AIR);
                }
            }
        }
    }

    @EventHandler
    private void onBreak(HangingBreakEvent event) {
        if (Reference.isFurnitureEntity(event.getEntity().getUniqueId().toString())) {
            event.setCancelled(true);
            return;
        }
    }
}
