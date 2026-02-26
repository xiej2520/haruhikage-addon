package haruhikage.mixins;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.IceBiome;
import net.minecraft.world.gen.feature.IceSpikeFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(IceBiome.class)
public abstract class IceBiomeMixin {

    @Shadow
    @Final
    private boolean spikes;

    @Shadow
    @Final
    private IceSpikeFeature iceSpike;

    @Inject(method = "decorate", at = @At("HEAD"))
    public void placeAll(World world, Random random, BlockPos pos, CallbackInfo ci) {
        if (this.spikes) {
            for (int x = 0; x < 16; x++) {
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        this.iceSpike.place(world, random, world.getHeight(pos.add(i + 8, 0, j + 8)));
                    }
                }
            }
        }

    }
}
