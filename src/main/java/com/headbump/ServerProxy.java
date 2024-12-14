package com.headbump;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class ServerProxy {

    public File getDataDir(){
        return FMLCommonHandler.instance().getMinecraftServerInstance().getDataDirectory();
    }
}
