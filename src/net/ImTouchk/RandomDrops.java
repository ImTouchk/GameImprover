package net.ImTouchk;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Random;

public class RandomDrops implements Module {
    HashMap<Material,Material> drops;
    Material[] materials;
    Random random;
    Boolean active;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(!active) return;
        e.setDropItems(false);
        World world = e.getPlayer().getWorld();
        ItemStack drop = new ItemStack(drops.get(e.getBlock().getType()), random.nextInt(128));
        world.dropItemNaturally(e.getBlock().getLocation(), drop);
    }

    @Override
    public void onEnable() {
        FileIO config = new FileIO("randomdrops.yml");
        if(!config.exists("Active")) config.write("Active", false);
        active = config.getBool("Active");
        config.close();

        random = new Random();
        drops = new HashMap<>();
        materials = Material.values();

        String[] illegals = {
            "spawn", "spawner", "command", "illusioner",
            "barrier", "structure", "jigsaw", "border",
            "legacy", "air"
        };

        for(Material material : materials) {
            Material drop = Material.AIR;
            Integer illegal = 1;
            while(illegal != 0) {
                drop = materials[random.nextInt(materials.length)];
                String name = drop.name().toLowerCase();
                illegal = 0;
                for(String blacklisted : illegals) if(name.contains(blacklisted)) illegal += 1;
            }
            drops.put(material, drop);
        }
    }

    @Override
    public void onDisable() {
        FileIO config = new FileIO("randomdrops.yml");
        config.write("Active", active);
        config.close();
    }

    @Override public void onTick() { }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(String.format("%s randomdrops %s[enable/disable]%s", CommonText.usage(), ChatColor.GRAY, ChatColor.RESET));
            return true;
        }

        if(args[0].equalsIgnoreCase("enable")) {
            if(active) {
                sender.sendMessage(String.format("%s Module is already active.", CommonText.error()));
                return true;
            }
            sender.getServer().broadcastMessage(String.format("%s %sRandomDrops%s is now active.", CommonText.info(), ChatColor.LIGHT_PURPLE, ChatColor.RESET));
            setActive(true);
        } else if(args[0].equalsIgnoreCase("disable")) {
            if(!active) {
                sender.sendMessage(String.format("%s Module is already inactive.", CommonText.error()));
                return true;
            }
            sender.getServer().broadcastMessage(String.format("%s %sRandomDrops%s is now inactive.", CommonText.info(), ChatColor.LIGHT_PURPLE, ChatColor.RESET));
            setActive(false);
        }
        return true;
    }

    @Override public String getName() { return "randomdrops"; }
    public void setActive(Boolean state) { active = state; }
}
