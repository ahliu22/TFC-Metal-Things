package net.ahliu22.tfc_metal_things;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems
{
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TFCMetalThings.MOD_ID);

    /** Metal handstones (quern grinding wheels), usable in the TFC quern via the {@code tfc:quern_handstones} tag. */
    public static final Map<MetalType, DeferredItem<Item>> HANDSTONES = mapOf(metal ->
        ITEMS.register("metal/handstone/" + metal.metalName(), () -> new Item(new Item.Properties().durability(metal.durability()).rarity(metal.rarity()))));

    /** Metal spindles, used like the TFC spindle for spinning wool into yarn. */
    public static final Map<MetalType, DeferredItem<Item>> SPINDLES = mapOf(metal ->
        ITEMS.register("metal/spindle/" + metal.metalName(), () -> new Item(new Item.Properties().durability(metal.durability()).rarity(metal.rarity()))));

    /** Metal spindle heads, forged from ingots on the anvil. Can carry a forging bonus. */
    public static final Map<MetalType, DeferredItem<Item>> SPINDLE_HEADS = mapOf(metal ->
        ITEMS.register("metal/spindle_head/" + metal.metalName(), () -> new Item(new Item.Properties().rarity(metal.rarity()))));

    private ModItems() {}

    private static Map<MetalType, DeferredItem<Item>> mapOf(Function<MetalType, DeferredItem<Item>> factory)
    {
        final Map<MetalType, DeferredItem<Item>> map = new EnumMap<>(MetalType.class);
        for (MetalType metal : MetalType.VALUES)
        {
            map.put(metal, factory.apply(metal));
        }
        return map;
    }
}
