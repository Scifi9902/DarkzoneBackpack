package com.brogabe.darkzonebackpack.configuration;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

@Getter
public class ConfigManager {

    private final DarkzoneBackpack plugin;

    private String darkzoneWorld;
    private String backpackName;
    private String sellName;
    private String upgradeName;
    private String guideName;
    private String backpackMaterial;
    private String sellMaterial;
    private String upgradeMaterial;
    private String guideMaterial;
    private String dzItemString;
    private int defaultCapacity;
    private int sellPrice;
    private List<String> backpackLore;
    private List<String> sellLore;
    private List<String> guideLore;
    private List<String> upgradeLore;
    private boolean autoPickup;
    private boolean koreSupport;

    public ConfigManager(DarkzoneBackpack plugin) {
        this.plugin = plugin;

        cacheValues();
    }

    public void cacheValues() {
        FileConfiguration config = plugin.getConfig();

        this.backpackName = config.getString("backpack-item.name");
        this.sellName = config.getString("sell-item.name");
        this.upgradeName = config.getString("upgrade-item.name");
        this.guideName = config.getString("guide-item.name");
        this.backpackMaterial = config.getString("backpack-item.material");
        this.guideMaterial = config.getString("guide-item.material");
        this.sellMaterial = config.getString("sell-item.material");
        this.upgradeMaterial = config.getString("upgrade-item.material");
        this.backpackLore = config.getStringList("backpack-item.lore");
        this.upgradeLore = config.getStringList("upgrade-item.lore");
        this.guideLore = config.getStringList("guide-item.lore");
        this.sellLore = config.getStringList("sell-item.lore");
        this.dzItemString = config.getString("settings.darkzone-item");
        this.darkzoneWorld = config.getString("settings.darkzone-world");
        this.defaultCapacity = config.getInt("settings.starting-capacity");
        this.sellPrice = config.getInt("settings.sell-price");
        this.autoPickup = config.getBoolean("settings.auto-pickup");
        this.koreSupport = config.getBoolean("settings.kore-support");
    }

    public void reload() {
        plugin.reloadConfig();
        cacheValues();
    }
}
