package com.brogabe.darkzonebackpack.listeners;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public class UpgradeListener implements Listener {

    private final DarkzoneBackpack plugin;

    @EventHandler
    public void onOpenMenu(PlayerInteractEvent event) {
        Action action = event.getAction();
        if(action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        this.plugin.getUpgradeMenu().openMenu(player);
    }
}
