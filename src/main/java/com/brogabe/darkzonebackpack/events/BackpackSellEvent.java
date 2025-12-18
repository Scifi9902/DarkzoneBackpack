package com.brogabe.darkzonebackpack.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BackpackSellEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Getter
    private final Player player;

    @Setter
    @Getter
    private int amount;

    private boolean isCancelled = false;

    public BackpackSellEvent(Player player, int amount) {
        this.player = player;
        this.amount = amount;
    }


    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
