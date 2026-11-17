package com.mukhda.editor;

import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

public final class Preview$loadAt$1 extends Lambda<Unit> implements Function0<Unit> {
    final Clip $c;
    final String $uri;
    final Preview this$0;

    public Preview$loadAt$1(Preview preview, Clip clip, String str) {
        super(0);
        this.this$0 = preview;
        this.$c = clip;
        this.$uri = str;
    }

    public static final void invoke$lambda$0(Preview this$0, String uri, Bitmap bitmap) {
        String str;
        ImageView imageView;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(uri, "$uri");
        str = this$0.shownImageFor;
        if (!Intrinsics.areEqual(str, uri) || bitmap == null) {
            return;
        }
        imageView = this$0.imageView;
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public Unit invoke() {
        invoke2();
        return Unit.INSTANCE;
    }

    public final void invoke2() {
        Handler handler;
        final Bitmap thumb = Media.INSTANCE.thumb(this.this$0.ctx, this.$c, 0L, 1280);
        handler = this.this$0.handler;
        final Preview preview = this.this$0;
        final String str = this.$uri;
        handler.post(new Runnable() {
            @Override
            public final void run() {
                Preview$loadAt$1.invoke$lambda$0(Preview$loadAt$1.this.this$0, str, thumb);
            }
        });
    }
}
