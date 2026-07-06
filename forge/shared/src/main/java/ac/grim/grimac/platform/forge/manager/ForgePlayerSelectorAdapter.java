package ac.grim.grimac.platform.forge.manager;

import ac.grim.grimac.platform.api.command.PlayerSelector;
import ac.grim.grimac.platform.api.sender.Sender;
import org.incendo.cloud.minecraft.modded.data.SinglePlayerSelector;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public class ForgePlayerSelectorAdapter implements PlayerSelector {
    private final SinglePlayerSelector selector;
    private final Function<SinglePlayerSelector, Sender> singleResolver;
    private final Function<SinglePlayerSelector, String> inputResolver;

    public ForgePlayerSelectorAdapter(
            SinglePlayerSelector selector,
            Function<SinglePlayerSelector, Sender> singleResolver,
            Function<SinglePlayerSelector, String> inputResolver
    ) {
        this.selector = selector;
        this.singleResolver = singleResolver;
        this.inputResolver = inputResolver;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Sender getSinglePlayer() {
        return singleResolver.apply(selector);
    }

    @Override
    public Collection<Sender> getPlayers() {
        return Collections.singletonList(getSinglePlayer());
    }

    @Override
    public String inputString() {
        return inputResolver.apply(selector);
    }
}