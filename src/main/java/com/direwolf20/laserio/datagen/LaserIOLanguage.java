package com.direwolf20.laserio.datagen;

import com.direwolf20.laserio.common.LaserIO;
import com.direwolf20.laserio.setup.ModSetup;
import com.direwolf20.laserio.setup.Registration;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class LaserIOLanguage extends LanguageProvider {
    public LaserIOLanguage(PackOutput output, String locale) {
        super(output, LaserIO.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        //Items and blocks names
        add("itemGroup." + ModSetup.TAB_NAME, LaserIO.MODNAME);
        add(Registration.LaserConnector.get(), "Laser Connector");
        add(Registration.LaserConnectorAdv.get(), "Advanced Laser Connector");
        add(Registration.LaserNode.get(), "Laser Node");
        add(Registration.Laser_Wrench.get(), "Laser Wrench");
        add(Registration.Card_Holder.get(), "Card Holder");
        add(Registration.Card_Cloner.get(), "Card Cloner");
        add(Registration.Card_Item.get(), "Item Card");
        add(Registration.Card_Fluid.get(), "Fluid Card");
        add(Registration.Card_Energy.get(), "Energy Card");
        add(Registration.Card_Redstone.get(), "Redstone Card");
        add(Registration.Card_Chemical.get(), "Chemical Card");
        add(Registration.Filter_Basic.get(), "Basic Filter");
        add(Registration.Filter_Count.get(), "Counting Filter");
        add(Registration.Filter_Tag.get(), "Tag Filter");
        add(Registration.Filter_Mod.get(), "Mod Filter");
        add(Registration.Filter_NBT.get(), "NBT Filter");
        add(Registration.Logic_Chip.get(), "Logic Chip");
        add(Registration.Logic_Chip_Raw.get(), "Raw Logic Chip");
        add(Registration.Overclocker_Node.get(), "Node Overclocker");
        add(Registration.Logistic_Overclocker_Card.get(), "Logistic Overclocker");

        //Screens informations
        add("screen.laserio.extractamt", "Transfer Amount");
        add("screen.laserio.tickSpeed", "Speed (Ticks)");

        add("screen.laserio.priority", "Priority");
        add("screen.laserio.channel", "Channel: ");
        add("screen.laserio.redstonechannel", "Redstone Channel: ");
        add("screen.laserio.regulate", "Regulate");
        add("screen.laserio.roundrobin", "Round Robin: ");
        add("screen.laserio.true", "True");
        add("screen.laserio.false", "False");
        add("screen.laserio.enforced", "Enforced");
        add("screen.laserio.exact", "Exact");
        add("screen.laserio.and", "And");
        add("screen.laserio.or", "Or");
        add("screen.laserio.allowlist", "Allow");
        add("screen.laserio.comparenbt", "NBT");
        add("screen.laserio.lasernode", "Laser Node");
        add("screen.laserio.energylimit", "Energy Limit (%)");

        add("screen.laserio.default", "Default");
        add("screen.laserio.up", "Up");
        add("screen.laserio.down", "Down");
        add("screen.laserio.north", "North");
        add("screen.laserio.south", "South");
        add("screen.laserio.west", "West");
        add("screen.laserio.east", "East");
        add("screen.laserio.settings", "Settings");
        add("screen.laserio.apply", "Apply");
        add("screen.laserio.red", "Red");
        add("screen.laserio.green", "Green");
        add("screen.laserio.blue", "Blue");
        add("screen.laserio.alpha", "Alpha");
        add("screen.laserio.wrench", "Wrench Alpha");

        add("screen.laserio.extract", "Extract");
        add("screen.laserio.insert", "Insert");
        add("screen.laserio.stock", "Stock");
        add("screen.laserio.sensor", "Sensor");
        add("screen.laserio.input", "Input");
        add("screen.laserio.output", "Output");
        add("screen.laserio.weak", "Weak");
        add("screen.laserio.strong", "Strong");
        add("screen.laserio.redstoneMode", "Redstone: ");
        add("screen.laserio.ignored", "Ignored");
        add("screen.laserio.low", "Low");
        add("screen.laserio.high", "High");

        add("screen.laserio.redstone.threshold", "Threshold");
        add("screen.laserio.redstone.thresholdlimit", "Limit");
        add("screen.laserio.redstone.thresholdoutput", "Output");
        add("screen.laserio.redstone.normal", "Normal");
        add("screen.laserio.redstone.complementary", "Complementary");
        add("screen.laserio.redstone.not", "NOT");
        add("screen.laserio.redstone.nologicoperation", "No logic operation");
        add("screen.laserio.redstone.and", "AND");
        add("screen.laserio.redstone.or", "OR");
        add("screen.laserio.redstone.xor", "XOR");

        add("screen.laserio.showparticles", "Show Particles");
        add("screen.laserio.hideparticles", "Hide Particles");

        add("screen.laserio.denylist", "Deny");
        add("screen.laserio.nbttrue", "Match NBT");
        add("screen.laserio.nbtfalse", "Ignore NBT");

        //Items tooltips
        add("laserio.tooltip.item.energy_overclocker.max_fe", "Max %d FE/operation");
        add("laserio.tooltip.item.show_details", "Hold shift to show details");
        add("laserio.tooltip.item.laser_wrench.select_node", "Select Node: ");
        add("laserio.tooltip.item.laser_wrench.select_node.keys", "Shift + R-Click");
        add("laserio.tooltip.item.laser_wrench.link_node", "Link Node: ");
        add("laserio.tooltip.item.laser_wrench.link_node.keys", "R-Click");
        add("laserio.tooltip.item.laser_wrench.autolink_node", "Auto-link Node: ");
        add("laserio.tooltip.item.laser_wrench.autolink_node.keys", "Offhand + Place Node");

        //Cards tooltips
        add("laserio.tooltip.item.show_settings", "Hold shift to show settings");
        add("laserio.tooltip.item.card.mode", "Mode: ");
        add("laserio.tooltip.item.card.channel", "Channel: ");
        add("laserio.tooltip.item.card.mode.EXTRACT", "Extract");
        add("laserio.tooltip.item.card.mode.INSERT", "Insert");
        add("laserio.tooltip.item.card.mode.STOCK", "Stock");
        add("laserio.tooltip.item.card.mode.SENSOR", "Sensor");
        add("laserio.tooltip.item.card.sneaky", "Sneaky: ");
        add("laserio.tooltip.item.card.sneaky.DOWN", "Down");
        add("laserio.tooltip.item.card.sneaky.UP", "Up");
        add("laserio.tooltip.item.card.sneaky.NORTH", "North");
        add("laserio.tooltip.item.card.sneaky.SOUTH", "South");
        add("laserio.tooltip.item.card.sneaky.WEST", "West");
        add("laserio.tooltip.item.card.sneaky.EAST", "East");
        add("laserio.tooltip.item.card.Filter", "Filter: ");
        add("laserio.tooltip.item.card.Overclocker", "Overclocker: ");
        add("laserio.tooltip.item.card.Overclockers", "Overclockers: ");
        add("laserio.tooltip.item.card.None", "None");

        //Filters tooltips
        add("laserio.tooltip.item.filter.type", "Type: ");
        add("laserio.tooltip.item.filter.type.allow", "Allow");
        add("laserio.tooltip.item.filter.type.deny", "Deny");
        add("laserio.tooltip.item.filter.nbt", "Match NBT: ");
        add("laserio.tooltip.item.filter.nbt.allow", "True");
        add("laserio.tooltip.item.filter.nbt.deny", "False");

        //Client messages
        add("message.laserio.wrenchrange", "Connection exceeds maximum range of %d");
        add("message.laserio.card_holder_pulling_enabled", "Card Holder pulling enabled");
        add("message.laserio.card_holder_pulling_disabled", "Card Holder pulling disabled");

        //Keybinds
        add("key.laserio.open_card_holder", "Open Card Holder");
        add("key.laserio.toggle_card_holder_pulling", "Toggle Card Holder Pulling");

        //Curios Card Holder slot
        add("curios.identifier.card_holder", "Card Holder");

        //add("", "");
    }
}