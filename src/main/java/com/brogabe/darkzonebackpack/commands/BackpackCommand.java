package com.brogabe.darkzonebackpack.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import com.brogabe.darkzonebackpack.modules.types.BackpackModule;
import com.brogabe.darkzonebackpack.utils.ColorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

@CommandAlias("backpack|backpacks")
public class BackpackCommand extends BaseCommand {

    private final DarkzoneBackpack plugin;

    public BackpackCommand(DarkzoneBackpack plugin) {
        this.plugin = plugin;
    }

    @Default
    @Subcommand("help")
    @CommandPermission("backpacks.help")
    public void onHelp(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage(ColorUtil.color("&4&lBackpack &c&lCommands &f-"));
        sender.sendMessage(" ");
        sender.sendMessage(ColorUtil.color("&e- &c/backpacks give &4<player> <tier>"));
        sender.sendMessage(ColorUtil.color("&e- &c/backpacks reload"));
        sender.sendMessage(" ");
        sender.sendMessage(ColorUtil.color("&7&oPlugin coded by BroGabe"));
    }

    @Subcommand("give")
    @Syntax("<player> <tier>")
    @CommandCompletion("@players")
    @CommandPermission("backpacks.give")
    public void onGive(CommandSender sender, OnlinePlayer player, int tier) {
        BackpackModule module = plugin.getModuleManager().getBackpackModule();

        ItemStack backpack = module.getBackpackItem(tier);

        player.getPlayer().getInventory().addItem(backpack);

        sender.sendMessage(ColorUtil.color("&4&lBACKPACKS &fYou have given &e" + player.getPlayer().getName() + "&f a backpack."));
    }

    @Subcommand("reload")
    @CommandPermission("backpacks.reload")
    public void onReload(CommandSender sender) {
        plugin.getConfigManager().reload();
        sender.sendMessage(ColorUtil.color("&4&lBACKPACKS &fThe plugin has been reloaded."));
    }
}
