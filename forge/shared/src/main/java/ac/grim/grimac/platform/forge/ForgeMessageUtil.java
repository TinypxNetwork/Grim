package ac.grim.grimac.platform.forge;

import ac.grim.grimac.platform.api.sender.Sender;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class ForgeMessageUtil {

    public Object textLiteral(String message) {
        return Component.literal(message);
    }

    public void sendMessage(Sender target, Object message, boolean overlay) {
        ((CommandSourceStack) (Object) target).sendSuccess((Component) message, overlay);
    }
}