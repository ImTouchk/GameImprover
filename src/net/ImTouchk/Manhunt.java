package net.ImTouchk;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventPriority;
import org.bukkit.command.CommandSender;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Manhunt implements Module {
    Player tracked = null;

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player hunter = e.getPlayer();
        if(tracked == null) {
            return;
        }

        World hunterWorld = hunter.getWorld();
        ItemStack mainHand = hunter.getInventory().getItemInMainHand();
        ItemStack offHand = hunter.getInventory().getItemInOffHand();

        if((mainHand.getType() != null && mainHand.getType() == Material.COMPASS)
        || (offHand.getType() != null && offHand.getType() == Material.COMPASS)) {
            World preyWorld = tracked.getWorld();
            if(hunterWorld == preyWorld) {
                hunter.setCompassTarget(tracked.getLocation());
                hunter.sendMessage(String.format("%sTracking %s.%s", ChatColor.GREEN, tracked.getDisplayName(), ChatColor.RESET));
            } else {
                hunter.sendMessage(String.format("%sThere is no-one to track.%s", ChatColor.GREEN, ChatColor.RESET));
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {

    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if(tracked == null || e.getPlayer() == tracked) {
            return;
        }

        if(e.getPlayer().getInventory().contains(Material.COMPASS)) {
            return;
        }

        e.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override public void onTick() { }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;

        if(args.length == 0) {
            sender.sendMessage(String.format("%s manhunt %s[player]%s", CommonText.usage(), ChatColor.GRAY, ChatColor.RESET));
            return true;
        }

        Player invited = Bukkit.getPlayer(args[0]);
        if(invited == null) {
            sender.sendMessage(String.format("%s manhunt %s[player]%s", CommonText.usage(), ChatColor.GRAY, ChatColor.RESET));
            tracked = null;
            return true;
        }

        tracked = invited;

        return true;
    }

    @Override public String getName() { return "manhunt"; }
}
