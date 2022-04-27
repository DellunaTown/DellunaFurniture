package me.lewin.dellunafurniture;

import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandTabCompleter implements TabCompleter {
    List<String> empty = new ArrayList<String>() {{ add(""); }};

    String[] en_commands = { "item", "model" };

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        if (!sender.isOp()) return empty;
        if (args.length > 0) {
            switch (args[0]) {
                case "model":
                    return empty;
                case "dye":
                    if (args.length == 2) {
                        ArrayList<String> list = new ArrayList<>();
                        list.add("<RED>");
                        return tabCompleteSort(list, args[1]);
                    }
                    else if (args.length == 3) {
                        ArrayList<String> list = new ArrayList<>();
                        list.add("<GREEN>");
                        return tabCompleteSort(list, args[2]);
                    }
                    else if (args.length == 4) {
                        ArrayList<String> list = new ArrayList<>();
                        list.add("<BLUE>");
                        return tabCompleteSort(list, args[3]);
                    }
                    else
                        return empty;
                case "item":
                    if (args.length == 2) {
                        ArrayList<String> list = new ArrayList<>();
                        list.add("add");
                        list.add("remove");
                        return tabCompleteSort(list, args[1]);
                    }
                    else if (args.length == 3) {
                        ArrayList<String> list = new ArrayList<>();
                        list.add("block");
                        list.add("wall");
                        list.add("ceiling");
                        list.add("floor");
                        return tabCompleteSort(list, args[2]);
                    }
                    else if (args.length == 4) {
                        ArrayList<String> list = new ArrayList<>();
                        list.add("true");
                        list.add("false");
                        return tabCompleteSort(list, args[3]);
                    }
                    else if (args.length == 5) {
                        ArrayList<String> list = new ArrayList<>();
                        list.add("chair_blocked");
                        list.add("chair");
                        list.add("chest");
                        list.add("trash");
                        list.add("bed");
                        list.add("light");
                        list.add("null");
                        list.add("furnace");
                        return tabCompleteSort(list, args[4]);
                    }
                    else if (args.length == 6) {
                        ArrayList<String> list = DataBase.getModelNames();
                        return tabCompleteSort(list, args[5]);
                    }
                    else if (args.length == 7) {
                        ArrayList<String> list = new ArrayList<>();
                        list.add("<offsetY>");
                        return tabCompleteSort(list, args[6]);
                    }
                    else
                        return empty;

                default:
                    List<String> list_en = new ArrayList<>(Arrays.asList(en_commands));
                    return tabCompleteSort(list_en, args[0]);
            }
        } else {
            return empty;
        }
    }

    private List<String> tabCompleteSort(List<String> list, String args) {
        List<String> sortList = new ArrayList<>();
        for (String s : list) {
            if (args.isEmpty()) return list;

            if (s.toLowerCase().startsWith(args.toLowerCase()))
                sortList.add(s);
        }
        return sortList;
    }
}
