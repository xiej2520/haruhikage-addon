package haruhikage.mixins;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IceSpikeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(IceSpikeFeature.class)
public abstract class IceSpikeFeatureMixin extends Feature {

    @Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;up(I)Lnet/minecraft/util/math/BlockPos;"))
    public void placeColumn(World world, Random random, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockPos pos2 = new BlockPos(pos.getX(), 255, pos.getZ());

        while (pos2.getY() > 50) {
            this.setBlockState(world, pos2, Blocks.PACKED_ICE.defaultState());
            pos2 = pos2.down();
        }
    }

}
