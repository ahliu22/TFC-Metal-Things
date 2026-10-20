package net.ahliu22.tfc_metal_things;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * Adds a "support range" tooltip line to support beam items: this mod's metal supports
 * (range read from {@link MetalType}) and TFC's wooden supports (matched via the
 * {@code tfc:support_beams} tag; their range is 9 x 9 x 5, as defined in TFC's
 * {@code tfc/support} data).
 */
@EventBusSubscriber(modid = TFCMetalThings.MOD_ID)
public final class SupportTooltipHandler
{
    private static final TagKey<Item> TFC_SUPPORT_BEAMS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("tfc", "support_beams"));

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        final ItemStack stack = event.getItemStack();
        for (MetalType metal : MetalType.VALUES)
        {
            final DeferredItem<Item> item = ModBlocks.SUPPORT_ITEMS.get(metal);
            if (item != null && stack.is(item.get()))
            {
                final int horizontal = metal.supportHorizontal() * 2 + 1;
                final int vertical = metal.supportVertical() * 2 + 1;
                addRange(event.getToolTip(), horizontal, vertical);
                return;
            }
        }
        if (stack.is(TFC_SUPPORT_BEAMS))
        {
            addRange(event.getToolTip(), 9, 5); // TFC wooden supports: horizontal radius 4, up/down 2
        }
    }

    private static void addRange(List<Component> tooltip, int horizontal, int vertical)
    {
        tooltip.add(Component.translatable("tooltip.tfc_metal_things.support_range", horizontal, horizontal, vertical).withStyle(ChatFormatting.GRAY));
    }

    private SupportTooltipHandler() {}
}
