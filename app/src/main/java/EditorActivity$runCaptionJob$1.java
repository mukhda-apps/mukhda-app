package com.mukhda.editor;

import android.app.ProgressDialog;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.mukhda.editor.EditorActivity$runCaptionJob$1;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

public final class EditorActivity$runCaptionJob$1 extends Lambda<Unit> implements Function0<Unit> {
    final ProgressDialog $dlg;
    final boolean $download;
    final Lang $lang;
    final List<Clip> $vids;
    final EditorActivity this$0;

    public static final class AnonymousClass1 extends Lambda<Unit> implements Function1<Float, Unit> {
        final ProgressDialog $dlg;
        final EditorActivity this$0;

        AnonymousClass1(EditorActivity editorActivity, ProgressDialog progressDialog) {
            super(1);
            this.this$0 = editorActivity;
            this.$dlg = progressDialog;
        }

        public static final void invoke$lambda$0(ProgressDialog dlg, float f) {
            Intrinsics.checkNotNullParameter(dlg, "$dlg");
            dlg.setProgress((int) (f * 100));
        }

        @Override
        public Unit invoke(Float f) {
            invoke(f.floatValue());
            return Unit.INSTANCE;
        }

        public final void invoke(final float f) {
            Handler handler;
            handler = this.this$0.main;
            final ProgressDialog progressDialog = this.$dlg;
            handler.post(new Runnable() {
                @Override
                public final void run() {
                    EditorActivity$runCaptionJob$1.AnonymousClass1.invoke$lambda$0(progressDialog, f);
                }
            });
        }
    }

    public static final class AnonymousClass5 extends Lambda<Unit> implements Function1<Float, Unit> {
        final ProgressDialog $dlg;
        final int $n;
        final List<Clip> $vids;
        final EditorActivity this$0;

        AnonymousClass5(EditorActivity editorActivity, ProgressDialog progressDialog, int i, List<Clip> list) {
            super(1);
            this.this$0 = editorActivity;
            this.$dlg = progressDialog;
            this.$n = i;
            this.$vids = list;
        }

        public static final void invoke$lambda$0(ProgressDialog dlg, int i, float f, List vids) {
            Intrinsics.checkNotNullParameter(dlg, "$dlg");
            Intrinsics.checkNotNullParameter(vids, "$vids");
            dlg.setProgress((int) (((i + f) / vids.size()) * 100));
        }

        @Override
        public Unit invoke(Float f) {
            invoke(f.floatValue());
            return Unit.INSTANCE;
        }

        public final void invoke(final float f) {
            Handler handler;
            handler = this.this$0.main;
            final ProgressDialog progressDialog = this.$dlg;
            final int i = this.$n;
            final List<Clip> list = this.$vids;
            handler.post(new Runnable() {
                @Override
                public final void run() {
                    EditorActivity$runCaptionJob$1.AnonymousClass5.invoke$lambda$0(progressDialog, i, f, list);
                }
            });
        }
    }

    public EditorActivity$runCaptionJob$1(boolean z, EditorActivity editorActivity, Lang lang, List<Clip> list, ProgressDialog progressDialog) {
        super(0);
        this.$download = z;
        this.this$0 = editorActivity;
        this.$lang = lang;
        this.$vids = list;
        this.$dlg = progressDialog;
    }

    public static final void invoke$lambda$0(ProgressDialog dlg) {
        Intrinsics.checkNotNullParameter(dlg, "$dlg");
        dlg.setTitle("Listening…");
        dlg.setProgress(0);
    }

