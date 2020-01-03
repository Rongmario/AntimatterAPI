package muramasa.gtu.data.providers;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

import javax.annotation.Nonnull;

public class GregTechBlockStateProvider extends PublicBlockStateProvider {

    public GregTechBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Ref.MODID, exFileHelper);
    }

    @Nonnull
    @Override
    public String getName() {
        return Ref.MODID + " BlockStates";
    }

    @Override
    protected void registerStatesAndModels() {
        GregTechAPI.all(Block.class).forEach(b -> {
            if (b instanceof IModelProvider) ((IModelProvider) b).onBlockModelBuild(b, this);
        });
    }

    public BlockModelBuilder getBuilder(Block block) {
        return getBuilder(block.getRegistryName().getPath());
    }

    public BlockModelBuilder cubeAll(Block block, ResourceLocation texture) {
        return super.cubeAll(block.getRegistryName().toString(), texture);
    }

    public void simpleState(Block block, ResourceLocation texture) {
        simpleBlock(block, getSimpleModel(block, texture));
    }

    public void layeredState(Block block, ResourceLocation base, ResourceLocation overlay) {
        simpleBlock(block, getLayeredModel(block, base, overlay));
    }

    public void texturedState(Block block, ResourceLocation[] textures) {
        if (textures.length == 1) {
            simpleState(block, textures[0]);
        } else if (textures.length == 2) {
            layeredState(block, textures[0], textures[1]);
        }
    }

    public BlockModelBuilder getSimpleModel(Block block, ResourceLocation texture) {
        return getBuilder(block).parent(getExistingFile(modLoc("block/preset/simple"))).texture("all", texture);
    }

    public BlockModelBuilder getLayeredModel(Block block, ResourceLocation base, ResourceLocation overlay) {
        return getBuilder(block).parent(getExistingFile(modLoc("block/preset/layered"))).texture("base", base).texture("overlay", overlay);
    }
}