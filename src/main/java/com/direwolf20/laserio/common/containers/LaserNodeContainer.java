package com.direwolf20.laserio.common.containers;

import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.direwolf20.laserio.common.containers.customhandler.LaserNodeItemHandler;
import com.direwolf20.laserio.common.containers.customslot.CardHolderSlot;
import com.direwolf20.laserio.common.containers.customslot.LaserNodeSlot;
import com.direwolf20.laserio.common.items.CardHolder;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.common.items.filters.BaseFilter;
import com.direwolf20.laserio.common.items.upgrades.OverclockerCard;
import com.direwolf20.laserio.common.items.upgrades.OverclockerNode;
import com.direwolf20.laserio.setup.Registration;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

import java.util.UUID;

public class LaserNodeContainer extends AbstractContainerMenu {
    public static final int CARDSLOTS = 9;
    public static final int SLOTS = CARDSLOTS + 1; //One slot is for Node Overclockers
    public Player playerEntity;
    private IItemHandler playerInventory;
    ContainerLevelAccess containerLevelAccess;
    public ItemStack cardHolder;
    public IItemHandler cardHolderHandler;
    public UUID cardHolderUUID;

    //Tile can be null and shouldn't be used for accessing any data that needs to be up to date on both sides
    public LaserNodeBE tile;
    public byte side;

    public LaserNodeContainer(int windowId, Inventory playerInventory, Player player, FriendlyByteBuf extraData) {
        this((LaserNodeBE) playerInventory.player.level().getBlockEntity(extraData.readBlockPos()), windowId, extraData.readByte(), playerInventory, player, new LaserNodeItemHandler(SLOTS), ContainerLevelAccess.NULL, extraData.readItem());
    }

