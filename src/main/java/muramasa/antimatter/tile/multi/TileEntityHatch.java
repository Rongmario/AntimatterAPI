package muramasa.antimatter.tile.multi;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.ComponentHandler;
import muramasa.antimatter.capability.machine.HatchComponentHandler;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.structure.IComponent;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Optional;

import static muramasa.antimatter.machine.MachineFlag.*;

public class TileEntityHatch extends TileEntityMachine implements IComponent {

    protected Optional<HatchComponentHandler> componentHandler = Optional.empty();

    public TileEntityHatch(Machine<?> type) {
        super(type);
    }

    @Override
    public void onLoad() {
        componentHandler = Optional.of(new HatchComponentHandler(this));
        if (isServerSide() && has(FLUID)) fluidHandler = Optional.of(new MachineFluidHandler(this, 8000, 1000));
        super.onLoad();
    }

    @Override
    public Optional<HatchComponentHandler> getComponentHandler() {
        return componentHandler;
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (event instanceof ContentEvent) {
            componentHandler.flatMap(ComponentHandler::getFirstController).ifPresent(controller -> {
                switch ((ContentEvent) event) {
                    case ITEM_INPUT_CHANGED:
                        controller.onMachineEvent(event, data);
                        break;
                    case ITEM_OUTPUT_CHANGED:
                        controller.onMachineEvent(event, data);
                    case ITEM_CELL_CHANGED:
                        //TODO handle cells
                        break;
                    case FLUID_INPUT_CHANGED:
                        //TODO
                        break;
                }
            });
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == AntimatterCaps.COMPONENT && componentHandler.isPresent()) return LazyOptional.of(() -> componentHandler.get()).cast();
        return super.getCapability(cap, side);
    }
}
