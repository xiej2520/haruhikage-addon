package carpet.commands;

import carpet.CarpetServer;
import carpet.ClusterHelper;
import carpet.ClusterUtil;
import carpet.MSTGeneration;
import carpet.utils.Messenger;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nullable;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;

public class CommandCluster extends CommandCarpetBase {

    public ClusterHelper.ClusterData tempData = null;

    @Override
    public String getName() {
        return "cluster";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "cluster peek/read <NBT as parameters>/compute/loadCluster/loadGrid/constructLoader";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "peek", "read", "compute", "loadCluster", "loadGrid", "constructLoader");
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!command_enabled("commandCluster", sender)) return;
        if (args.length < 1) return;
        switch (args[0]) {
            case "peek":
                Messenger.s(sender, tempData.writeToNBT(new NBTTagCompound()).toString());
                break;
            case "read":
                if (args.length < 2) return;
                try {
                    NBTTagCompound compound = JsonToNBT.getTagFromJson(args[1]);
                    tempData = new ClusterHelper.ClusterData(compound);
                    Messenger.s(sender, "Data successfully read! ");
                } catch (NBTException e) {
                    throw new CommandException("Malformed NBT");
                }
                break;
            case "compute":
                HttpUtil.DOWNLOADER_EXECUTOR.submit(() -> {
                    long startMs = System.currentTimeMillis();
                    ClusterHelper.ClusterData computedData = ClusterHelper.compute(tempData);
                    if (computedData == null) {
                        CarpetServer.minecraft_server.callFromMainThread(Executors.callable(() -> {
                            Messenger.s(sender, "Calculation failed");
                        }));
                    } else {
                        CarpetServer.minecraft_server.callFromMainThread(Executors.callable(() -> {
                            Messenger.s(sender, "Computation of optimal cluster chunks completed in " +
                                (System.currentTimeMillis() - startMs) + " milliseconds, and the grid has " + computedData.loadingGridSize + " chunks");
                            Messenger.s(sender, "The optimal target chunk is " + computedData.targetChunk);
                        }));
                    }
                });
                Messenger.s(sender, "Cluster computation started on an async thread. Please wait patiently. ");
                break;
            case "loadCluster":
                ClusterHelper.tempData.clusterChunks.forEach(chunkPos -> {
                    sender.getEntityWorld().getChunk(chunkPos.x, chunkPos.z);
                });
                Messenger.s(sender, "Cluster chunks loaded! ");
                break;
            case "loadGrid":
                Set<ChunkPos> structureMap = MSTGeneration.generateStructureMap(ClusterHelper.tempData.clusterCornerPos, ClusterHelper.tempData.MSTEdgeList, ClusterHelper.tempData.clusterChunks);
                structureMap.forEach(chunkPos -> {
                    sender.getEntityWorld().getChunk(chunkPos.x, chunkPos.z);
                });
                Messenger.s(sender, "Loading grid loaded! ");
                break;
            case "constructLoader":
                int y = 64;
                Block block = CommandBase.getBlockByText(sender, args[1]);
                int metadata = Integer.parseInt(args[2]);
                IBlockState buildingBlock = block.getStateFromMeta(metadata);
                if (args.length >= 4) y = Integer.parseInt(args[3]);
                MSTGeneration.buildLoadingGrid(ClusterHelper.tempData.clusterCornerPos, ClusterHelper.tempData.MSTEdgeList, ClusterHelper.tempData.clusterChunks, sender.getEntityWorld(), y, buildingBlock);
                Messenger.s(sender, "Loader for the cluster grid has been constructed!!! ");
                break;
        }
    }
}

