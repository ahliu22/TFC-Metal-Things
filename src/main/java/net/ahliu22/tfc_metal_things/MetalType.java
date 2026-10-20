package net.ahliu22.tfc_metal_things;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.MapColor;

/**
 * The nine TFC metals this mod adds items for, with per-metal properties:
 * durability (handstone / spindle), anvil tier, melt temperature, molten fluid
 * id, map color, rarity, and support beam range (horizontal radius, vertical up/down).
 */
public enum MetalType implements StringRepresentable
{
    COPPER("copper", 600, 1, 1080F, "copper", MapColor.COLOR_ORANGE, Rarity.COMMON, 5, 2),
    BRONZE("bronze", 1300, 2, 950F, "bronze", MapColor.TERRACOTTA_ORANGE, Rarity.COMMON, 6, 2),
    BISMUTH_BRONZE("bismuth_bronze", 1200, 2, 985F, "bismuth_bronze", MapColor.TERRACOTTA_BLUE, Rarity.COMMON, 6, 2),
    BLACK_BRONZE("black_bronze", 1460, 2, 1070F, "black_bronze", MapColor.TERRACOTTA_PINK, Rarity.COMMON, 6, 2),
    WROUGHT_IRON("wrought_iron", 2200, 3, 1535F, "cast_iron", MapColor.METAL, Rarity.COMMON, 7, 3),
    STEEL("steel", 3300, 4, 1540F, "steel", MapColor.COLOR_LIGHT_GRAY, Rarity.UNCOMMON, 8, 3),
    BLACK_STEEL("black_steel", 4200, 5, 1485F, "black_steel", MapColor.COLOR_BLACK, Rarity.RARE, 10, 4),
    BLUE_STEEL("blue_steel", 6500, 6, 1540F, "blue_steel", MapColor.COLOR_BLUE, Rarity.EPIC, 12, 5),
    RED_STEEL("red_steel", 6500, 6, 1540F, "red_steel", MapColor.COLOR_RED, Rarity.EPIC, 12, 5);

    public static final MetalType[] VALUES = values();

    private final String name;
    private final int durability;
    private final int tier;
    private final float meltTemperature;
    private final String fluidName; // TFC molten metal fluid id (wrought iron re-melts into cast iron, like TFC)
    private final MapColor mapColor;
    private final Rarity rarity;
    private final int supportHorizontal;
    private final int supportVertical;

    MetalType(String name, int durability, int tier, float meltTemperature, String fluidName, MapColor mapColor, Rarity rarity, int supportHorizontal, int supportVertical)
    {
        this.name = name;
        this.durability = durability;
        this.tier = tier;
        this.meltTemperature = meltTemperature;
        this.fluidName = fluidName;
        this.mapColor = mapColor;
        this.rarity = rarity;
        this.supportHorizontal = supportHorizontal;
        this.supportVertical = supportVertical;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    public String metalName()
    {
        return name;
    }

    /** Durability for the metal handstone and metal spindle. */
    public int durability()
    {
        return durability;
    }

    /** Minimum anvil tier required to forge items of this metal. */
    public int tier()
    {
        return tier;
    }

    public float meltTemperature()
    {
        return meltTemperature;
    }

    public String fluidName()
    {
        return fluidName;
    }

    public MapColor mapColor()
    {
        return mapColor;
    }

    public Rarity rarity()
    {
        return rarity;
    }

    public int supportHorizontal()
    {
        return supportHorizontal;
    }

    public int supportVertical()
    {
        return supportVertical;
    }
}
