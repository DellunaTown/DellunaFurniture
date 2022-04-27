package me.lewin.dellunafurniture;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DataBase {
    public static YamlConfiguration getItemConfig(String name) {
        return YamlConfiguration.loadConfiguration(getFile("item", name));
    }
    public static YamlConfiguration getModelConfig(String name) {
        return YamlConfiguration.loadConfiguration(getFile("model", name));
    }
    public static YamlConfiguration getLocationConfig(double x, double y, double z) {
        Double nameX = x/1000;
        Double nameY = y/1000;
        Double nameZ = z/1000;

        Integer intNameX = nameX.intValue();
        Integer intNameY = nameY.intValue();
        Integer intNameZ = nameZ.intValue();

        String name = intNameX + ";" + intNameY + ";" + intNameZ;

        return YamlConfiguration.loadConfiguration(getFile("location", name));
    }
    public static Object getLocConfig(Location location, String key) {
        Double nameX = location.getX()/1000;
        Double nameY = location.getY()/1000;
        Double nameZ = location.getZ()/1000;

        Integer intNameX = nameX.intValue();
        Integer intNameY = nameY.intValue();
        Integer intNameZ = nameZ.intValue();

        String filename = intNameX + ";" + intNameY + ";" + intNameZ;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(getFile("location", filename));

        Integer x = ((Double)location.getX()).intValue();
        Integer y = ((Double)location.getY()).intValue();
        Integer z = ((Double)location.getZ()).intValue();

        String name = x + ";" + y + ";" + z;

        return config.get(name + "." + key);
    }
    public static YamlConfiguration getItemModelConfig(String name) {
        YamlConfiguration config = getItemConfig(name);
        YamlConfiguration config2 = getModelConfig(config.getString("model"));
        return config2;
    }
    public static File getLocationFile(double x, double y, double z) {
        Double nameX = x/1000;
        Double nameY = y/1000;
        Double nameZ = z/1000;

        Integer intNameX = nameX.intValue();
        Integer intNameY = nameY.intValue();
        Integer intNameZ = nameZ.intValue();

        String name = intNameX + ";" + intNameY + ";" + intNameZ;
        return getFile("location", name);
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
    public static File getFile(String folderName, String fileName) {
        return new File(JavaPlugin.getPlugin(Main.class).getDataFolder() + "//" + folderName, fileName + ".yml");
    }
    public static void saveConfig(YamlConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    public static YamlConfiguration getChestConfig(String uuid) {
        return YamlConfiguration.loadConfiguration(getFile("chest", uuid));
    }
    public static ArrayList<String> getChestNames() {
        ArrayList<String> list = new ArrayList<>();
        for (File f : new File(JavaPlugin.getPlugin(Main.class).getDataFolder() + "//chest").listFiles()) {
            list.add(f.getName().substring(0, f.getName().length() - 4));
        }
        return list;
    }

}
