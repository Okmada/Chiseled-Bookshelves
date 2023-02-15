package me.adam.theater;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Display {

    private VideoStream videoStream;

    private Integer Width;
    private Integer Height;

    private final Set dataset;

    private final BlockPos bp;

    private final World world;

    private Boolean isRGB;

    public Display(World world, BlockPos bp, Set set, Boolean isRGB) {
        dataset = set;
        this.bp = bp;
        this.world = world;

        this.isRGB = isRGB;
    }
    public void changeVideoStream(VideoStream videoStream) {
        removeCanvas();

        this.videoStream = videoStream;

        if (this.videoStream == null) { return; }

        Width = this.videoStream.getWidth()/3;
        Height = this.videoStream.getHeight()/2;

        placeCanvas();
    }

    public void removeCanvas() {
        if (this.videoStream == null) { return; }

        for(int dx = 0; dx < Width; ++dx) {
            for (int dy = 0; dy < Height; ++dy) {
                world.setBlockState(bp.add(dx, dy, 0), Blocks.AIR.getDefaultState());
            }
        }
    }

    public void placeCanvas() {
        if (this.videoStream == null) {return;}

        for(int dx = 0; dx < Width; ++dx) {
            for (int dy = 0; dy < Height; ++dy) {
                world.setBlockState(bp.add(dx, dy, 0), Blocks.CHISELED_BOOKSHELF.getDefaultState());
            }
        }
    }

    public void setIsRGB(Boolean isRGB) {
        this.isRGB = isRGB;
    }

    public void update() {
        for(int dx = 0; dx < Width; ++dx) {
            for (int dy = 0; dy < Height; ++dy) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int sdy = 0; sdy < 2; ++sdy) {
                    if (isRGB) {
                        int[] RGB = videoStream.get_pixel_RGB((Width-dx-1)*3, ((Height-dy-1)*2)+sdy);
                        for (int color : RGB) {
                            if (color >= 127) {
                                stringBuilder.append("1");
                            } else {
                                stringBuilder.append("0");
                            }
                        }
                    } else {
                        for(int sdx = 0; sdx < 3; ++sdx) {
                            if (videoStream.get_pixel_GS(((Width-dx-1)*3)+sdx, ((Height-dy-1)*2)+sdy) >= 127) {
                                stringBuilder.append("1");
                            } else {
                                stringBuilder.append("0");
                            }
                        }
                    }
                }
                BlockState state = dataset.blockStateArray[Integer.parseInt(stringBuilder.toString(), 2)];
                BlockPos pos = bp.add(dx, dy, 0);

                world.setBlockState(pos, state);
            }
        }
    }
}
