package me.pajic.accessorify.util.compat;

import atonkish.reinfcore.screen.ReinforcedStorageScreenHandler;
import atonkish.reinfcore.util.ReinforcingMaterial;
import atonkish.reinfshulker.block.ReinforcedShulkerBoxBlock;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * Kompat mit Reinforced Shulker Boxes (reinfcore).
 *
 * <p>RSB-Boxen haben material-abhaengig mehr als 27 Slots (Kupfer 45, Eisen 54, Gold 81,
 * Diamant/Netherit 108). Die Vanilla-{@code ShulkerBoxMenu} ist dagegen fest auf 3x9
 * ausgelegt. Damit eine Box aus dem Shulker-Accessory-Slot heraus mit RSBs eigenem
 * Screen und dem vollen Slot-Raster oeffnet (so wie beim Platzieren in der Welt),
 * delegiert accessorify an {@code ReinforcedStorageScreenHandler.createShulkerBoxScreen}.
 *
 * <p>Der 4-Argumente-Overload akzeptiert ein beliebiges {@link Container}; accessorifys
 * {@code ShulkerBoxAccessoryContainerMenu} implementiert dieses Interface bereits und
 * uebernimmt das Lesen/Schreiben der {@code minecraft:container}-Komponente.
 *
 * <p><b>Wichtig:</b> Diese Klasse darf nur geladen werden, wenn der Mod zur Laufzeit
 * installiert ist -- Aufrufer muessen {@code CompatFlags.REINFORCED_SHULKER_BOXES_LOADED}
 * pruefen, bevor sie hierher referenzieren. Alle Rueckgaben sind bewusst auf
 * Minecraft-Typen (bzw. {@code int}) beschraenkt, damit kein reinfcore-Typ nach aussen
 * durchsickert und die Ladung der Klasse ausloest.
 */
public class ReinforcedShulkerBoxesCompat {

    private ReinforcedShulkerBoxesCompat() {
    }

    private static ReinforcingMaterial materialOf(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        Block block = Block.byItem(stack.getItem());
        return block instanceof ReinforcedShulkerBoxBlock reinforced ? reinforced.getMaterial() : null;
    }

    /**
     * @return {@code true}, wenn der Stack eine von RSB platzierte Shulker-Box ist
     */
    public static boolean isReinforcedShulkerBox(ItemStack stack) {
        return materialOf(stack) != null;
    }

    /**
     * @return die vom Material vorgegebene Slot-Anzahl, oder {@code -1} wenn der Stack
     *         keine RSB-Box ist
     */
    public static int materialSize(ItemStack stack) {
        ReinforcingMaterial material = materialOf(stack);
        return material == null ? -1 : material.getSize();
    }

    /**
     * Baut RSBs eigenes Screen-Handler ueber den mitgelieferten Container.
     *
     * @return den Handler, oder {@code null} wenn der Stack keine RSB-Box ist
     */
    public static AbstractContainerMenu createShulkerBoxMenu(ItemStack stack, int syncId, Inventory inventory, Container container) {
        ReinforcingMaterial material = materialOf(stack);
        if (material == null) {
            return null;
        }
        return ReinforcedStorageScreenHandler.createShulkerBoxScreen(material, syncId, inventory, container);
    }
}
