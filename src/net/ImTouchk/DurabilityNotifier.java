package net.ImTouchk;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Sound;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class DurabilityNotifier implements Module {
    @EventHandler
    public void onItemDamaged(PlayerItemDamageEvent e) {
        ItemStack item = e.getItem();
        Damageable meta = (Damageable)item.getItemMeta();
        Material material = item.getType();
        Integer durabilityLeft = material.getMaxDurability() - meta.getDamage();
        if(durabilityLeft < 10) {
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_BLAZE_HURT, 50.0f, 1.0f);
            String message = String.format("Your %s%s%s has %s%d%s durability left.", ChatColor.RED, item.getType().toString().toLowerCase(), ChatColor.RESET,
                    ChatColor.GREEN, durabilityLeft, ChatColor.RESET);
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        }
    }

    @Override public void onEnable() { }
    @Override public void onDisable() { }
    @Override public void onTick() { }
    @Override public String getName() { return "null"; }
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { return true; }
}
