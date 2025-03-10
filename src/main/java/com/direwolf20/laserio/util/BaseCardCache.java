package com.direwolf20.laserio.util;

import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.common.items.cards.CardEnergy;
import com.direwolf20.laserio.common.items.cards.CardFluid;
import com.direwolf20.laserio.common.items.cards.CardItem;
import com.direwolf20.laserio.common.items.cards.CardRedstone;
import com.direwolf20.laserio.common.items.filters.BaseFilter;
import com.direwolf20.laserio.common.items.filters.FilterBasic;
import com.direwolf20.laserio.common.items.filters.FilterCount;
import com.direwolf20.laserio.common.items.filters.FilterMod;
import com.direwolf20.laserio.common.items.filters.FilterNBT;
import com.direwolf20.laserio.common.items.filters.FilterTag;
import com.direwolf20.laserio.integration.mekanism.CardChemical;
import com.direwolf20.laserio.integration.mekanism.MekanismCardCache;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BaseCardCache {
    public final Direction direction;
    public final ItemStack cardItem;
    public final byte channel;
    public final byte redstoneMode;
    public final byte redstoneChannel;
    public final ItemStack filterCard;
    public final int cardSlot;
    public final List<ItemStack> filteredItems;
    public final List<FluidStack> filteredFluids;
    public final List<String> filterTags;
    public final List<String> filterNBTs;
    public final byte sneaky;
    public final LaserNodeBE be;
    public final BaseCard.CardType cardType;
    public int extractLimit = 0;
    public int insertLimit = 0;
    public boolean enabled = true;

    public final boolean isAllowList;
    public final boolean isCompareNBT;
    public final Map<ItemStackKey, Boolean> filterCache = new Object2BooleanOpenHashMap<>();
    public final Map<ItemStackKey, Integer> filterCounts = new Object2IntOpenHashMap<>();
    //Fluids
    public final Map<FluidStackKey, Boolean> filterCacheFluid = new Object2BooleanOpenHashMap<>();
    public final Map<FluidStackKey, Integer> filterCountsFluid = new Object2IntOpenHashMap<>();

    public MekanismCardCache mekanismCardCache;

    public BaseCardCache(Direction direction, ItemStack cardItem, int cardSlot, LaserNodeBE be) {
        this.cardItem = cardItem;
        this.direction = direction;
        this.sneaky = BaseCard.getSneaky(cardItem);
        this.channel = BaseCard.getChannel(cardItem);
        this.redstoneMode = BaseCard.getRedstoneMode(cardItem);
        this.redstoneChannel = BaseCard.getRedstoneChannel(cardItem);
        this.filterCard = BaseCard.getFilter(cardItem);
        this.cardSlot = cardSlot;
        if (cardItem.getItem() instanceof CardItem) {
            cardType = BaseCard.CardType.ITEM;
        } else if (cardItem.getItem() instanceof CardFluid) {
            cardType = BaseCard.CardType.FLUID;
        } else if (cardItem.getItem() instanceof CardEnergy) {
            cardType = BaseCard.CardType.ENERGY;
            this.insertLimit = CardEnergy.getInsertLimitPercent(cardItem);
            this.extractLimit = CardEnergy.getExtractLimitPercent(cardItem);
        } else if (cardItem.getItem() instanceof CardRedstone) {
            cardType = BaseCard.CardType.REDSTONE;
        } else if (cardItem.getItem() instanceof CardChemical) {
            cardType = BaseCard.CardType.CHEMICAL;
            mekanismCardCache = new MekanismCardCache(this);
        } else
            cardType = BaseCard.CardType.MISSING;
        this.be = be;
        if (filterCard.isEmpty()) {
            filteredItems = new ArrayList<>();
            filteredFluids = new ArrayList<>();
            filterTags = new ArrayList<>();
            filterNBTs = new ArrayList<>();
            isAllowList = false;
            isCompareNBT = false;
        } else {
            this.filteredItems = getFilteredItems();
            this.filteredFluids = getFilteredFluids();
            this.filterTags = getFilterTags();
            this.filterNBTs = getFilterNBTs();
            isAllowList = BaseFilter.getAllowList(filterCard);
            isCompareNBT = BaseFilter.getCompareNBT(filterCard);
        }
        setEnabled();
    }

    public void setEnabled() {
        if (redstoneMode == 0 || BaseCard.getNamedTransferMode(cardItem).equals(BaseCard.TransferMode.SENSOR)) { //Sensors are always enabled
            enabled = true;
        } else {
            byte strength = be.getRedstoneChannelStrength(redstoneChannel);
            if (strength > 0 && redstoneMode == 1) {
                enabled = false;
            } else if (strength == 0 && redstoneMode == 2) {
                enabled = false;
            } else {
                enabled = true;
            }
        }
    }

    public int getFilterAmt(ItemStack testStack) {
        if (filterCard.isEmpty())
            return 0; //If theres no filter in the card (This should never happen in theory)
        if (!(filterCard.getItem() instanceof FilterCount)) { //If this is a basic or tag Card return -1 which will mean infinite amount
            return -1;
        }
        ItemStackKey key = new ItemStackKey(testStack, isCompareNBT);
        if (filterCounts.containsKey(key)) //If we've already tested this, get it from the cache
            return filterCounts.get(key);
        for (ItemStack stack : filteredItems) { //If the item is not in the cache, loop through filtered items list
            if (key.equals(new ItemStackKey(stack, isCompareNBT))) {
                filterCounts.put(key, stack.getCount());
                return stack.getCount();
            }
        }
        filterCounts.put(key, 0);
        return 0; //Should never get here in theory
    }

    public int getFilterAmt(FluidStack testStack) {
        if (filterCard.isEmpty())
            return 0; //If theres no filter in the card (This should never happen in theory)
        if (!(filterCard.getItem() instanceof FilterCount)) { //If this is a basic or tag Card return -1 which will mean infinite amount
            return -1;
        }
        FluidStackKey key = new FluidStackKey(testStack, isCompareNBT);
        if (filterCountsFluid.containsKey(key)) //If we've already tested this, get it from the cache
            return filterCountsFluid.get(key);

        ItemStackHandler filterSlotHandler = FilterCount.getInventory(filterCard);
        for (int i = 0; i < filterSlotHandler.getSlots(); i++) { //Gotta iterate the card's NBT because of the way we store amounts (in the MBAmt tag)
            ItemStack itemStack = filterSlotHandler.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                LazyOptional<IFluidHandlerItem> fluidHandlerOptional = FluidUtil.getFluidHandler(itemStack);
                if (!fluidHandlerOptional.isPresent()) continue;
                IFluidHandler fluidHandler = fluidHandlerOptional.resolve().get();
                for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
                    FluidStack fluidStack = fluidHandler.getFluidInTank(tank);
                    if (key.equals(new FluidStackKey(fluidStack, isCompareNBT))) {
                        int mbAmt = FilterCount.getSlotAmount(filterCard, i);
                        filterCountsFluid.put(key, mbAmt);
                        return mbAmt;
                    }
                }
            }
        }
        filterCountsFluid.put(key, 0);
        return 0; //Should never get here in theory
    }

    public List<ItemStack> getFilteredItems() {
        List<ItemStack> filteredItems = new ArrayList<>();
        ItemStackHandler filterSlotHandler;
        if (filterCard.getItem() instanceof FilterBasic)
            filterSlotHandler = FilterBasic.getInventory(filterCard);
        else
            filterSlotHandler = FilterCount.getInventory(filterCard);
        for (int i = 0; i < filterSlotHandler.getSlots(); i++) {
            ItemStack itemStack = filterSlotHandler.getStackInSlot(i);
            if (!itemStack.isEmpty())
                filteredItems.add(itemStack); //If this is a basic card it'll always be one, but getFilterAmt handles the proper logic of returning a value
        }
        return filteredItems;
    }

    public List<FluidStack> getFilteredFluids() {
        List<FluidStack> filteredFluids = new ArrayList<>();
        ItemStackHandler filterSlotHandler;
        if (filterCard.getItem() instanceof FilterBasic)
            filterSlotHandler = FilterBasic.getInventory(filterCard);
        else
            filterSlotHandler = FilterCount.getInventory(filterCard);
        for (int i = 0; i < filterSlotHandler.getSlots(); i++) {
            ItemStack itemStack = filterSlotHandler.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                LazyOptional<IFluidHandlerItem> fluidHandlerOptional = FluidUtil.getFluidHandler(itemStack);
                if (!fluidHandlerOptional.isPresent()) continue;
                IFluidHandler fluidHandler = fluidHandlerOptional.resolve().get();
                for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
                    FluidStack fluidStack = fluidHandler.getFluidInTank(tank);
                    if (!fluidStack.isEmpty())
                        filteredFluids.add(fluidStack); //If this is a basic card it'll always be one, but getFilterAmt handles the proper logic of returning a value
                }
            }
        }
        return filteredFluids;
    }

    public List<String> getFilterTags() {
        List<String> filterTags = new ArrayList<>();
        if (filterCard.getItem() instanceof FilterTag) {
            filterTags = FilterTag.getTags(filterCard);
        }
        return filterTags;
    }

    public List<String> getFilterNBTs() {
        List<String> filterNBTs = new ArrayList<>();
        if (filterCard.getItem() instanceof FilterNBT) {
            filterNBTs = FilterTag.getTags(filterCard);
        }
        return filterNBTs;
    }

    public boolean isStackValidForCard(ItemStack testStack) {
        if (filterCard.isEmpty()) return true; //If theres no filter in the card
        ItemStackKey key = new ItemStackKey(testStack, isCompareNBT);
        if (filterCache.containsKey(key)) return filterCache.get(key);
        if (filterCard.getItem() instanceof FilterMod) {
            for (ItemStack stack : filteredItems) {
                if (stack.getItem().getCreatorModId(stack).equals(testStack.getItem().getCreatorModId(testStack))) {
                    filterCache.put(key, isAllowList);
                    return isAllowList;
                }
            }
        } else if (filterCard.getItem() instanceof FilterTag) {
            for (TagKey tagKey : testStack.getItem().builtInRegistryHolder().tags().toList()) {
                String tag = tagKey.location().toString().toLowerCase(Locale.ROOT);
                if (filterTags.contains(tag)) {
                    filterCache.put(key, isAllowList);
                    return isAllowList;
                }
            }
        } else if (filterCard.getItem() instanceof FilterNBT) {
            if (testStack.hasTag()) {
                for (String tag : testStack.getOrCreateTag().getAllKeys()) {
                    if (filterNBTs.contains(tag)) {
                        filterCache.put(key, isAllowList);
                        return isAllowList;
                    }
                }
            }
        } else {
            for (ItemStack stack : filteredItems) {
                if (key.equals(new ItemStackKey(stack, isCompareNBT))) {
                    filterCache.put(key, isAllowList);
                    return isAllowList;
                }
            }
        }
        filterCache.put(key, !isAllowList);
        return !isAllowList;
    }

    public boolean isStackValidForCard(FluidStack testStack) {
        if (filterCard.isEmpty()) return true; //If theres no filter in the card
        FluidStackKey key = new FluidStackKey(testStack, isCompareNBT);
        if (filterCacheFluid.containsKey(key)) return filterCacheFluid.get(key);
        if (filterCard.getItem() instanceof FilterMod) {
            for (FluidStack stack : filteredFluids) {
                if (ForgeRegistries.FLUIDS.getKey(stack.getFluid()).getNamespace().equals(ForgeRegistries.FLUIDS.getKey(testStack.getFluid()).getNamespace())) {
                    filterCacheFluid.put(key, isAllowList);
                    return isAllowList;
                }
            }
        } else if (filterCard.getItem() instanceof FilterTag) {
            for (TagKey tagKey : testStack.getFluid().builtInRegistryHolder().tags().toList()) {
                String tag = tagKey.location().toString().toLowerCase(Locale.ROOT);
                if (filterTags.contains(tag)) {
                    filterCacheFluid.put(key, isAllowList);
                    return isAllowList;
                }
            }
        } else {
            for (FluidStack stack : filteredFluids) {
                if (key.equals(new FluidStackKey(stack, isCompareNBT))) {
                    filterCacheFluid.put(key, isAllowList);
                    return isAllowList;
                }
            }
        }
        filterCacheFluid.put(key, !isAllowList);
        return !isAllowList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BaseCardCache) {
            return ((BaseCardCache) obj).be.equals(this.be) && ((BaseCardCache) obj).direction.equals(this.direction) && ((BaseCardCache) obj).cardSlot == this.cardSlot;
        }
        return false;
    }
}