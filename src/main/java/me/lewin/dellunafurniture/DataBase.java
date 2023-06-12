package me.lewin.dellunafurniture;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

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
    public static YamlConfiguration getLocationConfig(Location location) {
        Double nameX = location.getX()/1000;
        Double nameY = location.getY()/1000;
        Double nameZ = location.getZ()/1000;

        Integer intNameX = nameX.intValue();
        Integer intNameY = nameY.intValue();
        Integer intNameZ = nameZ.intValue();

        String name = intNameX + ";" + intNameY + ";" + intNameZ;

        return YamlConfiguration.loadConfiguration(getFile("location", name));
    }
    public static Object getLocationConfig(Location location, String key) {
        Double nameX = location.getX()/1000;
        Double nameY = location.getY()/1000;
        Double nameZ = location.getZ()/1000;

        Integer intNameX = nameX.intValue();
        Integer intNameY = nameY.intValue();
        Integer intNameZ = nameZ.intValue();

        String filename = intNameX + ";" + intNameY + ";" + intNameZ;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(getFile("location", filename));

        Double x = location.getX()*10;
        Double y = location.getY()*10;
        Double z = location.getZ()*10;

        String name = x.intValue() + ";" + y.intValue() + ";" + z.intValue();

        return config.get(name + "." + key);
    }

    public static YamlConfiguration getChestConfig(String uuid) {
        return YamlConfiguration.loadConfiguration(getFile("chest", uuid));
    }

    public static YamlConfiguration getFrameConfig(String uuid) {
        return YamlConfiguration.loadConfiguration(getFile("entity", uuid));
    }

    public static File getLocationFile(Location location) {
        Double nameX = location.getX()/1000;
        Double nameY = location.getY()/1000;
        Double nameZ = location.getZ()/1000;

        Integer intNameX = nameX.intValue();
        Integer intNameY = nameY.intValue();
        Integer intNameZ = nameZ.intValue();

        String name = intNameX + ";" + intNameY + ";" + intNameZ;
        return getFile("location", name);
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
    public static void removeFile(String folderName, String fileName) {
        getFile(folderName, fileName).delete();
    }
}
