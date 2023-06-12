package me.lewin.dellunafurniture;

import me.lewin.dellunafurniture.command.Command;
import me.lewin.dellunafurniture.command.CommandTabCompleter;
import me.lewin.dellunafurniture.func.Chair;
import me.lewin.dellunafurniture.func.Chest;
import me.lewin.dellunafurniture.furniture.FurnitureInteract;
import me.lewin.dellunafurniture.furniture.FurniturePlace;
import me.lewin.dellunafurniture.furniture.FurnitureRemove;
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

        Bukkit.getPluginManager().registerEvents(new Chest(), this);
        Bukkit.getPluginManager().registerEvents(new Chair(), this);
        Bukkit.getPluginManager().registerEvents(new FurniturePlace(), this);
        Bukkit.getPluginManager().registerEvents(new FurnitureRemove(), this);
        Bukkit.getPluginManager().registerEvents(new FurnitureInteract(), this);

        CoreProtectAPI api = CoreProtectSet.getCoreProtect();
        if (api != null){ // Ensure we have access to the API
            api.testAPI(); // Will print out "[CoreProtect] API test successful." in the console.
        }
    }
}
