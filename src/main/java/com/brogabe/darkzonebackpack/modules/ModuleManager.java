package com.brogabe.darkzonebackpack.modules;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import com.brogabe.darkzonebackpack.modules.types.BackpackModule;
import com.brogabe.darkzonebackpack.modules.types.SellModule;
import com.brogabe.darkzonebackpack.modules.types.UpgradeModule;
import lombok.Getter;

@Getter
public class ModuleManager {

    private final BackpackModule backpackModule;

    private final SellModule sellModule;

    private final UpgradeModule upgradeModule;

    public ModuleManager(DarkzoneBackpack plugin) {
        backpackModule = new BackpackModule(plugin);
        upgradeModule = new UpgradeModule(plugin);
        sellModule = new SellModule(plugin);
    }

}
