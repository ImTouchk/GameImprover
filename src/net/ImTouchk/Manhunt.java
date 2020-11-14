package net.ImTouchk;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventPriority;
import org.bukkit.command.CommandSender;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class Manhunt implements Module {
    HashMap<Player, Player> matches;

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player hunter = e.getPlayer();

        if(matches.get(hunter) == null)
            return;

        World hunterWorld = hunter.getWorld();
        ItemStack mainHand = hunter.getInventory().getItemInMainHand();
        ItemStack offHand = hunter.getInventory().getItemInOffHand();

        if((mainHand.getType() != null && mainHand.getType() == Material.COMPASS)
        || (offHand.getType() != null && offHand.getType() == Material.COMPASS)) {
            Player prey = matches.get(hunter);
            World preyWorld = prey.getWorld();
            if(hunterWorld == preyWorld) {
                hunter.setCompassTarget(prey.getLocation());
                hunter.sendMessage(String.format("%sTracking %s.%s", ChatColor.GREEN, prey.getDisplayName(), ChatColor.RESET));
            } else {
                hunter.sendMessage(String.format("%sThere is nothing to track.%s", ChatColor.GREEN, ChatColor.RESET));
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        for(Map.Entry<Player, Player> entry : matches.entrySet()) {
            if(e.getEntity().equals(entry.getValue())) {
                matches.remove(entry.getKey());
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        for(Player hunter : matches.keySet()) {
            if(e.getPlayer().equals(hunter)) {
                hunter.getInventory().addItem(new ItemStack(Material.COMPASS));
            }
        }
    }

    @Override public void onEnable() { matches = new HashMap<>(); }
    @Override public void onDisable() { matches.clear(); }
    @Override public void onTick() { }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        matches.put((Player)sender, (Player)sender);
        return true;
    }

    @Override public String getName() { return "manhunt"; }
}
