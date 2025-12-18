package com.brogabe.darkzonebackpack.listeners;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import com.brogabe.darkzonebackpack.configuration.ConfigManager;
import com.brogabe.darkzonebackpack.modules.types.BackpackModule;
import com.brogabe.darkzonebackpack.modules.types.SellModule;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    private final DarkzoneBackpack plugin;

    private final ConfigManager configManager;

    public PlayerListener(DarkzoneBackpack plugin) {
        this.plugin = plugin;

        configManager = plugin.getConfigManager();
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if(!event.getPlayer().getWorld().getName().equalsIgnoreCase(configManager.getDarkzoneWorld())) return;

        BackpackModule module = plugin.getModuleManager().getBackpackModule();
        module.itemPickup(event.getPlayer(), event);
    }

    @EventHandler
    public void onSell(PlayerInteractEvent event) {
        if(!event.getPlayer().isSneaking() || (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK))
            return;

        Player player = event.getPlayer();

        ItemStack itemStack = event.getPlayer().getItemInHand();

        if(itemStack == null || !itemStack.hasItemMeta()) return;

        NBTItem nbtItem = new NBTItem(itemStack);

        if(nbtItem.getCompound("DarkzoneBackpack") == null) return;

        SellModule module = plugin.getModuleManager().getSellModule();
        module.onSell(player, itemStack);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerKillMob(EntityDeathEvent event) {
        if(event.getEntity() instanceof HumanEntity) return;
        if(event.getEntity().getKiller() == null) return;
        if(!configManager.isAutoPickup()) return;
        if(configManager.isKoreSupport()) return;

        Player player = event.getEntity().getKiller();

        BackpackModule module = plugin.getModuleManager().getBackpackModule();

        module.onMobDeath(player, event);
    }
}
