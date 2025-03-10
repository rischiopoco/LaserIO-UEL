package com.direwolf20.laserio.common.containers;

import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.direwolf20.laserio.common.items.CardHolder;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class CardRedstoneContainer extends AbstractContainerMenu {
    public static final int SLOTS = 0;
    public Player playerEntity;
    private IItemHandler playerInventory;
    public ItemStack cardItem;
    public BlockPos sourceContainer = BlockPos.ZERO;
    public byte direction = -1;

    public CardRedstoneContainer(int windowId, Inventory playerInventory, Player player, FriendlyByteBuf extraData) {
        this(windowId, playerInventory, player, extraData.readItem());
        this.direction = extraData.readByte();
    }

    public CardRedstoneContainer(int windowId, Inventory playerInventory, Player player, ItemStack cardItem) {
        super(Registration.CardRedstone_Container.get(), windowId);
        playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);
        this.cardItem = cardItem;
        layoutPlayerInventorySlots(8, 84);
    }

    public CardRedstoneContainer(int windowId, Inventory playerInventory, Player player, BlockPos sourcePos, ItemStack cardItem, byte direction) {
        this(windowId, playerInventory, player, cardItem);
        this.sourceContainer = sourcePos;
        this.direction = direction;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if (clickTypeIn == ClickType.SWAP) {
            return;
        }
        if (slotId >= 0) {
            ItemStack stackInSlot = slots.get(slotId).getItem();
            Item itemInSlot = stackInSlot.getItem();
            if ((itemInSlot instanceof BaseCard && stackInSlot == player.getMainHandItem()) || itemInSlot instanceof CardHolder) {
                return;
            }
        }
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        return ItemStack.EMPTY;
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    @Override
    public void removed(Player playerIn) {
        Level world = playerIn.level();
        if (!sourceContainer.equals(BlockPos.ZERO)) {
            BlockEntity blockEntity = world.getBlockEntity(sourceContainer);
            if (blockEntity instanceof LaserNodeBE)
                ((LaserNodeBE) blockEntity).updateThisNode();
        }
        super.removed(playerIn);
    }
}