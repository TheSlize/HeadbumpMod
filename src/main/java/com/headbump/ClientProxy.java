package com.headbump;

import net.minecraft.client.Minecraft;

import java.io.File;

public class ClientProxy extends ServerProxy{
    @Override
    public File getDataDir() {
        return Minecraft.getMinecraft().gameDir;
    }
}
