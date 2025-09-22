package com.brogabe.darkzonebackpack.listeners;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class UpgradeListener implements Listener {

    private final DarkzoneBackpack plugin;

    public UpgradeListener(DarkzoneBackpack plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onOpenMenu(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        plugin.getUpgradeMenu().openMenu(event.getPlayer());
    }
}
