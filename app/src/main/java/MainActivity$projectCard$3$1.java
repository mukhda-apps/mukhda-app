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

public final class MainActivity$projectCard$3$1 extends Lambda<Unit> implements Function0<Unit> {
    final Clip $c;
    final ImageView $img;
    final MainActivity this$0;

    public MainActivity$projectCard$3$1(MainActivity mainActivity, Clip clip, ImageView imageView) {
        super(0);
        this.this$0 = mainActivity;
        this.$c = clip;
        this.$img = imageView;
    }

    public static final void invoke$lambda$0(ImageView img, Bitmap bitmap) {
        Intrinsics.checkNotNullParameter(img, "$img");
        img.setImageBitmap(bitmap);
    }

    @Override
    public Unit invoke() {
        invoke2();
        return Unit.INSTANCE;
    }

    public final void invoke2() {
        Handler handler;
        final Bitmap thumb = Media.INSTANCE.thumb(this.this$0, this.$c, 0L, 400);
        if (thumb != null) {
            handler = this.this$0.main;
            final ImageView imageView = this.$img;
            handler.post(new Runnable() {
                @Override
                public final void run() {
                    MainActivity$projectCard$3$1.invoke$lambda$0(imageView, thumb);
                }
            });
        }
    }
}
