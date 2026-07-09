package ac.grim.grimac.platform.forge.entity;

import ac.grim.grimac.platform.api.entity.GrimEntity;
import ac.grim.grimac.platform.api.world.PlatformWorld;
import ac.grim.grimac.utils.math.Location;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ForgeGrimEntity implements GrimEntity {
    protected final Entity entity;

    public ForgeGrimEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public UUID getUniqueId() {
        return entity.getUUID();
    }

    @Override
    public boolean isDead() {
        return !entity.isAlive();
    }

    @Override
    public boolean eject() {
        if (entity.isVehicle()) {
            entity.ejectPassengers();
            return true;
        }
        return false;
    }

    @Override
    public @NotNull Object getNative() {
        return entity;
    }

    @Override
    public PlatformWorld getWorld() {
        return (PlatformWorld) entity.level();
    }

    @Override
    public Location getLocation() {
        return new Location(
                getWorld(),
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                entity.getYRot(),
                entity.getXRot()
        );
    }

    @Override
    public double distanceSquared(double oX, double oY, double oZ) {
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        double distX = x - oX;
        double distY = y - oY;
        double distZ = z - oZ;
        return distX * distX + distY * distY + distZ * distZ;
    }

    @Override
    public CompletableFuture<Boolean> teleportAsync(Location location) {
        return CompletableFuture.supplyAsync(() -> {
            entity.teleportTo(
                    location.getX(),
                    location.getY(),
                    location.getZ()
            );
            entity.setYRot(location.getYaw());
            entity.setXRot(location.getPitch());
            return true;
        });
    }
}
