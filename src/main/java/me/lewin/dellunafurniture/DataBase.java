package me.lewin.dellunafurniture;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
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

    public static YamlConfiguration getItemModelConfig(String name) {
        YamlConfiguration config = getItemConfig(name);
        YamlConfiguration config2 = getModelConfig(config.getString("model"));
        return config2;
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
}
