package ac.grim.grimac.platform.forge.sender;

import ac.grim.grimac.platform.api.permissions.PermissionDefaultValue;
import ac.grim.grimac.platform.api.sender.SenderFactory;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractForgeSenderFactory<T> extends SenderFactory<T> {

    public static final boolean HAS_PERMISSIONS_API =
            ModList.get().isLoaded("forge-permissions-api");

    private final Map<String, PermissionDefaultValue> permissionDefaults = new HashMap<>();

    public void registerPermissionDefault(String permission, PermissionDefaultValue defaultValue) {
        permissionDefaults.put(permission, defaultValue);
    }

    protected abstract @Nullable Boolean queryPermissionValue(T sender, String node);

    protected abstract boolean queryPermission(T sender, String node, boolean defaultIfUnset);

    protected abstract boolean isOperator(T sender);

    @Override
    public boolean hasPermission(T sender, String node) {
        if (HAS_PERMISSIONS_API) {
            Boolean value = queryPermissionValue(sender, node);
            if (value != null) return value;
        }
        PermissionDefaultValue defaultValue = permissionDefaults.get(node);
        if (defaultValue == null) return isOperator(sender);
        return resolve(defaultValue, sender);
    }

    @Override
    public boolean hasPermission(T sender, String node, boolean defaultIfUnset) {
        if (HAS_PERMISSIONS_API) {
            return queryPermission(sender, node, defaultIfUnset);
        }
        PermissionDefaultValue defaultValue = permissionDefaults.get(node);
        if (defaultValue == null) return defaultIfUnset;
        return resolve(defaultValue, sender);
    }

    private boolean resolve(PermissionDefaultValue defaultValue, T sender) {
        return switch (defaultValue) {
            case TRUE -> true;
            case FALSE -> false;
            case OP -> isOperator(sender);
            case NOT_OP -> !isOperator(sender);
        };
    }
}