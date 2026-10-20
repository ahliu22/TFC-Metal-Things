package net.ahliu22.tfc_metal_things.client;

import net.ahliu22.tfc_metal_things.ModBlocks;
import net.ahliu22.tfc_metal_things.ModItems;
import net.ahliu22.tfc_metal_things.MetalType;
import net.ahliu22.tfc_metal_things.TFCMetalThings;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.bus.api.SubscribeEvent;

public final class ClientSetup
{
    @EventBusSubscriber(modid = TFCMetalThings.MOD_ID, value = Dist.CLIENT)
    public static class ForgeBusEvents
    {
        /**
         * Registers the {@code tfc:damaged} item model property (used by the handstone/spindle
         * item model overrides) on this mod's items. Mirrors what TFC does for its own handstone,
         * but scaled with max durability: the damaged texture shows in the last ~4% of durability.
         * Also puts the metal doors on the cutout render layer so the window openings in the
         * door textures render as holes instead of solid quads.
         */
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            event.enqueueWork(() -> {
                final ResourceLocation damaged = ResourceLocation.tryParse("tfc:damaged");
                for (MetalType metal : MetalType.VALUES)
                {
                    ItemProperties.register(ModItems.HANDSTONES.get(metal).get(), damaged,
                        (stack, level, entity, unused) -> stack.getDamageValue() * 25 > stack.getMaxDamage() ? 1.0F : 0.0F);
                    ItemProperties.register(ModItems.SPINDLES.get(metal).get(), damaged,
                        (stack, level, entity, unused) -> stack.getDamageValue() * 25 > stack.getMaxDamage() ? 1.0F : 0.0F);

                    ItemBlockRenderTypes.setRenderLayer(ModBlocks.DOORS.get(metal).get(), RenderType.cutout());
                }
            });
        }
    }
}
