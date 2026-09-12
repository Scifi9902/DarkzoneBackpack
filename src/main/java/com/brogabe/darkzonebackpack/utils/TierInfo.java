package com.brogabe.darkzonebackpack.utils;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;

@RequiredArgsConstructor
public class TierInfo {

    private final DarkzoneBackpack plugin;

    public boolean nextTierExists(int currentTier) {
        FileConfiguration config = plugin.getConfig();

        int nextTier = currentTier + 1;

        return (config.getConfigurationSection("backpack-tiers." + nextTier) != null);
    }

    public int nextTierPrice(int currentTier) {
        if(!nextTierExists(currentTier)) return -1;

        FileConfiguration config = plugin.getConfig();

        int nextTier = currentTier + 1;

        return config.getInt("backpack-tiers." + nextTier + ".cost");
    }

    public int getMaxCapacity(int tier) {
        FileConfiguration config = plugin.getConfig();

        int startingCapacity = config.getInt("settings.starting-capacity");

        if(config.getConfigurationSection("backpack-tiers." + tier) == null) return startingCapacity;

        return config.getInt("backpack-tiers." + tier + ".capacity");
    }

}
