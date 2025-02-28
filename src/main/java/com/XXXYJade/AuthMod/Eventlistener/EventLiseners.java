package com.XXXYJade.AuthMod.Eventlistener;

import com.XXXYJade.AuthMod.AuthMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDestroyBlockEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class EventLiseners {
    private AuthMod authMod;

    public EventLiseners(AuthMod authMod) {
        this.authMod = authMod;
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        AuthMod.getPlayerManager().storeSpawn(player);
        AuthMod.getPlayerManager().addPlayer(player);
    }

    @SubscribeEvent
    public void onPlayerMove(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        if (!AuthMod.getPlayerManager().isLoggedIn(player.getName().getString())) {
            AuthMod.getPlayerManager().moveToSpawn(player);
        }
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AuthMod.getPlayerManager().logout(player);
        }
    }

    @SubscribeEvent
    public void onPlayerReceivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player) {
            if (!AuthMod.getPlayerManager().isLoggedIn(player.getName().getString())) {
                event.setNewDamage(0.0F);
            }
        }
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (!AuthMod.getPlayerManager().isLoggedIn(player.getName().getString())) {
            ItemStack itemStack = player.getMainHandItem();
            if (itemStack.getItem() instanceof BucketItem) {
                ItemStack originalContent = itemStack.copy();
                itemStack.setCount(originalContent.getCount());
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onItemUse(LivingEntityUseItemEvent.Start event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player) {
            if (!AuthMod.getPlayerManager().isLoggedIn(player.getName().getString())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (!AuthMod.getPlayerManager().isLoggedIn(player.getName().getString())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerChat(ServerChatEvent event) {
        Player player = event.getPlayer();
        if (!AuthMod.getPlayerManager().isLoggedIn(player.getName().getString())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onItemDrop(ItemTossEvent event) {
        Player player = event.getPlayer();
        if (!AuthMod.getPlayerManager().isLoggedIn(player.getName().getString())) {
            event.setCanceled(true);
            ItemStack itemStack = event.getEntity().getItem();
            player.getInventory().add(itemStack);
        }
    }

    @SubscribeEvent
    public void onPlayerBreakBlock(LivingDestroyBlockEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player) {
            if (!AuthMod.getPlayerManager().isLoggedIn(player.getName().getString())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!AuthMod.getPlayerManager().isLoggedIn(player.getName().getString())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerInteract2(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if (!AuthMod.getPlayerManager().isLoggedIn(player.getName().getString())) {
            event.setCanceled(true);
        }
    }
}
