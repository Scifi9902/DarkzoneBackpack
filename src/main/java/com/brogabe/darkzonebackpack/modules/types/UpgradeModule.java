package com.brogabe.darkzonebackpack.modules.types;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import com.brogabe.darkzonebackpack.constant.DarkzoneBackpackConstant;
import com.brogabe.darkzonebackpack.utils.BackpackHelper;
import com.brogabe.darkzonebackpack.utils.ColorUtil;
import com.brogabe.darkzonebackpack.utils.TierInfo;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class UpgradeModule {

    private final DarkzoneBackpack plugin;

    private final TierInfo tierInfo;

    private final BackpackHelper backpackHelper;


    public void upgradeItem(Player player) {
        ItemStack itemInHand = player.getItemInHand();

        if (!backpackHelper.isValidBackpack(itemInHand)) {
            return;
        }

        this.processItemModification(player, itemInHand);
    }

    private void processItemModification(Player player, ItemStack item) {
        NBT.modify(item, nbt -> {
            ReadWriteNBT compound = nbt.getCompound(DarkzoneBackpackConstant.BACKPACK_COMPOUND_KEY);

            if (compound == null) {
                plugin.getLogger().warning("Could not find backpack compound key! " + DarkzoneBackpackConstant.BACKPACK_COMPOUND_KEY);
                return;
            }
            int tier = compound.getInteger("tier");
            int price = tierInfo.nextTierPrice(tier);

            if (price == -1) {
                player.sendMessage(ColorUtil.color("&4&lBACKPACKS &fYou already have a maxed backpack!"));
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 6, 6);
                return;
            }

            Economy economy = plugin.getEconomy();

            int updatedPrice = Math.max(0, price);

            if (!economy.has(player, updatedPrice)) {
                player.sendMessage(ColorUtil.color("&4&lBACKPACKS &fYou cannot afford this!"));
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 6, 6);
                return;
            }

            economy.withdrawPlayer(player, updatedPrice);

            BackpackModule backpackModule = plugin.getModuleManager().getBackpackModule();

            player.setItemInHand(backpackModule.getBackpackItem(tier + 1));
            player.closeInventory();
        });
    }
}
