package ac.grim.grimac.manager.compat;

public class ExternalMovementState implements Cloneable {
    public static final ExternalMovementState EMPTY = new ExternalMovementState();

    private boolean aiming;
    private boolean reloading;
    private boolean bolting;
    private boolean drawingGun;
    private boolean taczCrawling;
    private boolean gunActionActive;
    private boolean sprintSuppressedByMod;

    public ExternalMovementState() {
    }

    public ExternalMovementState(boolean aiming, boolean reloading, boolean bolting,
                                 boolean drawingGun, boolean taczCrawling,
                                 boolean gunActionActive, boolean sprintSuppressedByMod) {
        this.aiming = aiming;
        this.reloading = reloading;
        this.bolting = bolting;
        this.drawingGun = drawingGun;
        this.taczCrawling = taczCrawling;
        this.gunActionActive = gunActionActive;
        this.sprintSuppressedByMod = sprintSuppressedByMod;
    }

    public boolean isTacz() {
        return aiming || reloading || bolting || drawingGun || taczCrawling;
    }

    public boolean gunActionActive() {
        return gunActionActive;
    }

    public boolean sprintSuppressedByMod() {
        return sprintSuppressedByMod;
    }

    public boolean taczCrawling() {
        return taczCrawling;
    }

    public void tacz() {
        this.aiming = true;
    }

    public void reset() {
        this.aiming = false;
        this.reloading = false;
        this.bolting = false;
        this.drawingGun = false;
        this.taczCrawling = false;
        this.gunActionActive = false;
        this.sprintSuppressedByMod = false;
    }

    @Override
    public ExternalMovementState clone() {
        try {
            return (ExternalMovementState) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}