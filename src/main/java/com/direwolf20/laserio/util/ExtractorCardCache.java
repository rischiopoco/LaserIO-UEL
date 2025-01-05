package com.direwolf20.laserio.util;

import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.common.items.cards.CardEnergy;
import com.direwolf20.laserio.common.items.cards.CardFluid;
import com.direwolf20.laserio.common.items.cards.CardItem;
import com.direwolf20.laserio.integration.mekanism.CardChemical;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class ExtractorCardCache extends BaseCardCache {
    public final int extractAmt;
    public final int tickSpeed;
    public int remainingSleep;
    public boolean exact;
    public int roundRobin;

    public ExtractorCardCache(Direction direction, ItemStack cardItem, int cardSlot, LaserNodeBE be) {
        super(direction, cardItem, cardSlot, be);
        if (cardType == BaseCard.CardType.ITEM) {
            this.extractAmt = CardItem.getItemExtractAmt(cardItem);
            this.tickSpeed = CardItem.getExtractSpeed(cardItem);
        } else if (cardType == BaseCard.CardType.FLUID) {
            this.extractAmt = CardFluid.getFluidExtractAmt(cardItem);
            this.tickSpeed = CardFluid.getExtractSpeed(cardItem);
        } else if (cardType == BaseCard.CardType.ENERGY) {
            this.extractAmt = CardEnergy.getEnergyExtractAmt(cardItem);
            this.tickSpeed = CardEnergy.getExtractSpeed(cardItem);
        } else if (cardType == BaseCard.CardType.CHEMICAL) {
            this.extractAmt = CardChemical.getChemicalExtractAmt(cardItem);
            this.tickSpeed = CardChemical.getExtractSpeed(cardItem);
        } else {
            this.extractAmt = 0;
            this.tickSpeed = 1200;
        }

        this.exact = BaseCard.getExact(cardItem);
        this.roundRobin = BaseCard.getRoundRobin(cardItem);
    }

    public int getRemainingSleep() {
        return remainingSleep;
    }

    public void setRemainingSleep(int sleep) {
        remainingSleep = sleep;
    }

    public int decrementSleep() {
        remainingSleep--;
        if (remainingSleep <= 0) {
            remainingSleep = 0;
            return 0;
        }
        return remainingSleep;
    }
}