package com.brogabe.darkzonebackpack.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter @Setter
public class BackpackSellEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final Player player;

    private int amount;

    private boolean cancelled;

    public BackpackSellEvent(Player player, int amount) {
        this.player = player;
        this.amount = amount;
    }


    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
