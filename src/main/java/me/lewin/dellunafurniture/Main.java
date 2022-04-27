package me.lewin.dellunafurniture;

import net.coreprotect.CoreProtectAPI;
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
        Bukkit.getPluginCommand("furniture").setTabCompleter(new CommandTabCompleter());
        Bukkit.getPluginManager().registerEvents(new PlayerInteractEvent(), this);
        Bukkit.getPluginManager().registerEvents(new Chest(), this);
        Bukkit.getPluginManager().registerEvents(new Chair(), this);

        CoreProtectAPI api = CoreProtectSet.getCoreProtect();
        if (api != null){ // Ensure we have access to the API
            api.testAPI(); // Will print out "[CoreProtect] API test successful." in the console.
        }
    }
}
