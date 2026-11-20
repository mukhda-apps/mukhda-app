package com.mukhda.editor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

public final class MainActivity$createFrom$1 extends Lambda<Unit> implements Function0<Unit> {
    final ProgressDialog $d;
    final String $mode;
    final Template $tpl;
    final List<? extends Uri> $uris;
    final MainActivity this$0;

    public MainActivity$createFrom$1(MainActivity mainActivity, List<? extends Uri> list, ProgressDialog progressDialog, String str, Template template) {
        super(0);
        this.this$0 = mainActivity;
        this.$uris = list;
        this.$d = progressDialog;
        this.$mode = str;
        this.$tpl = template;
    }

    public static final void invoke$lambda$0(ProgressDialog d, List clips, MainActivity this$0, int i, String mode, Template template) {
        Intent intent;
        ActivityResultLauncher activityResultLauncher;
        Intrinsics.checkNotNullParameter(d, "$d");
        Intrinsics.checkNotNullParameter(clips, "$clips");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(mode, "$mode");
        UiKt.safeDismiss(this$0, d);
        if (clips.isEmpty()) {
            Toast.makeText(this$0, "Those files could not be opened", 1).show();
            return;
        }
        if (i > 0) {
            Toast.makeText(this$0, i + " file(s) skipped (could not be read)", 1).show();
        }
        if (!Intrinsics.areEqual(mode, "template") || template == null) {
            Project project = new Project(null, "Project " + new SimpleDateFormat("d MMM", Locale.getDefault()).format(new Date()), clips, null, null, null, null, 0.0f, null, null, 0L, 2041, null);
            Store.INSTANCE.save(this$0, project);
            this$0.openEditor(project, Intrinsics.areEqual(mode, EditorActivity.EXTRA_CAPTIONS));
            return;
        }
        intent = this$0.forcedSong;
        if (intent != null) {
            this$0.forcedSong = null;
            this$0.buildTemplateProject(clips, intent);
            return;
        }
        this$0.pendingClips = clips;
        activityResultLauncher = this$0.musicLib;
        activityResultLauncher.launch(new Intent(this$0, (Class<?>) MusicActivity.class).putExtra(MusicActivity.EXTRA_TITLE, "Song for " + template.getTitle()).putExtra(MusicActivity.EXTRA_SELECTED, template.getMusic()).putExtra(MusicActivity.EXTRA_TEMPLATE_SONG, true));
    }

    @Override
    public Unit invoke() {
        invoke2();
        return Unit.INSTANCE;
    }

    public final void invoke2() {
        Handler handler;
        Pair<List<Clip>, Integer> clips = Media.INSTANCE.toClips(this.this$0, this.$uris);
        final List<Clip> component1 = clips.component1();
        final int intValue = clips.component2().intValue();
        handler = this.this$0.main;
        final ProgressDialog progressDialog = this.$d;
        final MainActivity mainActivity = this.this$0;
        final String str = this.$mode;
        final Template template = this.$tpl;
        handler.post(new Runnable() {
            @Override
            public final void run() {
                MainActivity$createFrom$1.invoke$lambda$0(progressDialog, component1, mainActivity, intValue, str, template);
            }
        });
    }
}
