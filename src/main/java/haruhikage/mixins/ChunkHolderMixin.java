package haruhikage.mixins;

import haruhikage.HaruhikageAddonSettings;
import net.minecraft.network.packet.s2c.play.ForgetWorldChunkS2CPacket;
import net.minecraft.server.ChunkHolder;
import net.minecraft.server.ChunkMap;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChunkHolder.class)
public abstract class ChunkHolderMixin {
    @Shadow
    private boolean populated;

    @Shadow
    public abstract void sendChunk(ServerPlayerEntity player);

    @Shadow
    @Final
    private ChunkPos pos;

    @Shadow
    private @Nullable WorldChunk chunk;

    @Shadow
    @Final
    private ChunkMap chunkMap;

    @Shadow
    @Final
    private List<ServerPlayerEntity> players;

    @Inject(
        method = "addPlayer",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.AFTER,
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z"
        )
    )
    public void sendChunkAfterAddingPlayer(ServerPlayerEntity player, CallbackInfo ci) {
        if (!this.populated && HaruhikageAddonSettings.sendInvisibleChunks) {
            this.sendChunk(player);
        }
    }

    @Inject(
        method = "removePlayer",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;remove(Ljava/lang/Object;)Z"
        )
    )
    public void sendChunkBeforeRemovingPlayer(ServerPlayerEntity player, CallbackInfo ci) {
        if (!this.populated && HaruhikageAddonSettings.sendInvisibleChunks) {
            player.networkHandler.sendPacket(new ForgetWorldChunkS2CPacket(this.pos.x, this.pos.z));
        }
    }

    @Redirect(
        method = "sendChunk",
        at = @At(
            value = "FIELD",
            opcode = Opcodes.GETFIELD,
            target = "Lnet/minecraft/server/ChunkHolder;populated:Z"
        )
    )
    public boolean sendChunkAlways(ChunkHolder instance) {
        return this.populated || HaruhikageAddonSettings.sendInvisibleChunks;
    }

    @Redirect(
        method = "sendPacket",
        at = @At(
            value = "FIELD",
            opcode = Opcodes.GETFIELD,
            target = "Lnet/minecraft/server/ChunkHolder;populated:Z"
        )
    )
    public boolean sendPacketAlways(ChunkHolder instance) {
        return this.populated || HaruhikageAddonSettings.sendInvisibleChunks;
    }

    @Redirect(
        method = "sendChanges",
        at = @At(
            value = "FIELD",
            opcode = Opcodes.GETFIELD,
            target = "Lnet/minecraft/server/ChunkHolder;populated:Z"
        )
    )
    private boolean sendChangesAlways(ChunkHolder instance) {
        return (this.populated || HaruhikageAddonSettings.sendInvisibleChunks) && this.chunk != null;
    }
}
