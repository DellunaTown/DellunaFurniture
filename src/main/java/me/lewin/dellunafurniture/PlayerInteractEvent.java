package me.lewin.dellunafurniture;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;

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

                // 설치 가능여부 감지
                for (int i = 0; i < area_count; i++) {
                    Block block = startLoc.getWorld().getBlockAt(startLoc.clone().add(area.get(i).get(0), area.get(i).get(1), area.get(i).get(2)));
                    if (!block.getType().equals(Material.AIR)) {
                        player.sendMessage("§7[§c ! §7] §c공간이 부족합니다.");
                        return;
                    }
                }

                // 방벽 설치
                for (int i = 0; i < area_count; i++) {
                    Block block = startLoc.getWorld().getBlockAt(startLoc.clone().add(area.get(i).get(0), area.get(i).get(1), area.get(i).get(2)));
                    block.setMetadata("furniture", new FixedMetadataValue(JavaPlugin.getPlugin(Main.class), area.get(i)));
                    block.setMetadata("model", new FixedMetadataValue(JavaPlugin.getPlugin(Main.class), item_config.getString("model")));
                    block.setMetadata("direction", new FixedMetadataValue(JavaPlugin.getPlugin(Main.class), direction4));
                    block.setType(Material.BARRIER);
                }

                // 갑옷 거치대 설치
                Location armorLoc = startLoc.clone();
                if (item_config.getBoolean("rotation")) armorLoc.setYaw(yaw8);
                else armorLoc.setYaw(yaw4);
                armorLoc.setPitch(0);
                ArmorStand armorStand = startLoc.getWorld().spawn(armorLoc, ArmorStand.class);
                armorStand.setInvisible(true);
                armorStand.setGravity(false);
                armorStand.setSilent(true);
                armorStand.setInvulnerable(true);
                armorStand.setHelmet(event.getPlayer().getItemInHand());
                armorStand.setMetadata("furniture", new FixedMetadataValue(JavaPlugin.getPlugin(Main.class), true));

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
                    itemFrame.setMetadata("furniture", new FixedMetadataValue(JavaPlugin.getPlugin(Main.class), area));
                    itemFrame.setMetadata("model", new FixedMetadataValue(JavaPlugin.getPlugin(Main.class), item_config.getString("model")));
                    itemFrame.setMetadata("direction", new FixedMetadataValue(JavaPlugin.getPlugin(Main.class), direction4));
                }

                return;
            }

        }

        // 가구 아이템을 클릭한 경우
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().hasMetadata("furniture")) {

            // 손에 가구 아이템을 들고 있는 경우
            if (event.hasItem() && isFurniture(event.getItem())) {
                return;
            }

            // 쉬프트를 누르고 있는 경우
            if (event.getPlayer().isSneaking()) {
                return;
            }

            Block block = event.getClickedBlock();
            ArrayList<Integer> area = (ArrayList<Integer>) block.getMetadata("furniture").get(0).value();
            Location startLoc = block.getLocation().add(0.5-area.get(0), 0-area.get(1), 0.5-area.get(2));
            Location clickLoc = event.getClickedBlock().getLocation().add(0.5, 0, 0.5);
            Player player = event.getPlayer();

            Collection<Entity> nearbyEntities = block.getWorld().getNearbyEntities(startLoc, 0.49, 0.49, 0.49);
            for (Entity e : nearbyEntities) {
                if (e instanceof ArmorStand && e.hasMetadata("furniture")) {
                    ItemStack item = ((ArmorStand) e).getHelmet();
                    YamlConfiguration item_config = DataBase.getItemConfig(item.getItemMeta().getDisplayName());
                    switch (item_config.getString("func")) {
                        case "chair":
                            ArmorStand seatArmor = player.getWorld().spawn(clickLoc, ArmorStand.class, b -> {
                                try { b.setInvisible(true); } catch(Exception err) { }
                                try { b.setSmall(true); } catch(Exception err) { }
                                try { b.setGravity(false); } catch(Exception err) { }
                                try { b.setMarker(true); } catch(Exception err) { }
                                try { b.setBasePlate(false); } catch(Exception err) { }
                                try { b.setInvulnerable(true); } catch(Exception err) { }
                            });
                            seatArmor.addPassenger(player);
                            return;
                    }
                }
            }
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getClickedBlock().hasMetadata("furniture")) {
            event.setCancelled(true);
            Block block = event.getClickedBlock();

            ArrayList<Integer> area = (ArrayList<Integer>) block.getMetadata("furniture").get(0).value();
            String model = (String) block.getMetadata("model").get(0).value();
            String direction = (String) block.getMetadata("direction").get(0).value();

            Location startLoc = block.getLocation().add(0.5-area.get(0), 0-area.get(1), 0.5-area.get(2));

            Collection<Entity> nearbyEntities = block.getWorld().getNearbyEntities(startLoc, 0.49, 0.49, 0.49);
            for (Entity e : nearbyEntities) {
                if (e instanceof ArmorStand && e.hasMetadata("furniture")) {
                    event.getPlayer().getWorld().dropItem(startLoc, ((ArmorStand) e).getHelmet());
                    e.remove();
                }
            }

            YamlConfiguration model_config = DataBase.getModelConfig(model);
            ArrayList<ArrayList<Integer>> Area = (ArrayList<ArrayList<Integer>>) model_config.get("area_" + direction);
            for (int i = 0; i < model_config.getInt("area_count"); i++) {
                Block b = startLoc.getWorld().getBlockAt(startLoc.clone().add(Area.get(i).get(0), Area.get(i).get(1), Area.get(i).get(2)));
                b.breakNaturally();
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
}
