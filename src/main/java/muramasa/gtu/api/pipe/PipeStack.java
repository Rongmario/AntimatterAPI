package muramasa.gtu.api.pipe;

import muramasa.gtu.Ref;
import muramasa.gtu.api.pipe.types.Pipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IStringSerializable;

public class PipeStack implements IStringSerializable {

    private Block block;
    private Pipe type;
    private PipeSize size;

    public PipeStack(Block block, Pipe type, PipeSize size) {
        this.block = block;
        this.type = type;
        this.size = size;
    }

    public PipeSize getSize() {
        return size;
    }

    @Override
    public String getName() {
        return type.getName() + "_" + size.getName();
    }

    public ItemStack asItemStack() {
        ItemStack stack = new ItemStack(Item.getItemFromBlock(block));
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger(Ref.KEY_PIPE_STACK_SIZE, size.ordinal());
        return stack;
    }
}