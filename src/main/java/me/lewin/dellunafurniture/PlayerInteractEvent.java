package me.lewin.dellunafurniture;

import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerInteractEvent implements Listener {
    @EventHandler
    private void onInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        // 가구 아이템을 들고 우클릭 한 경우
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.hasItem() && isFurniture(event.getItem())) {
            ItemStack item = event.getItem();
            Player player = event.getPlayer();

            Location startLoc = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation().add(0.5, 0, 0.5);

            String direction4 = getDirBy4(event.getPlayer().getLocation());
            String direction8 = getDirBy8(event.getPlayer().getLocation());
            float yaw4 = getYawBy4(direction4);
            float yaw8 = getYawBy8(direction8);

            YamlConfiguration item_config = DataBase.getItemConfig(item.getItemMeta().getDisplayName());
            YamlConfiguration model_config = DataBase.getItemModelConfig(item.getItemMeta().getDisplayName());

            // 타입이 블록인 경우
            if (item_config.getString("type").equals("block")) {
                Integer area_count = model_config.getInt("area_count");
                ArrayList<ArrayList<Integer>> area = (ArrayList<ArrayList<Integer>>) model_config.get("area_" + direction4);
                CoreProtectAPI api = CoreProtectSet.getCoreProtect();

                // 설치 가능여부 감지
                for (int i = 0; i < area_count; i++) {
                    Block block = startLoc.getWorld().getBlockAt(startLoc.clone().add(area.get(i).get(0), area.get(i).get(1), area.get(i).get(2)));
                    if (!block.getType().equals(Material.AIR)) {
                        player.sendMessage("§7[§c ! §7] §c공간이 부족합니다.");
                        return;
                    }
                }

                // 갑옷 거치대 설치
                Location armorLoc = startLoc.clone();
                if (item_config.getBoolean("rotation")) armorLoc.setYaw(yaw8);
                else armorLoc.setYaw(yaw4);
                armorLoc.setPitch(0);

                ArmorStand armorStand = startLoc.getWorld().spawn(armorLoc, ArmorStand.class, a -> {
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

                String entityId = armorStand.getUniqueId().toString();

                // 방벽 설치
                for (int i = 0; i < area_count; i++) {
                    Block block = startLoc.getWorld().getBlockAt(startLoc.clone().add(area.get(i).get(0), area.get(i).get(1), area.get(i).get(2)));
                    block.setType(Material.BARRIER);

                    api.logPlacement(player.getDisplayName(), block.getLocation(), block.getType(), block.getBlockData());

                    Integer x = ((Double)block.getLocation().getX()).intValue();
                    Integer y = ((Double)block.getLocation().getY()).intValue();
                    Integer z = ((Double)block.getLocation().getZ()).intValue();

                    String name = x + ";" + y + ";" + z;

                    YamlConfiguration block_config = DataBase.getLocationConfig(x, y, z);
                    block_config.set(name + ".model", item_config.getString("model"));
                    block_config.set(name + ".direction4", direction4);
                    block_config.set(name + ".direction8", direction8);
                    block_config.set(name + ".startLoc", startLoc);
                    block_config.set(name + ".id", entityId);
                    DataBase.saveConfig(block_config, DataBase.getLocationFile(x, y, z));
                }

                // 아이템 제거
                event.getPlayer().setItemInHand(null);

                return;
            }

            // 타입이 벽인 경우
            if (item_config.getString("type").equals("wall")) {

                // 설치 가능여부 감지1
                if (event.getBlockFace().equals(BlockFace.DOWN) || event.getBlockFace().equals(BlockFace.UP)) {
                    player.sendMessage("§7[§c ! §7] §c천장이나 바닥에는 설치하실 수 없습니다.");
                    return;
                }

                Integer area_count = model_config.getInt("area_count");
                ArrayList<ArrayList<Integer>> area = (ArrayList<ArrayList<Integer>>) model_config.get("area_" + direction4);

                // 설치 가능여부 감지2
                for (int i = 0; i < area_count; i++) {
                    Block block = startLoc.getWorld().getBlockAt(startLoc.clone().add(area.get(i).get(0), area.get(i).get(1), area.get(i).get(2)));
                    if (!block.getType().equals(Material.AIR)) {
                        player.sendMessage("§7[§c ! §7] §c공간이 부족합니다.");
                        return;
                    }
                }

                // 아이템 액자와 아이템 설치 및 아이템 제거
                for (int i = 0; i < area_count; i++) {
                    ItemFrame itemFrame = startLoc.getWorld().spawn(startLoc.clone().add(area.get(i).get(0), area.get(i).get(1), area.get(i).get(2)), ItemFrame.class);
                    itemFrame.setVisible(false);
                    itemFrame.setGravity(false);
                    itemFrame.setSilent(true);
                    itemFrame.setInvulnerable(true);
                    if (i == 0) {
                        itemFrame.setItem(item);
                        event.getPlayer().setItemInHand(null);
                    }
                }

                return;
            }

        }

        // 가구 아이템을 우클릭한 경우
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && isFurnitureLocation(event.getClickedBlock().getLocation())) {
            // 쉬프트를 누르고 있는 경우
            if (event.getPlayer().isSneaking()) {
                return;
            }

            Block block = event.getClickedBlock();

            ItemStack furniture = ((ArmorStand) Bukkit.getEntity(UUID.fromString((String) DataBase.getLocConfig(block.getLocation(), "id")))).getHelmet();
            String direction = (String) DataBase.getLocConfig(block.getLocation(), "direction8");
            String uuid = (String) DataBase.getLocConfig(block.getLocation(), "id");
            YamlConfiguration item_config = DataBase.getItemConfig(furniture.getItemMeta().getDisplayName());
            Player player = event.getPlayer();

            // 손에 가구 아이템을 들고 있는 경우
            if (item_config.getString("func").equals("null")) {
                return;
            }

            event.setCancelled(true);
            switch (item_config.getString("func")) {
                case "chair_blocked":
                    Location clickLoc_chair = event.getClickedBlock().getLocation().add(0.5, 0 + item_config.getDouble("OffsetY"), 0.5);
                    clickLoc_chair.setPitch(0);
                    if (item_config.getBoolean("rotation")) {
                        clickLoc_chair.setYaw(getYawBy8(getOppositeDirBy8(direction)));
                    }
                    else {
                        clickLoc_chair.setYaw(getYawBy4(getOppositeDirBy4(direction)));
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
                    Chair.chairlist.put(player, seatArmor.getUniqueId().toString());

                    return;
                case "chair":

                    break;
                case "chest":
                    if (!Chest.clickMap.containsKey(player)) {
                        Chest.getInventory(player, uuid);
                        Chest.clickMap.put(player, true);
                    }
                    break;
                case "trash":
                    player.openInventory(Bukkit.createInventory(null, 27, "쓰레기통"));
                    break;
            }
        }

        // 가구 아이템을 좌클릭한 경우
        if (event.getAction() == Action.LEFT_CLICK_BLOCK && isFurnitureLocation(event.getClickedBlock().getLocation())) {
            event.setCancelled(true);
            Location clickLoc = event.getClickedBlock().getLocation();
            Location startLoc = (Location) DataBase.getLocConfig(clickLoc, "startLoc");
            String model = (String) DataBase.getLocConfig(clickLoc, "model");
            String direction = (String) DataBase.getLocConfig(clickLoc, "direction4");
            String id = (String) DataBase.getLocConfig(clickLoc, "id");
            ArmorStand entity = (ArmorStand) Bukkit.getEntity(UUID.fromString(id));

            if (DataBase.getChestNames().contains(id) && DataBase.getChestConfig(id).get("contents") != null) {
                for (ItemStack i : (ArrayList<ItemStack>) DataBase.getChestConfig(id).get("contents")) {
                    if (i != null)
                        clickLoc.getWorld().dropItem(clickLoc, i);
                }
            }

            event.getPlayer().getWorld().dropItem(startLoc, entity.getHelmet());
            entity.remove();

            YamlConfiguration model_config = DataBase.getModelConfig(model);
            ArrayList<ArrayList<Integer>> Area = (ArrayList<ArrayList<Integer>>) model_config.get("area_" + direction);
            CoreProtectAPI api = CoreProtectSet.getCoreProtect();

            for (int i = 0; i < model_config.getInt("area_count"); i++) {
                Location l = startLoc.clone().add(Area.get(i).get(0), Area.get(i).get(1), Area.get(i).get(2));
                Block b = startLoc.getWorld().getBlockAt(l);
                api.logRemoval(event.getPlayer().getDisplayName(), b.getLocation(), b.getType(), b.getBlockData());
                b.breakNaturally();


                String name = ((Double)l.getX()).intValue() + ";" + ((Double)l.getY()).intValue() + ";" + ((Double)(l.getZ()-1)).intValue();

                YamlConfiguration LocationConfig = DataBase.getLocationConfig(l.getX(), l.getY(), l.getZ());
                LocationConfig.set(name + ".model", null);
                LocationConfig.set(name + ".direction", null);
                LocationConfig.set(name + ".startLoc", null);
                LocationConfig.set(name + ".id", null);
                LocationConfig.set(name, null);
                DataBase.saveConfig(LocationConfig, DataBase.getLocationFile(l.getX(), l.getY(), l.getZ()));
            }
        }
    }

    private Boolean isFurniture(ItemStack item) {
        if (item.getType().equals(Material.LEATHER_HORSE_ARMOR)) {
            if (item.hasItemMeta() && item.getItemMeta().hasCustomModelData()) {
                if (DataBase.getFile("item", item.getItemMeta().getDisplayName()).exists()) {
                    return true;
                }
            }
        }
        return false;
    }
    private Boolean isFurnitureLocation(Location location) {
        Integer x = ((Double)location.getX()).intValue();
        Integer y = ((Double)location.getY()).intValue();
        Integer z = ((Double)location.getZ()).intValue();

        String name = x + ";" + y + ";" + z;

        YamlConfiguration block_config = DataBase.getLocationConfig(x, y, z);
        return block_config.contains(name);
    }

    private String getDirBy8(Location location) {
        double yaw = location.getYaw();
        if (yaw >= 157.5 && yaw < 180) return "north";
        if (yaw >= -180 && yaw < -157.5) return "north";
        if (yaw >= -157.5 && yaw < -112.5) return "northeast";
        if (yaw >= -112.5 && yaw < -67.5) return "east";
        if (yaw >= -67.5 && yaw < -22.5) return "southeast";
        if (yaw >= -22.5 && yaw < 22.5) return "south";
        if (yaw >= 22.5 && yaw < 67.5) return "southwest";
        if (yaw >= 67.5 && yaw < 112.5) return "west";
        if (yaw >= 112.5 && yaw < 157.5) return "northwest";
        return "error";
    }
    private String getDirBy4(Location location) {
        double yaw = location.getYaw();
        if (yaw >= 135 && yaw < 180) return "north";
        if (yaw >= -180 && yaw < -135) return "north";
        if (yaw >= -135 && yaw < -45) return "east";
        if (yaw >= -45 && yaw < 45) return "south";
        if (yaw >= 45 && yaw < 135) return "west";
        return "error";
    }

    private float getYawBy4(String direction) {
        if (direction.equals("north")) return -180;
        if (direction.equals("east")) return -90;
        if (direction.equals("south")) return 0;
        if (direction.equals("west")) return 90;
        return 0;
    }
    private float getYawBy8(String direction) {
        if (direction.equals("north")) return -180;
        if (direction.equals("northeast")) return -135;
        if (direction.equals("east")) return -90;
        if (direction.equals("southeast")) return -45;
        if (direction.equals("south")) return 0;
        if (direction.equals("southwest")) return 45;
        if (direction.equals("west")) return 90;
        if (direction.equals("northwest")) return 135;
        return 0;
    }

    private String getOppositeDirBy4(String direction) {
        if (direction.equals("south")) return "north";
        if (direction.equals("west")) return "east";
        if (direction.equals("north")) return "south";
        if (direction.equals("east")) return "west";
        return "error";
    }
    private String getOppositeDirBy8(String direction) {
        if (direction.equals("north")) return "south";
        if (direction.equals("northeast")) return "southwest";
        if (direction.equals("east")) return "west";
        if (direction.equals("southeast")) return "northwest";
        if (direction.equals("south")) return "north";
        if (direction.equals("southwest")) return "northeast";
        if (direction.equals("west")) return "east";
        if (direction.equals("northwest")) return "southeast";
        return "error";
    }
}
