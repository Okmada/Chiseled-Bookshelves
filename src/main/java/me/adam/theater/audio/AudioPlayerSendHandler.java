package me.adam.theater.audio;

import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

public class AudioPlayerSendHandler implements AudioSendHandler {
    private final Queue<ByteBuffer> bufferQueue = new LinkedList<>();

    @Override
    public boolean canProvide() {
        return !bufferQueue.isEmpty();
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return bufferQueue.remove();
    }

    public void appendBuffer(ByteBuffer buffer) {
        this.bufferQueue.add(buffer);
    }

    public void clearBuffer() {
        this.bufferQueue.clear();
    }
}

