package ac.grim.grimac.platform.forge.mc1201;

import ac.grim.grimac.platform.api.sender.Sender;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
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

public class ForgeServerCommandManager extends CommandManager<Sender> {

    public ForgeServerCommandManager(
            @NotNull ExecutionCoordinator<Sender> executionCoordinator,
            @NotNull SenderMapper<Sender, ?> senderMapper
    ) {
        super(executionCoordinator, CommandRegistrationHandler.nullCommandRegistrationHandler());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        org.incendo.cloud.internal.CommandNode<Sender> rootNode = this.commandTree().getRootNode();
        for (org.incendo.cloud.internal.CommandNode<Sender> child : rootNode.children()) {
            CommandNode<CommandSourceStack> brigadierNode = buildBrigadierNode(child);
            if (brigadierNode != null) {
                dispatcher.getRoot().addChild(brigadierNode);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private CommandNode<CommandSourceStack> buildBrigadierNode(
            org.incendo.cloud.internal.CommandNode<Sender> cloudNode
    ) {
        CommandComponent<Sender> component = cloudNode.component();
        if (component == null) return null;

        CommandNode<CommandSourceStack> brigadierNode;

        switch (component.type()) {
            case LITERAL -> {
                LiteralArgumentBuilder<CommandSourceStack> builder = LiteralArgumentBuilder.literal(component.name());
                if (cloudNode.command() != null) {
                    builder.executes(context -> {
                        String input = context.getInput();
                        if (input.startsWith("/")) {
                            input = input.substring(1);
                        }
                        this.commandExecutor().executeCommand((Sender) context.getSource(), input);
                        return 1;
                    });
                }
                for (org.incendo.cloud.internal.CommandNode<Sender> child : cloudNode.children()) {
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
                        String input = context.getInput();
                        if (input.startsWith("/")) {
                            input = input.substring(1);
                        }
                        this.commandExecutor().executeCommand((Sender) context.getSource(), input);
                        return 1;
                    });
                }
                for (org.incendo.cloud.internal.CommandNode<Sender> child : cloudNode.children()) {
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

    @Override
    public boolean hasPermission(Sender sender, String permission) {
        return sender.hasPermission(permission);
    }
}