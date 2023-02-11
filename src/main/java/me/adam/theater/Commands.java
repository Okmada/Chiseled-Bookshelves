package me.adam.theater;

import com.mojang.brigadier.CommandDispatcher;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import me.adam.theater.audio.Audio;
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

    private final Audio audio = new Audio();

    private final AudioPlayerManager playerManager = audio.getPlayerManager();
    private final AudioPlayer player = audio.getPlayer();
    //    private final TrackScheduler trackScheduler = audio.getTrackScheduler();
    private final AudioLoadResultHandler audioLoadResultHandler = audio.getLoadResultHandler();

    private VideoStream videoStream = null;

    private final Set set = new Set();

    private final List<Display> dp = new ArrayList<>();

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
                                .then(CommandManager.argument("Discord TAG", greedyString())
                                        .executes(ctx -> {
                                            if (bot == null) {
                                                ctx.getSource().sendMessage(Text.literal("Bot not logged in"));
                                                return 0;
                                            }

                                            String[] name = getString(ctx, "Discord TAG").split("#");

                                            for (Guild guild : bot.getGuilds()) {
                                                Member member = guild.getMemberByTag(name[0], name[1]);
                                                for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
                                                    if (voiceChannel.getMembers().contains(member)) {
                                                        guild.getAudioManager().openAudioConnection(voiceChannel);
                                                        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));

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

                                            player.setPaused(true);

                                            playerManager.loadItem(path, audioLoadResultHandler);

                                            if (videoStream != null) {
                                                videoStream.exit();
                                            }

                                            this.videoStream = new VideoStream(path, 2, 3, scale);
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


                                            player.setPaused(true);

                                            if (videoStream != null) {
                                                videoStream.exit();
                                            }

                                            this.videoStream = new VideoStream(":0.0+" + x + "," + y, 2, 3, scale);
                                            this.videoStream.linScreenRec(dx, dy);
                                            this.videoStream.finishLoading();

                                            for (Display d : dp) {
                                                d.changeVideoStream(this.videoStream);
                                            }

                                            player.stopTrack();
                                            return 1;
                                        })
                                ))))))

                                .then(CommandManager.literal("windows")
                                .then(CommandManager.argument("scale", string())
                                .then(CommandManager.argument("window", greedyString())
                                        .executes(ctx -> {
                                            double scale = Double.parseDouble(getString(ctx, "scale"));

                                            String window = getString(ctx, "window");

                                            player.setPaused(true);

                                            if (videoStream != null) {
                                                videoStream.exit();
                                            }

                                            this.videoStream = new VideoStream(window, 2, 3, scale);
                                            this.videoStream.winScreenRec();
                                            this.videoStream.finishLoading();

                                            for (Display d : dp) {
                                                d.changeVideoStream(videoStream);
                                            }
                                            player.stopTrack();
                                            return 1;
                                        })
                                )))
                        )

                        .then(CommandManager.literal("play")
                                .executes(ctx -> {
                                    player.setPaused(false);

                                    this.videoStream.start();

                                    return 1;
                                })
                        )
                )

                .then(CommandManager.literal("display")
                        .then(CommandManager.literal("add")
                        .then(CommandManager.argument("xPos", string())
                        .then(CommandManager.argument("yPos", string())
                        .then(CommandManager.argument("zPos", string())
                                .executes(ctx -> {
                                    double x = Double.parseDouble(getString(ctx, "xPos"));
                                    double y = Double.parseDouble(getString(ctx, "yPos"));
                                    double z = Double.parseDouble(getString(ctx, "zPos"));

                                    BlockPos bp = new BlockPos(x, y, z);

                                    Display display = new Display(ctx.getSource().getWorld(), bp, set);

                                    dp.add(display);

                                    if (this.videoStream != null) {
                                        display.changeVideoStream(this.videoStream);
                                    }

                                    return 1;
                                })
                        ))))

                        .then(CommandManager.literal("clean")
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
                )
        );
    }
}