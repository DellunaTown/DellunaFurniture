package me.lewin.dellunafurniture;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        if (!sender.isOp()) return false;

        if (args.length == 6 && args[0].equals("item")) {
            switch (args[1]) {
                case "add":
                    String name = ((Player) sender).getItemInHand().getItemMeta().getDisplayName();
                    String type = args[2];
                    Boolean rotation = Boolean.valueOf(args[3]);
                    String func = args[4];
                    String model = args[5];

                    YamlConfiguration config = DataBase.getItemConfig(name);
                    config.set("type", type);
                    config.set("rotation", rotation);
                    config.set("func", func);
                    config.set("model", model);
                    DataBase.saveConfig(config, DataBase.getFile("item", name));
                    return true;

                case "remove":
                    //
                    return true;
            }
        }
        return false;
    }
}
