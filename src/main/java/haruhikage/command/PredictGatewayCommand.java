package haruhikage.command;

import carpet.commands.CarpetAbstractCommand;
import carpet.utils.Messenger;
import haruhikage.HaruhikageAddonSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.AbstractCommand;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PredictGatewayCommand extends CarpetAbstractCommand {

    @Override
    public String getName() {
        return "predictGateway";
    }

    @Override
    public String getUsage(CommandSource source) {
        return "predictGateway x z";
    }

    @Override
    public void run(MinecraftServer server, CommandSource source, String[] args) throws CommandException {
        if (!HaruhikageAddonSettings.predictGatewayCommand) {
            Messenger.m(source, "r Command not active! Enable it with /carpet predictGatewayCommand true");
            return;
        }

        try {
            if (args.length != 2) {
                throw new IncorrectUsageException(getUsage(source));
            }
            BlockPos pos = parseBlockPos(source, new String[]{args[0], "0", args[1]}, 0, false);

            World world = source.getSourceWorld();

            findExitPortal(source, world, pos);

        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof IncorrectUsageException) {
                throw e;
            }
            throw new IncorrectUsageException(getUsage(source));
        }
    }


    @Override
    public List<String> getSuggestions(MinecraftServer server, CommandSource sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length >= 1 && args.length <= 2) {
            return AbstractCommand.suggestHorizontalCoordinate(args, 0, targetPos);
        }else {
            return Collections.emptyList();
        }
    }

    private void findExitPortal(CommandSource source, World world, BlockPos pos) {
        Messenger.m(source, "w Finding linked exit gateway for (" + pos.getX() + ", " + pos.getZ() + ")");
        Vec3d vec3d = new Vec3d(pos.getX(), 0.0, pos.getZ()).normalize();
        Vec3d vec3d2 = vec3d.scale(1024.0);

        for (int i = 16; getChunk(world, vec3d2).getHighestSectionOffset() > 0 && i-- > 0; vec3d2 = vec3d2.add(vec3d.scale(-16.0))) {
            Messenger.m(source, "y Skipping backwards past nonempty chunk " + toChunkString(vec3d2) + " at " + vec3d2);
        }

        for (int var5 = 16; getChunk(world, vec3d2).getHighestSectionOffset() == 0 && var5-- > 0; vec3d2 = vec3d2.add(vec3d.scale(16.0))) {
            Messenger.m(source, "c Skipping forwards past empty chunk " + toChunkString(vec3d2) + " at " + vec3d2);
        }

        Messenger.m(source, "l Found chunk " + toChunkString(vec3d2) + " at " + vec3d2);
        WorldChunk worldChunk = getChunk(world, vec3d2);
        BlockPos exitPos = findValidExitPos(worldChunk);
        if (exitPos == null) {
            exitPos = new BlockPos(vec3d2.x + 0.5, 75.0, vec3d2.z + 0.5);
            Messenger.m(source, "y Failed to find suitable block, placing end island at " + exitPos);
            return;
        } else {
            Messenger.m(source, "y Found valid end stone exit pos at " + exitPos);
        }

        exitPos = findExitPos(source, world, exitPos, 16, true);
        exitPos = exitPos.up(10);
        Messenger.m(source, "c Creating linked gateway at " + exitPos);
    }

    private static WorldChunk getChunk(World world, Vec3d pos) {
        return world.getChunkAt(MathHelper.floor(pos.x / 16.0), MathHelper.floor(pos.z / 16.0));
    }

    private static BlockPos findValidExitPos(WorldChunk chunk) {
        BlockPos blockPos = new BlockPos(chunk.chunkX * 16, 30, chunk.chunkZ * 16);
        int i = chunk.getHighestSectionOffset() + 16 - 1;
        BlockPos blockPos2 = new BlockPos(chunk.chunkX * 16 + 16 - 1, i, chunk.chunkZ * 16 + 16 - 1);
        BlockPos blockPos3 = null;
        double d = 0.0;

        for (BlockPos blockPos4 : BlockPos.iterateRegion(blockPos, blockPos2)) {
            BlockState blockState = chunk.getBlockState(blockPos4);
            if (blockState.getBlock() == Blocks.END_STONE
                && !chunk.getBlockState(blockPos4.up(1)).blocksAmbientLight()
                && !chunk.getBlockState(blockPos4.up(2)).blocksAmbientLight()) {
                double e = blockPos4.squaredDistanceToCenter(0.0, 0.0, 0.0);
                if (blockPos3 == null || e < d) {
                    blockPos3 = blockPos4;
                    d = e;
                }
            }
        }

        return blockPos3;
    }

    // radius == 16 and allowCenter == true for placing linked gateway
    private BlockPos findExitPos(CommandSource source, World world, BlockPos exitPos, int radius, boolean allowCenter) {
        BlockPos blockPos = null;

        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (i != 0 || j != 0 || allowCenter) {
                    for (int k = 255; k > (blockPos == null ? 0 : blockPos.getY()); k--) {
                        BlockPos blockPos2 = new BlockPos(exitPos.getX() + i, k, exitPos.getZ() + j);
                        BlockState blockState = world.getBlockState(blockPos2);
                        if (blockState.blocksAmbientLight() && (allowCenter || blockState.getBlock() != Blocks.BEDROCK)) {
                            blockPos = blockPos2;
                            break;
                        }
                    }
                }
            }
        }

        BlockPos pos = blockPos == null ? exitPos : blockPos;
        Messenger.m(source, String.format("g Highest valid exit position within %s blocks of %s is %s", radius, exitPos, pos));
        return blockPos;
    }

    private String toChunkString(Vec3d pos) {
        return String.format("(%d, %d)", MathHelper.floor(pos.x / 16.0), MathHelper.floor(pos.z / 16.0));
    }

}

