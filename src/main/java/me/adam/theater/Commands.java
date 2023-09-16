package me.adam.theater;

import com.mojang.brigadier.CommandDispatcher;
import me.adam.theater.audio.AudioPlayerSendHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.StringArgumentType.*;

import net.minecraft.text.Text;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class Commands {

    private JDA bot;

    private VideoStream videoStream = null;

    private final Set set = new Set();

    private final List<Display> dp = new ArrayList<>();

    private final AudioPlayerSendHandler handler = new AudioPlayerSendHandler();

    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("theater")
                .then(CommandManager.literal("discord")
                        .then(CommandManager.literal("login")
                                .then(CommandManager.argument("Discord bot token", greedyString())
                                        .executes(ctx -> {
                                            if (bot != null) {
                                                ctx.getSource().sendMessage(Text.literal("Bot already logged in"));
                                                return 0;
                                            }

                                            String token = getString(ctx, "Discord bot token");

                                            bot = JDABuilder.createDefault(token).build();

                                            ctx.getSource().sendMessage(Text.literal("Logged in successfully"));
                                            return 1;
                                        })
                                )
                        )

                        .then(CommandManager.literal("join")
                                .then(CommandManager.argument("Discord ID", greedyString())
                                        .executes(ctx -> {
                                            if (bot == null) {
                                                ctx.getSource().sendMessage(Text.literal("Bot not logged in"));
                                                return 0;
                                            }

                                            String name = getString(ctx, "Discord ID");

                                            for (Guild guild : bot.getGuilds()) {
                                                Member member = guild.getMemberById(name);
                                                for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
                                                    if (voiceChannel.getMembers().contains(member)) {
                                                        guild.getAudioManager().openAudioConnection(voiceChannel);
                                                        guild.getAudioManager().setSendingHandler(handler);

                                                        ctx.getSource().sendMessage(Text.literal("Joined user"));
                                                        return 1;
                                                    }
                                                }
                                            }
                                            ctx.getSource().sendMessage(Text.literal("Could not find user"));
                                            return 1;
                                        })
                                )
                        )
                )

                .then(CommandManager.literal("video")
                        .then(CommandManager.literal("load")
                                .then(CommandManager.argument("scale", string())
                                .then(CommandManager.argument("path to video", greedyString())
                                        .executes(ctx -> {
                                            double scale = Double.parseDouble(getString(ctx, "scale"));

                                            String path = getString(ctx, "path to video");

                                            if (videoStream != null) {
                                                videoStream.exit();
                                            }

                                            this.videoStream = new VideoStream(path, 2, 3, scale, handler);
                                            this.videoStream.finishLoading();

                                            for (Display d : dp) {
                                                d.changeVideoStream(videoStream);
                                            }

                                            return 1;
                                        })
                                ))
                        )

                        .then(CommandManager.literal("screenRec")
                                .then(CommandManager.literal("linux")
                                .then(CommandManager.argument("scale", string())
                                .then(CommandManager.argument("x", string())
                                .then(CommandManager.argument("y", string())
                                .then(CommandManager.argument("dx", string())
                                .then(CommandManager.argument("dy", string())
                                        .executes(ctx -> {
                                            double scale = Double.parseDouble(getString(ctx, "scale"));

                                            int x = Integer.parseInt(getString(ctx, "x"));
                                            int y = Integer.parseInt(getString(ctx, "y"));
                                            int dx = Integer.parseInt(getString(ctx, "dx"));
                                            int dy = Integer.parseInt(getString(ctx, "dy"));

                                            if (videoStream != null) {
                                                videoStream.exit();
                                            }

                                            this.videoStream = new VideoStream(":0.0+" + x + "," + y, 2, 3, scale, handler);
                                            this.videoStream.linScreenRec(dx, dy);
                                            this.videoStream.finishLoading();

                                            for (Display d : dp) {
                                                d.changeVideoStream(this.videoStream);
                                            }

                                            return 1;
                                        })
                                ))))))

                                .then(CommandManager.literal("windows")
                                .then(CommandManager.argument("scale", string())
                                .then(CommandManager.argument("window", greedyString())
                                        .executes(ctx -> {
                                            double scale = Double.parseDouble(getString(ctx, "scale"));

                                            String window = getString(ctx, "window");

                                            if (videoStream != null) {
                                                videoStream.exit();
                                            }

                                            this.videoStream = new VideoStream(window, 2, 3, scale, handler);
                                            this.videoStream.winScreenRec();
                                            this.videoStream.finishLoading();

                                            for (Display d : dp) {
                                                d.changeVideoStream(videoStream);
                                            }
                                            return 1;
                                        })
                                )))
                        )

                        .then(CommandManager.literal("play")
                                .executes(ctx -> {
                                    this.videoStream.start();

                                    return 1;
                                })
                        )

                        .then(CommandManager.literal("pause")
                                .executes(ctx -> {
                                    this.videoStream.pause();

                                    return 1;
                                })
                        )

                        .then(CommandManager.literal("stop")
                                .executes(ctx -> {
                                    this.videoStream.exit();

                                    return 1;
                                })
                        )
                )

                .then(CommandManager.literal("display")
                        .then(CommandManager.literal("add")
                        .then(CommandManager.argument("isRGB", string())
                        .then(CommandManager.argument("xPos", string())
                        .then(CommandManager.argument("yPos", string())
                        .then(CommandManager.argument("zPos", string())
                                .executes(ctx -> {
                                    boolean rgb = Boolean.parseBoolean(getString(ctx, "isRGB"));

                                    double x = Double.parseDouble(getString(ctx, "xPos"));
                                    double y = Double.parseDouble(getString(ctx, "yPos"));
                                    double z = Double.parseDouble(getString(ctx, "zPos"));

                                    BlockPos bp = new BlockPos((int)x, (int)y, (int)z);

                                    Display display = new Display(ctx.getSource().getWorld(), bp, set, rgb);

                                    dp.add(display);

                                    if (this.videoStream != null) {
                                        display.changeVideoStream(this.videoStream);
                                    }

                                    return 1;
                                })
                        )))))

                        .then(CommandManager.literal("clear")
                                .executes(ctx -> {
                                    for (Display d : dp) {
                                        d.removeCanvas();
                                    }
                                    dp.clear();
                                    return 1;
                                })
                        )

                        .then(CommandManager.literal("render")
                                .executes(ctx -> {
//                                    long startTime = System.nanoTime();
                                    for (Display d : dp) {
                                        d.update();
                                    }
//                                    System.out.printf("Rendering time: %d ms%n", ((System.nanoTime() - startTime)/1000000));
                                    return 1;
                                })
                        )

                        .then(CommandManager.literal("setRGB")
                                .then(CommandManager.argument("index", string())
                                .then(CommandManager.argument("isRGB", string())
                                        .executes(ctx -> {
                                            boolean rgb = Boolean.parseBoolean(getString(ctx, "isRGB"));
                                            int index = Integer.parseInt(getString(ctx, "index"));

                                            if (index >= dp.size()) {
                                                ctx.getSource().sendMessage(Text.literal("Index out of bound"));
                                                return 1;
                                            }
                                            dp.get(index).setIsRGB(rgb);
                                            ctx.getSource().sendMessage(Text.literal("Mode successfully changed"));
                                            return 1;
                                        })
                                ))
                        )
                )
        );
    }
}