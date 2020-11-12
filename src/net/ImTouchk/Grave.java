package net.ImTouchk;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.util.ArrayList;

public class Grave implements ConfigurationSerializable {
    public ArrayList<ItemStack> itemStacks;
    public Location location;
    public UUID owner;
    public Material initialBlock;

    public Grave(ArrayList<ItemStack> itemStacks, Location location, UUID owner, Material initialBlock) {
        this.itemStacks = itemStacks;
        this.location = location;
        this.owner = owner;
        this.initialBlock = initialBlock;
    }

    public Grave(Map<String, Object> serializedForm) {
        itemStacks = (ArrayList<ItemStack>)serializedForm.get("Items");
        location = (Location)serializedForm.get("Location");
        owner = UUID.fromString((String)serializedForm.get("Owner"));
        initialBlock = Material.valueOf((String)serializedForm.get("Initial-Block"));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serializedForm = new HashMap<>();
        serializedForm.put("Items", itemStacks);
        serializedForm.put("Location", location);
        serializedForm.put("Owner", owner.toString());
        serializedForm.put("Initial-Block", initialBlock.toString());
        return serializedForm;
    }
}
