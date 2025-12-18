package com.brogabe.darkzonebackpack;

import co.aikar.commands.PaperCommandManager;
import com.brogabe.darkzonebackpack.commands.BackpackCommand;
import com.brogabe.darkzonebackpack.configuration.ConfigManager;
import com.brogabe.darkzonebackpack.listeners.KoreListeners;
import com.brogabe.darkzonebackpack.listeners.PlayerListener;
import com.brogabe.darkzonebackpack.listeners.UpgradeListener;
import com.brogabe.darkzonebackpack.menus.UpgradeMenu;
import com.brogabe.darkzonebackpack.modules.ModuleManager;
import com.brogabe.darkzonebackpack.utils.BackpackUtils;
import com.brogabe.darkzonebackpack.utils.TierInfo;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class DarkzoneBackpack extends JavaPlugin {

    @Getter
    private ConfigManager configManager;

    @Getter
    private ModuleManager moduleManager;

    @Getter
    private Economy economy;

    @Getter
    private TierInfo tierInfo;

    @Getter
    private BackpackUtils backpackUtils;

    @Getter
    private UpgradeMenu upgradeMenu;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        // Registering Dependencies
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Registering Managers
        tierInfo = new TierInfo(this);
        configManager = new ConfigManager(this);
        backpackUtils = new BackpackUtils(this);
        moduleManager = new ModuleManager(this);

        // Register the Menus
        upgradeMenu = new UpgradeMenu(this);

        // Registering the listeners
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new KoreListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new UpgradeListener(this), this);

        // Registering the commands
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.registerCommand(new BackpackCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
}
