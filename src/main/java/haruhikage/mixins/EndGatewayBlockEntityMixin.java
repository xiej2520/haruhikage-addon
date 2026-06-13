package haruhikage.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import haruhikage.HaruhikageAddonSettings;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.WorldChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EndGatewayBlockEntity.class)
public abstract class EndGatewayBlockEntityMixin {
    @WrapOperation(
        method = "findExitPortal",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/WorldChunk;getHighestSectionOffset()I"
        )
    )
    public int fixHighestSectionOffsetCheck(WorldChunk instance, Operation<Integer> original) {
        if (HaruhikageAddonSettings.gatewaySectionCheckFix) {
            WorldChunkSection[] sections = instance.getSections();
            for (int i = sections.length - 1; i >= 0; i--) {
                WorldChunkSection levelChunkSection = sections[i];
                // check whether the section has no non-air blocks, not whether it exists
                if (levelChunkSection != null && !levelChunkSection.isEmpty()) {
                    return i;
                }
            }
            return 0;
        } else {
            return original.call(instance);
        }
    }
}
