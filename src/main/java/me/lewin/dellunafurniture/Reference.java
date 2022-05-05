package me.lewin.dellunafurniture;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

public class Reference {
    public static String FAIL = "§7[§c ! §7] §c";

    public static Boolean isFurniture(ItemStack item) {
        if (item.getType().equals(Material.LEATHER_HORSE_ARMOR)) {
            if (item.hasItemMeta()) {
                if (item.getItemMeta().hasCustomModelData()) {
                    if (DataBase.getFile("item", item.getItemMeta().getDisplayName()).exists()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static Boolean isFurnitureLocation(Location location) {
        String name = getLocFileName(location);

        YamlConfiguration block_config = DataBase.getLocationConfig(location.getX(), location.getY(), location.getZ());
        return block_config.contains(name);
    }
    public static Boolean isFurnitureEntity(String uuid) {

        return DataBase.getFile("entity", uuid).exists();
    }

    public static String getDirBy8(Location location) {
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
    public static String getDirBy4(Location location) {
        double yaw = location.getYaw();
        if (yaw >= 135 && yaw < 180) return "north";
        if (yaw >= -180 && yaw < -135) return "north";
        if (yaw >= -135 && yaw < -45) return "east";
        if (yaw >= -45 && yaw < 45) return "south";
        if (yaw >= 45 && yaw < 135) return "west";
        return "error";
    }

    public static float getYawBy4(String direction) {
        if (direction.equals("north")) return -180;
        if (direction.equals("east")) return -90;
        if (direction.equals("south")) return 0;
        if (direction.equals("west")) return 90;
        return 0;
    }
    public static float getYawBy8(String direction) {
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

    public static Rotation getRotationBy8(String direction) {
        if (direction.equals("north")) return Rotation.NONE;
        if (direction.equals("northeast")) return Rotation.CLOCKWISE_45;
        if (direction.equals("east")) return Rotation.CLOCKWISE;
        if (direction.equals("southeast")) return Rotation.CLOCKWISE_135;
        if (direction.equals("south")) return Rotation.FLIPPED;
        if (direction.equals("southwest")) return Rotation.FLIPPED_45;
        if (direction.equals("west")) return Rotation.COUNTER_CLOCKWISE;
        if (direction.equals("northwest")) return Rotation.COUNTER_CLOCKWISE_45;
        return Rotation.NONE;
    }

    public static String getLocFileName(Location location) {
        Double x = location.getX()*10;
        Double y = location.getY()*10;
        Double z = location.getZ()*10;

        String name = x.intValue() + ";" + y.intValue() + ";" + z.intValue();
        return name;
    }
    public static ArrayList<String> getModelNames() {
        File[] list = new File(JavaPlugin.getPlugin(Main.class).getDataFolder() + "//model").listFiles();
        ArrayList<String> nameList = new ArrayList<>();
        for (File f : list) {
            String name = f.getName().substring(0, f.getName().length() -4);
            nameList.add(name);
        }
        return nameList;
    }
    public static ArrayList<String> getItemNames() {
        File[] list = new File(JavaPlugin.getPlugin(Main.class).getDataFolder() + "//item").listFiles();
        ArrayList<String> nameList = new ArrayList<>();
        for (File f : list) {
            String name = f.getName().substring(0, f.getName().length() -4);
            nameList.add(name);
        }
        return nameList;
    }

    public static String getOppositeDirBy4(String direction) {
        if (direction.equals("south")) return "north";
        if (direction.equals("west")) return "east";
        if (direction.equals("north")) return "south";
        if (direction.equals("east")) return "west";
        return "error";
    }
    public static String getOppositeDirBy8(String direction) {
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
