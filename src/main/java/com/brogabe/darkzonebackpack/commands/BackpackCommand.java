package com.brogabe.darkzonebackpack.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import com.brogabe.darkzonebackpack.modules.types.BackpackModule;
import com.brogabe.darkzonebackpack.utils.ColorUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.stream.Stream;

@CommandAlias("backpack|backpacks")
@RequiredArgsConstructor
public class BackpackCommand extends BaseCommand {

    private final DarkzoneBackpack plugin;

    @Default
    @Subcommand("help")
    @CommandPermission("backpacks.help")
    public void onHelp(CommandSender sender) {
        Stream.of(" ",
                "&4&lBackpack &c&lCommands &f-",
                " ",
                "&e- &c/backpacks give &4<player> <tier>",
                "&e- &c/backpacks reload",
                " ",
                "&7&oPlugin coded by BroGabe")
                .map(ColorUtil::color)
                .forEach(message -> sender.sendMessage(ColorUtil.color(message)));
    }

    @Subcommand("give")
    @Syntax("<player> <tier>")
    @CommandCompletion("@players")
    @CommandPermission("backpacks.give")
    public void onGive(CommandSender sender, OnlinePlayer onlinePlayer, int tier) {
        BackpackModule module = plugin.getModuleManager().getBackpackModule();

        ItemStack backpack = module.getBackpackItem(tier);

        Player player = onlinePlayer.getPlayer();
        player.getInventory().addItem(backpack);

        sender.sendMessage(ColorUtil.color("&4&lBACKPACKS &fYou have given &e" + player.getName() + "&f a backpack."));
    }

    @Subcommand("reload")
    @CommandPermission("backpacks.reload")
    public void onReload(CommandSender sender) {
        plugin.getConfigManager().reload();
        sender.sendMessage(ColorUtil.color("&4&lBACKPACKS &fThe plugin has been reloaded."));
    }
}
