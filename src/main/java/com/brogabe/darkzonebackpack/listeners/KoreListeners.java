package com.brogabe.darkzonebackpack.listeners;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import com.brogabe.darkzonebackpack.configuration.ConfigManager;
import com.brogabe.darkzonebackpack.modules.types.BackpackModule;
import com.golfing8.kore.event.StackedEntityDeathEvent;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class KoreListeners implements Listener {

    private final DarkzoneBackpack plugin;

    private final ConfigManager configManager;

    public KoreListeners(DarkzoneBackpack plugin) {
        this.plugin = plugin;

        configManager = plugin.getConfigManager();
    }

    @EventHandler
    public void onPlayerKillKoreMob(StackedEntityDeathEvent event) {
        if(event.getStackedEntity().getBaseEntity() instanceof HumanEntity) return;
        if(event.getKiller() == null) return;
        if(!configManager.isAutoPickup()) return;

        Player player = event.getKiller();

        BackpackModule module = plugin.getModuleManager().getBackpackModule();

        module.onKoreMobDeath(player, event);
    }
}
