package com.direwolf20.laserio.setup;

import com.direwolf20.laserio.client.blockentityrenders.LaserConnectorAdvBERender;
import com.direwolf20.laserio.client.blockentityrenders.LaserConnectorBERender;
import com.direwolf20.laserio.client.blockentityrenders.LaserNodeBERender;
import com.direwolf20.laserio.client.events.ClientEvents;
import com.direwolf20.laserio.client.events.EventTooltip;
import com.direwolf20.laserio.client.events.KeybindHandler;
import com.direwolf20.laserio.client.screens.CardChemicalScreen;
import com.direwolf20.laserio.client.screens.CardEnergyScreen;
import com.direwolf20.laserio.client.screens.CardFluidScreen;
import com.direwolf20.laserio.client.screens.CardHolderScreen;
import com.direwolf20.laserio.client.screens.CardItemScreen;
import com.direwolf20.laserio.client.screens.CardRedstoneScreen;
import com.direwolf20.laserio.client.screens.FilterBasicScreen;
import com.direwolf20.laserio.client.screens.FilterCountScreen;
import com.direwolf20.laserio.client.screens.FilterNBTScreen;
import com.direwolf20.laserio.client.screens.FilterTagScreen;
import com.direwolf20.laserio.client.screens.LaserNodeScreen;
import com.direwolf20.laserio.common.LaserIO;
import com.direwolf20.laserio.common.blockentities.LaserConnectorAdvBE;
import com.direwolf20.laserio.common.blockentities.LaserConnectorBE;
import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.common.items.cards.CardRedstone;
import com.direwolf20.laserio.integration.mekanism.CardChemical;
import com.direwolf20.laserio.integration.mekanism.MekanismIntegration;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.awt.Color;

