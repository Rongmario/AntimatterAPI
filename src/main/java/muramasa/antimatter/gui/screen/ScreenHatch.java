package muramasa.antimatter.gui.screen;

import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.machine.MachineFlag;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class ScreenHatch<T extends ContainerMachine> extends ScreenMachine<T> {

    public ScreenHatch(T container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        gui = new ResourceLocation(Ref.ID, "textures/gui/machine/hatch.png");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        List<SlotData> list = container.getTile().getMachineType().getGui().getSlots(container.getTile().getMachineTier());
        for (SlotData slot : list) {
            if (slot.type == SlotType.IT_IN || slot.type == SlotType.IT_OUT) {
                drawTexture(gui, guiLeft + slot.x - 1, guiTop + slot.y - 1, xSize, 0, 18, 18);
            } else if (slot.type == SlotType.FL_IN || slot.type == SlotType.FL_OUT) {
                drawTexture(gui, guiLeft + slot.x - 1, guiTop + slot.y - 1, xSize, 18, 18, 18);
            }
        }
        if (container.getTile().getMachineType().has(MachineFlag.FLUID)) {
            drawTexture(gui, guiLeft + 7, guiTop + 15, xSize, 36, 18, 54);
        }
    }
}
