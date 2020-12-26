package net.ImTouchk;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.util.Vector;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventPriority;
import org.bukkit.command.CommandSender;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.ArrayList;

public class Graveyard implements Module {
    ArrayList<Grave> graves;
    Boolean graveThief;
    Boolean safeSpot;
    Boolean keepXP;
    Boolean active;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(!active) return;
        if(e.getClickedBlock() == null) return;
        if(e.getClickedBlock().getType() != Material.CHEST) return;
        if(graves.size() == 0) return;

        for(Grave grave : graves) {
            if(grave.location.equals(e.getClickedBlock().getLocation())) {
                if(graveThief && !grave.owner.equals(e.getPlayer().getUniqueId())) {
                    e.getPlayer().sendMessage(String.format("%s Only the owner may collect the grave.", CommonText.error()));
                    return;
                }

                for(ItemStack item : grave.itemStacks) { grave.location.getWorld().dropItemNaturally(grave.location, item); }
                e.getClickedBlock().setType(grave.initialBlock);
                graves.remove(grave);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if(!active) return;

        Location graveyardLocation = e.getEntity().getLocation();
        if(safeSpot) {
            graveyardLocation.subtract(new Vector(0, 1, 0));
            while(graveyardLocation.getBlock().getType() == Material.AIR) graveyardLocation.subtract(new Vector(0, 1, 0));
            graveyardLocation.add(new Vector(0, 1, 0));
        }

        Player player = e.getEntity();
        Material initial = graveyardLocation.getBlock().getType();

        graveyardLocation.getBlock().setType(Material.CHEST);
        Chest chest = (Chest)graveyardLocation.getBlock().getState();

        ArrayList<ItemStack> items = new ArrayList<>();
        for(ItemStack item : e.getDrops()) items.add(item.clone());
        graves.add(new Grave(items, chest.getLocation(), player.getUniqueId(), initial));

        e.setKeepLevel(keepXP);
        e.getDrops().clear();
        e.setDroppedExp(0);

        player.sendMessage(String.format("%s You have died at %s{x: %d, y: %d, z: %d}%s.", CommonText.info(), ChatColor.GREEN,
                chest.getLocation().getX(), chest.getLocation().getY(), chest.getLocation().getZ(),
                ChatColor.RESET));
    }


    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(Grave.class);
        FileIO config = new FileIO("graveyard.yml");
        if(!config.exists("Grave-Thief")) config.write("Grave-Thief", false);
        if(!config.exists("Safe-Spot")) config.write("Safe-Spot", true);
        if(!config.exists("Keep-XP")) config.write("Keep-XP", true);
        if(!config.exists("Active")) config.write("Active", true);

        graveThief = config.getBool("Grave-Thief");
        safeSpot = config.getBool("Safe-Spot");
        keepXP = config.getBool("Keep-XP");
        active = config.getBool("Active");

        if(!config.exists("Grave-Count")) config.write("Grave-Count", 0);
        graves = new ArrayList<>();

        Integer GraveCount = config.getInt("Grave-Count");
        if(GraveCount != 0) {
            for(Integer i = 0; i < GraveCount; i++) graves.add((Grave)config.getObject(String.format("Grave-%d", i), Object.class));
        }

        config.close();
    }

    @Override
    public void onDisable() {
        FileIO config = new FileIO("graveyard.yml");
        config.write("Grave-Thief", graveThief);
        config.write("Safe-Spot", safeSpot);
        config.write("Keep-XP", keepXP);
        config.write("Active", active);

        Integer PreviousCount = config.getInt("Grave-Count");
        if(PreviousCount != 0) {
            for(Integer i = 0; i < PreviousCount; i++) config.config.set(String.format("Grave-%d", i), null);
        }

        config.write("Grave-Count", graves.size());
        for(Integer i = 0; i < graves.size(); i++) config.write(String.format("Grave-%d", i), graves.get(i));

        config.close();

        ConfigurationSerialization.unregisterClass(Grave.class);
    }

    @Override
    public void onTick() { }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(String.format("%s graveyard %s[tomb-raider/safe-spot/keep-xp/enable/disable]%s", CommonText.usage(), ChatColor.GRAY, ChatColor.RESET));
            return true;
        }

        if(args[0].equalsIgnoreCase("enable")) {
            if(active) {
                sender.sendMessage(String.format("%s Module is already active.", CommonText.error()));
                return true;
            }
            sender.getServer().broadcastMessage(String.format("%s %Graveyard%s is now active.", CommonText.info(), ChatColor.LIGHT_PURPLE, ChatColor.RESET));
            setActive(true);
        } else if(args[0].equalsIgnoreCase("disable")) {
            if(!active) {
                sender.sendMessage(String.format("%s Module is already inactive.", CommonText.error()));
                return true;
            }
            sender.getServer().broadcastMessage(String.format("%s %Graveyard%s is now inactive.", CommonText.info(), ChatColor.LIGHT_PURPLE, ChatColor.RESET));
            setActive(false);
        } else if(args[0].equalsIgnoreCase("tomb-raider")) {
            if(args.length == 1) {
                sender.sendMessage(String.format("%s graveyard %stomb-raider [true/false]%s", CommonText.usage(), ChatColor.GRAY, ChatColor.RESET));
                return true;
            }

            Boolean newValue = Boolean.valueOf(args[1]);
            sender.getServer().broadcastMessage(String.format("%s %sTomb-Raider%s is now %b.", CommonText.info(), ChatColor.GRAY, ChatColor.RESET, newValue));
            setGraveThief(newValue);
        } else if(args[0].equalsIgnoreCase("keep-xp")) {
            if(args.length == 1) {
                sender.sendMessage(String.format("%s graveyard %skeep-xp [true/false]%s", CommonText.usage(), ChatColor.GRAY, ChatColor.RESET));
                return true;
            }

            Boolean newValue = Boolean.valueOf(args[1]);
            sender.getServer().broadcastMessage(String.format("%s %sKeep-XP%s is now %b.", CommonText.info(), ChatColor.GRAY, ChatColor.RESET, newValue));
            setKeepXP(newValue);
        } else if(args[0].equalsIgnoreCase("safe-spot")) {
            if(args.length == 1) {
                sender.sendMessage(String.format("%s graveyard %sSafe-spot [true/false]%s", CommonText.usage(), ChatColor.GRAY, ChatColor.RESET));
                return true;
            }

            Boolean newValue = Boolean.valueOf(args[1]);
            sender.getServer().broadcastMessage(String.format("%s %Safe-Spot%s is now %b.", CommonText.info(), ChatColor.GRAY, ChatColor.RESET, newValue));
            setSafeSpot(newValue);
        }

        return true;
    }

    @Override public String getName() { return "graveyard"; }
    public void setActive(Boolean state) { active = state; }
    public void setGraveThief(Boolean state) { graveThief = state; }
    public void setKeepXP(Boolean state) { keepXP = state; }
    public void setSafeSpot(Boolean state) { safeSpot = state; }
}
