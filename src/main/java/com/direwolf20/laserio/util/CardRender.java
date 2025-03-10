package com.direwolf20.laserio.util;

import com.direwolf20.laserio.client.blockentityrenders.LaserNodeBERender;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.common.items.cards.CardRedstone;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

public class CardRender {
    public Direction direction;
    public int cardSlot;
    public float r;
    public float g;
    public float b;
    public BlockPos startBlock;
    public BlockPos endBlock;
    public float diffX;
    public float diffY;
    public float diffZ;
    public Vector3f startLaser;
    public Vector3f endLaser;
    public float[] floatColors;

    public CardRender(Direction direction, int cardSlot, ItemStack card, BlockPos start, Level level, boolean enabled) {
        this.direction = direction;
        this.cardSlot = cardSlot;
        this.startBlock = start;
        endBlock = startBlock.relative(direction);
        BlockState targetState = level.getBlockState(endBlock);
        VoxelShape voxelShape = targetState.getShape(level, endBlock);
        BaseCard cardItem = (BaseCard) card.getItem();
        switch (cardItem.getCardType()) {
            case ITEM -> {
                r = 0f;
                g = 1f;
                b = 0f;
            }
            case FLUID -> {
                r = 0f;
                g = 0f;
                b = 1f;
            }
            case ENERGY -> {
                r = 1f;
                g = 1f;
                b = 0f;
            }
            case REDSTONE -> {
                r = 1f;
                g = 0f;
                b = 0f;
            }
            case CHEMICAL -> {
                r = 1f;
                g = 0f;
                b = 1f;
            }
            default -> {
                r = 0f;
                g = 0f;
                b = 0f;
            }
        }
        if (!enabled) {
            r /= 4f;
            g /= 4f;
            b /= 4f;
        }
        Vector3f offset = MiscTools.findOffset(direction, cardSlot, LaserNodeBERender.OFFSETS);
        Vector3f shapeOffset = shapeOffset(offset, voxelShape, startBlock, endBlock, direction, level, targetState);
        diffX = shapeOffset.x();
        diffY = shapeOffset.y();
        diffZ = shapeOffset.z();
        boolean reverse = !direction.equals(Direction.DOWN);
        if (cardItem instanceof CardRedstone) {
            if (BaseCard.getNamedTransferMode(card) != BaseCard.TransferMode.INSERT) {
                reverse = !reverse;
            }
        } else {
            if (BaseCard.getNamedTransferMode(card) != BaseCard.TransferMode.EXTRACT) {
                reverse = !reverse;
            }
        }
        if (cardItem instanceof CardRedstone || BaseCard.getNamedTransferMode(card) == BaseCard.TransferMode.SENSOR) {
            floatColors = LaserNodeBERender.COLORS[BaseCard.getRedstoneChannel(card)].getColorComponents(new float[3]);
        } else {
            floatColors = LaserNodeBERender.COLORS[BaseCard.getChannel(card)].getColorComponents(new float[3]);
        }
        if (reverse) {
            endLaser = new Vector3f(offset.x(), offset.y(), offset.z());
            startLaser = new Vector3f(diffX, diffY, diffZ);
        } else {
            startLaser = new Vector3f(offset.x(), offset.y(), offset.z());
            endLaser = new Vector3f(diffX, diffY, diffZ);
        }
    }

    public static Vector3f shapeOffset(Vector3f offset, VoxelShape voxelShape, BlockPos startBlock, BlockPos endBlock, Direction direction, Level level, BlockState targetState) {
        float diffX, diffY, diffZ;
        diffX = endBlock.getX() + offset.x() - startBlock.getX();
        diffY = endBlock.getY() + offset.y() - startBlock.getY();
        diffZ = endBlock.getZ() + offset.z() - startBlock.getZ();
        if (!voxelShape.isEmpty()) {
            diffX = (float) (((voxelShape.bounds().maxX - voxelShape.bounds().minX) * diffX) + voxelShape.bounds().minX);
            diffY = (float) (((voxelShape.bounds().maxY - voxelShape.bounds().minY) * diffY) + voxelShape.bounds().minY);
            diffZ = (float) (((voxelShape.bounds().maxZ - voxelShape.bounds().minZ) * diffZ) + voxelShape.bounds().minZ);
            if (direction.equals(Direction.WEST)) {
                if (targetState.getOffset(level, endBlock).x != 0) {
                    diffX = -1 - (float) (targetState.getOffset(level, endBlock).x + (float) 1 / 16 - (voxelShape.bounds().maxX - voxelShape.bounds().minX));
                } else {
                    diffX = -1 + (float) voxelShape.bounds().maxX;
                }
                offset.x = (offset.x() - 0.1875f);
            } else if (direction.equals(Direction.EAST)) {
                if (targetState.getOffset(level, endBlock).x != 0) {
                    diffX = 1 + (float) (targetState.getOffset(level, endBlock).x - (float) 1 / 16 + (voxelShape.bounds().maxX - voxelShape.bounds().minX));
                } else {
                    diffX = 1 + (float) voxelShape.bounds().minX;
                }
                offset.x = (offset.x() + 0.1875f);
            } else if (direction.equals(Direction.SOUTH)) {
                if (targetState.getOffset(level, endBlock).z != 0) {
                    diffZ = 1 + (float) (targetState.getOffset(level, endBlock).z - (float) 1 / 16 + (voxelShape.bounds().maxZ - voxelShape.bounds().minZ));
                } else {
                    diffZ = 1 + (float) voxelShape.bounds().minZ;
                }
                offset.z = (offset.z() + 0.1875f);
            } else if (direction.equals(Direction.NORTH)) {
                if (targetState.getOffset(level, endBlock).z != 0) {
                    diffZ = (float) (targetState.getOffset(level, endBlock).z + (float) 1 / 16 - (voxelShape.bounds().maxZ - voxelShape.bounds().minZ));
                } else {
                    diffZ = -1 + (float) voxelShape.bounds().maxZ;
                }
                offset.z = (offset.z() - 0.1875f);
            } else if (direction.equals(Direction.UP)) {
                if (targetState.getOffset(level, endBlock).y != 0) {
                    diffY = 1 + (float) (targetState.getOffset(level, endBlock).y - (float) 1 / 16 + (voxelShape.bounds().maxY - voxelShape.bounds().minY));
                } else {
                    diffY = 1 + (float) voxelShape.bounds().minY;
                }
                offset.y = (offset.y() + 0.1875f);
            } else if (direction.equals(Direction.DOWN)) {
                if (targetState.getOffset(level, endBlock).y != 0) {
                    diffY = (float) (targetState.getOffset(level, endBlock).y + (float) 1 / 16 - (voxelShape.bounds().maxY - voxelShape.bounds().minY));
                } else {
                    diffY = -1 + (float) voxelShape.bounds().maxY;
                }
                offset.y = (offset.y() - 0.1875f);
            }
        }
        return new Vector3f(diffX, diffY, diffZ);
    }
}