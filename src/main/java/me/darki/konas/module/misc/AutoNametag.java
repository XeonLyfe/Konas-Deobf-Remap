package me.darki.konas.module.misc;

import cookiedragon.eventsystem.Subscriber;
import java.util.Comparator;

import me.darki.konas.event.events.UpdateEvent;
import me.darki.konas.module.Category;
import me.darki.konas.module.Module;
import me.darki.konas.module.client.NewGui;
import me.darki.konas.setting.Setting;
import me.darki.konas.unremaped.Class496;
import me.darki.konas.unremaped.Class50;
import me.darki.konas.unremaped.Class566;
import me.darki.konas.util.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Items;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;

public class AutoNametag
extends Module {
    public static Setting<Float> range = new Setting<>("Range", Float.valueOf(4.3f), Float.valueOf(6.0f), Float.valueOf(0.5f), Float.valueOf(0.1f));
    public static Setting<Boolean> rotate = new Setting<>("Rotate", true);
    public static Setting<Boolean> ignoreNamed = new Setting<>("IgnoreNamed", true);
    public static Setting<Boolean> autoSwitch = new Setting<>("AutoSwitch", true);
    public static Setting<Boolean> withers = new Setting<>("Withers", true);
    public static Setting<Boolean> mobs = new Setting<>("Mobs", false);
    public static Setting<Boolean> animals = new Setting<>("Animals", false);
    public Class566 Field1928 = new Class566();
    public float Field1929;
    public float Field1930;
    public EntityLivingBase Field1931 = null;
    public int Field1932;

    public static Float Method1800(EntityLivingBase entityLivingBase) {
        return Float.valueOf(AutoNametag.mc.player.getDistance(entityLivingBase));
    }

    public boolean Method394() {
        return AutoNametag.mc.player.getHeldItemOffhand().getItem() == Items.NAME_TAG;
    }

    public boolean Method513(Entity entity) {
        if (!entity.getCustomNameTag().isEmpty() && ignoreNamed.getValue().booleanValue()) {
            return false;
        }
        if (animals.getValue().booleanValue() && entity instanceof EntityAnimal) {
            return true;
        }
        if (mobs.getValue().booleanValue() && entity instanceof IMob) {
            return true;
        }
        return withers.getValue() != false && entity instanceof EntityWither;
    }

    @Subscriber
    public void Method123(Class50 class50) {
        block1: {
            if (this.Field1931 != null) {
                AutoNametag.mc.player.connection.sendPacket(new CPacketUseEntity(this.Field1931, this.Method394() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
            }
            if (this.Field1932 == AutoNametag.mc.player.inventory.currentItem) break block1;
            AutoNametag.mc.player.inventory.currentItem = this.Field1932;
            AutoNametag.mc.playerController.updateController();
        }
    }

    public static EntityLivingBase Method656(Entity entity) {
        return (EntityLivingBase)entity;
    }

    public AutoNametag() {
        super("AutoNametag", "Automatically right clicks entities to name them", Category.MISC);
    }

    @Subscriber(priority=3)
    public void Method135(UpdateEvent updateEvent) {
        block7: {
            ItemStack itemStack;
            this.Field1931 = null;
            if (updateEvent.isCanceled() || !Class496.Method1966()) {
                return;
            }
            this.Field1932 = AutoNametag.mc.player.inventory.currentItem;
            if (!(AutoNametag.mc.player.getHeldItemMainhand().getItem() instanceof ItemNameTag) && !this.Method394()) {
                int n = -1;
                if (autoSwitch.getValue().booleanValue()) {
                    for (int i = 0; i < 9; ++i) {
                        ItemStack itemStack2 = AutoNametag.mc.player.inventory.getStackInSlot(i);
                        if (itemStack2.isEmpty() || !(itemStack2.getItem() instanceof ItemNameTag) || !itemStack2.hasDisplayName()) continue;
                        AutoNametag.mc.player.inventory.currentItem = n = i;
                        AutoNametag.mc.playerController.updateController();
                        break;
                    }
                }
                if (n == -1) {
                    return;
                }
            }
            ItemStack itemStack3 = itemStack = this.Method394() ? AutoNametag.mc.player.getHeldItemOffhand() : AutoNametag.mc.player.getHeldItemMainhand();
            if (!itemStack.hasDisplayName()) {
                return;
            }
            this.Field1931 = AutoNametag.mc.world.loadedEntityList.stream().filter(this::Method513).map(AutoNametag::Method656).min(Comparator.comparing(AutoNametag::Method1800)).orElse(null);
            if (this.Field1931 != null && rotate.getValue().booleanValue()) {
                double[] dArray = PlayerUtil.Method1088(this.Field1931.posX, this.Field1931.posY, this.Field1931.posZ, AutoNametag.mc.player);
                this.Field1929 = (float)dArray[0];
                this.Field1930 = (float)dArray[1];
                this.Field1928.Method739();
            }
            if (!rotate.getValue().booleanValue() || this.Field1928.Method737(350.0)) break block7;
            NewGui.INSTANCE.Field1139.Method1937(this.Field1929, this.Field1930);
        }
    }
}