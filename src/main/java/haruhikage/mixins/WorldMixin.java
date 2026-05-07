package haruhikage.mixins;

import carpet.CarpetServer;
import carpet.utils.Messenger;
import haruhikage.HaruhikageAddonSettings;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

@Mixin(World.class)
public abstract class WorldMixin {

    @Inject(method = "tickBlockNow", at = @At("RETURN"))
    public void printStackTraceOnIttEnd(BlockPos pos, BlockState state, Random random, CallbackInfo ci) {
        if (HaruhikageAddonSettings.logIttEnd) {
            Messenger.print_server_message(CarpetServer.minecraftServer, Arrays.stream(
                Thread.currentThread().getStackTrace()).skip(2).collect(Collectors.toList()).toString()
            );
        }
    }

    @Shadow
    public abstract WorldChunk getChunk(BlockPos pos);

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;I)Z",
        at = @At("HEAD")
    )
    public void printSetBlockState(
        BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<Boolean> cir
    ) {
        WorldChunk worldChunk = this.getChunk(pos);
        BlockState originalState = worldChunk.getBlockState(pos);
        if (((World) (Object) this) instanceof ServerWorld && pos.getX() == HaruhikageAddonSettings.logX
            && pos.getY() == HaruhikageAddonSettings.logY && pos.getZ() == HaruhikageAddonSettings.logZ) {
            System.out.printf("%s -> %s\n", originalState, state);
        } else {
            //System.out.println(pos);
        }
    }
}
