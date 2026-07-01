package haruhikage.mixins;

import carpet.CarpetServer;
import carpet.utils.Messenger;
import haruhikage.HaruhikageAddonSettings;
import haruhikage.command.ChunkTrackCommand;
import net.minecraft.server.world.chunk.ServerChunkCache;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin {

    @Unique
    private WorldChunk unloadedChunk;

    // Unload phase tracking
    @Inject(method = "tick()Z", at = @At("HEAD"))
    private void startUnloadTimer(CallbackInfoReturnable<Boolean> cir) {

        if (HaruhikageAddonSettings.logCertainTickPhases) {
            HaruhikageAddonSettings.LOGGER.info("Starting Unload Phase. Global timer: {}", System.nanoTime());
        }

    }

    @ModifyVariable(method = "tick()Z", at = @At(value = "STORE", ordinal = 0))
    private WorldChunk unloadedChunkCapture(WorldChunk chunk) {
        this.unloadedChunk = chunk;
        return chunk;
    }

    @Inject(method = "tick()Z", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;remove()V", shift = At.Shift.AFTER))
    private void checkForUnloadChunk(CallbackInfoReturnable<Boolean> cir) {
        if (HaruhikageAddonSettings.logCertainTickPhases && this.unloadedChunk.chunkX == HaruhikageAddonSettings.unloadChunkX && this.unloadedChunk.chunkZ == HaruhikageAddonSettings.unloadChunkZ) {
            Messenger.print_server_message(CarpetServer.minecraftServer, String.format("Unload Chunk %d %d has been unloaded. Global timer: %d", this.unloadedChunk.chunkX, this.unloadedChunk.chunkZ, System.nanoTime()));
        }

        // Chunk Tracking - Unloading Events
        if (HaruhikageAddonSettings.loadedChunksCommand && !ChunkTrackCommand.chunks.isEmpty()) {
            for(ChunkPos pos : ChunkTrackCommand.chunks) {
                if(this.unloadedChunk != null) {
                    if (pos.x == unloadedChunk.chunkX && pos.z == unloadedChunk.chunkZ) {
                        HaruhikageAddonSettings.LOGGER.info("- Chunk {} {} has been unloaded", unloadedChunk.chunkX, unloadedChunk.chunkZ);
                        Messenger.print_server_message(CarpetServer.minecraftServer, "- Chunk " + unloadedChunk.chunkX + " " + unloadedChunk.chunkZ + " has been unloaded!");
                    }
                }
            }
        }
    }

    @Inject(method = "tick()Z", at = @At("TAIL"))
    private void stopUnloadTimer(CallbackInfoReturnable<Boolean> cir) {
        if (HaruhikageAddonSettings.logCertainTickPhases) {
            HaruhikageAddonSettings.LOGGER.info("Unload phase exiting. Global timer: {}", System.nanoTime());
        }
    }

    // Chunk Tracking - Loading events
    @Inject(method = "loadChunk", at = @At("RETURN"))
    private void sniffLoadChunkEvents(int chunkX, int chunkZ, CallbackInfoReturnable<WorldChunk> cir) {
        if(HaruhikageAddonSettings.loadedChunksCommand && !ChunkTrackCommand.chunks.isEmpty()) {
            for(ChunkPos pos : ChunkTrackCommand.chunks) {
                if(pos.x == chunkX && pos.z == chunkZ) {
                    HaruhikageAddonSettings.LOGGER.info("+ Chunk {} {} has been loaded!", chunkX, chunkZ);
                    Messenger.print_server_message(CarpetServer.minecraftServer, "+ Chunk " + chunkX + " " + chunkZ + " has been loaded!");
                }
            }
        }
    }
}

