package muramasa.antimatter.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ServerHandler implements IProxyHandler {

    public ServerHandler() { }

    public static void setup(FMLDedicatedServerSetupEvent e) { }

    @Override
    public World getClientWorld() {
        throw new IllegalStateException("Cannot call on server!");
    }

    @Override
    public PlayerEntity getClientPlayer() {
        throw new IllegalStateException("Cannot call on server!");
    }
}
