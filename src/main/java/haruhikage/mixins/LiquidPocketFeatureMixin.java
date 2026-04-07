package haruhikage.mixins;

import carpet.CarpetServer;
import carpet.utils.Messenger;
import haruhikage.HaruhikageAddonSettings;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.LiquidPocketFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(LiquidPocketFeature.class)
public class LiquidPocketFeatureMixin {
    @Shadow
    @Final
    private boolean canBeExposedToAir;

    @Inject(
        method = "place",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;I)Z"
        ),
        cancellable = true)
    public void placeWater(World world, Random random, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (HaruhikageAddonSettings.logLiquidPocketPopulation) {
            // canBeExposedToAir should really be called cannotBeExposedToAir
            if (this.canBeExposedToAir) {
                Messenger.print_server_message(CarpetServer.minecraftServer,
                    String.format("unexposed liquid pocket success c(%d %d), %d %d %d",
                        pos.getX() >> 4, pos.getZ() >> 4,
                        pos.getX(), pos.getY(), pos.getZ())
                );
            } else {
                Messenger.print_server_message(CarpetServer.minecraftServer,
                    String.format("exposable liquid pocket success c(%d %d), %d %d %d",
                        pos.getX() >> 4, pos.getZ() >> 4,
                        pos.getX(), pos.getY(), pos.getZ())
                );
            }
        }
        if (HaruhikageAddonSettings.placeWaterInsteadOfLavaPockets) {
            if (!this.canBeExposedToAir) {
                world.setBlockState(pos, Blocks.FLOWING_WATER.defaultState(), 2);
                world.tickBlockNow(pos, Blocks.FLOWING_WATER.defaultState(), random);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "place", at = @At("HEAD"))
    public void logPlacementAttempt(World world, Random random, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (HaruhikageAddonSettings.logLiquidPocketPopulation) {
            // canBeExposedToAir should really be called cannotBeExposedToAir
            if (this.canBeExposedToAir) {
                Messenger.print_server_message(CarpetServer.minecraftServer,
                    String.format("unexposed liquid pocket attempt c(%d %d), %d %d %d",
                        pos.getX() >> 4, pos.getZ() >> 4,
                        pos.getX(), pos.getY(), pos.getZ())
                );
            } else {
                Messenger.print_server_message(CarpetServer.minecraftServer,
                    String.format("exposable liquid pocket attempt c(%d %d), %d %d %d",
                        pos.getX() >> 4, pos.getZ() >> 4,
                        pos.getX(), pos.getY(), pos.getZ())
                );

            }
        }
    }
}
