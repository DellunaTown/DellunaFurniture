package me.lewin.dellunafurniture.command;

import me.lewin.dellunafurniture.DataBase;
import me.lewin.dellunafurniture.Reference;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        if (!sender.isOp()) return false;

        if (args.length == 7 && args[0].equals("item")) {
            if (args[1].equals("add")) {
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
            }
        }

        if (args.length == 3 && args[0].equals("item")) {
            if (args[1].equals("remove")) {
                DataBase.removeFile("item", args[2]);
                sender.sendMessage("§7[§a ! §7] §a가구 삭제 성공");
                return true;
            }
        }

        if (args.length == 4 && args[0].equals("dye")) {
            ItemStack item = ((Player) sender).getItemInHand();

            if (!item.getType().equals(Material.LEATHER_HORSE_ARMOR)) {
                sender.sendMessage(Reference.FAIL + "가죽 말 갑옷만 염색이 가능합니다.");
                return true;
            }
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            try {
                meta.setColor(Color.fromRGB(Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3])));
            } catch (Error e) {
                sender.sendMessage(Reference.FAIL + "올바르지 않은 색상 값 입력입니다.");
                return true;
            }
            meta.addItemFlags(ItemFlag.HIDE_DYE);
            item.setItemMeta(meta);
            return true;
        }

        if (args.length == 1 && args[0].equals("version")){
            sender.sendMessage("1.2.1");
            return true;
        }

        sender.sendMessage(Reference.FAIL + "잘못된 입력입니다.");
        return false;
    }
}
