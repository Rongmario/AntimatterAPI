package muramasa.gtu.api.tileentities;

import muramasa.gtu.api.util.Utils;

public class TileEntityItemMachine extends TileEntityBasicMachine {

    @Override
    public void consumeInputs() {
        itemHandler.consumeInputs(activeRecipe.getInputItems());
    }

    @Override
    public boolean canOutput() {
        return itemHandler.canOutputsFit(activeRecipe.getOutputItems());
    }

    @Override
    public void addOutputs() {
        itemHandler.addOutputs(activeRecipe.getOutputItems());
    }

    @Override
    public boolean canRecipeContinue() {
        return Utils.doItemsMatchAndSizeValid(activeRecipe.getInputItems(), itemHandler.getInputs());
    }
}
