package ac.grim.grimac.platform.forge.manager;

import ac.grim.grimac.platform.api.manager.PermissionRegistrationManager;
import ac.grim.grimac.platform.api.permissions.PermissionDefaultValue;
import ac.grim.grimac.platform.forge.sender.AbstractForgeSenderFactory;

import java.util.function.Consumer;

public class ForgePermissionRegistrationManager implements PermissionRegistrationManager {

    private final AbstractForgeSenderFactory<?> forgeSenderFactory;
    private final Consumer<String> onRegister;

    public ForgePermissionRegistrationManager(AbstractForgeSenderFactory<?> forgeSenderFactory,
                                              Consumer<String> onRegister) {
        this.forgeSenderFactory = forgeSenderFactory;
        this.onRegister = onRegister;
        registerPermission("grim.exempt", PermissionDefaultValue.FALSE);
        registerPermission("grim.nosetback", PermissionDefaultValue.FALSE);
        registerPermission("grim.nomodifypacket", PermissionDefaultValue.FALSE);
        registerPermission("grim.disabled", PermissionDefaultValue.FALSE);
        registerPermission("grim.alerts.enable-on-join", PermissionDefaultValue.FALSE);
        registerPermission("grim.verbose.enable-on-join", PermissionDefaultValue.FALSE);
        registerPermission("grim.brand.enable-on-join", PermissionDefaultValue.FALSE);
        registerPermission("grim.alerts.enable-on-join.silent", PermissionDefaultValue.FALSE);
        registerPermission("grim.verbose.enable-on-join.silent", PermissionDefaultValue.FALSE);
        registerPermission("grim.brand.enable-on-join.silent", PermissionDefaultValue.FALSE);
    }

    @Override
    public void registerPermission(String name, PermissionDefaultValue defaultValue) {
        forgeSenderFactory.registerPermissionDefault(name, defaultValue);
        onRegister.accept(name);
    }
}