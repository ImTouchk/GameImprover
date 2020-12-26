package net.ImTouchk;

import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.entity.Shulker;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.CommandSender;
import org.bukkit.event.entity.EntityDeathEvent;

public class ShulkerDrops implements Module {
    Boolean active;

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if(!active || !(e.getEntity() instanceof Shulker)) {
            return;
        }
        e.getDrops().clear();
        e.getDrops().add(new ItemStack(Material.SHULKER_SHELL, 2));
    }

    @Override
    public void onEnable() {
        FileIO config = new FileIO("config.yml");
        if(!config.exists("ShulkerDrops:Active")) config.write("ShulkerDrops:Active", true);
        active = config.getBool("ShulkerDrops:Active");
        config.close();
    }

    @Override
    public void onDisable() {
        FileIO config = new FileIO("config.yml");
        config.write("ShulkerDrops:Active", active);
        config.close();
    }

    @Override
    public void onTick() {

    }

    @Override
    public String getName() {
        return "shulkerdrops";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(String.format("%s shulkerdrops %s[enable/disable]%s", CommonText.usage(), ChatColor.GRAY, ChatColor.RESET));
            return true;
        }

        if(args[0].equalsIgnoreCase("enable")) {
            if(active) {
                sender.sendMessage(String.format("%s Module is already active.", CommonText.error()));
                return true;
            }
            sender.getServer().broadcastMessage(String.format("%s %sShulkerDrops%s is now active.", CommonText.info(), ChatColor.LIGHT_PURPLE, ChatColor.RESET));
            active = true;
        } else if(args[0].equalsIgnoreCase("disable")) {
            if(!active) {
                sender.sendMessage(String.format("%s Module is already inactive.", CommonText.error()));
                return true;
            }
            sender.getServer().broadcastMessage(String.format("%s %sShulkerDrops%s is now inactive.", CommonText.info(), ChatColor.LIGHT_PURPLE, ChatColor.RESET));
            active = false;
        }
        return true;
    }
}