package com.mukhda.editor;

import android.graphics.Bitmap;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

public final class EditorActivity$loadThumbs$1 extends Lambda<Unit> implements Function0<Unit> {
    final List<Clip> $need;
    final EditorActivity this$0;

    public EditorActivity$loadThumbs$1(List<Clip> list, EditorActivity editorActivity) {
        super(0);
        this.$need = list;
        this.this$0 = editorActivity;
    }

    public static final void invoke$lambda$0(EditorActivity this$0, Clip c, Bitmap bitmap) {
        TimelineView timelineView;
        TimelineView timelineView2;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(c, "$c");
        timelineView = this$0.timeline;
        TimelineView timelineView3 = null;
        if (timelineView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("timeline");
            timelineView = null;
        }
        timelineView.getThumbs().put(c.getId(), bitmap);
        timelineView2 = this$0.timeline;
        if (timelineView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("timeline");
        } else {
            timelineView3 = timelineView2;
        }
        timelineView3.invalidate();
    }

    @Override
    public Unit invoke() {
        invoke2();
        return Unit.INSTANCE;
    }

    public final void invoke2() {
        Handler handler;
        for (final Clip clip : this.$need) {
            final Bitmap thumb = Media.INSTANCE.thumb(this.this$0, clip, 0L, 224);
            if (thumb != null) {
                handler = this.this$0.main;
                final EditorActivity editorActivity = this.this$0;
                handler.post(new Runnable() {
                    @Override
                    public final void run() {
                        EditorActivity$loadThumbs$1.invoke$lambda$0(EditorActivity$loadThumbs$1.this.this$0, clip, thumb);
                    }
                });
            }
        }
    }
}
