package com.brogabe.darkzonebackpack.configuration;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {

    private final DarkzoneBackpack plugin;

    @Getter
    private String darkzoneWorld;

    @Getter
    private String backpackName;

    @Getter
    private String sellName;

    @Getter
    private String upgradeName;

    @Getter
    private String guideName;

    @Getter
    private String backpackMaterial;

    @Getter
    private String sellMaterial;

    @Getter
    private String upgradeMaterial;

    @Getter
    private String guideMaterial;

    @Getter
    private String dzItemString;

    @Getter
    private int defaultCapacity;

    @Getter
    private int sellPrice;

    @Getter
    private List<String> backpackLore;

    @Getter
    private List<String> sellLore;

    @Getter
    private List<String> guideLore;

    @Getter
    private List<String> upgradeLore;

    @Getter
    private boolean autoPickup;

    @Getter
    private boolean koreSupport;

    public ConfigManager(DarkzoneBackpack plugin) {
        this.plugin = plugin;

        cacheValues();
    }

    public void cacheValues() {
        FileConfiguration config = plugin.getConfig();

        backpackName = config.getString("backpack-item.name");
        sellName = config.getString("sell-item.name");
        upgradeName = config.getString("upgrade-item.name");
        guideName = config.getString("guide-item.name");
        backpackMaterial = config.getString("backpack-item.material");
        guideMaterial = config.getString("guide-item.material");
        sellMaterial = config.getString("sell-item.material");
        upgradeMaterial = config.getString("upgrade-item.material");
        backpackLore = config.getStringList("backpack-item.lore");
        upgradeLore = config.getStringList("upgrade-item.lore");
        guideLore = config.getStringList("guide-item.lore");
        sellLore = config.getStringList("sell-item.lore");
        dzItemString = config.getString("settings.darkzone-item");
        darkzoneWorld = config.getString("settings.darkzone-world");
        defaultCapacity = config.getInt("settings.starting-capacity");
        sellPrice = config.getInt("settings.sell-price");
        autoPickup = config.getBoolean("settings.auto-pickup");
        koreSupport = config.getBoolean("settings.kore-support");
    }

    public void reload() {
        plugin.reloadConfig();
        cacheValues();
    }
}
