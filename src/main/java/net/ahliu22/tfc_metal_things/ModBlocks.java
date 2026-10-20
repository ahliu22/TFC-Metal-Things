package net.ahliu22.tfc_metal_things;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks
{
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TFCMetalThings.MOD_ID);

    /** Metal doors, openable by redstone only (like the vanilla iron door). */
    public static final Map<MetalType, DeferredBlock<DoorBlock>> DOORS = mapOf(metal ->
        BLOCKS.register("metal/door/" + metal.metalName(), () -> new DoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
            .mapColor(metal.mapColor())
            .requiresCorrectToolForDrops()
            .strength(5.0F)
            .sound(SoundType.METAL)
            .pushReaction(PushReaction.DESTROY)
            .noOcclusion())));

    /** Metal fences, crafted from six metal rods. */
    public static final Map<MetalType, DeferredBlock<FenceBlock>> FENCES = mapOf(metal ->
        BLOCKS.register("metal/fence/" + metal.metalName(), () -> new FenceBlock(metalBlockProperties(metal))));

    /** Metal fence gates, crafted from metal rods and sheets. */
    public static final Map<MetalType, DeferredBlock<FenceGateBlock>> FENCE_GATES = mapOf(metal ->
        BLOCKS.register("metal/fence_gate/" + metal.metalName(), () -> new FenceGateBlock(metalBlockProperties(metal), SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_DOOR_CLOSE)));

    /** Vertical metal support beams. Only connect to supports of the same metal. */
    public static final Map<MetalType, DeferredBlock<MetalVerticalSupportBlock>> VERTICAL_SUPPORTS = mapOf(metal ->
        BLOCKS.register("metal/vertical_support/" + metal.metalName(), () -> new MetalVerticalSupportBlock(BlockBehaviour.Properties.of()
            .mapColor(metal.mapColor())
            .requiresCorrectToolForDrops()
            .strength(3.0F, 6.0F)
            .sound(SoundType.METAL)
            .noOcclusion(), metal)));

    /** Horizontal metal support beams. */
    public static final Map<MetalType, DeferredBlock<MetalHorizontalSupportBlock>> HORIZONTAL_SUPPORTS = mapOf(metal ->
        BLOCKS.register("metal/horizontal_support/" + metal.metalName(), () -> new MetalHorizontalSupportBlock(BlockBehaviour.Properties.of()
            .mapColor(metal.mapColor())
            .requiresCorrectToolForDrops()
            .strength(3.0F, 6.0F)
            .sound(SoundType.METAL)
            .noOcclusion(), metal)));

    public static final Map<MetalType, DeferredItem<Item>> DOOR_ITEMS = mapOf(metal ->
        ModItems.ITEMS.register("metal/door/" + metal.metalName(), () -> new BlockItem(DOORS.get(metal).get(), new Item.Properties().rarity(metal.rarity()))));

    public static final Map<MetalType, DeferredItem<Item>> FENCE_ITEMS = mapOf(metal ->
        ModItems.ITEMS.register("metal/fence/" + metal.metalName(), () -> new BlockItem(FENCES.get(metal).get(), new Item.Properties().rarity(metal.rarity()))));

    public static final Map<MetalType, DeferredItem<Item>> FENCE_GATE_ITEMS = mapOf(metal ->
        ModItems.ITEMS.register("metal/fence_gate/" + metal.metalName(), () -> new BlockItem(FENCE_GATES.get(metal).get(), new Item.Properties().rarity(metal.rarity()))));

    /** One support item per metal: places the vertical form on the ground and the horizontal form against walls. */
    public static final Map<MetalType, DeferredItem<Item>> SUPPORT_ITEMS = mapOf(metal ->
        ModItems.ITEMS.register("metal/support/" + metal.metalName(), () -> new StandingAndWallBlockItem(VERTICAL_SUPPORTS.get(metal).get(), HORIZONTAL_SUPPORTS.get(metal).get(), new Item.Properties().rarity(metal.rarity()), Direction.DOWN)));

    private ModBlocks() {}

    private static BlockBehaviour.Properties metalBlockProperties(MetalType metal)
    {
        return BlockBehaviour.Properties.of()
            .mapColor(metal.mapColor())
            .requiresCorrectToolForDrops()
            .strength(6.0F, 7.0F)
            .sound(SoundType.METAL)
            .noOcclusion();
    }

    private static <T> Map<MetalType, T> mapOf(Function<MetalType, T> factory)
    {
        final Map<MetalType, T> map = new EnumMap<>(MetalType.class);
        for (MetalType metal : MetalType.VALUES)
        {
            map.put(metal, factory.apply(metal));
        }
        return map;
    }
}
