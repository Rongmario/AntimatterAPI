package muramasa.gtu.api.capability.impl;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.capability.ICoverHandler;
import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.machines.MachineEvent;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.tools.ToolType;
import muramasa.gtu.api.util.SoundType;
import muramasa.gtu.api.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import java.util.ArrayList;

public class CoverHandler implements ICoverHandler {

    private TileEntity tile;
    protected ArrayList<String> validCovers;

    //TODO
    protected Cover[] covers = new Cover[] {
        GregTechAPI.CoverNone, GregTechAPI.CoverNone, GregTechAPI.CoverNone, GregTechAPI.CoverNone, GregTechAPI.CoverNone, GregTechAPI.CoverNone
    };

    public CoverHandler(TileEntity tile, Cover... covers) {
        this.tile = tile;
        //TODO fix valid covers
        validCovers = new ArrayList<>();
        validCovers.add(GregTechAPI.CoverNone.getId());
        for (Cover cover : GregTechAPI.getRegisteredCovers()) {
            validCovers.add(cover.getId());
        }
    }

    @Override
    public void update() {
        for (int i = 0; i < covers.length; i++) {
            if (covers[i].isEmpty()) continue;
            covers[i].onUpdate(getTile(), Utils.rotateFacingAlt(EnumFacing.VALUES[i], getTileFacing()));
        }
    }

    @Override
    public boolean set(EnumFacing side, Cover cover) {
        side = Utils.rotateFacing(side, getTileFacing());
        if (!isValid(side, covers[side.getIndex()], cover)) return false;
        covers[side.getIndex()] = cover;
        SoundType.PLACE_METAL.play(getTile().getWorld(), getTile().getPos());
        Utils.markTileForRenderUpdate(getTile());
        return true;
    }

    @Override /** Fires ones per hand **/
    public boolean onInteract(EntityPlayer player, EnumHand hand, EnumFacing side, ToolType type) {
        Cover cover = get(side);
        if (cover.isEmpty() || !cover.onInteract(getTile(), player, hand, side, type)) return false;
        if (type == null) return false;
        switch (type) {
            case CROWBAR: return GregTechAPI.removeCover(player, this, side);
            default: return false;
        }
    }

    @Override
    public void onMachineEvent(MachineEvent event) {
        for (int i = 0; i < covers.length; i++) {
            if (covers[i].isEmpty()) continue;
            covers[i].onMachineEvent((TileEntityMachine) getTile(), event);
        }
    }

    @Override
    public Cover get(EnumFacing side) {
        return covers[Utils.rotateFacing(side, getTileFacing()).getIndex()];
    }

    public Cover[] getAll() {
        return covers;
    }

    @Override
    public boolean hasCover(EnumFacing side, Cover cover) {
        return get(side).isEqual(cover);
    }

    @Override
    public boolean isValid(EnumFacing side, Cover existing, Cover replacement) {
        return (existing.isEmpty() || replacement.isEqual(GregTechAPI.CoverNone)) && validCovers.contains(replacement.getId());
    }

    @Override
    public EnumFacing getTileFacing() {
        return EnumFacing.NORTH;
    }

    @Override
    public TileEntity getTile() {
        if (tile == null) throw new NullPointerException("CoverHandler cannot have a null tile");
        return tile;
    }
}
