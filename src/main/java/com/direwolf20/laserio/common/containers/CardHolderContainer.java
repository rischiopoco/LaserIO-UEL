package com.direwolf20.laserio.common.containers;

import com.direwolf20.laserio.common.containers.customslot.CardHolderSlot;
import com.direwolf20.laserio.common.items.CardHolder;
import com.direwolf20.laserio.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.UUID;

public class CardHolderContainer extends AbstractContainerMenu {
    public static final int SLOTS = 15;
    public ItemStack cardHolder;
    public UUID cardHolderUUID;
    public Player playerEntity;
    private IItemHandler playerInventory;
    public BlockPos sourceContainer = BlockPos.ZERO;
    public IItemHandler itemHandler;

    public CardHolderContainer(int windowId, Inventory playerInventory, Player player, FriendlyByteBuf extraData) {
        this(windowId, playerInventory, player, extraData.readItem(), new ItemStackHandler(SLOTS));
    }

    public CardHolderContainer(int windowId, Inventory playerInventory, Player player, ItemStack cardHolder, IItemHandler itemHandler) {
        super(Registration.CardHolder_Container.get(), windowId);
        playerEntity = player;
        this.itemHandler = itemHandler;
        this.playerInventory = new InvWrapper(playerInventory);
        this.cardHolder = cardHolder;
        cardHolderUUID = CardHolder.getUUID(cardHolder);
        if (itemHandler != null) {
            addSlotBox(itemHandler, 0, 44, 17, 5, 18, 3, 18);
        }
        layoutPlayerInventorySlots(8, 84);
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if (clickTypeIn == ClickType.SWAP) {
            return;
        }
        if (slotId >= 0) {
            Slot slot = slots.get(slotId);
            ItemStack stackInSlot = slot.getItem();
            if (slotId < SLOTS && slot instanceof CardHolderSlot) {
                if (stackInSlot.getMaxStackSize() == 1 && stackInSlot.getCount() > 1) {
                    ItemStack carriedItem = getCarried();
                    if (!carriedItem.isEmpty() && !stackInSlot.isEmpty() && !ItemStack.isSameItemSameTags(carriedItem, stackInSlot)) {
                        return;
                    }
                }
            } else if (stackInSlot.getItem() instanceof CardHolder && CardHolder.getUUID(stackInSlot).equals(cardHolderUUID)) {
                return;
            }
        }
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return (cardHolder.getItem() instanceof CardHolder && CardHolder.getUUID(cardHolder).equals(cardHolderUUID));
    }

    @Override
    protected boolean moveItemStackTo(ItemStack itemStack, int fromSlot, int toSlot, boolean p_38907_) {
        boolean flag = false;
        int i = fromSlot;
        if (p_38907_) {
            i = toSlot - 1;
        }

        while (!itemStack.isEmpty()) {
            if (p_38907_) {
                if (i < fromSlot) {
                    break;
                }
            } else if (i >= toSlot) {
                break;
            }

            Slot slot = this.slots.get(i);
            ItemStack itemstack = slot.getItem();
            if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(itemStack, itemstack)) {
                int j = itemstack.getCount() + itemStack.getCount();
                int maxSize = Math.min(slot.getMaxStackSize(), slot.getMaxStackSize(itemStack));
                if (j <= maxSize) {
                    itemStack.setCount(0);
                    itemstack.setCount(j);
                    slot.setChanged();
                    flag = true;
                } else if (itemstack.getCount() < maxSize) {
                    itemStack.shrink(maxSize - itemstack.getCount());
                    itemstack.setCount(maxSize);
                    slot.setChanged();
                    flag = true;
                }
            }

            if (p_38907_) {
                --i;
            } else {
                ++i;
            }
        }

        if (!itemStack.isEmpty()) {
            if (p_38907_) {
                i = toSlot - 1;
            } else {
                i = fromSlot;
            }

            while (true) {
                if (p_38907_) {
                    if (i < fromSlot) {
                        break;
                    }
                } else if (i >= toSlot) {
                    break;
                }

                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(itemStack) && slot1.getItem().getCount() < slot1.getMaxStackSize(itemStack)) {
                    if (itemStack.getCount() > slot1.getMaxStackSize()) {
                        slot1.set(itemStack.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.set(itemStack.split(slot1.getMaxStackSize(itemStack)));
                    }

                    slot1.setChanged();
                    flag = true;
                    break;
                }

                if (p_38907_) {
                    --i;
                } else {
                    ++i;
                }
            }
        }
        return flag;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
        if (slot instanceof CardHolderSlot) {
            return false;
        }
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemStack = stack.copy();
            //If its one of the 20 slots at the top try to move it into your inventory
            if (index < SLOTS) {
                if (playerIn.getInventory().getFreeSlot() != -1) {
                    // moveItemStackTo() always moves the item, no matter the return value. fixes #87
                    if (stack.getMaxStackSize() == 1) {
                        this.moveItemStackTo(stack.split(1), SLOTS, (36 + SLOTS), true);
                    } else {
                        this.moveItemStackTo(stack, SLOTS, (36 + SLOTS), true);
                    }
                } else {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemStack);
            } else {
                if (!this.moveItemStackTo(stack, 0, SLOTS, false)) {
                    return ItemStack.EMPTY;
                }
            }

            slot.onTake(playerIn, stack);
            if (stack.getCount() < itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
        }
        return itemStack;
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            if (handler.getSlots() == SLOTS) {
                addSlot(new CardHolderSlot(handler, index, x, y));
            } else {
                addSlot(new SlotItemHandler(handler, index, x, y));
            }
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
        super.removed(playerIn);
    }
}