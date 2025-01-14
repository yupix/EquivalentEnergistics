package com.mordenkainen.equivalentenergistics.integration.ae2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.oredict.OreDictionary;

import appeng.api.AEApi;
import appeng.api.storage.ICellHandler;

import com.mordenkainen.equivalentenergistics.integration.ae2.cache.crafting.EMCCraftingGrid;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.crafting.IEMCCraftingGrid;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.storage.EMCStorageGrid;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.storage.IEMCStorageGrid;
import com.mordenkainen.equivalentenergistics.items.ItemEnum;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class AppliedEnergistics2 {

    private static AppliedEnergistics2 instance;

    private AppliedEnergistics2() {}

    public static void init() {
        instance = new AppliedEnergistics2();
        MinecraftForge.EVENT_BUS.register(instance);
        AEApi.instance().registries().gridCache().registerGridCache(IEMCStorageGrid.class, EMCStorageGrid.class);
        AEApi.instance().registries().gridCache().registerGridCache(IEMCCraftingGrid.class, EMCCraftingGrid.class);
        AEApi.instance().registries().cell().addCellHandler((ICellHandler) ItemEnum.EMCCELL.getItem());
        AEApi.instance().registries().cell().addCellHandler((ICellHandler) ItemEnum.EMCCELLCREATIVE.getItem());
    }

    @SubscribeEvent
    public void worldLoad(final WorldEvent.Load event) {
        try {
            final Class<?> cellInv = Class.forName("appeng.me.storage.CellInventory");
            final Method blackList = cellInv.getDeclaredMethod("addBasicBlackList", int.class, int.class);
            blackList.invoke(null, Item.getIdFromItem(ItemEnum.EMCCRYSTAL.getItem()), OreDictionary.WILDCARD_VALUE);
            blackList.invoke(null, Item.getIdFromItem(ItemEnum.EMCCRYSTALOLD.getItem()), OreDictionary.WILDCARD_VALUE);
        } catch (final IllegalArgumentException | IllegalAccessException | InvocationTargetException
                | ClassNotFoundException | NoSuchMethodException | SecurityException e) {
            CommonUtils.debugLog("Failed to blacklist EMC Crystals from AE Cells", e);
        }
    }

}
