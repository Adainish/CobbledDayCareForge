package io.github.adainish.cobbleddaycare;

import ca.landonjw.gooeylibs2.api.tasks.Task;
import io.github.adainish.cobbleddaycare.cmd.DayCareCommand;
import io.github.adainish.cobbleddaycare.config.PenConfig;
import io.github.adainish.cobbleddaycare.config.SpeciesConfig;
import io.github.adainish.cobbleddaycare.listener.PlayerListener;
import io.github.adainish.cobbleddaycare.obj.DayCareManager;
import io.github.adainish.cobbleddaycare.storage.DayCareStorage;
import io.github.adainish.cobbleddaycare.tasks.SaveManagerRunnable;
import io.github.adainish.cobbleddaycare.tasks.UpdatePensRunnable;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerLifecycleEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CobbledDayCare.MODID)
public class CobbledDayCare {

    public static CobbledDayCare instance;
    // Define mod id in a common place for everything to reference
    public static final String MODID = "cobbleddaycare";
    public static final String MOD_NAME = "CobbledDayCare";
    public static final String VERSION = "1.0.0-Beta";
    public static final String AUTHORS = "Winglet";
    public static final String YEAR = "2023";
    private static final Logger log = LogManager.getLogger(MOD_NAME);
    public static DayCareManager manager;
    private static MinecraftServer server;

    private static File configDir;
    private static File storage;

    public List<Task> taskList = new ArrayList<>();

    public CobbledDayCare() {
        instance = this;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static MinecraftServer getServer() {
        return server;
    }

    public static void setServer(MinecraftServer server) {
        CobbledDayCare.server = server;
    }

    public static File getConfigDir() {
        return configDir;
    }

    public static void setConfigDir(File configDir) {
        CobbledDayCare.configDir = configDir;
    }

    public static File getStorage() {
        return storage;
    }

    public static void setStorage(File storage) {
        CobbledDayCare.storage = storage;
    }

    public static DayCareStorage dayCareStorage;

    public static SpeciesConfig speciesConfig;
    public static PenConfig penConfig;

    public static Logger getLog() {
        return log;
    }



    private void commonSetup(final FMLCommonSetupEvent event) {
        log.info("Booting up %n by %authors %v %y"
                .replace("%n", MOD_NAME)
                .replace("%authors", AUTHORS)
                .replace("%v", VERSION)
                .replace("%y", YEAR)
        );
    }


    @SubscribeEvent
    public void onCommandRegistry(RegisterCommandsEvent event) {

        //register commands
        event.getDispatcher().register(DayCareCommand.getCommand());

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event)
    {
        setServer(ServerLifecycleHooks.getCurrentServer());
        reload();
        MinecraftForge.EVENT_BUS.register(new PlayerListener());
    }

    @SubscribeEvent
    public void onServerShutDown(ServerStoppingEvent event)
    {
        shutdownTasks();
        dayCareStorage.save(manager);
    }

    public void initDirs() {
        getLog().warn("Writing paths/directories if they don't exist");
        setConfigDir(new File(FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()) + "/CobbledDayCare/"));
        getConfigDir().mkdir();
        setStorage(new File(getConfigDir(), "/storage/"));
        getStorage().mkdirs();
    }

    public void initConfigs() {
        getLog().warn("Writing and loading config data for breedable species and pens");
        SpeciesConfig.writeConfig();
        speciesConfig = SpeciesConfig.getConfig();
        PenConfig.writeConfig();
        penConfig = PenConfig.getConfig();
    }

    public void initStorage()
    {
        if (dayCareStorage != null) {
            dayCareStorage.save(manager);
        } else {
            DayCareStorage.writeConfig();
            dayCareStorage = DayCareStorage.getConfig();
        }

        if (dayCareStorage != null) {
            if (dayCareStorage.dayCareManager == null) {
                dayCareStorage.dayCareManager = new DayCareManager();
            }
            manager = dayCareStorage.dayCareManager;
            manager.loadPenData();
        } else {
            getLog().error("Failed to verify storage data, forcefully shut down the server to prevent corruption. Please contact the dev!");
            System.exit(4);
        }
    }

    public void shutdownTasks()
    {
        if (!taskList.isEmpty())
        {
            getLog().warn("Shutting down old task data");
            for (Task t:taskList) {
                t.setExpired();
            }
            taskList.clear();
        }
    }

    public void reload() {
        initDirs();
        shutdownTasks();
        initConfigs();

        //load other data
        initStorage();

        taskList.add(Task.builder().infinite().interval(20).execute(new UpdatePensRunnable()).build());
        taskList.add(Task.builder().infinite().interval(20 * 60 * 10).execute(new SaveManagerRunnable()).build());
        //whatever other tasks
    }

}
