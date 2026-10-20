package net.ahliu22.tfc_metal_things;

import java.util.Map;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(TFCMetalThings.MOD_ID)
public class TFCMetalThings
{
    public static final String MOD_ID = "tfc_metal_things";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> METAL_THINGS_TAB = CREATIVE_MODE_TABS.register("metal_things", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.tfc_metal_things.metal_things"))
        .icon(() -> ModBlocks.DOOR_ITEMS.get(MetalType.BRONZE).get().getDefaultInstance())
        .displayItems((parameters, output) -> {
            for (MetalType metal : MetalType.VALUES)
            {
                accept(output, ModItems.HANDSTONES, metal);
                accept(output, ModItems.SPINDLE_HEADS, metal);
                accept(output, ModItems.SPINDLES, metal);
                accept(output, ModBlocks.DOOR_ITEMS, metal);
                accept(output, ModBlocks.FENCE_ITEMS, metal);
                accept(output, ModBlocks.FENCE_GATE_ITEMS, metal);
                accept(output, ModBlocks.SUPPORT_ITEMS, metal);
            }
        })
        .build());

    public TFCMetalThings(IEventBus modEventBus)
    {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        LOGGER.info("TFC Metal Things loaded");
    }

    private static void accept(CreativeModeTab.Output output, Map<MetalType, ? extends DeferredItem<? extends Item>> map, MetalType metal)
    {
        final DeferredItem<? extends Item> item = map.get(metal);
        if (item != null)
        {
            output.accept(item.get());
        }
    }
}
