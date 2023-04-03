package io.github.adainish.cobbleddaycare;

import ca.landonjw.gooeylibs2.api.tasks.Task;
import io.github.adainish.cobbleddaycare.obj.DayCareManager;
import io.github.adainish.cobbleddaycare.tasks.UpdatePensRunnable;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CobbledDayCare.MODID)
public class CobbledDayCare {

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

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        initDirs();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event)
    {
        //check storage, if doesn't exist create new one, otherwise serialise from gson
        manager = new DayCareManager();
        taskList.add(Task.builder().infinite().interval(20).execute(new UpdatePensRunnable()).build());
    }

    public void initDirs() {
        setConfigDir(new File(FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).toString() + "/CobbledDayCare/"));
        getConfigDir().mkdir();
        setStorage(new File(getConfigDir(), "/storage/"));
        getStorage().mkdirs();
    }

}
