package net.ImTouchk;

import org.bukkit.ChatColor;

public class CommonText {
    public static String info() { return String.format("%s%sINFO%s", ChatColor.GOLD, ChatColor.BOLD, ChatColor.RESET); }
    public static String error() { return String.format("%s%sERROR%s", ChatColor.RED, ChatColor.BOLD, ChatColor.RESET); }
    public static String usage() { return String.format("%s%sUSAGE%s", ChatColor.AQUA, ChatColor.BOLD, ChatColor.RESET); }
}
