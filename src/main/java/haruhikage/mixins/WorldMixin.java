package haruhikage.mixins;

import carpet.CarpetServer;
import carpet.utils.Messenger;
import haruhikage.HaruhikageAddonSettings;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

@Mixin(World.class)
public class WorldMixin {

    @Inject(method = "tickBlockNow", at = @At("RETURN"))
    public void printStackTraceOnIttEnd(BlockPos pos, BlockState state, Random random, CallbackInfo ci) {
        if (HaruhikageAddonSettings.logIttEnd) {
            Messenger.print_server_message(CarpetServer.minecraftServer, Arrays.stream(
                Thread.currentThread().getStackTrace()).skip(2).collect(Collectors.toList()).toString()
            );
        }
    }
}
