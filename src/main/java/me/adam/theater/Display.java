package me.adam.theater;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

public class Display {

    private final VideoStream videoStream;

    private final Integer Width;
    private final Integer Height;

    private final Set dataset;

    private final BlockPos dbp;

    public Display(VideoStream videoStream, BlockPos bp, Set set, ServerCommandSource source) {
        this.videoStream = videoStream;

        Width = videoStream.getWidth()/3;
        Height = videoStream.getHeight()/2;

        dataset = set;

        dbp = bp;

        for(int dx = 0; dx < Width; ++dx) {
            for (int dy = 0; dy < Height; ++dy) {
                source.getWorld().setBlockState(bp.add(dx, dy, 0), Blocks.CHISELED_BOOKSHELF.getDefaultState());
            }
        }
    }

    public void update(ServerCommandSource ctx) {
        for(int dx = 0; dx < Width; ++dx) {
            for (int dy = 0; dy < Height; ++dy) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int sdy = 0; sdy < 2; ++sdy) {
                    for(int sdx = 0; sdx < 3; ++sdx) {
                        if (videoStream.get_pixel_GS(((Width-dx-1)*3)+sdx, ((Height-dy-1)*2)+sdy) >= 127) {
                            stringBuilder.append("1");
                        } else {
                            stringBuilder.append("0");
                        }
                    }
                }
                BlockState state = dataset.blockStateArray[Integer.parseInt(stringBuilder.toString(), 2)];
                BlockPos pos = dbp.add(dx, dy, 0);

                ctx.getWorld().setBlockState(pos, state);
            }
        }
    }
}
