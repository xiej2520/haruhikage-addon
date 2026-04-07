package haruhikage.mixins;

import carpet.CarpetServer;
import carpet.utils.Messenger;
import haruhikage.HaruhikageAddonSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.NetherChunkGenerator;
import net.minecraft.world.gen.feature.LiquidPocketFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(NetherChunkGenerator.class)
public class NetherChunkGeneratorMixin {

    @Shadow private final Random random;

    public NetherChunkGeneratorMixin(Random random) {
        this.random = random;
    }

    @Unique private int unexposedLavaPocketSuccesses = 0;
    @Unique private int exposableLavaPocketSuccesses = 0;

    @Inject(
        method = "populateChunk",
        at = @At("HEAD")
    )
    public void seedRngAndResetCount(int chunkX, int chunkZ, CallbackInfo ci) {
        if (HaruhikageAddonSettings.logLiquidPocketPopulation) {
            unexposedLavaPocketSuccesses = 0;
            exposableLavaPocketSuccesses = 0;
        }
    }

    @Redirect(
        method = "populateChunk",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/gen/feature/LiquidPocketFeature;place(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;)Z"
        )
    )
    private boolean redirectLavaPocketPlace(
        LiquidPocketFeature instance, World world, Random random, BlockPos pos
    ) {
        boolean result = instance.place(world, random, pos);
        if (HaruhikageAddonSettings.logLiquidPocketPopulation) {
            if (result) {
                // canBeExposedToAir should really be called cannotBeExposedToAir, flip these
                if (instance.canBeExposedToAir) {
                    unexposedLavaPocketSuccesses++;
                } else {
                    exposableLavaPocketSuccesses++;
                }
            }
        }
        return result;
    }

    @Inject(method = "populateChunk", at = @At("TAIL"))
    private void logResults(int chunkX, int chunkZ, CallbackInfo ci) {
        if (HaruhikageAddonSettings.logLiquidPocketPopulation) {
            // canBeExposedToAir should really be called cannotBeExposedToAir, flip these
            Messenger.print_server_message(CarpetServer.minecraftServer, String.format(
                "exposable liquid pocket successes c(%d %d): %d", chunkX, chunkZ, exposableLavaPocketSuccesses)
            );
            Messenger.print_server_message(CarpetServer.minecraftServer, String.format(
                "unexposed liquid pocket successes c(%d %d): %d", chunkX, chunkZ, unexposedLavaPocketSuccesses)
            );
        }
    }
}
