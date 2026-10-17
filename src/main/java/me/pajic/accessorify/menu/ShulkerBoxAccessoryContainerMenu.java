package me.pajic.accessorify.menu;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
//? if >= 1.21.10
import net.minecraft.world.entity.ContainerUser;
//? if >= 1.21.1 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemContainerContents;
//?}
//? if >= 1.21.11 {
import me.pajic.accessorify.util.CompatFlags;
import me.pajic.accessorify.util.compat.ReinforcedShulkerBoxesCompat;
//?}
//? if 1.20.1
//import net.minecraft.nbt.CompoundTag;

public class ShulkerBoxAccessoryContainerMenu implements Container, MenuProvider {

    private final ItemStack shulker;
    protected NonNullList<ItemStack> items;

    public ShulkerBoxAccessoryContainerMenu(ItemStack shulker, int size) {
        this.shulker = shulker;
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    /**
     * Vanilla-Shulker-Boxen haben 27 Slots, aber ModBoxen koennen deutlich mehr haben
     * (z.B. reinforced-shulker-boxes: Kupfer 45, Eisen 54, Gold 81, Diamant/Netherit 108).
     * Die Menueliste muss so gross sein wie die gespeicherte container-Komponente, sonst
     * kopiert startOpen() nur die ersten 27 Eintraege und schreibt stopOpen() die Komponente
     * auf 27 zuruecks -- alle Items jenseits Slot 27 waeren dann dauerhaft verloren.
     * Hinweis: die Komponente ist bereits auf den letzten nicht-leeren Slot gekuerzt,
     * stream() liefert daher exakt die gespeicherte Anzahl.
     */
    public static int requiredSize(ItemStack shulker) {
        //? if >= 1.21.1 {
        int stored = (int) shulker.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).stream().count();
        int generic = Math.max(27, stored);
        //?} else {
        //int generic = 27;
        //?}
        //? if >= 1.21.11 {
        // Reinforced Shulker Boxes: das Material bestimmt die Slot-Anzahl (Kupfer 45,
        // Eisen 54, Gold 81, Diamant/Netherit 108). Fuer diese Boxen muss die volle
        // Groesse her, sonst faellt RSBs Screen-Handler auf 27 Slots zurueck.
        if (CompatFlags.REINFORCED_SHULKER_BOXES_LOADED) {
            int reinforcedSize = ReinforcedShulkerBoxesCompat.materialSize(shulker);
            if (reinforcedSize > 0) {
                return Math.max(reinforcedSize, generic);
            }
        }
        //?}
        return generic;
    }

    //? if < 1.21.10 {
    /*@Override
    public void startOpen(@NotNull Player player) {
        //? if >= 1.21.1
        shulker.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
        //? if 1.20.1 {
        /^CompoundTag tag = getOrCreateBlockEntityTag();
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        if (tag.contains("Items", 9)) {
            ContainerHelper.loadAllItems(tag, items);
        }
        ^///?}
        player.playSound(SoundEvents.SHULKER_BOX_OPEN);
    }
    @Override
    public void stopOpen(@NotNull Player player) {
        //? if >= 1.21.1
        shulker.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        //? if 1.20.1
        //ContainerHelper.saveAllItems(getOrCreateBlockEntityTag(), items, true);
        player.playSound(SoundEvents.SHULKER_BOX_CLOSE);
    }
    *///?} else {
    @Override
    public void startOpen(@NotNull ContainerUser user) {
        if (user instanceof Player player) {
            shulker.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
            player.playSound(SoundEvents.SHULKER_BOX_OPEN);
        }
    }
    @Override
    public void stopOpen(@NotNull ContainerUser user) {
        if (user instanceof Player player) {
            shulker.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
            player.playSound(SoundEvents.SHULKER_BOX_CLOSE);
        }
    }
    //?}

    //? if 1.20.1 {
    /*private CompoundTag getOrCreateBlockEntityTag() {
        CompoundTag tag = shulker.getTagElement("BlockEntityTag");
        if (tag == null) {
            tag = new CompoundTag();
            tag.put("BlockEntityTag", new CompoundTag());
            shulker.setTag(tag);
        }
        return tag;
    }
    *///?}

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(items, slot, amount);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, @Nullable ItemStack stack) {
        this.items.set(slot, stack == null ? ItemStack.EMPTY : stack);
        if (stack != null && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return shulker.getHoverName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        //? if >= 1.21.11 {
        // Reinforced Shulker Boxes: RSBs eigenen Screen mit dem vollen Slot-Raster
        // verwenden (identisch zum Oeffnen der platzierten Box in der Welt).
        if (CompatFlags.REINFORCED_SHULKER_BOXES_LOADED) {
            AbstractContainerMenu reinforced = ReinforcedShulkerBoxesCompat.createShulkerBoxMenu(shulker, i, inventory, this);
            if (reinforced != null) {
                return reinforced;
            }
        }
        //?}
        return new ShulkerBoxMenu(i, inventory, this);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return !(Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock);
    }
}
