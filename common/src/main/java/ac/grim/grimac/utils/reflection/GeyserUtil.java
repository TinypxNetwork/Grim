package ac.grim.grimac.utils.reflection;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Method;
import java.util.UUID;

@UtilityClass
public class GeyserUtil {
    // Floodgate is the authentication system for Geyser on servers that use Geyser as a proxy instead of installing it as a plugin directly on the server
    private static final boolean floodgate = ReflectionUtils.hasClass("org.geysermc.floodgate.api.FloodgateApi");
    private static final boolean geyser = ReflectionUtils.hasClass("org.geysermc.api.Geyser");

    private static final boolean FORGE = ReflectionUtils.hasClass("net.minecraftforge.fml.loading.FMLLoader");

    public static boolean isBedrockPlayer(UUID uuid) {
        if (FORGE) return false;
        if (floodgate) {
            try {
                Class<?> floodgateApiClass = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
                Method getInstance = floodgateApiClass.getMethod("getInstance");
                Object instance = getInstance.invoke(null);
                Method isFloodgatePlayer = instance.getClass().getMethod("isFloodgatePlayer", UUID.class);
                return (Boolean) isFloodgatePlayer.invoke(instance, uuid);
            } catch (Exception ignored) {
            }
        }
        if (geyser) {
            try {
                Class<?> geyserClass = Class.forName("org.geysermc.api.Geyser");
                Method api = geyserClass.getMethod("api");
                Object apiInstance = api.invoke(null);
                Method isBedrockPlayer = apiInstance.getClass().getMethod("isBedrockPlayer", UUID.class);
                return (Boolean) isBedrockPlayer.invoke(apiInstance, uuid);
            } catch (Exception ignored) {
            }
        }
        return false;
    }
}
