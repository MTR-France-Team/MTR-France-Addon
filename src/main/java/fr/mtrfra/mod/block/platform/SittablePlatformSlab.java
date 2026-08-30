package fr.mtrfra.mod.block.platform;

import fr.mtrfra.mod.block.base.Sittable;
import fr.mtrfra.mod.util.SeatHelper;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.BlockHitResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.World;
import org.mtr.mod.block.BlockPlatformSlab;

public class SittablePlatformSlab extends BlockPlatformSlab implements Sittable {

    public SittablePlatformSlab(BlockSettings blockSettings) {
        super(blockSettings);
    }

    @Override
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getStackInHand(hand).isEmpty()) {
            return ActionResult.PASS;
        }
        return SeatHelper.trySit(world, pos, state, player);
    }

}
