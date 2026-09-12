package com.brogabe.darkzonebackpack;

import co.aikar.commands.PaperCommandManager;
import com.brogabe.darkzonebackpack.commands.BackpackCommand;
import com.brogabe.darkzonebackpack.configuration.ConfigManager;
import com.brogabe.darkzonebackpack.listeners.KoreListeners;
import com.brogabe.darkzonebackpack.listeners.PlayerListener;
import com.brogabe.darkzonebackpack.listeners.UpgradeListener;
import com.brogabe.darkzonebackpack.menus.UpgradeMenu;
import com.brogabe.darkzonebackpack.modules.ModuleManager;
import com.brogabe.darkzonebackpack.utils.BackpackHelper;
import com.brogabe.darkzonebackpack.utils.TierInfo;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

@Getter
public final class DarkzoneBackpack extends JavaPlugin {

    private ConfigManager configManager;
    private ModuleManager moduleManager;
    private Economy economy;
    private TierInfo tierInfo;
    private BackpackHelper backpackHelper;
    private UpgradeMenu upgradeMenu;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        Logger logger = this.getLogger();
        Server server = this.getServer();
        PluginManager pluginManager =  server.getPluginManager();

        // Registering Dependencies
        if (!setupEconomy(server, pluginManager)) {
            String logMessage = String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName());
            logger.severe(logMessage);
            pluginManager.disablePlugin(this);
            return;
        }

        // Registering Managers
        this.tierInfo = new TierInfo(this);
        this.configManager = new ConfigManager(this);
        this.backpackHelper = new BackpackHelper(this.configManager, this.tierInfo, logger);
        this.moduleManager = new ModuleManager(this);

        // Register the Menus
        this.upgradeMenu = new UpgradeMenu(this);

        // Registering the listeners
        this.registerListeners(pluginManager);

        // Registering the commands
        this.registerCommands();
    }

    private void registerListeners(PluginManager pluginManager) {
        pluginManager.registerEvents(new PlayerListener(this, this.configManager), this);
        pluginManager.registerEvents(new KoreListeners(this, this.configManager), this);
        pluginManager.registerEvents(new UpgradeListener(this), this);
    }

    private void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.registerCommand(new BackpackCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private boolean setupEconomy(Server server, PluginManager pluginManager) {
        if (pluginManager.getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> economyProvider = server.getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            return false;
        }

        return (economy = economyProvider.getProvider()) != null;
    }
}
