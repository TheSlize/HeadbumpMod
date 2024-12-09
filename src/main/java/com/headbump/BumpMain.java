package com.headbump;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.common.network.MessagePlayHurtSound;
import ichttt.mods.firstaid.common.network.MessageSyncDamageModel;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

@Mod(
        modid = BumpMain.MOD_ID,
        name = BumpMain.MOD_NAME,
        version = BumpMain.VERSION
)
public class BumpMain {

    public static final String MOD_ID = "headbump";
    public static final String MOD_NAME = "Headbump Mod";
    public static final String VERSION = "1.12.2-1.0";


    @Mod.Instance(MOD_ID)
    public static BumpMain INSTANCE;


    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {

    }


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerJump(TickEvent.PlayerTickEvent event) {
        if (!event.player.world.isRemote && event.phase == TickEvent.Phase.END) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            if (player.motionY != -0.0784000015258789) {
                BlockPos bPos = new BlockPos(player.getPosition().getX(), player.getEntityBoundingBox().maxY + 0.2, player.getPosition().getZ());
                if (!player.world.getBlockState(bPos).getMaterial().isLiquid() && !player.world.getBlockState(bPos).getBlock().isAir(player.world.getBlockState(bPos), player.world, bPos)) {
                    float damage = 1.0f;
                    applyHeadDamage(player, damage);
                }
            }
        }
    }

    private void applyHeadDamage(EntityPlayerMP player, float damage) {
        if (isFirstAidLoaded()) {
            applyFirstAidHeadDamage(player, damage);
        } else {
            player.attackEntityFrom(DamageSource.FALL, damage);
        }
    }

    @Optional.Method(modid = "firstaid")
    private void applyFirstAidHeadDamage(EntityPlayerMP player, float damage) {
        AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel) Objects.requireNonNull(player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null));
        try{
            if(FMLCommonHandler.instance().getMinecraftServerInstance() != null) {
                Field invulnerabilityField = ObfuscationReflectionHelper.findField(EntityPlayerMP.class, "field_147101_bU");
                invulnerabilityField.setAccessible(true);
                int invulnerabilityTicks = invulnerabilityField.getInt(player);
                if (!player.isInvulnerableDimensionChange() && invulnerabilityTicks <= 0 && !player.capabilities.isCreativeMode && player.hurtResistantTime <= 0) {
                    damageModel.HEAD.damage(damage, player, true, 0.0f);
                    if(damageModel.HEAD.currentHealth <= 0.0f) {
                        player.attackEntityFrom(DamageSource.FALL, 10000.0f);
                    } else {
                        player.attackEntityFrom(DamageSource.ANVIL, 0.0001f);
                    }
                    FirstAid.NETWORKING.sendTo(new MessageSyncDamageModel(damageModel, false), player);
                }
            }
        } catch (IllegalAccessException e) {
        }
    }

    private boolean isFirstAidLoaded() {
        return net.minecraftforge.fml.common.Loader.isModLoaded("firstaid");
    }
}
