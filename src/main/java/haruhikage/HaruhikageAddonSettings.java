package haruhikage;

import carpet.CarpetSettings;
import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HaruhikageAddonSettings {
    public static final Logger LOGGER = LogManager.getLogger("haruhikage-addon");
    public static final String fallingblock = "Haruhikage";

    @Rule(
            desc = "Log async beacom beam times in server console.",
            categories = {fallingblock},
            options = {"true", "false"}
    )
    public static boolean logAsyncTimes = false;

    @Rule(
            desc = "Logs 'Chunk Unload' phase and 'Player' phase in server console. Unload chunk unload will be displayed in chat",
            categories = {fallingblock},
            options = {"true", "false"}
    )
    public static boolean logCertainTickPhases = false;

    @Rule(
            desc = "Logs whenever an autosave induced by pressing ESC or writing a book happens in console",
            categories = {fallingblock},
            options = {"true", "false"}
    )
    public static boolean logAutosaveTime = false;

    @Rule(
            desc = "Unload Chunk X coordinate for the `logUnloadChunkPhase` logger",
            categories = {fallingblock},
            options = {"1", "2", "3"},
            strict = false
    )
    public static int unloadChunkX = 1;

    @Rule(
            desc = "Unload Chunk Z coordinate for the `logUnloadChunkPhase` logger",
            categories = {fallingblock},
            options = {"1", "2", "3"},
            strict = false
    )
    public static int unloadChunkZ = 1;

    @Rule(
            desc = "Logs population of certain chunks",
            categories = {fallingblock},
            options = {"true", "false"}
    )
    public static boolean logChunkPopulation = false;

    @Rule(
            desc = "Enables and tracks loading events of chunks using the /chunkTrack command in chat. Serves as an alternative to chunk debug without the need of external tools",
            categories = {fallingblock},
            options = {"true", "false"}
    )
    public static boolean chunkTrackCommand = false;

    @Rule(
        desc = "",
        categories = {fallingblock},
        options = {"true", "false"}
    )
    public static boolean loadedChunksCommand = false;

    @Rule(
        desc = "Enables the /palette command to debug the subchunk palette.",
        categories = {fallingblock},
        options = {"true", "false"}
    )
    public static boolean paletteCommand = false;

    @Rule(
        desc = "Enables the /cluster command to compute falling block swap clusters.",
        categories = { fallingblock },
        options = {"true", "false"}
    )
    public static boolean clusterCommand = false;

    @Rule(
        desc = "Enables the /predictGateway command to debug gateway placement.",
        extra = {"Will generate chunks!"},
        categories = { RuleCategory.COMMAND, RuleCategory.CREATIVE },
        options = {"true", "false"}
    )
    public static boolean predictGatewayCommand = false;


    @Rule(
        desc = "Make linking end gateway check for blocks in sections, instead of section being created, like 1.14+",
        extra = {
            "Removing all blocks above the first chunk section (y >= 16) will make the search skip the chunk."
        },
        categories = { RuleCategory.BUGFIX, RuleCategory.FEATURE },
        options = {"true", "false"}
    )
    public static boolean gatewaySectionCheckFix = false;

    @Rule(
            desc = "Disables terrain population. Useful when testing and interacting with contraptions with unpopulated chunks",
            categories = {fallingblock},
            options = {"true", "false"}
    )
    public static boolean disableTerrainPopulation = false;

    @Rule(
        desc = "Place water pockets instead of lava for non-exposed pockets, to see which is which in the nether",
        categories = {fallingblock},
        options = {"true", "false"}
    )
    public static boolean placeWaterInsteadOfLavaPockets = false;

    @Rule(
        desc = "log all ITT turning off",
        categories = {fallingblock},
        options = {"true", "false"}
    )
    public static boolean logIttEnd = false;

    @Rule(
        desc = "Send chunk data for unpopulated chunks, i.e. make invisible chunks not invisible",
        categories = {fallingblock},
        options = {"true", "false"}
    )
    public static boolean sendInvisibleChunks = false;

    @Rule(
        desc = "Logs Liquid Pocket population",
        categories = {fallingblock},
        options = {"true", "false"}
    )
    public static boolean logLiquidPocketPopulation = false;

    @Rule(
        desc = "x",
        categories = {fallingblock},
        options = {"true", "false"}
    )
    public static int logX = 0;
    @Rule(
        desc = "y",
        categories = {fallingblock},
        options = {"true", "false"}
    )
    public static int logY = 0;
    @Rule(
        desc = "z",
        categories = {fallingblock},
        options = {"true", "false"}
    )
    public static int logZ = 0;

}
