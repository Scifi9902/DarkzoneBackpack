package com.brogabe.darkzonebackpack.listeners;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import com.brogabe.darkzonebackpack.configuration.ConfigManager;
import com.brogabe.darkzonebackpack.constant.DarkzoneBackpackConstant;
import com.brogabe.darkzonebackpack.modules.types.BackpackModule;
import com.brogabe.darkzonebackpack.modules.types.SellModule;
import de.tr7zw.nbtapi.NBT;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final DarkzoneBackpack plugin;

    private final ConfigManager configManager;

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getName().equalsIgnoreCase(this.configManager.getDarkzoneWorld())) {
            return;
        }

        BackpackModule module = this.plugin.getModuleManager().getBackpackModule();
        module.itemPickup(player, event);
    }

    @EventHandler
    public void onSell(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (!player.isSneaking() || (action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK)) {
            return;
        }

        ItemStack itemStack = player.getItemInHand();

        if (itemStack == null || !itemStack.hasItemMeta()) {
            return;
        }

        NBT.modify(itemStack, nbt -> {
            if (nbt.getCompound(DarkzoneBackpackConstant.BACKPACK_COMPOUND_KEY) == null) {
                return;
            }

            SellModule module = plugin.getModuleManager().getSellModule();
            module.onSell(player, itemStack);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerKillMob(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity instanceof HumanEntity) {
            return;
        }

        Player killer = entity.getKiller();

        if (killer == null || !configManager.isAutoPickup() || configManager.isKoreSupport()) {
            return;
        }

        BackpackModule module = plugin.getModuleManager().getBackpackModule();
        module.onMobDeath(killer, event);
    }
}
