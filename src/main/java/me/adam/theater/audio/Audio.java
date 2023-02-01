package me.adam.theater.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class Audio {

    private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private final AudioPlayer player = playerManager.createPlayer();
//    private final TrackScheduler trackScheduler = new TrackScheduler(player);
    private final AudioLoadResultHandler loadResultHandler = new AudioLoadResultHandler() {
        @Override
        public void trackLoaded(AudioTrack track) {
            player.playTrack(track);
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
        }

        @Override
        public void noMatches() {
        }

        @Override
        public void loadFailed(FriendlyException exception) {
        }
    };

    public Audio() {
        AudioSourceManagers.registerLocalSource(playerManager);

//        player.addListener(trackScheduler);
    }

    public AudioPlayer getPlayer() {
        return player;
    }

//    public TrackScheduler getTrackScheduler() {
//        return trackScheduler;
//    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public AudioLoadResultHandler getLoadResultHandler() {
        return loadResultHandler;
    }
}
