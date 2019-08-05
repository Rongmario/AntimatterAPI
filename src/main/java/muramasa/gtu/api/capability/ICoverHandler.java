package muramasa.gtu.api.capability;

import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.machines.MachineEvent;
import muramasa.gtu.api.tools.ToolType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

public interface ICoverHandler {

    void update();

    boolean set(EnumFacing side, Cover cover);

    Cover get(EnumFacing side);

    boolean onInteract(EntityPlayer player, EnumHand hand, EnumFacing side, ToolType type);

    void onMachineEvent(MachineEvent event);

    Cover[] getAll();

    boolean hasCover(EnumFacing side, Cover cover);

    boolean isValid(EnumFacing side, Cover existing, Cover replacement);

    EnumFacing getTileFacing();

    TileEntity getTile();
}
