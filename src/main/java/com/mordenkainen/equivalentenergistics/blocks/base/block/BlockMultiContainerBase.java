package com.mordenkainen.equivalentenergistics.blocks.base.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BlockMultiContainerBase extends BlockContainerBase {

    protected int numBlocks = 1;

    public BlockMultiContainerBase(final Material material, final int numBlocks) {
        super(material);
        this.numBlocks = numBlocks;
    }

    @Override
    public int damageDropped(final int meta) {
        return meta;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final Item item, final CreativeTabs tab, final List list) {
        for (int i = 0; i < numBlocks; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

}
