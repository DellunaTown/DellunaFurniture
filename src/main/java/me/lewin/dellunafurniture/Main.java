package me.lewin.dellunafurniture;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        Plugin plugin = JavaPlugin.getPlugin(Main.class);

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        this.getCommand("furniture").setExecutor(new Command());
        Bukkit.getPluginManager().registerEvents(new PlayerInteractEvent(), this);
    }
}
