package me.lewin.dellunafurniture;

import org.bukkit.Color;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        if (!sender.isOp()) return false;

        if (args.length == 7 && args[0].equals("item")) {
            switch (args[1]) {
                case "add":
                    String name = ((Player) sender).getItemInHand().getItemMeta().getDisplayName();
                    String type = args[2];
                    Boolean rotation = Boolean.valueOf(args[3]);
                    String func = args[4];
                    String model = args[5];
                    Double OffsetY = Double.parseDouble(args[6]);

                    YamlConfiguration config = DataBase.getItemConfig(name);
                    config.set("type", type);
                    config.set("rotation", rotation);
                    config.set("func", func);
                    config.set("model", model);
                    config.set("OffsetY", OffsetY);
                    DataBase.saveConfig(config, DataBase.getFile("item", name));

                    sender.sendMessage("§7[§a ! §7] §a가구 등록 성공");
                    return true;

                case "remove":
                    //
                    return true;
            }
        }

        if (args.length == 4 && args[0].equals("dye")) {
            ItemStack item = ((Player) sender).getItemInHand();
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(Color.fromRGB(Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3])));
            item.setItemMeta(meta);
        }
        return false;
    }
}
