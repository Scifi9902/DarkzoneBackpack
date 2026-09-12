package com.brogabe.darkzonebackpack.modules;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import com.brogabe.darkzonebackpack.configuration.ConfigManager;
import com.brogabe.darkzonebackpack.modules.types.BackpackModule;
import com.brogabe.darkzonebackpack.modules.types.SellModule;
import com.brogabe.darkzonebackpack.modules.types.UpgradeModule;
import com.brogabe.darkzonebackpack.utils.BackpackHelper;
import com.brogabe.darkzonebackpack.utils.TierInfo;
import lombok.Getter;

@Getter
public class ModuleManager {

    private final BackpackModule backpackModule;

    private final SellModule sellModule;

    private final UpgradeModule upgradeModule;

    public ModuleManager(DarkzoneBackpack plugin) {
        ConfigManager configManager = plugin.getConfigManager();
        TierInfo tierInfo = plugin.getTierInfo();
        BackpackHelper backpackHelper = plugin.getBackpackHelper();
        backpackModule = new BackpackModule(configManager, tierInfo, backpackHelper);
        upgradeModule = new UpgradeModule(plugin, tierInfo, backpackHelper);
        sellModule = new SellModule(configManager, backpackHelper);
    }

}
