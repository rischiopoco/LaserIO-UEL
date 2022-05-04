package com.direwolf20.laserio.client.screens;

import com.direwolf20.laserio.client.screens.widgets.IconButton;
import com.direwolf20.laserio.client.screens.widgets.ToggleButton;
import com.direwolf20.laserio.common.LaserIO;
import com.direwolf20.laserio.common.containers.FilterTagContainer;
import com.direwolf20.laserio.common.containers.customslot.FilterBasicSlot;
import com.direwolf20.laserio.common.items.filters.FilterTag;
import com.direwolf20.laserio.common.network.PacketHandler;
import com.direwolf20.laserio.common.network.packets.PacketGhostSlot;
import com.direwolf20.laserio.common.network.packets.PacketUpdateFilterTag;
import com.direwolf20.laserio.util.MagicHelpers;
import com.direwolf20.laserio.util.MiscTools;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FilterTagScreen extends AbstractContainerScreen<FilterTagContainer> {
    private final ResourceLocation GUI = new ResourceLocation(LaserIO.MODID, "textures/gui/filtertag.png");

    protected final FilterTagContainer container;
    private ItemStack filter;
    private boolean isAllowList;
    private EditBox tagField;
    private int page = 0;
    private int maxPages = 0;
    private int overSlot = -1;
    private int selectedSlot = -1;
    List<String> displayTags;
    List<String> tags = new ArrayList<>();
    List<String> stackInSlotTags = new ArrayList<>();


    public FilterTagScreen(FilterTagContainer container, Inventory inv, Component name) {
        super(container, inv, name);
        this.container = container;
        this.filter = container.filterItem;
        this.imageWidth = 200;
        this.imageHeight = 254;
        this.tags = FilterTag.getTags(filter);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
        if (MiscTools.inBounds(getGuiLeft() + 5, getGuiTop() + 10, 16, 16, mouseX, mouseY)) {
            if (isAllowList)
                this.renderTooltip(matrixStack, new TranslatableComponent("screen.laserio.allowlist"), mouseX, mouseY);
            else
                this.renderTooltip(matrixStack, new TranslatableComponent("screen.laserio.denylist"), mouseX, mouseY);
        }
        int availableItemsstartX = getGuiLeft() + 7;
        int availableItemstartY = getGuiTop() + 47;
        int color = 0x885B5B5B;

        if (page > maxPages)
            page = maxPages;

        matrixStack.pushPose();
        //RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        fillGradient(matrixStack, availableItemsstartX - 2, availableItemstartY - 4, availableItemsstartX + 162, availableItemstartY + 110, color, color);
        RenderSystem.colorMask(true, true, true, true);
        matrixStack.popPose();

        ItemStack stackInSlot = container.handler.getStackInSlot(0);
        stackInSlotTags = new ArrayList<>();
        if (!stackInSlot.isEmpty()) {
            stackInSlot.getItem().builtInRegistryHolder().tags().forEach(t -> {
                String tag = t.location().toString().toLowerCase(Locale.ROOT);
                if (!stackInSlotTags.contains(tag) && !tags.contains(tag))
                    stackInSlotTags.add(tag);
            });
        }

        int tagsPerPage = 11;
        maxPages = (int) Math.floor((double) (tags.size() + stackInSlotTags.size()) / (tagsPerPage + 1));

        String pagesLabel = MagicHelpers.withSuffix(page + 1) + " / " + MagicHelpers.withSuffix(maxPages + 1);
        font.draw(matrixStack, pagesLabel, (availableItemsstartX - 2) / 2 + (availableItemsstartX + 162) / 2 - font.width(pagesLabel) / 2, getGuiTop() + 160, Color.DARK_GRAY.getRGB());

        int itemStackMin = (page * tagsPerPage);
        int itemStackMax = Math.min((page * tagsPerPage) + tagsPerPage, tags.size());
        displayTags = tags.subList(itemStackMin, itemStackMax);
        int tagStartY = availableItemstartY;

        int slot = 0;
        overSlot = -1;

        for (String tag : stackInSlotTags) {
            matrixStack.pushPose();
            matrixStack.scale(0.75f, 0.75f, 0.75f);
            font.draw(matrixStack, tag, availableItemsstartX / 0.75f, tagStartY / 0.75f, Color.BLUE.getRGB());
            matrixStack.popPose();

            if (MiscTools.inBounds(availableItemsstartX, tagStartY - 2, 160, 8, mouseX, mouseY)) {
                overSlot = slot;
                color = -2130706433;// : 0xFF5B5B5B;

                matrixStack.pushPose();
                //RenderSystem.disableLighting();
                RenderSystem.disableDepthTest();
                RenderSystem.colorMask(true, true, true, false);
                fillGradient(matrixStack, availableItemsstartX - 1, tagStartY - 2, availableItemsstartX + 160, tagStartY + 8, color, color);
                RenderSystem.colorMask(true, true, true, true);
                matrixStack.popPose();
            }

            if (slot == selectedSlot) {
                color = 0xFF00FF00;

                matrixStack.pushPose();
                //RenderSystem.disableLighting();
                RenderSystem.disableDepthTest();
                RenderSystem.colorMask(true, true, true, false);

                int x1 = availableItemsstartX + 160;
                int y1 = tagStartY + 10;
                hLine(matrixStack, availableItemsstartX - 2, x1 - 0, tagStartY - 2, color);
                hLine(matrixStack, availableItemsstartX - 2, x1 - 0, y1 - 3, color);
                vLine(matrixStack, availableItemsstartX - 2, tagStartY - 2, y1 - 2, color);
                vLine(matrixStack, x1 - 0, tagStartY - 2, y1 - 2, color);

                RenderSystem.colorMask(true, true, true, true);
                matrixStack.popPose();
            }

            tagStartY += 10;
            slot++;
        }

        for (String tag : displayTags) {
            matrixStack.pushPose();
            matrixStack.scale(0.75f, 0.75f, 0.75f);
            font.draw(matrixStack, tag, availableItemsstartX / 0.75f, tagStartY / 0.75f, Color.DARK_GRAY.getRGB());
            matrixStack.popPose();

            if (MiscTools.inBounds(availableItemsstartX, tagStartY - 2, 160, 8, mouseX, mouseY)) {
                overSlot = slot;
                color = -2130706433;// : 0xFF5B5B5B;

                matrixStack.pushPose();
                //RenderSystem.disableLighting();
                RenderSystem.disableDepthTest();
                RenderSystem.colorMask(true, true, true, false);
                fillGradient(matrixStack, availableItemsstartX - 1, tagStartY - 2, availableItemsstartX + 160, tagStartY + 8, color, color);
                RenderSystem.colorMask(true, true, true, true);
                matrixStack.popPose();
            }

            if (slot == selectedSlot) {
                color = 0xFFFF0000;

                matrixStack.pushPose();
                //RenderSystem.disableLighting();
                RenderSystem.disableDepthTest();
                RenderSystem.colorMask(true, true, true, false);

                int x1 = availableItemsstartX + 160;
                int y1 = tagStartY + 10;
                hLine(matrixStack, availableItemsstartX - 2, x1 - 0, tagStartY - 2, color);
                hLine(matrixStack, availableItemsstartX - 2, x1 - 0, y1 - 3, color);
                vLine(matrixStack, availableItemsstartX - 2, tagStartY - 2, y1 - 2, color);
                vLine(matrixStack, x1 - 0, tagStartY - 2, y1 - 2, color);

                RenderSystem.colorMask(true, true, true, true);
                matrixStack.popPose();
            }

            tagStartY += 10;
            slot++;
        }
    }

    @Override
    public void init() {
        super.init();
        List<AbstractWidget> leftWidgets = new ArrayList<>();

        this.isAllowList = FilterTag.getAllowList(filter);

        ResourceLocation[] allowListTextures = new ResourceLocation[2];
        allowListTextures[0] = new ResourceLocation(LaserIO.MODID, "textures/gui/buttons/allowlistfalse.png");
        allowListTextures[1] = new ResourceLocation(LaserIO.MODID, "textures/gui/buttons/allowlisttrue.png");

        leftWidgets.add(new ToggleButton(getGuiLeft() + 5, getGuiTop() + 5, 16, 16, allowListTextures, isAllowList ? 1 : 0, (button) -> {
            isAllowList = !isAllowList;
            ((ToggleButton) button).setTexturePosition(isAllowList ? 1 : 0);
        }));

        ResourceLocation add = new ResourceLocation(LaserIO.MODID, "textures/gui/buttons/add.png");
        Button addButton = new IconButton(getGuiLeft() + 155, getGuiTop() + 5, 16, 16, add, (button) -> {
            if (!tagField.getValue().isEmpty()) {
                String tag = tagField.getValue().toLowerCase(Locale.ROOT);
                if (!tags.contains(tag))
                    tags.add(tag);
                tagField.setValue("");
            } else {
                ItemStack stack = container.handler.getStackInSlot(0);
                if (!stack.isEmpty()) {
                    if (hasShiftDown()) {
                        stack.getItem().builtInRegistryHolder().tags().forEach(t -> {
                            String tag = t.location().toString().toLowerCase(Locale.ROOT);
                            if (!tags.contains(tag))
                                tags.add(tag);
                        });
                        container.handler.setStackInSlot(0, ItemStack.EMPTY);
                    } else {
                        if (selectedSlot != -1) {
                            if (selectedSlot <= stackInSlotTags.size()) {
                                tags.add(stackInSlotTags.get(selectedSlot));
                                selectedSlot = -1;
                            }
                        }
                    }
                }
            }
        });
        leftWidgets.add(addButton);

        ResourceLocation remove = new ResourceLocation(LaserIO.MODID, "textures/gui/buttons/remove.png");
        Button removeButton = new IconButton(getGuiLeft() + 135, getGuiTop() + 5, 16, 16, remove, (button) -> {
            if (selectedSlot != -1) {
                if (selectedSlot >= stackInSlotTags.size()) {
                    tags.remove(displayTags.get(selectedSlot - stackInSlotTags.size()));
                    selectedSlot = -1;
                }
            }
        });
        leftWidgets.add(removeButton);

        ResourceLocation clear = new ResourceLocation(LaserIO.MODID, "textures/gui/buttons/clear.png");
        Button clearButton = new IconButton(getGuiLeft() + 115, getGuiTop() + 5, 16, 16, clear, (button) -> {
            tags.clear();
        });
        leftWidgets.add(clearButton);

        ResourceLocation pageup = new ResourceLocation(LaserIO.MODID, "textures/gui/buttons/pageup.png");
        Button pageUp = new IconButton(getGuiLeft() + 100, getGuiTop() + 157, 12, 12, pageup, (button) -> {
            if (page < maxPages) page++;
        });
        leftWidgets.add(pageUp);

        ResourceLocation pagedown = new ResourceLocation(LaserIO.MODID, "textures/gui/buttons/pagedown.png");
        Button pageDown = new IconButton(getGuiLeft() + 58, getGuiTop() + 157, 12, 12, pagedown, (button) -> {
            if (page > 0) page--;
        });
        leftWidgets.add(pageDown);

        tagField = new EditBox(font, getGuiLeft() + 7, getGuiTop() + 25, 160, 15, TextComponent.EMPTY);
        leftWidgets.add(tagField);


        // Lay the buttons out, too lazy to figure out the math every damn time.
        // Ordered by where you add them.
        for (int i = 0; i < leftWidgets.size(); i++) {
            addRenderableWidget(leftWidgets.get(i));
        }
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        //font.draw(stack, new TranslatableComponent("screen.laserio.allowlist").getString(), 5, 5, Color.DARK_GRAY.getRGB());
        //font.draw(stack, new TranslatableComponent("screen.laserio.comparenbt").getString(), 7, 35, Color.DARK_GRAY.getRGB());
        //super.renderLabels(matrixStack, x, y);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI);
        this.blit(matrixStack, getGuiLeft(), getGuiTop(), 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        PacketHandler.sendToServer(new PacketUpdateFilterTag(isAllowList, tags));
        super.onClose();
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        InputConstants.Key mouseKey = InputConstants.getKey(p_keyPressed_1_, p_keyPressed_2_);
        if (p_keyPressed_1_ == 256) {
            if (tagField.isFocused()) {
                tagField.setFocus(false);
                return true;
            } else {
                onClose();
                return true;
            }
        }
        if (minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
            if (tagField.isFocused()) {
                return true;
            } else {
                onClose();
                return true;
            }
        }

        if (tagField.isFocused() && (p_keyPressed_1_ == 257 || p_keyPressed_1_ == 335)) { //enter key
            if (!tagField.getValue().isEmpty()) {
                String tag = tagField.getValue().toLowerCase(Locale.ROOT);
                if (!tags.contains(tag))
                    tags.add(tag);
                tagField.setValue("");
                tagField.setValue("");
            }
        }

        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    @Override
    public boolean mouseClicked(double x, double y, int btn) {
        if (MiscTools.inBounds(tagField.x, tagField.y, tagField.getWidth(), tagField.getHeight(), x, y) && btn == 1)
            tagField.setValue("");

        if (overSlot >= 0) {
            selectedSlot = overSlot;
            if (hasShiftDown()) {
                if (selectedSlot != -1) {
                    if (selectedSlot >= stackInSlotTags.size()) {
                        tags.remove(displayTags.get(selectedSlot - stackInSlotTags.size()));
                        selectedSlot = -1;
                        return true;
                    }
                    if (selectedSlot < stackInSlotTags.size()) {
                        tags.add(stackInSlotTags.get(selectedSlot));
                        selectedSlot = -1;
                        return true;
                    }
                }
            }
            //tagField.setText(displayTags.get(selectedSlot));
            return true;
        }

        if (hoveredSlot instanceof FilterBasicSlot) {


            // By splitting the stack we can get air easily :) perfect removal basically
            ItemStack stack = this.menu.getCarried();// getMinecraft().player.inventoryMenu.getCarried();
            stack = stack.copy().split(hoveredSlot.getMaxStackSize()); // Limit to slot limit
            hoveredSlot.set(stack); // Temporarily update the client for continuity purposes
            PacketHandler.sendToServer(new PacketGhostSlot(hoveredSlot.index, stack, stack.getCount()));
            return true;
        }
        return super.mouseClicked(x, y, btn);
    }

    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
        return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private static TranslatableComponent getTrans(String key, Object... args) {
        return new TranslatableComponent(LaserIO.MODID + "." + key, args);
    }
}
