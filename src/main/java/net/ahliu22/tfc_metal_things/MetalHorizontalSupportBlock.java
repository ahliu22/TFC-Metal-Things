package net.ahliu22.tfc_metal_things;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A metal horizontal support beam. Mirrors TFC's wooden horizontal support:
 * when placed against a same-metal support, it bridges up to five blocks
 * towards the next vertical support, and breaks itself when its connections
 * become incomplete. Only connects to supports of the same metal.
 */
public class MetalHorizontalSupportBlock extends MetalVerticalSupportBlock
{
    private final Map<BlockState, VoxelShape> cachedShapes;

    public MetalHorizontalSupportBlock(Properties properties, MetalType metal)
    {
        super(properties, metal);
        cachedShapes = makeShapes(box(5.0, 10.0, 5.0, 11.0, 16.0, 11.0));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        Direction direction = null;
        for (Direction checkDir : Direction.Plane.HORIZONTAL)
        {
            if (isSameFamily(level.getBlockState(pos.relative(checkDir))))
            {
                direction = checkDir.getOpposite();
                break;
            }
        }
        if (direction == null)
        {
            return;
        }

        final int distance = getHorizontalDistance(direction, level, pos);
        if (distance == 0 || stack.getCount() < distance)
        {
            level.destroyBlock(pos, true);
        }
        else if (distance > 0)
        {
            stack.shrink(distance - 1); // first one will be used by BlockItem
            final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            for (int i = 1; i < distance; i++)
            {
                mutablePos.set(pos).move(direction, i);
                final BlockState stateAt = level.getBlockState(mutablePos);
                if (canPlaceInto(stateAt))
                {
                    level.setBlock(mutablePos, defaultBlockState()
                        .setValue(propertyFor(direction), true)
                        .setValue(propertyFor(direction.getOpposite()), true)
                        .setValue(WATERLOGGED, stateAt.getFluidState().getType() == Fluids.WATER), 2);
                    mutablePos.move(Direction.DOWN);
                    level.scheduleTick(mutablePos, level.getFluidState(mutablePos).getType(), 3);
                }
            }
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing.getAxis().isHorizontal())
        {
            state = state.setValue(propertyFor(facing), isSameFamily(facingState));
            // if support incomplete, try the other way (E/W vs N/S)
            if (!isSameFamily(facingState) || !isSameFamily(level.getBlockState(currentPos.relative(facing.getOpposite()))))
            {
                // if support incomplete here, we definitely can break
                if (!isSameFamily(level.getBlockState(currentPos.relative(facing.getClockWise())))
                    || !isSameFamily(level.getBlockState(currentPos.relative(facing.getCounterClockWise()))))
                {
                    return Blocks.AIR.defaultBlockState();
                }
            }
            return state;
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    /**
     * In 1.16 canPlaceBlockOnSide is no longer a thing, instead we use this to trick ItemBlock into not placing in invalid conditions.
     * This eliminates cases of placing and then immediately breaking.
     */
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        for (Direction d : Direction.Plane.HORIZONTAL)
        {
            if (getHorizontalDistance(d, level, pos) > 0) // we found a pole it could connect to
            {
                if (isSameFamily(level.getBlockState(pos.relative(d.getOpposite()))))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        final VoxelShape shape = cachedShapes.get(state);
        if (shape != null) return shape;
        throw new IllegalArgumentException("Asked for Support VoxelShape that was not cached");
    }

    private int getHorizontalDistance(Direction direction, LevelReader level, BlockPos pos)
    {
        int distance = -1;
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 5; i++)
        {
            cursor.set(pos).move(direction, i);
            final BlockState stateAt = level.getBlockState(cursor);
            if (!isSameFamily(stateAt) && !canPlaceInto(stateAt))
            {
                return 0;
            }
            cursor.move(direction, 1);
            final BlockState state = level.getBlockState(cursor);
            if (isSameFamily(state)) // vertical only?
            {
                distance = i;
                break;
            }
        }
        return distance == -1 ? 0 : distance + 1;
    }
}
