package com.brogabe.darkzonebackpack.listeners;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import com.brogabe.darkzonebackpack.configuration.ConfigManager;
import com.brogabe.darkzonebackpack.modules.types.BackpackModule;
import com.golfing8.kore.event.StackedEntityDeathEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class KoreListeners implements Listener {

    private final DarkzoneBackpack plugin;

    private final ConfigManager configManager;


    @EventHandler
    public void onPlayerKillKoreMob(StackedEntityDeathEvent event) {
        LivingEntity livingEntity = event.getStackedEntity().getBaseEntity();

        if (livingEntity instanceof HumanEntity) {
            return;
        }

        Player killer = event.getKiller();

        if (killer == null) {
            return;
        }

        if (!this.configManager.isAutoPickup()) {
            return;
        }

        Player player = event.getKiller();

        BackpackModule module = this.plugin.getModuleManager().getBackpackModule();

        module.onKoreMobDeath(player, event);
    }
}
