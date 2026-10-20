package net.ahliu22.tfc_metal_things;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A metal vertical support beam. Behaves like TFC's wooden vertical support
 * (auto-builds three high, connects arms towards neighbours, breaks when the
 * block below can no longer hold it), except it only ever connects to supports
 * of the same metal.
 */
public class MetalVerticalSupportBlock extends Block implements SimpleWaterloggedBlock
{
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected final MetalType metal;
    private final Map<BlockState, VoxelShape> cachedShapes;

    public MetalVerticalSupportBlock(Properties properties, MetalType metal)
    {
        super(properties);
        this.metal = metal;
        registerDefaultState(stateDefinition.any()
            .setValue(NORTH, false).setValue(EAST, false)
            .setValue(SOUTH, false).setValue(WEST, false)
            .setValue(WATERLOGGED, false));
        cachedShapes = makeShapes(box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0));
    }

    /** Is the given state part of the same metal support family as this block? */
    public boolean isSameFamily(BlockState state)
    {
        return state.is(ModBlocks.VERTICAL_SUPPORTS.get(metal).get()) || state.is(ModBlocks.HORIZONTAL_SUPPORTS.get(metal).get());
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState()
            .setValue(NORTH, isSameFamily(context.getLevel().getBlockState(context.getClickedPos().north())))
            .setValue(EAST, isSameFamily(context.getLevel().getBlockState(context.getClickedPos().east())))
            .setValue(SOUTH, isSameFamily(context.getLevel().getBlockState(context.getClickedPos().south())))
            .setValue(WEST, isSameFamily(context.getLevel().getBlockState(context.getClickedPos().west())))
            .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if (level.isClientSide() || placer == null) return;
        if (stack.getCount() > 2 && !placer.isShiftKeyDown()) // need two because the item block hasn't shrunk the stack yet
        {
            final BlockPos above = pos.above(), above2 = above.above();
            final BlockState stateAbove = level.getBlockState(above), stateAbove2 = level.getBlockState(above2);
            if (canPlaceInto(stateAbove) && canPlaceInto(stateAbove2))
            {
                if (level.getEntities(null, new AABB(above)).isEmpty())
                {
                    level.setBlock(above, defaultBlockState().setValue(WATERLOGGED, stateAbove.getFluidState().getType() == Fluids.WATER), 2);
                    if (level.getEntities(null, new AABB(above2)).isEmpty())
                    {
                        level.setBlock(above2, defaultBlockState().setValue(WATERLOGGED, stateAbove2.getFluidState().getType() == Fluids.WATER), 2);
                        stack.shrink(2);
                    }
                    else
                    {
                        stack.shrink(1);
                    }
                }
            }
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing.getAxis().isHorizontal())
        {
            return state.setValue(propertyFor(facing), isSameFamily(facingState));
        }
        else if (facing == Direction.DOWN)
        {
            if (isSameFamily(facingState) || facingState.isFaceSturdy(level, facingPos, Direction.UP, SupportType.CENTER))
            {
                return state;
            }
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final BlockPos belowPos = pos.below();
        final BlockState belowState = level.getBlockState(belowPos);
        return isSameFamily(belowState) || belowState.isFaceSturdy(level, belowPos, Direction.UP, SupportType.CENTER);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        final VoxelShape shape = cachedShapes.get(state);
        if (shape != null) return shape;
        throw new IllegalArgumentException("Asked for Support VoxelShape that was not cached");
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(NORTH, EAST, SOUTH, WEST, WATERLOGGED));
    }

    @Override
    protected FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    protected static BooleanProperty propertyFor(Direction direction)
    {
        return switch (direction)
        {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            default -> throw new IllegalArgumentException("No support arm property for " + direction);
        };
    }

    protected boolean canPlaceInto(BlockState state)
    {
        return state.isAir() || state.getFluidState().getType() == Fluids.WATER;
    }

    protected Map<BlockState, VoxelShape> makeShapes(VoxelShape middleShape)
    {
        final Map<BlockState, VoxelShape> shapes = new java.util.HashMap<>();
        for (BlockState state : getStateDefinition().getPossibleStates())
        {
            VoxelShape shape = middleShape;
            for (Direction d : Direction.Plane.HORIZONTAL)
            {
                if (state.getValue(propertyFor(d)))
                {
                    VoxelShape joinShape = switch (d)
                        {
                            case NORTH -> box(5.0, 10.0, 0.0, 11.0, 16.0, 10.0);
                            case SOUTH -> box(5.0, 10.0, 11.0, 11.0, 16.0, 16.0);
                            case EAST -> box(11.0, 10.0, 5.0, 16.0, 16.0, 11.0);
                            case WEST -> box(0.0, 10.0, 5.0, 5.0, 16.0, 11.0);
                            default -> Shapes.empty();
                        };
                    shape = Shapes.or(shape, joinShape);
                }
            }
            shapes.put(state, shape);
        }
        return shapes;
    }
}
