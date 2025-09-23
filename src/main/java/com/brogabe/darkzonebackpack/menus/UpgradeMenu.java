package com.brogabe.darkzonebackpack.menus;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import com.brogabe.darkzonebackpack.configuration.ConfigManager;
import com.brogabe.darkzonebackpack.modules.types.SellModule;
import com.brogabe.darkzonebackpack.modules.types.UpgradeModule;
import com.brogabe.darkzonebackpack.utils.*;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UpgradeMenu {

    private final DarkzoneBackpack plugin;

    private final ConfigManager configManager;

    private final BackpackUtils backpackUtils;

    private final TierInfo tierInfo;

    public UpgradeMenu(DarkzoneBackpack plugin) {
        this.plugin = plugin;

        backpackUtils = plugin.getBackpackUtils();
        configManager = plugin.getConfigManager();
        tierInfo = plugin.getTierInfo();
    }

    public void openMenu(Player player) {
        Gui gui = Gui.gui()
                .title(Component.text(ColorUtil.color("&4&lBackpack &c&lUpgrades")))
                .rows(3)
                .create();

        gui.setDefaultClickAction(action -> action.setCancelled(true));

        UpgradeModule module = plugin.getModuleManager().getUpgradeModule();

        SellModule sellModule = plugin.getModuleManager().getSellModule();

        if(!backpackUtils.isValidBackpack(player.getItemInHand())) return;

        NBTItem nbtItem = new NBTItem(player.getItemInHand());

        NBTCompound compound = nbtItem.getCompound("DarkzoneBackpack");

        int tier = compound.getInteger("tier");

        String currentTier = (tierInfo.nextTierExists(tier) ? String.valueOf(tier) : "MAXED");
        String nextTier = (tierInfo.nextTierExists(tier) ? String.valueOf(tier + 1) : "MAXED");
        String nextPrice = (tierInfo.nextTierExists(tier) ? MoneyUtil.intToDollars(tierInfo.nextTierPrice(tier)) : "MAXED");

        List<String> upgradeLore = formatUpgradeLore(configManager.getUpgradeLore(), currentTier, nextTier, nextPrice);

        ItemStack sellItem = new ItemCreator(Material.valueOf(configManager.getSellMaterial()), configManager.getSellName(), 1, 0, "", configManager.getSellLore()).getItem();
        ItemStack guideItem = new ItemCreator(Material.valueOf(configManager.getGuideMaterial()), configManager.getGuideName(), 1, 0, "", configManager.getGuideLore()).getItem();
        ItemStack upgradeItem = new ItemCreator(Material.valueOf(configManager.getUpgradeMaterial()), configManager.getUpgradeName(), 1, 0, "", upgradeLore).getItem();
        ItemStack glassItem = new ItemCreator(Material.STAINED_GLASS_PANE, "&7", 1, 15, "", Collections.emptyList()).getItem();

        GuiItem sellGuiItem = new GuiItem(sellItem);
        sellGuiItem.setAction(action -> sellModule.onSell(player, player.getItemInHand()));

        GuiItem upgradeGuiItem = new GuiItem(upgradeItem);
        upgradeGuiItem.setAction(action -> module.upgradeItem(player));

        GuiItem guideGuiItem = new GuiItem(guideItem);

        gui.setItem(11, sellGuiItem);
        gui.setItem(13, upgradeGuiItem);
        gui.setItem(15, guideGuiItem);

        gui.getFiller().fill(new GuiItem(glassItem));

        gui.open(player);
    }

    private List<String> formatUpgradeLore(List<String> baseLore, String currentTier, String nextTier, String nextPrice) {
        return baseLore.stream()
                .map(s -> s.replace("%current%", currentTier)
                        .replace("%next%", nextTier)
                        .replace("%cost%", nextPrice))
                .collect(Collectors.toList());
    }
}
