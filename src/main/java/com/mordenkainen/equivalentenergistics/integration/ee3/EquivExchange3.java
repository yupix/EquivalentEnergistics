package com.mordenkainen.equivalentenergistics.integration.ee3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import com.mojang.authlib.GameProfile;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafterBase;
import com.mordenkainen.equivalentenergistics.integration.IEMCHandler;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.crafting.EMCCraftingGrid;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCell;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCrystal;
import com.mordenkainen.equivalentenergistics.items.ItemEnum;
import com.pahimar.ee3.api.event.EnergyValueEvent;
import com.pahimar.ee3.api.event.PlayerKnowledgeEvent;
import com.pahimar.ee3.api.exchange.EnergyValue;
import com.pahimar.ee3.api.exchange.EnergyValueRegistryProxy;
import com.pahimar.ee3.api.knowledge.PlayerKnowledgeRegistryProxy;
import com.pahimar.ee3.util.ItemStackUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class EquivExchange3 implements IEMCHandler {

    private static Item tomeItem;

    @Override
    public boolean hasEMC(final ItemStack itemStack) {
        return EnergyValueRegistryProxy.hasEnergyValue(itemStack);
    }

    @Override
    public double getEnergyValue(final ItemStack itemStack) {
        final EnergyValue val = EnergyValueRegistryProxy.getEnergyValue(itemStack);
        return val == null ? 0.0F : val.getValue();
    }

    @Override
    public double getCrystalEMC(final int tier) {
        return ItemEMCCrystal.CRYSTAL_VALUES[tier];
    }

    @Override
    public List<ItemStack> getTransmutations(final TileEMCCrafterBase tile) {
        List<ItemStack> transmutations;

        transmutations = new ArrayList<ItemStack>(
                PlayerKnowledgeRegistryProxy.getKnownItemStacks(ItemStackUtils.getOwnerName(tile.getCurrentTome())));

        final Iterator<ItemStack> iter = transmutations.iterator();
        while (iter.hasNext()) {
            final ItemStack currentItem = iter.next();
            if (currentItem == null || ItemEnum.isCrystal(currentItem)) {
                iter.remove();
            }
        }
        return transmutations;
    }

    @Override
    public boolean isValidTome(final ItemStack itemStack) {
        if (tomeItem == null) {
            tomeItem = GameRegistry.findItem("EE3", "alchenomicon");
        }
        return itemStack != null && itemStack.getItem() == tomeItem && ItemStackUtils.getOwnerUUID(itemStack) != null;
    }

    @Override
    public void setCrystalEMC() {}

    @Override
    public UUID getTomeUUID(final ItemStack currentTome) {
        return ItemStackUtils.getOwnerUUID(currentTome);
    }

    @Override
    public String getTomeOwner(final ItemStack currentTome) {
        return ItemStackUtils.getOwnerName(currentTome);
    }

    @Override
    public double getSingleEnergyValue(final ItemStack stack) {
        final ItemStack singleStack = stack.copy();
        singleStack.stackSize = 1;
        return getEnergyValue(singleStack);
    }

    @SubscribeEvent
    public void onPlayerKnowledgeChange(final PlayerKnowledgeEvent event) {
        EMCCraftingGrid.knowledgeEvent(event.playerUUID);
    }

    @SubscribeEvent
    public void onEnergyValueChange(final EnergyValueEvent event) {
        EMCCraftingGrid.energyEvent();
    }

    @Override
    public boolean isEMCStorage(final ItemStack stack) {
        return ItemEnum.EMCCELL.isSameItem(stack);
    }

    @Override
    public double getStoredEMC(final ItemStack stack) {
        return ItemEnum.EMCCELL.isSameItem(stack) ? ((ItemEMCCell) ItemEnum.EMCCELL.getItem()).getStoredCellEMC(stack)
                : 0;
    }

    @Override
    public double extractEMC(final ItemStack stack, final double toStore) {
        return ItemEnum.EMCCELL.isSameItem(stack)
                ? ((ItemEMCCell) ItemEnum.EMCCELL.getItem()).extractCellEMC(stack, toStore)
                : 0;
    }

    public static void postPlayerLearn(final String player) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer() && player != null) {
            final GameProfile profile = MinecraftServer.getServer().func_152358_ax().func_152655_a(player);
            if (profile != null) {
                MinecraftForge.EVENT_BUS
                        .post(new PlayerKnowledgeEvent.PlayerLearnKnowledgeEvent(profile.getId(), null));
            }
        }
    }

    public static void postPlayerForget(final String player) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer() && player != null) {
            final GameProfile profile = MinecraftServer.getServer().func_152358_ax().func_152655_a(player);
            if (profile != null) {
                MinecraftForge.EVENT_BUS
                        .post(new PlayerKnowledgeEvent.PlayerForgetKnowledgeEvent(profile.getId(), null));
            }
        }
    }

}
