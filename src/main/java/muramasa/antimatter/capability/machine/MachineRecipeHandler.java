package muramasa.antimatter.capability.machine;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.nbt.CompoundNBT;

import static muramasa.antimatter.machine.MachineFlag.RECIPE;
import static muramasa.antimatter.machine.MachineState.*;

public class MachineRecipeHandler<T extends TileEntityMachine> implements IMachineHandler {

    protected T tile;
    protected Recipe activeRecipe;
    protected int curProgress, maxProgress;
    protected int overclock;

    public MachineRecipeHandler(T tile) {
        this.tile = tile;
    }

    public void onUpdate() {
        if (tile.isClientSide()) return;
        if (tile.getMachineState() != IDLE) tickMachineLoop();
    }

    protected void tickMachineLoop() {
        //        if (getMachineState() == ACTIVE || getMachineState() == OUTPUT_FULL) {
//            setMachineState(tickRecipe());
//        } else if (getMachineState() == NO_POWER) {
//            setMachineState(IDLE);
//            checkRecipe();
//        }
        switch (tile.getMachineState()) {
            case ACTIVE:
                tile.setMachineState(tickRecipe());
                break;
            case OUTPUT_FULL:
                tile.setMachineState(tickRecipe());
                break;
            case NO_POWER:
                noPowerCheck();
                break;
        }
    }

    public Recipe findRecipe() {
        return tile.getMachineType().getRecipeMap().find(tile.itemHandler.orElse(null), tile.fluidHandler.orElse(null));
    }
    //called when a new recipe is found, to process overclocking
    public void activateRecipe() {
        //if (canOverclock)
        curProgress = 0;
        overclock = 0;
        if (this.tile.getMachineTier().getVoltage() > activeRecipe.getPower()) {
            int tempoverclock =(this.tile.getMachineTier().getVoltage() / Ref.V[Utils.getVoltageTier(activeRecipe.getPower())]);
            while(tempoverclock > 1) {
                tempoverclock >>= 2;
                overclock++;
            }
        }
        maxProgress = Math.max(1,activeRecipe.getDuration() / (1 << overclock));
        tile.setMachineState(ACTIVE);
        onRecipeFound();
    }

    public void checkRecipe() {
        if (tile.getMachineState().allowRecipeCheck()) { //No active recipes, see of contents match one
            System.out.println("check recipe");
            if (!tile.hadFirstTick()) return; //TODO fixme
            if ((activeRecipe = findRecipe()) != null) {
                if (activeRecipe.getPower() > tile.getMaxInputVoltage()) {
                    return;
                    //TODO machine tier cannot process recipe
                }
                activateRecipe();
            }
        }
    }

    public void noPowerCheck() {
        if (activeRecipe != null) {
            tickRecipe();
        } else {
            checkRecipe();
        }
    }

    public MachineState tickRecipe() {
        onRecipeTick();
        if (activeRecipe == null) return IDLE; //TODO this null check added if saved state triggers tickMachineLoop, but there is no recipe to process
        if (curProgress == maxProgress) { //End of current recipe cycle, deposit items
            if (!canOutput()) return OUTPUT_FULL; //Return and loop until outputs can be added
            curProgress = 0;
            addOutputs(); //Add outputs and reset to process next recipe cycle
            if (!canRecipeContinue()) {
                resetRecipe();
                return IDLE;
            } else {
                return ACTIVE;
            }
        } else {
            //Calculate per recipe tick so user has risk of losing items
            if (!consumeResourceForRecipe()) return curProgress == 0 ? NO_POWER : POWER_LOSS;
            if (curProgress == 0) consumeInputs(); //Consume recipe inputs on first recipe tick
            curProgress++;
            return ACTIVE;
        }
    }

    public void consumeInputs() {
        tile.itemHandler.ifPresent(h -> h.consumeInputs(activeRecipe.getInputItems()));
        tile.fluidHandler.ifPresent(h -> h.consumeInputs(activeRecipe.getInputFluids()));
    }

    public boolean canOutput() {
        if (tile.itemHandler.isPresent() && activeRecipe.hasOutputItems() && !tile.itemHandler.get().canOutputsFit(activeRecipe.getOutputItems())) return false;
        if (tile.fluidHandler.isPresent() && activeRecipe.hasOutputFluids() && !tile.fluidHandler.get().canOutputsFit(activeRecipe.getOutputFluids())) return false;
        return true;
    }

    public void addOutputs() {
        tile.itemHandler.ifPresent(h -> {
            h.addOutputs(activeRecipe.getOutputItems());
            tile.onMachineEvent(MachineEvent.ITEMS_OUTPUTTED);
        });
        tile.fluidHandler.ifPresent(h -> h.addOutputs(activeRecipe.getOutputFluids()));
    }

    public boolean canRecipeContinue() {
        if (tile.itemHandler.isPresent() && activeRecipe.hasInputItems() && !Utils.doItemsMatchAndSizeValid(activeRecipe.getInputItems(), tile.itemHandler.get().getInputs())) return false;
        if (tile.fluidHandler.isPresent() && activeRecipe.hasInputFluids() && !Utils.doFluidsMatchAndSizeValid(activeRecipe.getInputFluids(), tile.fluidHandler.get().getInputs())) return false;
        return true;
    }

    public boolean consumeResourceForRecipe() {
        if (tile.energyHandler.isPresent()) {
            if (tile.energyHandler.get().extract((activeRecipe.getPower() * (1 << overclock)), true) == activeRecipe.getPower()*(1 << overclock)) {
                tile.energyHandler.get().extract((activeRecipe.getPower() * (1 << overclock)), false);
                return true;
            }
        }
        return false;
    }

    public void resetRecipe() {
        activeRecipe = null;
        overclock = 0;
    }

    /** Events **/
    public void onRecipeFound() {
        //NOOP
    }

    public void onRecipeTick() {
        //NOOP
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (event instanceof ContentEvent) {
            //TODO seems to fire before the inventory has actually changed?
            switch ((ContentEvent) event) {
                case ITEM_INPUT_CHANGED:
                    if (tile.getMachineState().allowLoopTick() || tile.getMachineState() == NO_POWER) tickMachineLoop();
                    if (tile.getMachineType().has(RECIPE)) checkRecipe();
                    break;
                case ITEM_OUTPUT_CHANGED:
                    if (tile.getMachineState().allowLoopTick() || tile.getMachineState() == NO_POWER) tickMachineLoop();
                    break;
            }
            tile.markDirty(); //TODO determine if needed
        }
    }

    public int getCurProgress() {
        return curProgress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }


    /** NBT **/
    // TODO: Finish
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        return tag;
    }

    public void deserialize(CompoundNBT compound) {
    }
}
