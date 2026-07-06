package ac.grim.grimac.platform.forge.mc1201;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.internal.CommandRegistrationHandler;
import org.jetbrains.annotations.NotNull;

public class ForgeServerCommandManager extends CommandManager<CommandSourceStack> {

    public ForgeServerCommandManager(
            @NotNull ExecutionCoordinator<CommandSourceStack> executionCoordinator,
            @NotNull SenderMapper<CommandSourceStack, ?> senderMapper
    ) {
        super(executionCoordinator, new CommandRegistrationHandler<>() {
            @Override
            public void register(org.incendo.cloud.@NotNull Command<CommandSourceStack> command) {
            }
        });
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext buildContext = event.getBuildContext();

        // Build the command tree from Cloud's command tree and register with Brigadier
        org.incendo.cloud.internal.CommandNode<CommandSourceStack> rootNode = this.commandTree().getRootNode();
        for (org.incendo.cloud.internal.CommandNode<CommandSourceStack> child : rootNode.children()) {
            CommandNode<CommandSourceStack> brigadierNode = buildBrigadierNode(child);
            if (brigadierNode != null) {
                dispatcher.getRoot().addChild(brigadierNode);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private CommandNode<CommandSourceStack> buildBrigadierNode(
            org.incendo.cloud.internal.CommandNode<CommandSourceStack> cloudNode
    ) {
        CommandComponent<CommandSourceStack> component = cloudNode.component();
        if (component == null) return null;

        CommandNode<CommandSourceStack> brigadierNode;

        switch (component.type()) {
            case LITERAL -> {
                LiteralArgumentBuilder<CommandSourceStack> builder = LiteralArgumentBuilder.literal(component.name());
                if (cloudNode.command() != null) {
                    builder.executes(context -> {
                        cloudNode.command().execute(context.getSource());
                        return 1;
                    });
                }
                for (org.incendo.cloud.internal.CommandNode<CommandSourceStack> child : cloudNode.children()) {
                    CommandNode<CommandSourceStack> childNode = buildBrigadierNode(child);
                    if (childNode != null) {
                        builder.then(childNode);
                    }
                }
                brigadierNode = builder.build();
            }
            case ARGUMENT -> {
                RequiredArgumentBuilder<CommandSourceStack, ?> builder =
                        RequiredArgumentBuilder.argument(component.name(), component.argumentType());
                if (cloudNode.command() != null) {
                    builder.executes(context -> {
                        cloudNode.command().execute(context.getSource());
                        return 1;
                    });
                }
                for (org.incendo.cloud.internal.CommandNode<CommandSourceStack> child : cloudNode.children()) {
                    CommandNode<CommandSourceStack> childNode = buildBrigadierNode(child);
                    if (childNode != null) {
                        builder.then(childNode);
                    }
                }
                brigadierNode = builder.build();
            }
            default -> {
                return null;
            }
        }

        return brigadierNode;
    }
}