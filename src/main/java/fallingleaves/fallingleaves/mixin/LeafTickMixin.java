package fallingleaves.fallingleaves.mixin;

import fallingleaves.fallingleaves.FallingLeaves;
import fallingleaves.fallingleaves.client.FallingLeavesClient;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin(LeavesBlock.class)
public class LeafTickMixin {
    @Inject(at = @At("HEAD"), method = "randomDisplayTick")
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo info) {
        if (random.nextInt(75) == 0) {
            BlockPos ogPos = pos;
            Direction direction = Direction.DOWN;
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            if (!(!blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite()) && !blockState.isTranslucent(world, blockPos) && !blockState.isSolidBlock(world, blockPos))) {
                double d = direction.getOffsetX() == 0 ? random.nextDouble() : 0.5D + (double) direction.getOffsetX() * 0.6D;
                double f = direction.getOffsetZ() == 0 ? random.nextDouble() : 0.5D + (double) direction.getOffsetZ() * 0.6D;

                int j = MinecraftClient.getInstance().getBlockColors().getColor(state, world, blockPos.offset(Direction.UP), 0);
                if (j == -1) {
                    List<BakedQuad> quads = MinecraftClient.getInstance().getBlockRenderManager().getModel(blockState).getQuads(blockState, Direction.DOWN, random);
                    for (int i = 0; i < Direction.values().length; i++) {
                        quads = MinecraftClient.getInstance().getBlockRenderManager().getModel(blockState).getQuads(blockState, Direction.byId(i), random);
                        if (!quads.isEmpty()) {
                            if (quads.get(quads.size() - 1).hasColor()) {
                                break;
                            }
                        }
                    }
                    if (!quads.isEmpty()) {
                        j = MinecraftClient.getInstance().getBlockColors().getColor(state, world, blockPos, quads.get(quads.size() - 1).getColorIndex());
                    }
                    j = state.getMaterial().getColor().color;
                    System.out.println("Color is " + j);
                }
                //if (j == 16777215) {

                //}
                float k = (float) (j >> 16 & 255) / 255.0F;
                float l = (float) (j >> 8 & 255) / 255.0F;
                float m = (float) (j & 255) / 255.0F;

                //Regular leaves
                for (int leaf = 0; leaf < FallingLeaves.coniferLeaves.length; leaf++) {
                    if (state.getBlock() == FallingLeaves.coniferLeaves[leaf]) {
                        world.addParticle(FallingLeavesClient.FALLING_SPRUCE_LEAF, (double)pos.getX() + d, pos.getY(), (double)pos.getZ() + f, k, l, m);
                    } else {
                        world.addParticle(FallingLeavesClient.FALLING_LEAF, (double)pos.getX() + d, pos.getY(), (double)pos.getZ() + f, k, l, m);
                    }
                }
                //Dynamic leaves
                /*
                if (world.isClient) {
                    new DynamicLeafParticle((ClientWorld) world, (double) pos.getX() + d, pos.getY(), (double) pos.getZ() + f, k, l, m, state);
                }
                 */
            }
        }
    }
}
