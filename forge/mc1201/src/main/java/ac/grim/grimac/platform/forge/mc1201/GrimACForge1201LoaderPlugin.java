package ac.grim.grimac.platform.forge.mc1201;

import ac.grim.grimac.command.CloudCommandService;
import ac.grim.grimac.platform.api.command.CommandService;
import ac.grim.grimac.platform.api.sender.Sender;
import ac.grim.grimac.platform.forge.AbstractGrimACForgeLoaderPlugin;
import ac.grim.grimac.platform.forge.ForgeConversionUtil;
import ac.grim.grimac.platform.forge.ForgeMessageUtil;
import ac.grim.grimac.platform.forge.entity.ForgeGrimEntity;
import ac.grim.grimac.platform.forge.manager.ForgeCloudPlatformCommandArguments;
import ac.grim.grimac.platform.forge.manager.ForgeItemResetHandler;
import ac.grim.grimac.platform.forge.manager.ForgePermissionRegistrationManager;
import ac.grim.grimac.platform.forge.mc1201.player.Forge1201PlatformPlayer;
import ac.grim.grimac.platform.forge.mc1201.player.Forge1201PlatformInventory;
import ac.grim.grimac.platform.forge.player.ForgePlayerFactory;
import ac.grim.grimac.platform.forge.scheduler.ForgePlatformScheduler;
import ac.grim.grimac.platform.forge.sender.ForgeSenderFactory;
import ac.grim.grimac.utils.lazy.LazyHolder;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import net.minecraft.commands.CommandSourceStack;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.modded.data.SinglePlayerSelector;

public class GrimACForge1201LoaderPlugin extends AbstractGrimACForgeLoaderPlugin<
        ForgePlayerFactory,
        Forge1201PlatformServer,
        ForgePlatformScheduler,
        ForgeSenderFactory,
        ForgeItemResetHandler,
        ForgeCloudPlatformCommandArguments
        > {

    public GrimACForge1201LoaderPlugin() {
        super(
                LazyHolder.simple(ForgePlatformScheduler::new),
                LazyHolder.simple(ForgeSenderFactory::new),
                LazyHolder.simple(ForgeItemResetHandler::new),
                LazyHolder.simple(GrimACForge1201LoaderPlugin::createCommandArguments),
                LazyHolder.simple(() -> new ForgePermissionRegistrationManager(
                        AbstractGrimACForgeLoaderPlugin.LOADER.getForgeSenderFactory(),
                        name -> {}
                )),
                new ForgePlayerFactory(
                        Forge1201PlatformPlayer::new,
                        ForgeGrimEntity::new,
                        Forge1201PlatformInventory::new
                ),
                new Forge1201PlatformServer(),
                new ForgeMessageUtil(),
                new ForgeConversionUtil()
        );
    }

    private static ForgeCloudPlatformCommandArguments createCommandArguments() {
        return new ForgeCloudPlatformCommandArguments(
                selector -> AbstractGrimACForgeLoaderPlugin.LOADER
                        .getForgeSenderFactory()
                        .wrap(selector.single().createCommandSourceStack()),
                SinglePlayerSelector::inputString
        );
    }

    @Override
    public ServerVersion getNativeVersion() {
        return ServerVersion.V_1_20_1;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected CommandService createPlatformCommandService() {
        CommandManager<Sender> manager = new ForgeServerCommandManager(
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.identity()
        );
        return new CloudCommandService(() -> manager, commandArguments.get());
    }
}