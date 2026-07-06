package ac.grim.grimac.platform.forge.resolver;

import ac.grim.grimac.api.plugin.BasicGrimPlugin;
import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.grim.grimac.internal.plugin.resolver.GrimExtensionManager;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public final class ForgeResolverRegistrar {

    private final Map<ModContainer, GrimPlugin> modContainerCache = new ConcurrentHashMap<>();
    private final Map<Class<?>, GrimPlugin> classCache = new ConcurrentHashMap<>();

    public void registerAll(GrimExtensionManager extensionManager) {
        extensionManager.setFailureHandler(this::createFailureException);
        extensionManager.registerResolver(this::resolveModContainer);
        extensionManager.registerResolver(this::resolveStringId);
        extensionManager.registerResolver(this::resolveClass);
    }

    private GrimPlugin resolveMod(ModContainer modContainer) {
        return modContainerCache.computeIfAbsent(modContainer, container -> {
            IModInfo metadata = container.getModInfo();
            String folderName = metadata.getModId().equals("grimac") ? metadata.getDisplayName() : metadata.getModId();
            return new BasicGrimPlugin(
                    Logger.getLogger(metadata.getDisplayName()),
                    new File("config", folderName),
                    metadata.getVersion().toString(),
                    metadata.getDescription(),
                    Collections.emptyList()
            );
        });
    }

    private GrimPlugin resolveModContainer(Object context) {
        return (context instanceof ModContainer mc) ? resolveMod(mc) : null;
    }

    private GrimPlugin resolveStringId(Object context) {
        if (context instanceof String modId) {
            return ModList.get().getModContainerById(modId.toLowerCase(Locale.ROOT))
                    .map(this::resolveMod)
                    .orElse(null);
        }
        return null;
    }

    private GrimPlugin resolveClass(Object context) {
        if (context instanceof Class<?> clazz) {
            return classCache.computeIfAbsent(clazz, this::findClassProvider);
        }
        return null;
    }

    private GrimPlugin findClassProvider(Class<?> c) {
        try {
            java.security.CodeSource codeSource = c.getProtectionDomain().getCodeSource();
            if (codeSource == null) return null;
            java.net.URL sourceUrl = codeSource.getLocation();
            if (sourceUrl == null) return null;
            Path sourcePath = Paths.get(sourceUrl.toURI());

            for (ModContainer modContainer : ModList.get().getMods()) {
                Path modPath = modContainer.getModInfo().getOwningFile().getFile().getFilePath();
                try {
                    if (Files.isSameFile(modPath, sourcePath)) {
                        return resolveMod(modContainer);
                    }
                } catch (IOException ignored) {
                }
            }
        } catch (URISyntaxException | NullPointerException e) {
            return null;
        }
        return null;
    }

    private RuntimeException createFailureException(Object failedContext) {
        String message = """
        Failed to resolve GrimPlugin context from the provided object of type '%s'.

        Please ensure you are passing one of the following:
          - A Forge ModContainer instance.
          - The mod ID as a String (e.g., "my-mod-id").
          - Any Class from your mod's JAR file (e.g., MyListener.class).
          - A pre-existing GrimPlugin instance.
        """.formatted(failedContext.getClass().getName());
        return new IllegalArgumentException(message);
    }
}