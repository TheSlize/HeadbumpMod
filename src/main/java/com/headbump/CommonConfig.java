package com.headbump;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class CommonConfig {

    public static boolean createConfigBool(Configuration config, String category, String name, String comment, boolean def) {

        Property prop = config.get(category, name, def);
        prop.setComment(comment);
        return prop.getBoolean();
    }

    public static boolean useArmor = false;

    public static void loadFromConfig(Configuration config) {
        useArmor = createConfigBool(config, "General", "useArmor", "This is a module (turned off by default). If set to true - wearing any helmet will prevent you from bumping your head.", false);
    }
}
