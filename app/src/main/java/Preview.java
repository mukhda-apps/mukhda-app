package com.mukhda.editor;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.concurrent.ThreadsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;

public final class Preview {
    public final Context ctx;
    public boolean errorShown;
    public final Handler handler;
    public int idx;
    public final ImageView imageView;
    public long imgBaseMs;
    public long imgStartNs;
    public String loadedUri;
    public final ExoPlayer music;
    public String musicUri;
    public List<Long> offsets;
    public final Function1<? super Boolean, Unit> onPlaying;
    public final Function1<? super Long, Unit> onTime;
    public final ExoPlayer player;
    public final PlayerView playerView;
    public boolean playing;
    public Project project;
    public String shownImageFor;
    public final Runnable ticker;
    public long timelineMs;

    public Preview(Context ctx, PlayerView playerView, ImageView imageView, Function1<? super Long, Unit> onTime, Function1<? super Boolean, Unit> onPlaying) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(playerView, "playerView");
        Intrinsics.checkNotNullParameter(imageView, "imageView");
        Intrinsics.checkNotNullParameter(onTime, "onTime");
        Intrinsics.checkNotNullParameter(onPlaying, "onPlaying");
        this.ctx = ctx;
        this.playerView = playerView;
        this.imageView = imageView;
        this.onTime = onTime;
        this.onPlaying = onPlaying;
        ExoPlayer build = new ExoPlayer.Builder(ctx).build();
        Intrinsics.checkNotNullExpressionValue(build, "build(...)");
        this.player = build;
        ExoPlayer build2 = new ExoPlayer.Builder(ctx).build();
        Intrinsics.checkNotNullExpressionValue(build2, "build(...)");
        this.music = build2;
        this.handler = new Handler(Looper.getMainLooper());
        this.project = new Project(null, null, null, null, null, null, null, 0.0f, null, null, 0L, 2047, null);
        this.offsets = CollectionsKt.emptyList();
        this.ticker = new Runnable() {
            @Override
            public void run() {
                Handler handler;
                Preview.this.tick();
                if (Preview.this.getPlaying()) {
                    handler = Preview.this.handler;
                    handler.postDelayed(this, 33L);
                }
            }
        };
        playerView.setPlayer(build);
        build.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                Intrinsics.checkNotNullParameter(error, "error");
                if (Preview.this.errorShown) {
                    return;
                }
                Preview.this.errorShown = true;
                Toast.makeText(Preview.this.ctx, "This clip can't be previewed on this phone, export may still work.", 1).show();
            }
        });
    }

    public final int indexAt(long t) {
        if (this.project.getClips().isEmpty()) {
            return -1;
        }
        int size = this.project.getClips().size();
        for (int i = 0; i < size; i++) {
            if (t < this.offsets.get(i).longValue() + this.project.getClips().get(i).getLengthMs()) {
                return i;
            }
        }
        return this.project.getClips().size() - 1;
    }

    public final void loadAt(long t, boolean autoplay) {
        int indexAt = indexAt(t);
        if (indexAt < 0) {
            this.player.stop();
            this.loadedUri = null;
            this.imageView.setVisibility(8);
            return;
        }
        this.idx = indexAt;
        Clip clip = this.project.getClips().get(indexAt);
        long coerceIn = RangesKt.coerceIn(t - this.offsets.get(indexAt).longValue(), 0L, Math.max(0L, clip.getLengthMs() - 1));
        this.timelineMs = this.offsets.get(indexAt).longValue() + coerceIn;
        if (clip.isImage()) {
            this.player.setPlayWhenReady(false);
            this.imageView.setVisibility(0);
            if (!Intrinsics.areEqual(this.shownImageFor, clip.getUri())) {
                this.shownImageFor = clip.getUri();
                new Thread(() -> new Preview$loadAt$1(this, clip, clip.getUri()).invoke()).start();
            }
            this.imgStartNs = System.nanoTime();
            this.imgBaseMs = coerceIn;
        } else {
            this.imageView.setVisibility(8);
            this.shownImageFor = null;
            long startMs = clip.getStartMs() + ((long)(coerceIn * clip.getSpeed()));
            if (!Intrinsics.areEqual(this.loadedUri, clip.getUri())) {
                this.player.setMediaItem(MediaItem.fromUri(Uri.parse(clip.getUri())));
                this.player.prepare();
                this.loadedUri = clip.getUri();
            }
            this.player.setPlaybackSpeed(clip.getSpeed());
            this.player.seekTo(startMs);
            this.player.setPlayWhenReady(autoplay);
        }
        if (this.playing) {
            syncMusic(true);
        }
        this.onTime.invoke(Long.valueOf(this.timelineMs));
    }

    public final void next() {
        if (this.idx + 1 < this.project.getClips().size()) {
            loadAt(this.offsets.get(this.idx + 1).longValue(), true);
            return;
        }
        pause();
        long totalMs = this.project.getTotalMs();
        this.timelineMs = totalMs;
        this.onTime.invoke(Long.valueOf(totalMs));
    }

    public final void syncMusic(boolean play) {
        if (this.musicUri == null) {
            return;
        }
        long duration = this.music.getDuration();
        this.music.seekTo(duration > 0 ? this.timelineMs % duration : this.timelineMs);
        this.music.setPlayWhenReady(play);
    }

    public final void tick() {
        Clip clip;
        if (!this.playing || this.project.getClips().isEmpty() || (clip = (Clip) CollectionsKt.getOrNull(this.project.getClips(), this.idx)) == null) {
            return;
        }
        if (clip.isImage()) {
            long nanoTime = this.imgBaseMs + ((System.nanoTime() - this.imgStartNs) / 1000000);
            if (nanoTime >= clip.getLengthMs()) {
                next();
                return;
            }
            long longValue = this.offsets.get(this.idx).longValue() + nanoTime;
            this.timelineMs = longValue;
            this.onTime.invoke(Long.valueOf(longValue));
            return;
        }
        long currentPosition = this.player.getCurrentPosition();
        if (this.player.getPlaybackState() == 4 || currentPosition >= clip.getEndMs() - 15) {
            next();
            return;
        }
        long longValue2 = this.offsets.get(this.idx).longValue() + ((long)(RangesKt.coerceAtLeast(currentPosition - clip.getStartMs(), 0L) / clip.getSpeed()));
        this.timelineMs = longValue2;
        this.onTime.invoke(Long.valueOf(longValue2));
    }

    public final boolean getPlaying() {
        return this.playing;
    }

    public final long getTimelineMs() {
        return this.timelineMs;
    }

    public final void pause() {
        this.playing = false;
        this.player.setPlayWhenReady(false);
        this.music.setPlayWhenReady(false);
        this.handler.removeCallbacks(this.ticker);
        this.onPlaying.invoke(false);
    }

    public final void play() {
        if (this.project.getClips().isEmpty()) {
            return;
        }
        if (this.timelineMs >= this.project.getTotalMs() - 50) {
            loadAt(0L, false);
        }
        this.playing = true;
        loadAt(this.timelineMs, true);
        this.onPlaying.invoke(true);
        this.handler.removeCallbacks(this.ticker);
        this.handler.post(this.ticker);
    }

    public final void release() {
        this.handler.removeCallbacks(this.ticker);
        this.player.release();
        this.music.release();
    }

    public final void seek(long t) {
        loadAt(RangesKt.coerceIn(t, 0L, Math.max(0L, this.project.getTotalMs())), this.playing);
    }

    public final void setProject(Project p) {
        Intrinsics.checkNotNullParameter(p, "p");
        this.project = p;
        this.offsets = Timeline.INSTANCE.offsets(p.getClips());
        this.timelineMs = RangesKt.coerceIn(this.timelineMs, 0L, Math.max(0L, p.getTotalMs()));
        this.player.setVolume(p.getOriginalVolume());
        if (!Intrinsics.areEqual(p.getMusicUri(), this.musicUri)) {
            this.musicUri = p.getMusicUri();
            this.music.stop();
            String str = this.musicUri;
            if (str != null) {
                this.music.setMediaItem(MediaItem.fromUri(Uri.parse(str)));
                this.music.prepare();
            } else {
                this.music.clearMediaItems();
            }
        }
        this.loadedUri = null;
        loadAt(this.timelineMs, this.playing);
    }
}
