// TODO fix config handling
package com.mordenkainen.equivalentenergistics.core.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import com.mordenkainen.equivalentenergistics.blocks.BlockEnum;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.items.ItemEnum;

public final class ConfigManager {

    public static Configuration config;
    public static boolean useEE3;
    public static boolean debug;
    public static double maxStackEMC = 131072;

    private ConfigManager() {}

    public static void init(final File file) {
        config = new Configuration(file);

        config.load();

        Integration.loadConfig(config);

        BlockEnum.loadConfig(config);

        ItemEnum.loadConfig(config);

        if (Integration.Mods.EE3.isEnabled()) {
            useEE3 = true;
        }
        useEE3 = config.get("General", "UseEE3", useEE3).getBoolean(useEE3);

        debug = config.get("General", "Debug", debug).getBoolean(false);

        maxStackEMC = config.get("General", "MaxEMCForAStack", maxStackEMC).getDouble(maxStackEMC);

        if (config.hasChanged()) {
            config.save();
        }
    }

}
