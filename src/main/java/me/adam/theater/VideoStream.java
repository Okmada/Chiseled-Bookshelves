package me.adam.theater;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

public class VideoStream extends Thread{

    private final FFmpegFrameGrabber frameGrabber;

    private int width;
    private int height;

    private final int round_to_w;
    private final int round_to_h;

    private final double scale;

    private int[][][] display;

    private final AtomicBoolean running = new AtomicBoolean(false);

    private final Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();


    public VideoStream(String video, int round_to_h, int round_to_w, double scale ) {
        this.frameGrabber = new FFmpegFrameGrabber(video);

        this.round_to_w = round_to_w;
        this.round_to_h = round_to_h;

        this.scale = scale;
    }

    public void linScreenRec(int captureRegionX, int captureRegionY) {
        frameGrabber.setFormat("x11grab");
        frameGrabber.setImageWidth(captureRegionX);
        frameGrabber.setImageHeight(captureRegionY);
    }

    public void winScreenRec() {
        frameGrabber.setFormat("gdigrab");
        frameGrabber.setFrameRate(30);
    }

    public void finishLoading() {
        try {
            frameGrabber.start();
        } catch (FFmpegFrameGrabber.Exception e) {
            e.printStackTrace();
        }

        this.width = (Math.floorDiv((int)(frameGrabber.getImageWidth() * scale), round_to_w) * round_to_w);
        this.height = (Math.floorDiv((int)(frameGrabber.getImageHeight() * scale), round_to_h) * round_to_h);

        System.out.printf("Input video dims %d x %d \nOutput video dims %d x %d\n",
                this.frameGrabber.getImageWidth(), this.frameGrabber.getImageHeight(),
                width, height
        );

        this.display = new int[width][height][3];
    }

    public void exit() {
        running.set(false);
    }

    public void run() {
        running.set(true);
        long lastTime = System.nanoTime();
        final double ns = 1000000000.0 / frameGrabber.getFrameRate();
        double delta = 0;

        while (running.get()){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while(delta >= 1){
                try {
                    Frame frame = frameGrabber.grab();
                    if (frame == null) {
                        System.out.println("FINISHED");
                        return;
                    }

                    BufferedImage img = java2DFrameConverter.convert(frame);
                    if (img != null) {
                        for(int x = 0; x < this.width; ++x) {
                            for(int y = 0; y < this.height; ++y) {
                                int color = img.getRGB((int)(x * (1/this.scale)), (int)(y * (1/this.scale)));

                                // blue
                                this.display[x][y][2] = color & 0xff;
                                // green
                                this.display[x][y][1] = (color & 0xff00) >> 8;
                                // red
                                this.display[x][y][0] = (color & 0xff0000) >> 16;
                            }
                        }
                    } else {
                        continue;
                    }
                } catch (FFmpegFrameGrabber.Exception e) {
                    e.printStackTrace();
                }
                delta--;
            }
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int get_pixel_GS(int x, int y) {
        int[] pixel = this.display[x][y];
        return (pixel[0] + pixel[1] + pixel[2]) / 3;
    }

    public int[] get_pixel_RGB(int x, int y) {
        return this.display[x][y];
    }
}
