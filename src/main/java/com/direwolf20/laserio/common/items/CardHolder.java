package com.direwolf20.laserio.common.items;

import com.direwolf20.laserio.common.containers.CardHolderContainer;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.common.items.filters.BaseFilter;
import com.direwolf20.laserio.common.items.upgrades.OverclockerCard;
import com.direwolf20.laserio.common.items.upgrades.OverclockerNode;
import com.direwolf20.laserio.util.ItemStackHandlerProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CardHolder extends Item {
    public CardHolder() {
        super(new Item.Properties()
                .stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack cardHolder = player.getItemInHand(hand);
        if (level.isClientSide()) {
            if (player.isShiftKeyDown()) {
                String translationKey = "message.laserio.card_holder_pulling_" + (CardHolder.getActive(cardHolder) ? "disabled" : "enabled");
                player.displayClientMessage(Component.translatable(translationKey), true);
                player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP);
            }
            return InteractionResultHolder.pass(cardHolder);
        }
        if (player.isShiftKeyDown()) {
            setActive(cardHolder, !getActive(cardHolder));
            return InteractionResultHolder.pass(cardHolder);
        }
        cardHolder.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(h -> {
            NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider(
                    (windowId, playerInventory, playerEntity) -> new CardHolderContainer(windowId, playerInventory, player, cardHolder, h), Component.translatable("")), (buf -> {
                buf.writeItem(cardHolder);
            }));
        });
        return InteractionResultHolder.pass(cardHolder);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return getActive(itemStack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ItemStackHandlerProvider(CardHolderContainer.SLOTS);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int itemSlot, boolean isSelected) {
        if (!world.isClientSide() && entity instanceof Player player && getActive(stack)) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack cardStack = player.getInventory().getItem(i);
                if (cardStack.getItem() instanceof BaseCard || cardStack.getItem() instanceof BaseFilter || cardStack.getItem() instanceof OverclockerCard || cardStack.getItem() instanceof OverclockerNode) {
                    addCardToInventory(stack, cardStack);
                }
            }
        }
    }

    public static ItemStack addCardToInventory(ItemStack cardHolder, ItemStack card) {
        if (card.getItem() instanceof BaseFilter && card.hasTag()) {
            return card;
        }
        IItemHandler handler = cardHolder.getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElse(new ItemStackHandler(CardHolderContainer.SLOTS));
        List<Integer> emptySlots = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stackInSlot = handler.getStackInSlot(i);
            if (stackInSlot.isEmpty()) {
                emptySlots.add(i);
            }
            if (!stackInSlot.isEmpty() && ItemStack.isSameItemSameTags(stackInSlot, card)) {
                int j = stackInSlot.getCount() + card.getCount();
                int maxSize = 64;
                if (j <= maxSize) {
                    card.setCount(0);
                    stackInSlot.setCount(j);
                } else if (stackInSlot.getCount() < maxSize) {
                    card.shrink(maxSize - stackInSlot.getCount());
                    stackInSlot.setCount(maxSize);
                }
                if (card.isEmpty()) {
                    return card;
                }
            }
        }
        if (emptySlots.isEmpty()) return card;
        handler.insertItem(emptySlots.get(0), card.split(card.getCount()), false);
        return card;
    }

    public static UUID getUUID(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (!nbt.hasUUID("UUID")) {
            UUID newId = UUID.randomUUID();
            nbt.putUUID("UUID", newId);
            return newId;
        }
        return nbt.getUUID("UUID");
    }

    public static boolean getActive(ItemStack stack) {
        CompoundTag compound = stack.getTag();
        if (compound == null || !compound.contains("active")) {
            return false;
        }
        return compound.getBoolean("active");
    }

    public static boolean setActive(ItemStack stack, boolean active) {
        if (!active) {
            stack.removeTagKey("active");
        } else {
            stack.getOrCreateTag().putBoolean("active", active);
        }
        return active;
    }
}