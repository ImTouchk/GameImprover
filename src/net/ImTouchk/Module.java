package net.ImTouchk;

import org.bukkit.event.Listener;
import org.bukkit.command.CommandExecutor;

public interface Module extends Listener, CommandExecutor {
    void onEnable();
    void onDisable();
    void onTick();
    String getName();
}
