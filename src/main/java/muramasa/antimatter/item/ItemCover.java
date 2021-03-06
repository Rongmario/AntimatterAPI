package muramasa.antimatter.item;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverTiered;
import muramasa.antimatter.machine.Tier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ItemCover extends ItemBasic<ItemCover> {

    private Cover cover;

    public ItemCover(String domain, String id, Properties properties) {
        super(domain, id, properties);
    }

    public ItemCover(String domain, String id) {
        super(domain, id);
        cover = Objects.requireNonNull(AntimatterAPI.get(Cover.class, this.getId()));
        if (cover instanceof CoverTiered) {
            throw new RuntimeException("Invalid non-tiered cover instantiation");
        }
        cover.setItem(this);
    }

    public Cover getCover() {
        return cover;
    }

    public ItemCover(String domain, String id, Tier tier) {
        super(domain,id + "_" + tier.getId());
        cover = Objects.requireNonNull(AntimatterAPI.get(Cover.class, this.getId()));
        cover.setItem(this);
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        TileEntity tile = context.getWorld().getTileEntity(context.getPos());
        return itemPlaceCover(context.getPlayer(),context.getHand(),tile, context.getFace()) ? ActionResultType.SUCCESS : ActionResultType.PASS;
    }

    private boolean itemPlaceCover(PlayerEntity player, Hand hand, TileEntity tile,Direction dir) {
        if (tile != null) {
            LazyOptional<ICoverHandler> coverable = tile.getCapability(AntimatterCaps.COVERABLE,dir);
            //since we are holding an ItemCover it will try to place it
            return coverable.map(i -> i.onInteract(player,hand,dir,null)).orElse(false);
        }
        return false;
    }
}
