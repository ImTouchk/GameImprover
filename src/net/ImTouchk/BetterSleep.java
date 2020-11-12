package net.ImTouchk;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.util.ArrayList;

public class BetterSleep implements Module {
    ArrayList<Player> sleepers;
    Boolean amplify;
    Boolean active;
    Integer speed;

    @EventHandler
    public void onEnterBed(PlayerBedEnterEvent e) {
        if(!active) return;
        if(e.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

        sleepers.add(e.getPlayer());
        Server server = Bukkit.getServer();
        server.broadcastMessage(String.format("%s %sis now sleeping. Sweet dreams!%s", e.getPlayer().getDisplayName(), ChatColor.YELLOW, ChatColor.RESET));
    }

    @EventHandler
    public void onLeaveBed(PlayerBedLeaveEvent e) {
        if(!active) return;
        sleepers.remove(e.getPlayer());
    }

    @Override
    public void onEnable() {
        FileIO config = new FileIO("bettersleep.yml");
        if(!config.exists("Amplify")) config.write("Amplify", true);
        if(!config.exists("Active")) config.write("Active", true);
        if(!config.exists("Speed")) config.write("Speed", 100);
        amplify = config.getBool("Amplify");
        active = config.getBool("Active");
        speed = config.getInt("Speed");
        sleepers = new ArrayList<>();
        config.close();
    }

    @Override
    public void onDisable() {
        FileIO config = new FileIO("bettersleep.yml");
        config.write("Amplify", amplify);
        config.write("Active", true);
        config.write("Speed", speed);
        config.close();
        sleepers.clear();
    }

    @Override
    public void onTick() {
        if(!active) return;
        if(sleepers.size() == 0) return;
        long finalSpeed = speed * (amplify ? sleepers.size() : 1);
        World world = sleepers.get(0).getWorld();
        world.setTime(world.getTime() + finalSpeed);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(String.format("%s bettersleep %s[amplify/speed/enable/disable]%s", CommonText.usage(), ChatColor.GRAY, ChatColor.RESET));
            return true;
        }

        if(args[0].equalsIgnoreCase("enable")) {
            if(active) {
                sender.sendMessage(String.format("%s Module is already active.", CommonText.error()));
                return true;
            }
            sender.getServer().broadcastMessage(String.format("%s %sBetterSleep%s is now active.", CommonText.info(), ChatColor.LIGHT_PURPLE, ChatColor.RESET));
            setActive(true);
        } else if(args[0].equalsIgnoreCase("disable")) {
            if(!active) {
                sender.sendMessage(String.format("%s Module is already inactive.", CommonText.error()));
                return true;
            }
            sender.getServer().broadcastMessage(String.format("%s %sBetterSleep%s is now inactive.", CommonText.info(), ChatColor.LIGHT_PURPLE, ChatColor.RESET));
            setActive(false);
        } else if(args[0].equalsIgnoreCase("speed")) {
            if(args.length == 1) {
                sender.sendMessage(String.format("%s bettersleep %speed [100-500]%s", CommonText.usage(), ChatColor.GRAY, ChatColor.RESET));
                return true;
            }

            Integer newValue;
            try { newValue = Integer.valueOf(args[1]); }
            catch (NumberFormatException e) { return true; }

            if(newValue < 100) newValue = 100;
            else if(newValue > 500) newValue = 500;
            sender.getServer().broadcastMessage(String.format("%s %sSpeed%s is now %d.", CommonText.info(), ChatColor.GRAY, ChatColor.RESET, newValue));
            setSpeed(newValue);
        } else if(args[0].equalsIgnoreCase("amplify")) {
            if(args.length == 1) {
                sender.sendMessage(String.format("%s bettersleep %samplify [true/false]%s", CommonText.usage(), ChatColor.GRAY, ChatColor.RESET));
                sender.sendMessage(String.format("%sAmplify%s - make the night go by faster when multiple players are sleeping", ChatColor.GRAY, ChatColor.RESET));
                return true;
            }

            Boolean newValue = Boolean.valueOf(args[1]);
            sender.getServer().broadcastMessage(String.format("%s %sAmplify%s is now %b.", CommonText.info(), ChatColor.GRAY, ChatColor.RESET, newValue));
            setAmplify(newValue);
        }
        return true;
    }

    @Override public String getName() { return "bettersleep"; }
    public void setSpeed(Integer state) { speed = state; }
    public void setAmplify(Boolean state) { amplify = state; }
    public void setActive(Boolean state) {
        active = state;
        if(!active) sleepers.clear();
    }
}
