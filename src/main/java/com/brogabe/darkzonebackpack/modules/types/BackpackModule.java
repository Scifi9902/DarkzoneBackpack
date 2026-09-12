package com.brogabe.darkzonebackpack.modules.types;

import com.brogabe.darkzonebackpack.configuration.ConfigManager;
import com.brogabe.darkzonebackpack.utils.BackpackHelper;
import com.brogabe.darkzonebackpack.utils.ItemCreator;
import com.brogabe.darkzonebackpack.utils.TierInfo;
import com.golfing8.kore.event.StackedEntityDeathEvent;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BackpackModule {

    private final ConfigManager configManager;

    private final TierInfo tierInfo;

    private final BackpackHelper backpackHelper;

    public void onKoreMobDeath(Player player, StackedEntityDeathEvent event) {
        handleMobDeath(player, event.getStackedDrops().getDrops());
    }

    public void onMobDeath(Player player, EntityDeathEvent event) {
        handleMobDeath(player, event.getDrops());
    }

    private void handleMobDeath(Player player, Collection<ItemStack> drops) {
        Material darkzoneMaterial = Material.valueOf(configManager.getDzItemString());

        List<ItemStack> mobDrops = drops.stream()
                .filter(item -> item.getType() == darkzoneMaterial && !item.hasItemMeta())
                .collect(Collectors.toList());

        if (mobDrops.isEmpty()) return;

        int backpackSlot = backpackHelper.getBackpackSlot(player);
        if (backpackSlot == -1) return;

        ItemStack backpack = player.getInventory().getItem(backpackSlot);
        NBTItem nbtItem = new NBTItem(backpack);
        NBTCompound compound = nbtItem.getCompound("DarkzoneBackpack");

        int tier = compound.getInteger("tier");
        int maxCapacity = tierInfo.getMaxCapacity(tier);
        int capacity = compound.getInteger("capacity");

        if (capacity >= maxCapacity && maxCapacity != -1) return;

        int newCapacity = capacity;

        for (ItemStack itemStack : mobDrops) {
            int pickupAmount = itemStack.getAmount();
            int overflow = (maxCapacity == -1) ? 0 : Math.max((newCapacity + pickupAmount) - maxCapacity, 0);

            newCapacity = (maxCapacity == -1) ? newCapacity + pickupAmount : Math.min(maxCapacity, newCapacity + pickupAmount);

            if (overflow > 0) {
                ItemStack overflowItem = new ItemStack(itemStack);
                overflowItem.setAmount(overflow);
                player.getInventory().addItem(overflowItem);
            }
        }

        compound.setInteger("capacity", newCapacity);
        backpackHelper.updateBackpackSlot(player, backpackSlot, nbtItem);

        drops.removeAll(mobDrops);
    }

    public void itemPickup(Player player, PlayerPickupItemEvent event) {
        ItemStack pickedItem = event.getItem().getItemStack();

        if(pickedItem.getType() != Material.valueOf(configManager.getDzItemString())) return;

        if(pickedItem.hasItemMeta()) return;

        int backpackSlot = backpackHelper.getBackpackSlot(player);

        if(backpackSlot == -1) return;

        ItemStack backpack = player.getInventory().getItem(backpackSlot);
        NBTItem nbtItem = new NBTItem(backpack);
        NBTCompound compound = nbtItem.getCompound("DarkzoneBackpack");

        int tier = compound.getInteger("tier");
        int maxCapacity = tierInfo.getMaxCapacity(tier);
        int capacity = compound.getInteger("capacity");
        int pickupAmount = event.getItem().getItemStack().getAmount();

        if(capacity >= maxCapacity && maxCapacity != -1) return;

        int newCapacity = (maxCapacity == -1) ? capacity + pickupAmount : Math.min(capacity + pickupAmount, maxCapacity);
        int overflow = (maxCapacity == -1) ? 0 : (capacity + pickupAmount) - maxCapacity;

        compound.setInteger("capacity", newCapacity);
        backpackHelper.updateBackpackSlot(player, backpackSlot, nbtItem);

        if(overflow > 0) {
            ItemStack leftover = new ItemStack((pickedItem));
            leftover.setAmount(overflow);
            player.getInventory().addItem(leftover);
        }

        event.setCancelled(true);
        event.getItem().remove();
    }

    public ItemStack getBackpackItem(int tier) {
        Material material = Material.valueOf(configManager.getBackpackMaterial());
        String name = configManager.getBackpackName().replace("%tier%", String.valueOf(tier));

        List<String> lore = formatBackpackLore(configManager.getBackpackLore(), 0, tierInfo.getMaxCapacity(tier));

        ItemStack itemStack = new ItemCreator(material, name, 1, 0, "", lore).getItem();
        NBTItem nbtItem = new NBTItem(itemStack);

        NBTCompound compound = nbtItem.getOrCreateCompound("DarkzoneBackpack");
        compound.setInteger("tier", tier);
        compound.setInteger("capacity", 0);

        return nbtItem.getItem();
    }

    private List<String> formatBackpackLore(List<String> baseLore, int amount, int max) {
        return baseLore.stream()
                .map(s -> s.replace("%amount%", String.valueOf(amount))
                        .replace("%max%", String.valueOf(max)))
                .collect(Collectors.toList());
    }
}
