package me.lewin.dellunafurniture.furniture;

import me.lewin.dellunafurniture.CoreProtectSet;
import me.lewin.dellunafurniture.DataBase;
import me.lewin.dellunafurniture.Reference;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class FurniturePlace implements Listener {
    @EventHandler
    private void onInteract(org.bukkit.event.player.PlayerInteractEvent event) {

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.hasItem() && Reference.isFurniture(event.getItem())) {
            ItemStack item = event.getItem();
            Player player = event.getPlayer();

            Location player_loc = player.getLocation();
            Location start_loc = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation().add(0.5, 0.5, 0.5);

            String direction4 = Reference.getDirBy4(player_loc);
            String direction8 = Reference.getDirBy8(player_loc);

            YamlConfiguration item_config = DataBase.getItemConfig(item.getItemMeta().getDisplayName());

            String item_type = item_config.getString("type");
            Boolean item_rotation = item_config.getBoolean("rotation");
            String item_func = item_config.getString("func");
            String item_model = item_config.getString("model");
            Double item_offsetY = item_config.getDouble("OffsetY");

            YamlConfiguration model_config = DataBase.getModelConfig(item_model);

            Integer model_area_count = model_config.getInt("area_count");
            ArrayList<ArrayList<Integer>> model_area = (ArrayList<ArrayList<Integer>>) model_config.get("area_" + direction4);


            if (item_type.equals("block")) {

                for (int i = 0; i < model_area_count; i++) {
                    ArrayList<Integer> offset = model_area.get(i);
                    Location l = start_loc.clone().add(offset.get(0), offset.get(1), offset.get(2));
                    Block b = l.getBlock();
                    if (!b.getType().equals(Material.AIR)) {
                        if (!b.getType().equals(Material.LIGHT)) {
                            player.sendMessage(Reference.FAIL + "공간이 부족합니다.");
                            return;
                        }
                    }
                }

                Location armorStand_loc = start_loc.clone().add(0, -0.5, 0);
                if (item_rotation) armorStand_loc.setYaw(Reference.getYawBy8(direction8));
                else armorStand_loc.setYaw(Reference.getYawBy4(direction4));
                armorStand_loc.setPitch(0);
                ArmorStand armorStand = armorStand_loc.getWorld().spawn(armorStand_loc, ArmorStand.class, a -> {
                    try { a.setInvisible(true); } catch(Exception err) { }
                    try { a.setGravity(false); } catch(Exception err) { }
                    try { a.setSilent(true); } catch(Exception err) { }
                    try { a.setBasePlate(false); } catch(Exception err) { }
                    try { a.setInvulnerable(true); } catch(Exception err) { }
                    try { a.setMarker(true); } catch(Exception err) { }
                    try { a.setHelmet(event.getPlayer().getItemInHand()); } catch(Exception err) { }
                    try { a.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING); } catch(Exception err) { }
                    try { a.addEquipmentLock(EquipmentSlot.CHEST, ArmorStand.LockType.ADDING); } catch(Exception err) { }
                    try { a.addEquipmentLock(EquipmentSlot.FEET, ArmorStand.LockType.ADDING); } catch(Exception err) { }
                    try { a.addEquipmentLock(EquipmentSlot.LEGS, ArmorStand.LockType.ADDING); } catch(Exception err) { }
                    try { a.addEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.ADDING); } catch(Exception err) { }
                    try { a.addEquipmentLock(EquipmentSlot.OFF_HAND, ArmorStand.LockType.ADDING); } catch(Exception err) { }
                });

                String armorStand_UUID = armorStand.getUniqueId().toString();

                for (int i = 0; i < model_area_count; i++) {
                    ArrayList<Integer> offset = model_area.get(i);
                    Location l = start_loc.clone().add(offset.get(0), offset.get(1), offset.get(2));
                    Block b = l.getBlock();
                    CoreProtectAPI api = CoreProtectSet.getCoreProtect();

                    b.setType(Material.BARRIER);
                    api.logPlacement(player.getDisplayName(), b.getLocation(), b.getType(), b.getBlockData());

                    YamlConfiguration loc_config = DataBase.getLocationConfig(l);
                    loc_config.set(Reference.getLocFileName(l) + ".type", item_type);
                    loc_config.set(Reference.getLocFileName(l) + ".rotation", item_rotation);
                    loc_config.set(Reference.getLocFileName(l) + ".func", item_func);
                    loc_config.set(Reference.getLocFileName(l) + ".model", item_model);
                    loc_config.set(Reference.getLocFileName(l) + ".uuid", armorStand_UUID);
                    loc_config.set(Reference.getLocFileName(l) + ".startLoc", start_loc);
                    loc_config.set(Reference.getLocFileName(l) + ".direction4", direction4);
                    loc_config.set(Reference.getLocFileName(l) + ".direction8", direction8);
                    loc_config.set(Reference.getLocFileName(l) + ".offsetY", item_offsetY);
                    DataBase.saveConfig(loc_config, DataBase.getLocationFile(l));
                }

                player.setItemInHand(null);

                if (item_func.equals("chest")) {
                    YamlConfiguration chest_config = DataBase.getChestConfig(armorStand_UUID);
                    DataBase.saveConfig(chest_config, DataBase.getFile("chest", armorStand_UUID));
                }
            }

            else if (item_type.equals("wall_frame")) {
                if (!start_loc.getBlock().getType().equals(Material.AIR)) {
                    if (!start_loc.getBlock().getType().equals(Material.LIGHT)) {
                        player.sendMessage(Reference.FAIL + "공간이 부족합니다.");
                        return;
                    }
                }

                ItemFrame item_frame = start_loc.getWorld().spawn(start_loc.add(-0.5, -0.5, -0.5), ItemFrame.class, i -> {
                    try { i.setVisible(false); } catch(Exception err) { }
                    try { i.setGravity(false); } catch(Exception err) { }
                    try { i.setSilent(true); } catch(Exception err) { }
                    try { i.setCustomNameVisible(false); } catch(Exception err) { }
                    if (event.getBlockFace().equals(BlockFace.DOWN) || event.getBlockFace().equals(BlockFace.UP)) {
                        try { i.setRotation(Reference.getRotationBy8(direction8)); } catch(Exception err) { }
                        try { i.setFacingDirection(event.getBlockFace()); } catch(Exception err) { }
                    }
                    try { i.setItem(event.getPlayer().getItemInHand(),false); } catch(Exception err) { }
                });

                CoreProtectAPI api = CoreProtectSet.getCoreProtect();
                api.logPlacement(player.getDisplayName(), item_frame.getLocation(), Material.ITEM_FRAME, null);

                String itemFrame_UUID = item_frame.getUniqueId().toString();

                YamlConfiguration frame_config = DataBase.getFrameConfig(itemFrame_UUID);
                frame_config.set("type", item_type);
                frame_config.set("func", item_func);
                frame_config.set("func_bool", false);
                DataBase.saveConfig(frame_config, DataBase.getFile("entity", itemFrame_UUID));

                player.setItemInHand(null);
            }
        }
    }
}