    public LaserNodeContainer(@Nullable LaserNodeBE tile, int windowId, byte side, Inventory playerInventory, Player player, LaserNodeItemHandler handler, ContainerLevelAccess containerLevelAccess, ItemStack cardHolder) {
        super(Registration.LaserNode_Container.get(), windowId);
        this.playerEntity = player;
        this.tile = tile;
        this.side = side;
        this.playerInventory = new InvWrapper(playerInventory);
        this.containerLevelAccess = containerLevelAccess;
        if (handler != null) {
            addSlotBox(handler, 0, 62, 32, 3, 18, 3, 18);
            addSlotRange(handler, 9, 152, 78, 1, 18);
        }
        this.cardHolder = cardHolder;
        if (!cardHolder.isEmpty()) {
            this.cardHolderHandler = cardHolder.getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElse(new ItemStackHandler(CardHolderContainer.SLOTS));
            addSlotBox(cardHolderHandler, 0, -92, 32, 5, 18, 3, 18);
            cardHolderUUID = CardHolder.getUUID(cardHolder);
        }
        layoutPlayerInventorySlots(8, 99);
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if (clickTypeIn == ClickType.SWAP) {
            return;
        }
        if (slotId >= 0) {
            Slot slot = slots.get(slotId);
            ItemStack stackInSlot = slot.getItem();
            if (slot instanceof CardHolderSlot) {
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
        if (cardHolderUUID != null) {
            if (!(cardHolder.getItem() instanceof CardHolder) || !CardHolder.getUUID(cardHolder).equals(cardHolderUUID)) {
                return false;
            }
        }
        return stillValid(containerLevelAccess, playerEntity, Registration.LaserNode.get());
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
        if (slot instanceof CardHolderSlot || slot instanceof LaserNodeSlot)
            return false;
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        ItemStack stack = slot.getItem();
        if (slot instanceof CardHolderSlot) { //If we click on a cardHolder slot
            ItemStack stackToMove;
            if (stack.getMaxStackSize() == 1) {
                stackToMove = stack.split(1);
            } else {
                stackToMove = stack;
            }
            //Try to move 1 card to the node slots first, failing that, to the inventory!
            if (this.moveItemStackTo(stackToMove, 0, SLOTS, false)) {
                return ItemStack.EMPTY;
            } else if (this.moveItemStackTo(stackToMove, (SLOTS + CardHolderContainer.SLOTS), (36 + SLOTS + CardHolderContainer.SLOTS), true)) {
                return ItemStack.EMPTY;
            } else {
                stack.grow(1);
                return ItemStack.EMPTY;
            }
        } else if (index < CARDSLOTS) { //If its a node CARD slot
            if (!cardHolder.isEmpty()) { //Do the below set of logic if we have a card holder, otherwise just try to move to inventory
                if (this.moveItemStackTo(stack, SLOTS, (SLOTS + CardHolderContainer.SLOTS), false)) { //Move to card holder
                    if (!playerIn.level().isClientSide() && !(tile == null)) {
                        tile.updateThisNode();
                    }
                    return ItemStack.EMPTY;
                } else if (super.moveItemStackTo(stack, (SLOTS + CardHolderContainer.SLOTS), (36 + SLOTS + CardHolderContainer.SLOTS), true)) { //Move to inventory
                    if (!playerIn.level().isClientSide() && !(tile == null)) {
                        tile.updateThisNode();
                    }
                    return ItemStack.EMPTY;
                }
            } else {
                if (super.moveItemStackTo(stack, SLOTS, (36 + SLOTS), true)) { //Move to inventory
                    if (!playerIn.level().isClientSide() && !(tile == null)) {
                        tile.updateThisNode();
                    }
                    return ItemStack.EMPTY;
                }
            }
        } else { //If its not a cardHolder slot nor a Card slot in the node - it must be the Node Overclocker slot or the inventory...
            if (stack.getItem() instanceof OverclockerNode) {
                itemStack = stack.copy();
                //If its one of the 9 slots at the top try to move it into your inventory
                if (!cardHolder.isEmpty()) { //Do the below set of logic if we have a card holder, otherwise just try to move to inventory
                    if (index < (SLOTS + CardHolderContainer.SLOTS)) {
                        if (this.moveItemStackTo(stack, SLOTS, (SLOTS + CardHolderContainer.SLOTS), false)) { //Move to card holder
                            return ItemStack.EMPTY;
                        } else if (!super.moveItemStackTo(stack, (SLOTS + CardHolderContainer.SLOTS), (36 + SLOTS + CardHolderContainer.SLOTS), true)) {
                            return ItemStack.EMPTY;
                        }
                        slot.onQuickCraft(stack, itemStack);
                    } else {
                        if (!super.moveItemStackTo(stack, 0, SLOTS, false)) {
                            return ItemStack.EMPTY;
                        }
                        if (!playerIn.level().isClientSide() && !(tile == null)) {
                            tile.updateThisNode();
                        }
                    }
                } else {
                    if (index < SLOTS) {
                        if (!super.moveItemStackTo(stack, SLOTS , (36 + SLOTS), true)) {
                            return ItemStack.EMPTY;
                        }
                        slot.onQuickCraft(stack, itemStack);
                    } else {
                        if (!super.moveItemStackTo(stack, 0, SLOTS, false)) {
                            return ItemStack.EMPTY;
                        }
                        if (!playerIn.level().isClientSide() && !(tile == null)) {
                            tile.updateThisNode();
                        }
                    }
                }

                if (stack.isEmpty()) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }

                if (stack.getCount() == itemStack.getCount()) {
                    return ItemStack.EMPTY;
                }

                slot.onTake(playerIn, stack);
                return itemStack;
            } else if (stack.getItem() instanceof BaseCard || stack.getItem() instanceof BaseFilter || stack.getItem() instanceof OverclockerCard) { //If it's a BaseCard - it must be in the inventory, since these don't fit in the other slot...
                if (!cardHolder.isEmpty()) { //Do the below set of logic if we have a card holder, otherwise just try to move to inventory
                    if (super.moveItemStackTo(stack, 0, CARDSLOTS, false))
                        return ItemStack.EMPTY;
                    else if (this.moveItemStackTo(stack, SLOTS, (SLOTS + CardHolderContainer.SLOTS), false)) //Move to Card Holder
                        return ItemStack.EMPTY;
                } else {
                    if (super.moveItemStackTo(stack, 0, CARDSLOTS, false)) //Move to node
                        return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
        }
        return ItemStack.EMPTY;
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            if (handler instanceof LaserNodeItemHandler && index < CARDSLOTS)
                addSlot(new LaserNodeSlot(handler, index, x, y));
            else if (handler.getSlots() == CardHolderContainer.SLOTS)
                addSlot(new CardHolderSlot(handler, index, x, y));
            else
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
}