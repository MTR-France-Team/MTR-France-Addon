package fr.mtrfranceaddon.mod.common.block;

import fr.mtrfranceaddon.mod.common.webserver.TactileMapServer;
import org.mtr.core.data.Station;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mod.InitClient;
import org.mtr.mod.block.BlockDirectionalDoubleBlockBase;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;

import javax.annotation.Nonnull;
import java.awt.Desktop;
import java.net.URI;
import java.util.List;

public class RatpInformationKioskBlock extends BlockDirectionalDoubleBlockBase {

    public RatpInformationKioskBlock(BlockSettings blockSettings) {
        super(blockSettings);
    }

    @Nonnull
    @Override
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) {
            final Station station = InitClient.findStation(pos);

            if (station == null) {
                final MutableText mutableText = TextHelper.translatable(
                        "message.mtrfranceaddon.ratp_information_kiosk.no_station"
                );

                IDrawing.narrateOrAnnounce(
                        mutableText.getString(),
                        ObjectArrayList.of(mutableText)
                );

                return ActionResult.CONSUME;
            }

            openInformationKiosk(station.getHexId());
            return ActionResult.SUCCESS;
        }

        return ActionResult.CONSUME;
    }

    private static void openInformationKiosk(String stationHexId) {
        final int port = TactileMapServer.getPortOrStart();

        if (port <= 0) {
            return;
        }

        final String url = String.format(
                "http://localhost:%d/?station=%s",
                port,
                stationHexId
        );

        try {
            if (Desktop.isDesktopSupported()
                    && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {

                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            fr.mtrfranceaddon.mod.common.Init.LOGGER.error(
                    "Unable to open WebApp: ({})",
                    url,
                    e
            );
        }
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(
            BlockState state,
            BlockView world,
            BlockPos pos,
            ShapeContext context
    ) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final int height = IBlock.getStatePropertySafe(state, HALF)
                == DoubleBlockHalf.UPPER ? 14 : 16;

        return IBlock.getVoxelShapeByDirection(
                6.0,
                0.0,
                3.0,
                10.0,
                height,
                13.0,
                facing
        );
    }

    @Override
    public void addBlockProperties(
            List<org.mtr.mapping.tool.HolderBase<?>> properties
    ) {
        properties.add(FACING);
        properties.add(HALF);
    }
}