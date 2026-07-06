package ac.grim.grimac.platform.forge.manager;

import ac.grim.grimac.platform.api.command.PlayerSelector;
import ac.grim.grimac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.grim.grimac.platform.api.sender.Sender;
import ac.grim.grimac.platform.forge.AbstractGrimACForgeLoaderPlugin;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.level.ServerPlayer;
import org.incendo.cloud.minecraft.modded.data.SinglePlayerSelector;
import org.incendo.cloud.minecraft.modded.parser.VanillaArgumentParsers;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@RequiredArgsConstructor
public class ForgeCloudPlatformCommandArguments implements CloudPlatformCommandArguments {

    private final Function<SinglePlayerSelector, Sender> singleResolver;
    private final Function<SinglePlayerSelector, String> inputResolver;

    @Override
    @SuppressWarnings("unchecked")
    public ParserDescriptor<Sender, PlayerSelector> singlePlayerSelectorParser() {
        ParserDescriptor<Sender, ?> descriptor = (ParserDescriptor<Sender, ?>)
                VanillaArgumentParsers.singlePlayerSelectorParser();
        return ParserDescriptor.of(
                descriptor.parser().mapSuccess((context, selector) -> CompletableFuture.completedFuture(
                        new ForgePlayerSelectorAdapter((SinglePlayerSelector) selector, singleResolver, inputResolver)
                )),
                PlayerSelector.class
        );
    }

    @Override
    public SuggestionProvider<Sender> onlinePlayerSuggestions() {
        return (context, input) -> {
            Collection<ServerPlayer> players = AbstractGrimACForgeLoaderPlugin.FORGE_SERVER.getPlayerList().getPlayers();
            List<Suggestion> suggestions = new ArrayList<>(players.size());

            for (ServerPlayer player : players) {
                suggestions.add(Suggestion.suggestion(player.getGameProfile().getName()));
            }

            return CompletableFuture.completedFuture(suggestions);
        };
    }
}