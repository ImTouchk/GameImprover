package net.ImTouchk;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class Main extends JavaPlugin {
    ArrayList<Module> modules;

    @Override
    public void onEnable() {
        FileIO.dataFolder= getDataFolder();
        modules = new ArrayList<>();
        modules.add(new BetterSleep());
        modules.add(new Graveyard());
        modules.add(new DurabilityNotifier());
        modules.add(new RandomDrops());
        modules.add(new Manhunt());
        for(Module module : modules) {
            getServer().getPluginManager().registerEvents(module, this);
            if(!module.getName().equalsIgnoreCase("null"))
                getCommand(module.getName()).setExecutor(module);
            module.onEnable();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Module module : modules) { module.onTick(); }
            }
        }.runTaskTimer(this, 0L, 1L);
    }

    @Override
    public void onDisable() {
        for(Module module : modules) { module.onDisable(); }
        HandlerList.unregisterAll(this);
    }
}