    public static final void invoke$lambda$3(ProgressDialog dlg, ArrayList all, EditorActivity this$0, List vids, Lang lang) {
        Project project;
        Project project2;
        Project project3;
        Project copy;
        Intrinsics.checkNotNullParameter(dlg, "$dlg");
        Intrinsics.checkNotNullParameter(all, "$all");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(vids, "$vids");
        Intrinsics.checkNotNullParameter(lang, "$lang");
        dlg.dismiss();
        if (all.isEmpty()) {
            this$0.toast("No speech found in this clip. You can add text with the Text tool.");
            return;
        }
        List list = vids;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        Iterator it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(((Clip) it.next()).getId());
        }
        Set set = CollectionsKt.toSet(arrayList);
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
        List<Caption> captions = project4.getCaptions();
        ArrayList arrayList2 = new ArrayList();
        for (Object obj : captions) {
            if (!set.contains(((Caption) obj).getClipId())) {
                arrayList2.add(obj);
            }
        }
        copy = project2.copy(project2.id, project2.name, project2.clips, CollectionsKt.plus((Collection) arrayList2, (Iterable) all), project2.captionStyle, lang.getCode(), project2.musicUri, project2.originalVolume, project2.filter, project2.templateId, project2.updatedAt);
        this$0.commit(copy);
        this$0.showTool(EditorActivity.EXTRA_CAPTIONS);
    }

    public static final void invoke$lambda$4(ProgressDialog dlg, EditorActivity this$0, Throwable e, boolean z, Lang lang) {
        boolean z2;
        Intrinsics.checkNotNullParameter(dlg, "$dlg");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(e, "$e");
        Intrinsics.checkNotNullParameter(lang, "$lang");
        dlg.dismiss();
        z2 = this$0.cancelJob;
        if (!z2) {
            String message = e.getMessage();
            if (message == null) {
                message = "unknown problem";
            }
            this$0.toast("Captions did not work: " + message + ". Please try again.");
        }
        if (z) {
            ModelStore.INSTANCE.isReady(this$0, lang.getModel());
        }
    }

    @Override
    public Unit invoke() {
        invoke2();
        return Unit.INSTANCE;
    }

    public final void invoke2() {
        Handler handler;
        Handler handler2;
        Handler handler3;
        boolean z;
        try {
            if (this.$download) {
                ModelStore modelStore = ModelStore.INSTANCE;
                EditorActivity editorActivity = this.this$0;
                String model = this.$lang.getModel();
                AnonymousClass1 anonymousClass1 = new AnonymousClass1(this.this$0, this.$dlg);
                final EditorActivity editorActivity2 = this.this$0;
                modelStore.download(editorActivity, model, anonymousClass1, new Function0<Boolean>() {

                    @Override
                    public final Boolean invoke() {
                        boolean z2;
                        z2 = EditorActivity$runCaptionJob$1.this.this$0.cancelJob;
                        return Boolean.valueOf(z2);
                    }
                });
            }
            handler2 = this.this$0.main;
            final ProgressDialog progressDialog = this.$dlg;
            handler2.post(new Runnable() {
                @Override
                public final void run() {
                    EditorActivity$runCaptionJob$1.invoke$lambda$0(progressDialog);
                }
            });
            final ArrayList arrayList = new ArrayList();
            int i = 0;
            for (Clip clip : this.$vids) {
                int i2 = i + 1;
                z = this.this$0.cancelJob;
                if (z) {
                    throw new IOException("Cancelled");
                }
                CaptionEngine captionEngine = CaptionEngine.INSTANCE;
                EditorActivity editorActivity3 = this.this$0;
                Lang lang = this.$lang;
                final EditorActivity editorActivity4 = this.this$0;
                CollectionsKt.addAll(arrayList, captionEngine.transcribe(editorActivity3, clip, lang, new Function0<Boolean>() {

                    @Override
                    public final Boolean invoke() {
                        boolean z2;
                        z2 = EditorActivity$runCaptionJob$1.this.this$0.cancelJob;
                        return Boolean.valueOf(z2);
                    }
                }, new AnonymousClass5(this.this$0, this.$dlg, i, this.$vids)));
                i = i2;
            }
            handler3 = this.this$0.main;
            final ProgressDialog progressDialog2 = this.$dlg;
            final EditorActivity editorActivity5 = this.this$0;
            final List<Clip> list = this.$vids;
            final Lang lang2 = this.$lang;
            handler3.post(new Runnable() {
                @Override
                public final void run() {
                    EditorActivity$runCaptionJob$1.invoke$lambda$3(progressDialog2, arrayList, editorActivity5, list, lang2);
                }
            });
        } catch (Throwable th) {
            handler = this.this$0.main;
            final ProgressDialog progressDialog3 = this.$dlg;
            final EditorActivity editorActivity6 = this.this$0;
            final boolean z2 = this.$download;
            final Lang lang3 = this.$lang;
            handler.post(new Runnable() {
                @Override
                public final void run() {
                    EditorActivity$runCaptionJob$1.invoke$lambda$4(progressDialog3, editorActivity6, th, z2, lang3);
                }
            });
        }
    }
}
