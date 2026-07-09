package ac.grim.grimac.platform.forge.mc1201;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.platform.forge.*;
import ac.grim.grimac.platform.forge.initables.ForgeLuckPermsInitable;
import ac.grim.grimac.platform.forge.initables.ForgePlaceholderAPIInitable;
import ac.grim.grimac.platform.forge.mc1201.compat.ForgeTaczExternalMovementStateProvider;
import ac.grim.grimac.platform.forge.scheduler.ForgePlatformScheduler;
import ac.grim.grimac.utils.anticheat.LogUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod("grimac")
public class GrimACForge {

    private static GrimACForge1201LoaderPlugin loader;

    public GrimACForge() {
        loader = new GrimACForge1201LoaderPlugin();
        AbstractGrimACForgeLoaderPlugin.LOADER = loader;

        GrimAPI.INSTANCE.load(
                loader,
                new ForgeTickEndEvent(),
                new ForgeLuckPermsInitable(),
                new ForgePlaceholderAPIInitable()
        );

        GrimAPI.INSTANCE.getCommandService().registerCommands();
        registerTaczCompatibility();

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerTaczCompatibility() {
        if (!ModList.get().isLoaded("tacz")) return;
        try {
            GrimAPI.INSTANCE.getExternalMovementStateManager().setProvider(new ForgeTaczExternalMovementStateProvider());
        } catch (IllegalStateException e) {
            LogUtil.warn("TACZ is present but Grim could not initialize TACZ compatibility: " + e.getMessage());
        }
    }

    @Mod.EventBusSubscriber(modid = "grimac", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {

        @SubscribeEvent
        public static void onServerAboutToStart(ServerAboutToStartEvent event) {
            AbstractGrimACForgeLoaderPlugin.FORGE_SERVER = event.getServer();
            ForgeServerEvents.fireServerStarting(event.getServer());
            GrimAPI.INSTANCE.start();
        }

        @SubscribeEvent
        public static void onServerStopping(ServerStoppingEvent event) {
            ForgeServerEvents.fireServerStopping(event.getServer());
            GrimAPI.INSTANCE.stop();
            ForgePlatformScheduler scheduler = (ForgePlatformScheduler) loader.getScheduler();
            scheduler.shutdown();
        }

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                ForgeServerEvents.fireEndTick(event.getServer());
            }
        }
    }
}
