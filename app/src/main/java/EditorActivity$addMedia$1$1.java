package com.mukhda.editor;

import android.net.Uri;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

public final class EditorActivity$addMedia$1$1 extends Lambda<Unit> implements Function0<Unit> {
    final List<Uri> $uris;
    final EditorActivity this$0;

    public EditorActivity$addMedia$1$1(EditorActivity editorActivity, List<Uri> list) {
        super(0);
        this.this$0 = editorActivity;
        this.$uris = list;
    }

    public static final void invoke$lambda$0(List clips, EditorActivity this$0, int i) {
        Project project;
        Project project2;
        Project project3;
        Project copy;
        Intrinsics.checkNotNullParameter(clips, "$clips");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (!clips.isEmpty()) {
            project = this$0.project;
            Project project4 = null;
            if (project == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
                project2 = null;
            } else {
                project2 = project;
            }
            project3 = this$0.project;
            if (project3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
            } else {
                project4 = project3;
            }
            copy = project2.copy(project2.id, project2.name, CollectionsKt.plus((Collection) project4.getClips(), (Iterable) clips), project2.captions, project2.captionStyle, project2.captionLang, project2.musicUri, project2.originalVolume, project2.filter, project2.templateId, project2.updatedAt);
            this$0.commit(copy);
        }
        this$0.loadThumbs();
        if (i > 0) {
            this$0.toast(i + " file(s) could not be read and were skipped");
        }
    }

    @Override
    public Unit invoke() {
        invoke2();
        return Unit.INSTANCE;
    }

    public final void invoke2() {
        Handler handler;
        Media media = Media.INSTANCE;
        EditorActivity editorActivity = this.this$0;
        List<Uri> uris = this.$uris;
        Intrinsics.checkNotNullExpressionValue(uris, "$uris");
        Pair<List<Clip>, Integer> clips = media.toClips(editorActivity, uris);
        final List<Clip> component1 = clips.component1();
        final int intValue = clips.component2().intValue();
        handler = this.this$0.main;
        final EditorActivity editorActivity2 = this.this$0;
        handler.post(new Runnable() {
            @Override
            public final void run() {
                EditorActivity$addMedia$1$1.invoke$lambda$0(component1, editorActivity2, intValue);
            }
        });
    }
}
