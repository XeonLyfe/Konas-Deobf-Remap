package me.darki.konas.unremaped;

import cookiedragon.eventsystem.Subscriber;
import me.darki.konas.event.events.PacketEvent;
import me.darki.konas.event.events.PushOutOfBlocksEvent;
import me.darki.konas.event.events.TickEvent;
import me.darki.konas.mixin.mixins.IEntityPlayerSP;
import me.darki.konas.mixin.mixins.ISPacketPlayerPosLook;
import me.darki.konas.module.Category;
import me.darki.konas.module.Module;
import me.darki.konas.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class RubberFill
extends Module {
    public Setting<Boolean> rotate = new Setting<>("Rotate", true);
    public Setting<Boolean> swing = new Setting<>("Swing", true);
    public Setting<Boolean> strict = new Setting<>("Strict", false);
    public Setting<Boolean> skulls = new Setting<>("Skulls", true);
    public static Setting<Class443> customBlocks = new Setting<>("CustomBlocks", new Class443(new String[0]));
    public static Setting<Class348> filter = new Setting<>("Filter", Class348.NONE);
    public Class346 Field294 = Class346.WAITING;
    public Class566 Field295 = new Class566();

    @Subscriber
    public void Method461(PushOutOfBlocksEvent pushOutOfBlocksEvent) {
        pushOutOfBlocksEvent.setCanceled(true);
    }

    @Override
    public void onEnable() {
        if (RubberFill.mc.player == null || RubberFill.mc.world == null) {
            this.toggle();
            return;
        }
        if (!RubberFill.mc.player.onGround) {
            this.toggle();
            return;
        }
        this.Field294 = Class346.WAITING;
    }

    @Subscriber
    public void Method131(PacketEvent packetEvent) {
        block1: {
            if (RubberFill.mc.currentScreen instanceof GuiDownloadTerrain) {
                this.toggle();
                return;
            }
            if (!(packetEvent.getPacket() instanceof SPacketPlayerPosLook) || ((Boolean)this.strict.getValue()).booleanValue()) break block1;
            ((ISPacketPlayerPosLook) packetEvent.getPacket()).Method40(RubberFill.mc.player.rotationYaw);
            ((ISPacketPlayerPosLook) packetEvent.getPacket()).Method41(RubberFill.mc.player.rotationPitch);
        }
    }

    public RubberFill() {
        super("RubberFill", "Fills your own hole", Category.EXPLOIT, "Burrow", "SelfFill");
    }

    @Subscriber
    public void Method462(TickEvent tickEvent) {
        if (RubberFill.mc.player == null || RubberFill.mc.world == null) {
            return;
        }
        if (this.Field294 == Class346.DISABLING) {
            if (this.Field295.Method737(500.0)) {
                this.toggle();
            }
            return;
        }
        if (!RubberFill.mc.player.onGround) {
            this.toggle();
            return;
        }
        if (RubberFill.mc.world.getBlockState(new BlockPos((Entity) RubberFill.mc.player)).getBlock() == Blocks.AIR) {
            if (((Boolean)this.skulls.getValue()).booleanValue() && RubberFill.mc.world.getBlockState(new BlockPos((Entity) RubberFill.mc.player).up(2)).getBlock() != Blocks.AIR) {
                if (this.getBlockInHotbar() == -1) {
                    this.toggle();
                    return;
                }
                BlockPos blockPos = new BlockPos(RubberFill.mc.player.posX, RubberFill.mc.player.posY, RubberFill.mc.player.posZ);
                BlockPos blockPos2 = blockPos.down();
                EnumFacing enumFacing = EnumFacing.UP;
                Vec3d vec3d = new Vec3d((Vec3i)blockPos2).add(0.5, 0.5, 0.5).add(new Vec3d(enumFacing.getDirectionVec()).scale(0.5));
                if (((Boolean)this.rotate.getValue()).booleanValue()) {
                    if (((IEntityPlayerSP) RubberFill.mc.player).Method240() < 0.0f) {
                        RubberFill.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(RubberFill.mc.player.rotationYaw, 0.0f, true));
                    }
                    RubberFill.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(RubberFill.mc.player.posX, RubberFill.mc.player.posY, RubberFill.mc.player.posZ, RubberFill.mc.player.rotationYaw, 90.0f, true));
                    ((IEntityPlayerSP) RubberFill.mc.player).Method233(RubberFill.mc.player.posY + 1.16);
                    ((IEntityPlayerSP) RubberFill.mc.player).Method239(90.0f);
                }
                float f = (float)(vec3d.x - (double)blockPos.getX());
                float f2 = (float)(vec3d.y - (double)blockPos.getY());
                float f3 = (float)(vec3d.z - (double)blockPos.getZ());
                boolean bl = RubberFill.mc.player.inventory.currentItem != this.getBlockInHotbar();
                int n = RubberFill.mc.player.inventory.currentItem;
                if (bl) {
                    RubberFill.mc.player.inventory.currentItem = this.getBlockInHotbar();
                    RubberFill.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.getBlockInHotbar()));
                }
                RubberFill.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(blockPos2, enumFacing, EnumHand.MAIN_HAND, f, f2, f3));
                if (((Boolean)this.swing.getValue()).booleanValue()) {
                    RubberFill.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
                }
                if (bl) {
                    RubberFill.mc.player.inventory.currentItem = n;
                    RubberFill.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(n));
                }
                this.Field295.Method739();
                this.Field294 = Class346.DISABLING;
                return;
            }
            if (this.Method464() == -1) {
                this.toggle();
                return;
            }
            BlockPos blockPos = new BlockPos(RubberFill.mc.player.posX, RubberFill.mc.player.posY, RubberFill.mc.player.posZ);
            BlockPos blockPos3 = blockPos.down();
            EnumFacing enumFacing = EnumFacing.UP;
            Vec3d vec3d = new Vec3d((Vec3i)blockPos3).add(0.5, 0.5, 0.5).add(new Vec3d(enumFacing.getDirectionVec()).scale(0.5));
            if (((Boolean)this.rotate.getValue()).booleanValue()) {
                if (((IEntityPlayerSP) RubberFill.mc.player).Method240() < 0.0f) {
                    RubberFill.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(RubberFill.mc.player.rotationYaw, 0.0f, true));
                }
                RubberFill.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(RubberFill.mc.player.posX, RubberFill.mc.player.posY, RubberFill.mc.player.posZ, RubberFill.mc.player.rotationYaw, 90.0f, true));
                ((IEntityPlayerSP) RubberFill.mc.player).Method233(RubberFill.mc.player.posY + 1.16);
                ((IEntityPlayerSP) RubberFill.mc.player).Method239(90.0f);
            }
            RubberFill.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(RubberFill.mc.player.posX, RubberFill.mc.player.posY + 0.42, RubberFill.mc.player.posZ, false));
            RubberFill.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(RubberFill.mc.player.posX, RubberFill.mc.player.posY + 0.75, RubberFill.mc.player.posZ, false));
            RubberFill.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(RubberFill.mc.player.posX, RubberFill.mc.player.posY + 1.01, RubberFill.mc.player.posZ, false));
            RubberFill.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(RubberFill.mc.player.posX, RubberFill.mc.player.posY + 1.16, RubberFill.mc.player.posZ, false));
            if (mc.getCurrentServerData() != null && RubberFill.mc.getCurrentServerData().serverIP.toLowerCase().contains("crystalpvp")) {
                RubberFill.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(RubberFill.mc.player.posX, RubberFill.mc.player.posY + 1.16, RubberFill.mc.player.posZ, false));
                RubberFill.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(RubberFill.mc.player.posX, RubberFill.mc.player.posY + 1.24, RubberFill.mc.player.posZ, false));
            }
            float f = (float)(vec3d.x - (double)blockPos.getX());
            float f4 = (float)(vec3d.y - (double)blockPos.getY());
            float f5 = (float)(vec3d.z - (double)blockPos.getZ());
            boolean bl = RubberFill.mc.player.inventory.currentItem != this.Method464();
            int n = RubberFill.mc.player.inventory.currentItem;
            if (bl) {
                RubberFill.mc.player.inventory.currentItem = this.Method464();
                RubberFill.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.Method464()));
            }
            RubberFill.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(blockPos3, enumFacing, EnumHand.MAIN_HAND, f, f4, f5));
            if (((Boolean)this.swing.getValue()).booleanValue()) {
                RubberFill.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
            }
            if (bl) {
                RubberFill.mc.player.inventory.currentItem = n;
                RubberFill.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(n));
            }
            RubberFill.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(RubberFill.mc.player.posX, this.Method463(), RubberFill.mc.player.posZ, false));
            this.Field295.Method739();
            this.Field294 = Class346.DISABLING;
        } else {
            this.toggle();
        }
    }

    public double Method463() {
        BlockPos blockPos;
        if (mc.getCurrentServerData() != null) {
            if (RubberFill.mc.getCurrentServerData().serverIP.toLowerCase().contains("crystalpvp") || RubberFill.mc.getCurrentServerData().serverIP.toLowerCase().contains("2b2t")) {
                blockPos = new BlockPos(RubberFill.mc.player.posX, RubberFill.mc.player.posY + 2.34, RubberFill.mc.player.posZ);
                if (RubberFill.mc.world.getBlockState(blockPos).getBlock() instanceof BlockAir && RubberFill.mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir) {
                    return RubberFill.mc.player.posY + 2.34;
                }
            } else {
                if (RubberFill.mc.getCurrentServerData().serverIP.toLowerCase().contains("endcrystal")) {
                    if (RubberFill.mc.world.getBlockState(new BlockPos(RubberFill.mc.player.posX, RubberFill.mc.player.posY + 4.0, RubberFill.mc.player.posZ)).getBlock() instanceof BlockAir) {
                        return RubberFill.mc.player.posY + 4.0;
                    }
                    return RubberFill.mc.player.posY + 3.0;
                }
                if (RubberFill.mc.getCurrentServerData().serverIP.toLowerCase().contains("netheranarchy")) {
                    if (RubberFill.mc.world.getBlockState(new BlockPos(RubberFill.mc.player.posX, RubberFill.mc.player.posY + 8.5, RubberFill.mc.player.posZ)).getBlock() instanceof BlockAir) {
                        return RubberFill.mc.player.posY + 8.5;
                    }
                    return RubberFill.mc.player.posY + 9.5;
                }
                if (RubberFill.mc.getCurrentServerData().serverIP.toLowerCase().contains("9b9t")) {
                    BlockPos blockPos2 = new BlockPos(RubberFill.mc.player.posX, RubberFill.mc.player.posY + 9.0, RubberFill.mc.player.posZ);
                    if (RubberFill.mc.world.getBlockState(blockPos2).getBlock() instanceof BlockAir && RubberFill.mc.world.getBlockState(blockPos2.up()).getBlock() instanceof BlockAir) {
                        return RubberFill.mc.player.posY + 9.0;
                    }
                    for (int i = 10; i < 20; ++i) {
                        BlockPos blockPos3 = new BlockPos(RubberFill.mc.player.posX, RubberFill.mc.player.posY + (double)i, RubberFill.mc.player.posZ);
                        if (!(RubberFill.mc.world.getBlockState(blockPos3).getBlock() instanceof BlockAir) || !(RubberFill.mc.world.getBlockState(blockPos3.up()).getBlock() instanceof BlockAir)) continue;
                        return RubberFill.mc.player.posY + (double)i;
                    }
                    return RubberFill.mc.player.posY + 20.0;
                }
            }
        }
        if (RubberFill.mc.world.getBlockState(blockPos = new BlockPos(RubberFill.mc.player.posX, RubberFill.mc.player.posY - 9.0, RubberFill.mc.player.posZ)).getBlock() instanceof BlockAir && RubberFill.mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir) {
            return RubberFill.mc.player.posY - 9.0;
        }
        for (int i = -10; i > -20; --i) {
            BlockPos blockPos4 = new BlockPos(RubberFill.mc.player.posX, RubberFill.mc.player.posY - (double)i, RubberFill.mc.player.posZ);
            if (!(RubberFill.mc.world.getBlockState(blockPos4).getBlock() instanceof BlockAir) || !(RubberFill.mc.world.getBlockState(blockPos4.up()).getBlock() instanceof BlockAir)) continue;
            return RubberFill.mc.player.posY - (double)i;
        }
        return RubberFill.mc.player.posY - 24.0;
    }

    public int Method464() {
        int n = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = RubberFill.mc.player.inventory.getStackInSlot(i);
            if (itemStack == ItemStack.EMPTY || !(itemStack.getItem() instanceof ItemBlock)) continue;
            Block block = ((ItemBlock)itemStack.getItem()).getBlock();
            if (filter.getValue() == Class348.BLACKLIST ? ((Class443)customBlocks.getValue()).Method682().contains(block) : filter.getValue() == Class348.WHITELIST && !((Class443)customBlocks.getValue()).Method682().contains(block)) continue;
            n = i;
            break;
        }
        return n;
    }

    public int getBlockInHotbar() {
        int n = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = RubberFill.mc.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem() instanceof ItemSkull)) continue;
            n = i;
            break;
        }
        return n;
    }
}