@Mod.EventBusSubscriber(modid = LaserIO.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(Registration.LaserNode.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(Registration.LaserConnector.get(), RenderType.cutout());

        //Register our Render Events Class
        MinecraftForge.EVENT_BUS.register(ClientEvents.class);
        MinecraftForge.EVENT_BUS.register(EventTooltip.class);

        //Register our KeybindHandler
        MinecraftForge.EVENT_BUS.register(new KeybindHandler());

        //Screens
        event.enqueueWork(() -> {
            MenuScreens.register(Registration.LaserNode_Container.get(), LaserNodeScreen::new);           // Attach our container to the screen
            MenuScreens.register(Registration.CardItem_Container.get(), CardItemScreen::new);           // Attach our container to the screen
            MenuScreens.register(Registration.CardFluid_Container.get(), CardFluidScreen::new);           // Attach our container to the screen
            MenuScreens.register(Registration.CardEnergy_Container.get(), CardEnergyScreen::new);           // Attach our container to the screen
            MenuScreens.register(Registration.CardRedstone_Container.get(), CardRedstoneScreen::new);           // Attach our container to the screen
            MenuScreens.register(Registration.CardHolder_Container.get(), CardHolderScreen::new);           // Attach our container to the screen
            MenuScreens.register(Registration.FilterBasic_Container.get(), FilterBasicScreen::new);           // Attach our container to the screen
            MenuScreens.register(Registration.FilterCount_Container.get(), FilterCountScreen::new);           // Attach our container to the screen
            MenuScreens.register(Registration.FilterTag_Container.get(), FilterTagScreen::new);           // Attach our container to the screen
            MenuScreens.register(Registration.FilterNBT_Container.get(), FilterNBTScreen::new);           // Attach our container to the screen
        });

        //Item Properties -- For giving the Cards an Insert/Extract on the itemstack
        event.enqueueWork(() -> {
            ItemProperties.register(Registration.Card_Item.get(),
                    new ResourceLocation(LaserIO.MODID, "mode"), (stack, level, living, id) -> {
                        return (int) BaseCard.getTransferMode(stack);
                    });
            ItemProperties.register(Registration.Card_Fluid.get(),
                    new ResourceLocation(LaserIO.MODID, "mode"), (stack, level, living, id) -> {
                        return (int) BaseCard.getTransferMode(stack);
                    });
            ItemProperties.register(Registration.Card_Energy.get(),
                    new ResourceLocation(LaserIO.MODID, "mode"), (stack, level, living, id) -> {
                        return (int) BaseCard.getTransferMode(stack);
                    });
            ItemProperties.register(Registration.Card_Redstone.get(),
                    new ResourceLocation(LaserIO.MODID, "mode"), (stack, level, living, id) -> {
                        return (int) CardRedstone.getTransferMode(stack);
                    });
        });

        //Mekanism
        if (MekanismIntegration.isLoaded()) {
            event.enqueueWork(() -> {
                MenuScreens.register(Registration.CardChemical_Container.get(), CardChemicalScreen::new);
                ItemProperties.register(Registration.Card_Chemical.get(),
                        new ResourceLocation(LaserIO.MODID, "mode"), (stack, level, living, id) -> {
                            return (int) CardChemical.getTransferMode(stack);
                        });
            });
        }
    }

    //Register Block Entity Renders
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Registration.LaserConnector_BE.get(), LaserConnectorBERender::new);
        event.registerBlockEntityRenderer(Registration.LaserNode_BE.get(), LaserNodeBERender::new);
        event.registerBlockEntityRenderer(Registration.LaserConnectorAdv_BE.get(), LaserConnectorAdvBERender::new);
    }

    @SubscribeEvent
    public static void registerTooltipFactory(RegisterClientTooltipComponentFactoriesEvent event) {
        //LOGGER.debug("Registering custom tooltip component factories for {}", Reference.MODID);
        event.register(EventTooltip.CopyPasteTooltipComponent.Data.class, EventTooltip.CopyPasteTooltipComponent::new);
    }

    //Register keybinds
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeybindHandler.OPEN_CARD_HOLDER);
        event.register(KeybindHandler.TOGGLE_CARD_HOLDER_PULLING);
    }

    //For giving the cards their channel color on the itemstack
    @SubscribeEvent
    static void itemColors(RegisterColorHandlersEvent.Item event) {
        final ItemColors colors = event.getItemColors();

        colors.register((stack, index) -> {
            if (index == 2) {
                if (BaseCard.getTransferMode(stack) == (byte) 3) {
                    Color color = LaserNodeBERender.COLORS[BaseCard.getRedstoneChannel(stack)];
                    return color.getRGB();
                } else {
                    Color color = LaserNodeBERender.COLORS[BaseCard.getChannel(stack)];
                    return color.getRGB();
                }
            }
            return 0xFFFFFFFF;
        }, Registration.Card_Item.get());
        colors.register((stack, index) -> {
            if (index == 2) {
                if (BaseCard.getTransferMode(stack) == (byte) 3) {
                    Color color = LaserNodeBERender.COLORS[BaseCard.getRedstoneChannel(stack)];
                    return color.getRGB();
                } else {
                    Color color = LaserNodeBERender.COLORS[BaseCard.getChannel(stack)];
                    return color.getRGB();
                }
            }
            return 0xFFFFFFFF;
        }, Registration.Card_Fluid.get());
        if (MekanismIntegration.isLoaded()) {
            colors.register((stack, index) -> {
                if (index == 2) {
                    if (BaseCard.getTransferMode(stack) == (byte) 3) {
                        Color color = LaserNodeBERender.COLORS[BaseCard.getRedstoneChannel(stack)];
                        return color.getRGB();
                    } else {
                        Color color = LaserNodeBERender.COLORS[BaseCard.getChannel(stack)];
                        return color.getRGB();
                    }
                }
                return 0xFFFFFFFF;
            }, Registration.Card_Chemical.get());
        }
        colors.register((stack, index) -> {
            if (index == 2) {
                if (BaseCard.getTransferMode(stack) == (byte) 3) {
                    Color color = LaserNodeBERender.COLORS[BaseCard.getRedstoneChannel(stack)];
                    return color.getRGB();
                } else {
                    Color color = LaserNodeBERender.COLORS[BaseCard.getChannel(stack)];
                    return color.getRGB();
                }
            }
            return 0xFFFFFFFF;
        }, Registration.Card_Energy.get());
        colors.register((stack, index) -> {
            if (index == 2) {
                Color color = LaserNodeBERender.COLORS[CardRedstone.getRedstoneChannel(stack)];
                return color.getRGB();
            }
            return 0xFFFFFFFF;
        }, Registration.Card_Redstone.get());
        colors.register((stack, index) -> {
            if (index == 1) {
                Color color = new Color(255, 0, 0, 255);
                return color.getRGB();
            }
            return 0xFFFFFFFF;
        }, Registration.LaserNode_ITEM.get());
        colors.register((stack, index) -> {
            if (index == 1) {
                Color color = new Color(255, 0, 0, 255);
                return color.getRGB();
            }
            return 0xFFFFFFFF;
        }, Registration.LaserConnector_ITEM.get());
        colors.register((stack, index) -> {
            if (index == 1) {
                Color color = new Color(255, 0, 0, 255);
                return color.getRGB();
            }
            return 0xFFFFFFFF;
        }, Registration.LaserConnectorAdv_ITEM.get());
    }

    @SubscribeEvent
    public static void blockColors(RegisterColorHandlersEvent.Block event) {
        event.register(
                (state, env, pos, index) -> {
                    if (env != null && pos != null && env.getBlockEntity(pos) instanceof LaserNodeBE laserNodeBE) {
                        Color color = laserNodeBE.getColor();
                        return FastColor.ARGB32.color(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
                    }
                    return FastColor.ARGB32.color(255, 255, 0, 0);
                },
                Registration.LaserNode.get()
        );
        event.register(
                (state, env, pos, index) -> {
                    if (env != null && pos != null && env.getBlockEntity(pos) instanceof LaserConnectorBE laserConnectorBE) {
                        Color color = laserConnectorBE.getColor();
                        return FastColor.ARGB32.color(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
                    }
                    return FastColor.ARGB32.color(255, 255, 0, 0);
                },
                Registration.LaserConnector.get()
        );
        event.register(
                (state, env, pos, index) -> {
                    if (env != null && pos != null && env.getBlockEntity(pos) instanceof LaserConnectorAdvBE laserConnectorAdvBE) {
                        Color color = laserConnectorAdvBE.getColor();
                        return FastColor.ARGB32.color(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
                    }
                    return FastColor.ARGB32.color(255, 255, 0, 0);
                },
                Registration.LaserConnectorAdv.get()
        );
    }
}