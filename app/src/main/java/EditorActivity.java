package com.mukhda.editor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.view.ViewCompat;
import androidx.media3.common.MimeTypes;
import androidx.media3.ui.PlayerView;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.ArrayDeque;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.concurrent.ThreadsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;

public final class EditorActivity extends AppCompatActivity {
    public static final String EXTRA_CAPTIONS = "captions";
    public static final String EXTRA_ID = "id";
    public final ActivityResultLauncher<String[]> addMedia;
    public float bpm;
    public volatile boolean cancelJob;
    public TextView captionView;
    public HorizontalScrollView hscroll;
    public final ActivityResultLauncher<Intent> musicLib;
    public LinearLayout panel;
    public final ActivityResultLauncher<String[]> pickMusic;
    public TextView playBtn;
    public FrameLayout playerHost;
    public PlayerView playerView;
    public TemplateView templateView;
    public MediaPlayer templateMusic;
    public Preview preview;
    public ProgressDialog progress;
    public Project project;
    public String selectedId;
    public TextView timeLabel;
    public TimelineView timeline;
    public TextView titleView;
    public final ArrayDeque<Project> undo = new ArrayDeque<>();
    public final ArrayDeque<Project> redo = new ArrayDeque<>();
    public String tool = "";
    public List<Long> beats = CollectionsKt.emptyList();
    public final Handler main = new Handler(Looper.getMainLooper());

    public EditorActivity() {
        ActivityResultLauncher<String[]> registerForActivityResult = registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), new ActivityResultCallback() {
            @Override
            public final void onActivityResult(Object obj) {
                EditorActivity.addMedia$lambda$0(EditorActivity.this, (List) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult, "registerForActivityResult(...)");
        this.addMedia = registerForActivityResult;
        ActivityResultLauncher<String[]> registerForActivityResult2 = registerForActivityResult(new ActivityResultContracts.OpenDocument(), new ActivityResultCallback() {
            @Override
            public final void onActivityResult(Object obj) {
                EditorActivity.pickMusic$lambda$1(EditorActivity.this, (Uri) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult2, "registerForActivityResult(...)");
        this.pickMusic = registerForActivityResult2;
        ActivityResultLauncher<Intent> registerForActivityResult3 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() {
            @Override
            public final void onActivityResult(Object obj) {
                EditorActivity.musicLib$lambda$2(EditorActivity.this, (ActivityResult) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult3, "registerForActivityResult(...)");
        this.musicLib = registerForActivityResult3;
    }

    public static final void addMedia$lambda$0(EditorActivity this$0, List list) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        List list2 = list;
        if (list2 == null || list2.isEmpty()) {
            return;
        }
        this$0.runBusy("Adding…", new EditorActivity$addMedia$1$1(this$0, list));
    }

    public final void addText() {
        Object obj;
        Project project = this.project;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        Iterator it = project.getClips().iterator();
        while (true) {
            if (!it.hasNext()) {
                obj = null;
                break;
            } else {
                obj = it.next();
                if (Intrinsics.areEqual(((Clip) obj).getId(), currentClipId())) {
                    break;
                }
            }
        }
        final Clip clip = (Clip) obj;
        if (clip == null) {
            toast("Add a clip first");
            return;
        }
        EditorActivity editorActivity = this;
        final EditText editText = new EditText(editorActivity);
        editText.setHint("Type your text");
        editText.setInputType(131073);
        new AlertDialog.Builder(editorActivity).setTitle("Text on video").setView(editText).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                EditorActivity.addText$lambda$59(editText, editorActivity, clip, dialogInterface, i);
            }
        }).setNegativeButton("Cancel", (DialogInterface.OnClickListener) null).show();
    }

    public static final void addText$lambda$59(EditText et, EditorActivity this$0, Clip c, DialogInterface dialogInterface, int i) {
        Project project;
        Project copy;
        Intrinsics.checkNotNullParameter(et, "$et");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(c, "$c");
        String obj = StringsKt.trim((CharSequence) et.getText().toString()).toString();
        if (obj.length() == 0) {
            return;
        }
        Timeline timeline = Timeline.INSTANCE;
        Project project2 = this$0.project;
        Project project3 = null;
        if (project2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project2 = null;
        }
        List<Long> offsets = timeline.offsets(project2.getClips());
        Project project4 = this$0.project;
        if (project4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project4 = null;
        }
        int indexOf = project4.getClips().indexOf(c);
        Preview preview = this$0.preview;
        if (preview == null) {
            Intrinsics.throwUninitializedPropertyAccessException("preview");
            preview = null;
        }
        long startMs = c.getStartMs() + ((long)(RangesKt.coerceAtLeast(preview.getTimelineMs() - offsets.get(indexOf).longValue(), 0L) * c.getSpeed()));
        long min = Math.min(c.getEndMs(), ((long)(3000.0f * c.getSpeed())) + startMs);
        if (min - startMs < 300) {
            this$0.toast("Move the line earlier in the clip");
            return;
        }
        Project project5 = this$0.project;
        if (project5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        } else {
            project = project5;
        }
        Project project6 = this$0.project;
        if (project6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
        } else {
            project3 = project6;
        }
        copy = project.copy(project.id, project.name, project.clips, CollectionsKt.plus((Collection<? extends Caption>) project3.getCaptions(), new Caption(c.getId(), startMs, min, obj)), project.captionStyle, project.captionLang, project.musicUri, project.originalVolume, project.filter, project.templateId, project.updatedAt);
        this$0.commit(copy);
    }

    public final void analyseBeats() {
        Project project = this.project;
        TimelineView timelineView = null;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        String musicUri = project.getMusicUri();
        if (musicUri == null) {
            return;
        }
        Templates templates = Templates.INSTANCE;
        Project project2 = this.project;
        if (project2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project2 = null;
        }
        String templateId = project2.getTemplateId();
        if (templateId == null) {
            templateId = "";
        }
        Template template = templates.get(templateId);
        if (template == null || !StringsKt.contains(musicUri, template.getMusic(), false)) {
            new Thread(() -> new EditorActivity$analyseBeats$1(this, musicUri).invoke()).start();
            return;
        }
        this.beats = Templates.INSTANCE.beatsMs(template, 120000L);
        this.bpm = template.getBpm();
        TimelineView timelineView2 = this.timeline;
        if (timelineView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("timeline");
        } else {
            timelineView = timelineView2;
        }
        timelineView.setBeats(this.beats);
    }

    public final void applyFilterPreview() {
        PlayerView playerView = this.playerView;
        if (playerView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("playerView");
            playerView = null;
        }
        View videoSurfaceView = playerView.getVideoSurfaceView();
        TextureView textureView = videoSurfaceView instanceof TextureView ? (TextureView) videoSurfaceView : null;
        if (textureView == null) {
            return;
        }
        ColorMatrix colorMatrix = new ColorMatrix();
        Project project = this.project;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        String filter = project.getFilter();
        switch (filter.hashCode()) {
            case 3157:
                if (filter.equals("bw")) {
                    colorMatrix.setSaturation(0.0f);
                    break;
                }
                break;
            case 3059529:
                if (filter.equals("cool")) {
                    colorMatrix.setScale(0.9f, 1.0f, 1.12f, 1.0f);
                    break;
                }
                break;
            case 3083853:
                if (filter.equals("diya")) {
                    colorMatrix.setSaturation(1.25f);
                    ColorMatrix colorMatrix2 = new ColorMatrix();
                    colorMatrix2.setScale(1.12f, 1.0f, 0.82f, 1.0f);
                    colorMatrix.postConcat(colorMatrix2);
                    break;
                }
                break;
            case 3641989:
                if (filter.equals("warm")) {
                    colorMatrix.setScale(1.1f, 1.02f, 0.88f, 1.0f);
                    break;
                }
                break;
            case 112220286:
                if (filter.equals("vivid")) {
                    colorMatrix.setSaturation(1.35f);
                    break;
                }
                break;
        }
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        Project project2 = this.project;
        if (project2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project2 = null;
        }
        textureView.setLayerType(2, Intrinsics.areEqual(project2.getFilter(), "none") ? null : paint);
    }

    public final void audioPanel() {
        String str;
        LinearLayout linearLayout = this.panel;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("panel");
            linearLayout = null;
        }
        EditorActivity editorActivity = this;
        Project project = this.project;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        if (project.getMusicUri() == null) {
            str = "No music";
        } else {
            float f = this.bpm;
            str = "Music added" + (f > 0.0f ? "  ·  " + ((int) f) + " BPM" : "");
        }
        linearLayout.addView(UiKt.label$default(editorActivity, str, 13.0f, C.INSTANCE.getDim(), false, 0, 24, null));
        LinearLayout linearLayout2 = this.panel;
        if (linearLayout2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("panel");
            linearLayout2 = null;
        }
        linearLayout2.addView(row(UiKt.chip(editorActivity, "♪ Song library", false, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                ActivityResultLauncher activityResultLauncher;
                activityResultLauncher = EditorActivity.this.musicLib;
                activityResultLauncher.launch(new Intent(EditorActivity.this, (Class<?>) MusicActivity.class).putExtra(MusicActivity.EXTRA_TITLE, "Add a song"));
            }
        }), UiKt.chip(editorActivity, "📁 My phone", false, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                ActivityResultLauncher activityResultLauncher;
                activityResultLauncher = EditorActivity.this.pickMusic;
                activityResultLauncher.launch(new String[]{"audio/*"});
            }
        }), UiKt.chip(editorActivity, "Remove music", false, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                Project project2;
                Project project3;
                Project copy;
                TimelineView timelineView;
                List<Long> list;
                EditorActivity editorActivity2 = EditorActivity.this;
                project2 = editorActivity2.project;
                TimelineView timelineView2 = null;
                if (project2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("project");
                    project3 = null;
                } else {
                    project3 = project2;
                }
                copy = project3.copy(project3.id, project3.name, project3.clips, project3.captions, project3.captionStyle, project3.captionLang, project3.musicUri, project3.originalVolume, project3.filter, project3.templateId, project3.updatedAt);
                editorActivity2.commit(copy);
                EditorActivity.this.beats = CollectionsKt.emptyList();
                timelineView = EditorActivity.this.timeline;
                if (timelineView == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("timeline");
                } else {
                    timelineView2 = timelineView;
                }
                list = EditorActivity.this.beats;
                timelineView2.setBeats(list);
                EditorActivity.this.showTool(MimeTypes.BASE_TYPE_AUDIO);
            }
        }), UiKt.chip(editorActivity, "Snap cuts to beat", false, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                EditorActivity.this.snap();
            }
        })));
        LinearLayout linearLayout3 = this.panel;
        if (linearLayout3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("panel");
            linearLayout3 = null;
        }
        TextView label$default = UiKt.label$default(editorActivity, "Volume of the video's own sound", 13.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        label$default.setPadding(0, UiKt.dp(editorActivity, 6), 0, 0);
        linearLayout3.addView(label$default);
        List listOf = CollectionsKt.listOf(new Pair[]{TuplesKt.to(Float.valueOf(0.0f), "Mute"), TuplesKt.to(Float.valueOf(0.35f), "Low"), TuplesKt.to(Float.valueOf(0.7f), "Medium"), TuplesKt.to(Float.valueOf(1.0f), "Full")});
        LinearLayout linearLayout4 = this.panel;
        if (linearLayout4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("panel");
            linearLayout4 = null;
        }
        List<Pair> list = listOf;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        for (Pair pair : list) {
            final float floatValue = ((Number) pair.component1()).floatValue();
            String str2 = (String) pair.component2();
            Project project2 = this.project;
            if (project2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
                project2 = null;
            }
            arrayList.add(UiKt.chip(editorActivity, str2, Math.abs(project2.getOriginalVolume() - floatValue) < 0.01f, new Function0<Unit>() {

                @Override
                public Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                public final void invoke2() {
                    Project project3;
                    Project copy;
                    EditorActivity editorActivity2 = EditorActivity.this;
                    project3 = editorActivity2.project;
                    if (project3 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("project");
                        project3 = null;
                    }
                    copy = project3.copy(project3.id, project3.name, project3.clips, project3.captions, project3.captionStyle, project3.captionLang, project3.musicUri, floatValue, project3.filter, project3.templateId, project3.updatedAt);
                    editorActivity2.commit(copy);
                    EditorActivity.this.showTool(MimeTypes.BASE_TYPE_AUDIO);
                }
            }));
        }
        TextView[] textViewArr = (TextView[]) arrayList.toArray(new TextView[0]);
        linearLayout4.addView(row((View[]) Arrays.copyOf(textViewArr, textViewArr.length)));
    }

    public final void buildUi() {
        EditorActivity editorActivity = this;
        LinearLayout linearLayout = new LinearLayout(editorActivity);
        linearLayout.setOrientation(1);
        linearLayout.setBackgroundColor(C.INSTANCE.getBg());
        LinearLayout linearLayout2 = new LinearLayout(editorActivity);
        linearLayout2.setGravity(16);
        linearLayout2.setPadding(UiKt.dp(EditorActivity.this, 8), UiKt.dp(editorActivity, 10), UiKt.dp(editorActivity, 12), UiKt.dp(editorActivity, 6));
        TextView label$default = UiKt.label$default(editorActivity, "‹", 30.0f, C.INSTANCE.getText(), true, 0, 16, null);
        label$default.setPadding(UiKt.dp(editorActivity, 12), 0, UiKt.dp(editorActivity, 14), UiKt.dp(editorActivity, 4));
        label$default.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                EditorActivity.buildUi$lambda$8$lambda$7(EditorActivity.this, view);
            }
        });
        linearLayout2.addView(label$default);
        Project project = this.project;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        TextView label$default2 = UiKt.label$default(editorActivity, project.getName(), 17.0f, C.INSTANCE.getText(), true, 0, 16, null);
        label$default2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                EditorActivity.buildUi$lambda$10$lambda$9(EditorActivity.this, view);
            }
        });
        this.titleView = label$default2;
        if (label$default2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("titleView");
            label$default2 = null;
        }
        linearLayout2.addView(label$default2, UiKt.lp(0, UiKt.getWRAP(), 1.0f));
        TextView label$default3 = UiKt.label$default(editorActivity, "↶", 22.0f, C.INSTANCE.getDim(), true, 0, 16, null);
        label$default3.setPadding(UiKt.dp(editorActivity, 10), 0, UiKt.dp(editorActivity, 10), 0);
        label$default3.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                EditorActivity.buildUi$lambda$12$lambda$11(EditorActivity.this, view);
            }
        });
        linearLayout2.addView(label$default3);
        TextView label$default4 = UiKt.label$default(editorActivity, "↷", 22.0f, C.INSTANCE.getDim(), true, 0, 16, null);
        label$default4.setPadding(UiKt.dp(editorActivity, 10), 0, UiKt.dp(editorActivity, 14), 0);
        label$default4.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                EditorActivity.buildUi$lambda$14$lambda$13(EditorActivity.this, view);
            }
        });
        linearLayout2.addView(label$default4);
        TextView textView = new TextView(editorActivity);
        textView.setText("Export");
        textView.setTextColor(-1);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTextSize(15.0f);
        textView.setPadding(UiKt.dp(editorActivity, 18), UiKt.dp(editorActivity, 9), UiKt.dp(editorActivity, 18), UiKt.dp(editorActivity, 9));
        textView.setBackground(UiKt.gradient$default(C.INSTANCE.getSaffron(), C.INSTANCE.getPink(), UiKt.dp(editorActivity, 20), null, 8, null));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                EditorActivity.buildUi$lambda$16$lambda$15(EditorActivity.this, view);
            }
        });
        linearLayout2.addView(textView);
        linearLayout.addView(linearLayout2);
        FrameLayout frameLayout = new FrameLayout(editorActivity);
        frameLayout.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        this.playerHost = frameLayout;
        LayoutInflater from = LayoutInflater.from(editorActivity);
        int i = R.layout.player_view;
        FrameLayout frameLayout2 = this.playerHost;
        if (frameLayout2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("playerHost");
            frameLayout2 = null;
        }
        View inflate = from.inflate(i, (ViewGroup) frameLayout2, false);
        Intrinsics.checkNotNull(inflate, "null cannot be cast to non-null type androidx.media3.ui.PlayerView");
        this.playerView = (PlayerView) inflate;
        FrameLayout frameLayout3 = this.playerHost;
        if (frameLayout3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("playerHost");
            frameLayout3 = null;
        }
        PlayerView playerView = this.playerView;
        if (playerView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("playerView");
            playerView = null;
        }
        frameLayout3.addView(playerView);
        Project tproj = this.project;
        final TemplateSpec templateSpec = tproj == null ? null : TemplateSpec.byId(tproj.getTemplateId());
        if (templateSpec != null) {
            playerView.setVisibility(8);
            final TemplateView templateView2 = new TemplateView(editorActivity);
            this.templateView = templateView2;
            templateView2.setTemplate(templateSpec, null);
            FrameLayout frameLayout7 = this.playerHost;
            if (frameLayout7 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("playerHost");
                frameLayout7 = null;
            }
            frameLayout7.addView(templateView2, 0, new FrameLayout.LayoutParams(UiKt.getMATCH(), UiKt.getMATCH()));
            TemplateEngine.applyOverrides(editorActivity, tproj.getId());
            final Project fproj = tproj;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final java.util.List<Bitmap> bmps = TemplatePhotos.load(editorActivity, fproj.getClips(), 540, 960);
                    templateView2.post(new Runnable() {
                        @Override
                        public void run() {
                            templateView2.setTemplate(templateSpec, bmps);
                            templateView2.play();
                        }
                    });
                }
            }).start();
            if (tproj.getMusicUri() != null) {
                try {
                    MediaPlayer mp = new MediaPlayer();
                    mp.setDataSource(editorActivity, Uri.parse(tproj.getMusicUri()));
                    mp.setLooping(true);
                    mp.prepare();
                    mp.start();
                    this.templateMusic = mp;
                } catch (Exception e) {
                    this.templateMusic = null;
                }
            }
        }
        ImageView imageView = new ImageView(editorActivity);
        imageView.setId(R.id.dummy_image);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setVisibility(8);
        imageView.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        FrameLayout frameLayout4 = this.playerHost;
        if (frameLayout4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("playerHost");
            frameLayout4 = null;
        }
        frameLayout4.addView(imageView, new FrameLayout.LayoutParams(UiKt.getMATCH(), UiKt.getMATCH()));
        TextView textView2 = new TextView(editorActivity);
        textView2.setTextColor(-1);
        textView2.setTextSize(20.0f);
        textView2.setGravity(17);
        textView2.setVisibility(8);
        textView2.setPadding(UiKt.dp(editorActivity, 14), UiKt.dp(editorActivity, 6), UiKt.dp(editorActivity, 14), UiKt.dp(editorActivity, 6));
        this.captionView = textView2;
        FrameLayout frameLayout5 = this.playerHost;
        if (frameLayout5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("playerHost");
            frameLayout5 = null;
        }
        TextView textView3 = this.captionView;
        if (textView3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("captionView");
            textView3 = null;
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(UiKt.getWRAP(), UiKt.getWRAP(), 81);
        layoutParams.bottomMargin = UiKt.dp(editorActivity, 40);
        Unit unit = Unit.INSTANCE;
        frameLayout5.addView(textView3, layoutParams);
        FrameLayout frameLayout6 = this.playerHost;
        if (frameLayout6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("playerHost");
            frameLayout6 = null;
        }
        frameLayout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                EditorActivity.buildUi$lambda$21(EditorActivity.this, view);
            }
        });
        FrameLayout frameLayout7 = this.playerHost;
        if (frameLayout7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("playerHost");
            frameLayout7 = null;
        }
        LinearLayout.LayoutParams lp = UiKt.lp(UiKt.getMATCH(), 0, 1.0f);
        lp.setMargins(UiKt.dp(editorActivity, 60), 0, UiKt.dp(editorActivity, 60), 0);
        Unit unit2 = Unit.INSTANCE;
        linearLayout.addView(frameLayout7, lp);
        LinearLayout linearLayout3 = new LinearLayout(editorActivity);
        linearLayout3.setGravity(16);
        linearLayout3.setPadding(UiKt.dp(editorActivity, 16), UiKt.dp(EditorActivity.this, 8), UiKt.dp(editorActivity, 16), UiKt.dp(editorActivity, 2));
        TextView label$default5 = UiKt.label$default(editorActivity, "0:00 / 0:00", 13.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        this.timeLabel = label$default5;
        if (label$default5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("timeLabel");
            label$default5 = null;
        }
        linearLayout3.addView(label$default5, UiKt.lp(0, UiKt.getWRAP(), 1.0f));
        TextView label$default6 = UiKt.label$default(editorActivity, "▶", 24.0f, C.INSTANCE.getText(), true, 0, 16, null);
        label$default6.setPadding(UiKt.dp(editorActivity, 20), UiKt.dp(editorActivity, 4), UiKt.dp(editorActivity, 20), UiKt.dp(editorActivity, 4));
        label$default6.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                EditorActivity.buildUi$lambda$25$lambda$24(EditorActivity.this, view);
            }
        });
        this.playBtn = label$default6;
        if (label$default6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("playBtn");
            label$default6 = null;
        }
        linearLayout3.addView(label$default6);
        linearLayout3.addView(new View(editorActivity), UiKt.lp(0, 1, 1.0f));
        linearLayout.addView(linearLayout3);
        TimelineView timelineView = new TimelineView(editorActivity);
        this.timeline = timelineView;
        timelineView.setOnSeek(new Function1<Long, Unit>() {

            @Override
            public Unit invoke(Long l) {
                invoke(l.longValue());
                return Unit.INSTANCE;
            }

            public final void invoke(long j) {
                Preview preview;
                preview = EditorActivity.this.preview;
                if (preview == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("preview");
                    preview = null;
                }
                preview.seek(j);
            }
        });
        TimelineView timelineView2 = this.timeline;
        if (timelineView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("timeline");
            timelineView2 = null;
        }
        timelineView2.setOnSelect(new Function1<String, Unit>() {

            @Override
            public Unit invoke(String str) {
                invoke2(str);
                return Unit.INSTANCE;
            }

            public final void invoke2(String str) {
                TimelineView timelineView3;
                String str2;
                String str3;
                String str4;
                EditorActivity.this.selectedId = str;
                timelineView3 = EditorActivity.this.timeline;
                if (timelineView3 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("timeline");
                    timelineView3 = null;
                }
                timelineView3.setSelectedId(str);
                str2 = EditorActivity.this.tool;
                if (!Intrinsics.areEqual(str2, "trim")) {
                    str4 = EditorActivity.this.tool;
                    if (!Intrinsics.areEqual(str4, "speed")) {
                        return;
                    }
                }
                EditorActivity editorActivity2 = EditorActivity.this;
                str3 = editorActivity2.tool;
                editorActivity2.showTool(str3);
            }
        });
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(editorActivity);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        TimelineView timelineView3 = this.timeline;
        if (timelineView3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("timeline");
            timelineView3 = null;
        }
        horizontalScrollView.addView(timelineView3);
        this.hscroll = horizontalScrollView;
        horizontalScrollView.post(new Runnable() {
            @Override
            public final void run() {
                EditorActivity.buildUi$lambda$27(EditorActivity.this);
            }
        });
        HorizontalScrollView horizontalScrollView2 = this.hscroll;
        if (horizontalScrollView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("hscroll");
            horizontalScrollView2 = null;
        }
        linearLayout.addView(horizontalScrollView2, UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        LinearLayout linearLayout4 = new LinearLayout(editorActivity);
        linearLayout4.setOrientation(1);
        linearLayout4.setPadding(UiKt.dp(editorActivity, 14), UiKt.dp(editorActivity, 6), UiKt.dp(editorActivity, 14), UiKt.dp(editorActivity, 6));
        this.panel = linearLayout4;
        ScrollView scrollView = new ScrollView(editorActivity);
        LinearLayout linearLayout5 = this.panel;
        if (linearLayout5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("panel");
            linearLayout5 = null;
        }
        scrollView.addView(linearLayout5);
        linearLayout.addView(scrollView, UiKt.lp$default(UiKt.getMATCH(), UiKt.dp(editorActivity, 200), 0.0f, 4, null));
        List<Pair> listOf = CollectionsKt.listOf(new Pair[]{TuplesKt.to("add", "＋\nAdd"), TuplesKt.to("split", "✂\nSplit"), TuplesKt.to("trim", "⇔\nTrim"), TuplesKt.to("speed", "⏱\nSpeed"), TuplesKt.to(EXTRA_CAPTIONS, "Ａ\nCaptions"), TuplesKt.to("text", "T\nText"), TuplesKt.to(MimeTypes.BASE_TYPE_AUDIO, "\u266a\nMusic"), TuplesKt.to("effects", "\u2728\nEffects"), TuplesKt.to("filters", "\u25d0\nFilters"), TuplesKt.to("delete", "🗑\nDelete")});
        LinearLayout linearLayout6 = new LinearLayout(editorActivity);
        linearLayout6.setPadding(UiKt.dp(editorActivity, 6), UiKt.dp(editorActivity, 4), UiKt.dp(editorActivity, 6), UiKt.dp(EditorActivity.this, 8));
        for (Pair pair : listOf) {
            final String str = (String) pair.component1();
            String str2 = (String) pair.component2();
            TextView textView4 = new TextView(editorActivity);
            String str3 = str2;
            SpannableString spannableString = new SpannableString(str3);
            spannableString.setSpan(new RelativeSizeSpan(1.7f), 0, StringsKt.indexOf(str3, '\n', 0, false), 0);
            textView4.setText(spannableString);
            textView4.setTextColor(C.INSTANCE.getText());
            textView4.setTextSize(12.0f);
            textView4.setGravity(17);
            textView4.setPadding(UiKt.dp(editorActivity, 14), UiKt.dp(EditorActivity.this, 8), UiKt.dp(editorActivity, 14), UiKt.dp(EditorActivity.this, 8));
            textView4.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    EditorActivity.buildUi$lambda$32$lambda$31(EditorActivity.this, str, view);
                }
            });
            linearLayout6.addView(textView4);
        }
        HorizontalScrollView horizontalScrollView3 = new HorizontalScrollView(editorActivity);
        horizontalScrollView3.setHorizontalScrollBarEnabled(false);
        horizontalScrollView3.addView(linearLayout6);
        horizontalScrollView3.setBackgroundColor(C.INSTANCE.getCard());
        linearLayout.addView(horizontalScrollView3);
        setContentView(linearLayout);
    }

    public static final void buildUi$lambda$10$lambda$9(EditorActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.renameProject();
    }

    public static final void buildUi$lambda$12$lambda$11(EditorActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.doUndo();
    }

    public static final void buildUi$lambda$14$lambda$13(EditorActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.doRedo();
    }

    public static final void buildUi$lambda$16$lambda$15(EditorActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.export();
    }

    public static final void buildUi$lambda$21(EditorActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.togglePlay();
    }

    public static final void buildUi$lambda$25$lambda$24(EditorActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.togglePlay();
    }

    public static final void buildUi$lambda$27(EditorActivity this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        TimelineView timelineView = this$0.timeline;
        TimelineView timelineView2 = null;
        if (timelineView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("timeline");
            timelineView = null;
        }
        HorizontalScrollView horizontalScrollView = this$0.hscroll;
        if (horizontalScrollView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("hscroll");
            horizontalScrollView = null;
        }
        timelineView.setMinWidth(horizontalScrollView.getWidth());
        TimelineView timelineView3 = this$0.timeline;
        if (timelineView3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("timeline");
        } else {
            timelineView2 = timelineView3;
        }
        timelineView2.requestLayout();
    }

    public static final void buildUi$lambda$32$lambda$31(EditorActivity this$0, String k, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(k, "$k");
        this$0.onTool(k);
    }

    public static final void buildUi$lambda$8$lambda$7(EditorActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.finish();
    }

    public final void captionsPanel() {
        LinearLayout linearLayout;
        LinearLayout linearLayout2 = this.panel;
        if (linearLayout2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("panel");
            linearLayout2 = null;
        }
        EditorActivity editorActivity = this;
        linearLayout2.addView(UiKt.label$default(editorActivity, "Language of the speech", 13.0f, C.INSTANCE.getDim(), false, 0, 24, null));
        LinearLayout linearLayout3 = this.panel;
        if (linearLayout3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("panel");
            linearLayout3 = null;
        }
        List<Lang> all = Langs.INSTANCE.getAll();
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(all, 10));
        for (final Lang lang : all) {
            String label = lang.getLabel();
            Project project = this.project;
            if (project == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
                project = null;
            }
            arrayList.add(UiKt.chip(editorActivity, label, Intrinsics.areEqual(project.getCaptionLang(), lang.getCode()), new Function0<Unit>() {

                @Override
                public Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                public final void invoke2() {
                    Project project2;
                    Project copy;
                    EditorActivity editorActivity2 = EditorActivity.this;
                    project2 = editorActivity2.project;
                    if (project2 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("project");
                        project2 = null;
                    }
                    copy = project2.copy(project2.id, project2.name, project2.clips, project2.captions, project2.captionStyle, lang.getCode(), project2.musicUri, project2.originalVolume, project2.filter, project2.templateId, project2.updatedAt);
                    editorActivity2.project = copy;
                    EditorActivity.this.showTool(EditorActivity.EXTRA_CAPTIONS);
                }
            }));
        }
        TextView[] textViewArr = (TextView[]) arrayList.toArray(new TextView[0]);
        linearLayout3.addView(row((View[]) Arrays.copyOf(textViewArr, textViewArr.length)));
        LinearLayout linearLayout4 = this.panel;
        if (linearLayout4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("panel");
            linearLayout4 = null;
        }
        TextView label$default = UiKt.label$default(editorActivity, "Style", 13.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        label$default.setPadding(0, UiKt.dp(editorActivity, 6), 0, 0);
        linearLayout4.addView(label$default);
        List listOf = CollectionsKt.listOf(new Pair[]{TuplesKt.to("garba", "ગરબા"), TuplesKt.to("diwali", "દિવાળી"), TuplesKt.to("reels", "Reels")});
        LinearLayout linearLayout5 = this.panel;
        if (linearLayout5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("panel");
            linearLayout5 = null;
        }
        List<Pair> list = listOf;
        ArrayList arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        for (Pair pair : list) {
            final String str = (String) pair.component1();
            String str2 = (String) pair.component2();
            Project project2 = this.project;
            if (project2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
                project2 = null;
            }
            arrayList2.add(UiKt.chip(editorActivity, str2, Intrinsics.areEqual(project2.getCaptionStyle(), str), new Function0<Unit>() {

                @Override
                public Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                public final void invoke2() {
                    Project project3;
                    Project copy;
                    EditorActivity editorActivity2 = EditorActivity.this;
                    project3 = editorActivity2.project;
                    if (project3 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("project");
                        project3 = null;
                    }
                    copy = project3.copy(project3.id, project3.name, project3.clips, project3.captions, str, project3.captionLang, project3.musicUri, project3.originalVolume, project3.filter, project3.templateId, project3.updatedAt);
                    editorActivity2.commit(copy);
                    EditorActivity.this.showTool(EditorActivity.EXTRA_CAPTIONS);
                }
            }));
        }
        TextView[] textViewArr2 = (TextView[]) arrayList2.toArray(new TextView[0]);
        linearLayout5.addView(row((View[]) Arrays.copyOf(textViewArr2, textViewArr2.length)));
        LinearLayout linearLayout6 = this.panel;
        if (linearLayout6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("panel");
            linearLayout6 = null;
        }
        linearLayout6.addView(row(UiKt.chip(editorActivity, "✨ Auto-add: this clip", false, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                Clip selected;
                EditorActivity editorActivity2 = EditorActivity.this;
                selected = editorActivity2.selected();
                editorActivity2.generateCaptions(CollectionsKt.listOfNotNull(selected));
            }
        }), UiKt.chip(editorActivity, "✨ Auto-add: all clips", false, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                Project project3;
                EditorActivity editorActivity2 = EditorActivity.this;
                project3 = editorActivity2.project;
                if (project3 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("project");
                    project3 = null;
                }
                editorActivity2.generateCaptions(project3.getClips());
            }
        }), UiKt.chip(editorActivity, "Clear captions", false, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                Project project3;
                Project copy;
                EditorActivity editorActivity2 = EditorActivity.this;
                project3 = editorActivity2.project;
                if (project3 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("project");
                    project3 = null;
                }
                copy = project3.copy(project3.id, project3.name, project3.clips, CollectionsKt.emptyList(), project3.captionStyle, project3.captionLang, project3.musicUri, project3.originalVolume, project3.filter, project3.templateId, project3.updatedAt);
                editorActivity2.commit(copy);
                EditorActivity.this.showTool(EditorActivity.EXTRA_CAPTIONS);
            }
        })));
        Project project3 = this.project;
        if (project3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project3 = null;
        }
        List<Caption> sortedWith = CollectionsKt.sortedWith(project3.getCaptions(), new Comparator() {
            @Override
            public final int compare(Object t, Object t2) {
                Project project4;
                int i;
                Project project5;
                Caption caption = (Caption) t;
                project4 = EditorActivity.this.project;
                Project project6 = null;
                if (project4 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("project");
                    project4 = null;
                }
                Iterator<Clip> it = project4.getClips().iterator();
                int i2 = 0;
                int i3 = 0;
                while (true) {
                    i = -1;
                    if (!it.hasNext()) {
                        i3 = -1;
                        break;
                    }
                    if (Intrinsics.areEqual(it.next().getId(), caption.getClipId())) {
                        break;
                    }
                    i3++;
                }
                Long valueOf = Long.valueOf((i3 * 10000000) + caption.getSrcStartMs());
                Caption caption2 = (Caption) t2;
                project5 = EditorActivity.this.project;
                if (project5 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("project");
                } else {
                    project6 = project5;
                }
                Iterator<Clip> it2 = project6.getClips().iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    if (Intrinsics.areEqual(it2.next().getId(), caption2.getClipId())) {
                        i = i2;
                        break;
                    }
                    i2++;
                }
                return ComparisonsKt.compareValues(valueOf, Long.valueOf((i * 10000000) + caption2.getSrcStartMs()));
            }
        });
        if (sortedWith.isEmpty()) {
            LinearLayout linearLayout7 = this.panel;
            if (linearLayout7 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("panel");
                linearLayout7 = null;
            }
            TextView label$default2 = UiKt.label$default(editorActivity, "Works offline after the language pack is downloaded once. Tap a caption later to fix a word.", 12.0f, C.INSTANCE.getDim(), false, 0, 24, null);
            label$default2.setPadding(0, UiKt.dp(editorActivity, 6), 0, 0);
            linearLayout7.addView(label$default2);
        }
        for (final Caption caption : sortedWith) {
            LinearLayout linearLayout8 = this.panel;
            if (linearLayout8 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("panel");
                linearLayout = null;
            } else {
                linearLayout = linearLayout8;
            }
            TextView label$default3 = UiKt.label$default(editorActivity, "•  " + caption.getText(), 14.0f, C.INSTANCE.getText(), false, 0, 24, null);
            label$default3.setPadding(0, UiKt.dp(EditorActivity.this, 8), 0, UiKt.dp(EditorActivity.this, 8));
            label$default3.setTypeface(CaptionRenderer.INSTANCE.fontFor(editorActivity, caption.getText()));
            label$default3.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    EditorActivity.captionsPanel$lambda$68$lambda$67(EditorActivity.this, caption, view);
                }
            });
            linearLayout.addView(label$default3);
        }
    }

    public static final void captionsPanel$lambda$68$lambda$67(EditorActivity this$0, Caption cap, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(cap, "$cap");
        this$0.editCaption(cap);
    }

    public final void commit(Project p) {
        Project copy;
        ArrayDeque<Project> arrayDeque = this.undo;
        Project project = this.project;
        Project project2 = null;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        arrayDeque.addLast(project);
        if (this.undo.size() > 50) {
            this.undo.removeFirst();
        }
        this.redo.clear();
        copy = p.copy(p.id, p.name, p.clips, p.captions, p.captionStyle, p.captionLang, p.musicUri, p.originalVolume, p.filter, p.templateId, System.currentTimeMillis());
        this.project = copy;
        Store store = Store.INSTANCE;
        EditorActivity editorActivity = this;
        Project project3 = this.project;
        if (project3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
        } else {
            project2 = project3;
        }
        store.save(editorActivity, project2);
        refreshAll();
    }

    public final String currentClipId() {
        Integer num;
        int size;
        Timeline timeline = Timeline.INSTANCE;
        Project project = this.project;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        List<Long> offsets = timeline.offsets(project.getClips());
        Preview preview = this.preview;
        if (preview == null) {
            Intrinsics.throwUninitializedPropertyAccessException("preview");
            preview = null;
        }
        long timelineMs = preview.getTimelineMs();
        Project project2 = this.project;
        if (project2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project2 = null;
        }
        Iterator<Integer> it = CollectionsKt.getIndices(project2.getClips()).iterator();
        while (true) {
            if (!it.hasNext()) {
                num = null;
                break;
            }
            num = it.next();
            int intValue = num.intValue();
            if (timelineMs >= offsets.get(intValue).longValue()) {
                long longValue = offsets.get(intValue).longValue();
                Project project3 = this.project;
                if (project3 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("project");
                    project3 = null;
                }
                if (timelineMs < longValue + project3.getClips().get(intValue).getLengthMs()) {
                    break;
                }
            }
        }
        Integer num2 = num;
        if (num2 != null) {
            size = num2.intValue();
        } else {
            Project project4 = this.project;
            if (project4 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
                project4 = null;
            }
            size = project4.getClips().size() - 1;
        }
        Project project5 = this.project;
        if (project5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project5 = null;
        }
        Clip clip = (Clip) CollectionsKt.getOrNull(project5.getClips(), size);
        if (clip != null) {
            return clip.getId();
        }
        return null;
    }

    public final void doRedo() {
        if (this.redo.isEmpty()) {
            return;
        }
        ArrayDeque<Project> arrayDeque = this.undo;
        Project project = this.project;
        Project project2 = null;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        arrayDeque.addLast(project);
        this.project = this.redo.removeLast();
        Store store = Store.INSTANCE;
        EditorActivity editorActivity = this;
        Project project3 = this.project;
        if (project3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
        } else {
            project2 = project3;
        }
        store.save(editorActivity, project2);
        refreshAll();
    }

    public final void doUndo() {
        if (this.undo.isEmpty()) {
            return;
        }
        ArrayDeque<Project> arrayDeque = this.redo;
        Project project = this.project;
        Project project2 = null;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        arrayDeque.addLast(project);
        this.project = this.undo.removeLast();
        Store store = Store.INSTANCE;
        EditorActivity editorActivity = this;
        Project project3 = this.project;
        if (project3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
        } else {
            project2 = project3;
        }
        store.save(editorActivity, project2);
        refreshAll();
    }

    public final void editCaption(final Caption cap) {
        EditorActivity editorActivity = this;
        final EditText editText = new EditText(editorActivity);
        editText.setText(cap.getText());
        new AlertDialog.Builder(editorActivity).setTitle("Edit caption").setView(editText).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                EditorActivity.editCaption$lambda$71(editText, editorActivity, cap, dialogInterface, i);
            }
        }).setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                EditorActivity.editCaption$lambda$72(EditorActivity.this, cap, dialogInterface, i);
            }
        }).setNegativeButton("Cancel", (DialogInterface.OnClickListener) null).show();
    }

    public static final void editCaption$lambda$71(EditText et, EditorActivity this$0, Caption cap, DialogInterface dialogInterface, int i) {
        Project project;
        ArrayList arrayList;
        Project copy;
        Intrinsics.checkNotNullParameter(et, "$et");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(cap, "$cap");
        String obj = StringsKt.trim((CharSequence) et.getText().toString()).toString();
        Project project2 = this$0.project;
        Project project3 = null;
        if (project2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        } else {
            project = project2;
        }
        if (obj.length() == 0) {
            Project project4 = this$0.project;
            if (project4 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
            } else {
                project3 = project4;
            }
            arrayList = new ArrayList<Caption>(CollectionsKt.minus((Iterable<Caption>) project3.getCaptions(), cap));
        } else {
            Project project5 = this$0.project;
            if (project5 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
            } else {
                project3 = project5;
            }
            List<Caption> captions = project3.getCaptions();
            ArrayList arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(captions, 10));
            for (Caption caption : captions) {
                if (Intrinsics.areEqual(caption, cap)) {
                    caption = Caption.copy$default(caption, null, 0L, 0L, obj, 7, null);
                }
                arrayList2.add(caption);
            }
            arrayList = arrayList2;
        }
        copy = project3.copy(project3.id, project3.name, project3.clips, arrayList, project3.captionStyle, project3.captionLang, project3.musicUri, project3.originalVolume, project3.filter, project3.templateId, project3.updatedAt);
        this$0.commit(copy);
        this$0.showTool(EXTRA_CAPTIONS);
    }

    public static final void editCaption$lambda$72(EditorActivity this$0, Caption cap, DialogInterface dialogInterface, int i) {
        Project project;
        Project copy;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(cap, "$cap");
        Project project2 = this$0.project;
        Project project3 = null;
        if (project2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        } else {
            project = project2;
        }
        Project project4 = this$0.project;
        if (project4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
        } else {
            project3 = project4;
        }
        copy = project.copy(project.id, project.name, project.clips, (List<Caption>) CollectionsKt.minus((Iterable<Caption>) project3.getCaptions(), cap), project.captionStyle, project.captionLang, project.musicUri, project.originalVolume, project.filter, project.templateId, project.updatedAt);
        this$0.commit(copy);
        this$0.showTool(EXTRA_CAPTIONS);
    }

    public final void export() {
        Preview preview = this.preview;
        Project project = null;
        if (preview == null) {
            Intrinsics.throwUninitializedPropertyAccessException("preview");
            preview = null;
        }
        preview.pause();
        Project project2 = this.project;
        if (project2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project2 = null;
        }
        if (project2.getClips().isEmpty()) {
            toast("Add a clip first");
            return;
        }
        Store store = Store.INSTANCE;
        EditorActivity editorActivity = this;
        Project project3 = this.project;
        if (project3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project3 = null;
        }
        store.save(editorActivity, project3);
        Intent intent = new Intent(editorActivity, (Class<?>) ExportActivity.class);
        Project project4 = this.project;
        if (project4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
        } else {
            project = project4;
        }
        startActivity(intent.putExtra("id", project.getId()));
    }

    public final void filtersPanel() {
        Project fpProject = this.project;
        if (fpProject != null && TemplateSpec.byId(fpProject.getTemplateId()) != null) {
            templateFiltersPanel();
            return;
        }
        LinearLayout linearLayout = this.panel;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("panel");
            linearLayout = null;
        }
        EditorActivity editorActivity = this;
        linearLayout.addView(UiKt.label$default(editorActivity, "Look for the whole video", 13.0f, C.INSTANCE.getDim(), false, 0, 24, null));
        List listOf = CollectionsKt.listOf(new Pair[]{TuplesKt.to("none", "Original"), TuplesKt.to("warm", "Warm"), TuplesKt.to("cool", "Cool"), TuplesKt.to("bw", "Black & white"), TuplesKt.to("vivid", "Vivid"), TuplesKt.to("diya", "Diya glow")});
        LinearLayout linearLayout2 = this.panel;
        if (linearLayout2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("panel");
            linearLayout2 = null;
        }
        List<Pair> list = listOf;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        for (Pair pair : list) {
            final String str = (String) pair.component1();
            String str2 = (String) pair.component2();
            Project project = this.project;
            if (project == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
                project = null;
            }
            arrayList.add(UiKt.chip(editorActivity, str2, Intrinsics.areEqual(project.getFilter(), str), new Function0<Unit>() {

                @Override
                public Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                public final void invoke2() {
                    Project project2;
                    Project copy;
                    EditorActivity editorActivity2 = EditorActivity.this;
                    project2 = editorActivity2.project;
                    if (project2 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("project");
                        project2 = null;
                    }
                    copy = project2.copy(project2.id, project2.name, project2.clips, project2.captions, project2.captionStyle, project2.captionLang, project2.musicUri, project2.originalVolume, str, project2.templateId, project2.updatedAt);
                    editorActivity2.commit(copy);
                    EditorActivity.this.showTool("filters");
                }
            }));
        }
        TextView[] textViewArr = (TextView[]) arrayList.toArray(new TextView[0]);
        linearLayout2.addView(row((View[]) Arrays.copyOf(textViewArr, textViewArr.length)));
    }

    public final void templateFiltersPanel() {
        LinearLayout panel = this.panel;
        if (panel == null) {
            Intrinsics.throwUninitializedPropertyAccessException("panel");
            panel = null;
        }
        final EditorActivity editorActivity = this;
        panel.addView(UiKt.label$default(editorActivity, "Filter (live in preview)", 13.0f, C.INSTANCE.getDim(), false, 0, 24, null));
        Project project = this.project;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        final Project fp = project;
        String cur = project.getFilter();
        String[] names = {"Original", "Warm", "Vivid", "Diya glow", "Rose", "B&W", "Noir", "Cool", "Sepia", "Fade", "Aqua", "Sunset", "Cinema"};
        LinearLayout row2 = new LinearLayout(editorActivity);
        for (int fi = 0; fi < TemplateEngine.FILTERS.length; fi++) {
            final String f = TemplateEngine.FILTERS[fi];
            row2.addView(UiKt.chip(editorActivity, names[fi], Intrinsics.areEqual(cur, f), new Function0<Unit>() {
                @Override
                public Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                public final void invoke2() {
                    Project copy = fp.copy(fp.id, fp.name, fp.clips, fp.captions, fp.captionStyle, fp.captionLang, fp.musicUri, fp.originalVolume, f, fp.templateId, fp.updatedAt);
                    editorActivity.commit(copy);
                    TemplateEngine.filterOverride = "none".equals(f) ? null : f;
                    editorActivity.getSharedPreferences("mukhda", 0).edit().putString("filter_" + fp.getId(), f).apply();
                    if (editorActivity.templateView != null) {
                        editorActivity.templateView.invalidate();
                    }
                    editorActivity.showTool("filters");
                }
            }));
        }
        panel.addView(row2);
    }

    public final void effectsPanel() {
        LinearLayout panel = this.panel;
        if (panel == null) {
            Intrinsics.throwUninitializedPropertyAccessException("panel");
            panel = null;
        }
        final EditorActivity editorActivity = this;
        panel.addView(UiKt.label$default(editorActivity, "Beat effect (live in preview)", 13.0f, C.INSTANCE.getDim(), false, 0, 24, null));
        Project project = this.project;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        final Project fp = project;
        String curFx = editorActivity.getSharedPreferences("mukhda", 0).getString("fx_" + fp.getId(), null);
        String[] fxNames = {"None", "Punch", "Flash", "Shake", "Glitch", "Zoom pulse", "Shine"};
        LinearLayout row2 = new LinearLayout(editorActivity);
        for (int ei = 0; ei < TemplateEngine.EFFECTS.length; ei++) {
            final String fx = TemplateEngine.EFFECTS[ei];
            boolean sel = curFx == null ? "none".equals(fx) : fx.equals(curFx);
            row2.addView(UiKt.chip(editorActivity, fxNames[ei], sel, new Function0<Unit>() {
                @Override
                public Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                public final void invoke2() {
                    editorActivity.getSharedPreferences("mukhda", 0).edit().putString("fx_" + fp.getId(), fx).apply();
                    TemplateEngine.fxOverride = "none".equals(fx) ? null : fx;
                    if (editorActivity.templateView != null) {
                        editorActivity.templateView.invalidate();
                    }
                    editorActivity.showTool("effects");
                }
            }));
        }
        panel.addView(row2);
    }

    public final void generateCaptions(List<Clip> clips) {
        ArrayList arrayList = new ArrayList();
        for (Object obj : clips) {
            if (!((Clip) obj).isImage()) {
                arrayList.add(obj);
            }
        }
        final ArrayList arrayList2 = arrayList;
        if (arrayList2.isEmpty()) {
            toast("Pick a video clip first");
            return;
        }
        Preview preview = this.preview;
        if (preview == null) {
            Intrinsics.throwUninitializedPropertyAccessException("preview");
            preview = null;
        }
        preview.pause();
        Langs langs = Langs.INSTANCE;
        Project project = this.project;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        final Lang lang = langs.get(project.getCaptionLang());
        EditorActivity editorActivity = this;
        if (ModelStore.INSTANCE.isReady(editorActivity, lang.getModel())) {
            runCaptionJob(arrayList2, lang, false);
            return;
        }
        if (!isOnline()) {
            toast("First time needs internet to get the " + lang.getLabel() + " language pack (" + lang.getSizeMb() + " MB). After that it works offline.");
        } else {
            new AlertDialog.Builder(editorActivity).setTitle("Download " + lang.getLabel() + " captions?").setMessage("A one-time " + lang.getSizeMb() + " MB language pack. After this, captions work offline and free.").setPositiveButton("Download", new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    EditorActivity.generateCaptions$lambda$74(EditorActivity.this, arrayList2, lang, dialogInterface, i);
                }
            }).setNegativeButton("Cancel", (DialogInterface.OnClickListener) null).show();
        }
    }

    public static final void generateCaptions$lambda$74(EditorActivity this$0, List vids, Lang lang, DialogInterface dialogInterface, int i) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(vids, "$vids");
        Intrinsics.checkNotNullParameter(lang, "$lang");
        this$0.runCaptionJob(vids, lang, true);
    }

    public final boolean isOnline() {
        Object systemService = getSystemService("connectivity");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.net.ConnectivityManager");
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) systemService).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public final void loadThumbs() {
        Project project = this.project;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        List<Clip> clips = project.getClips();
        ArrayList arrayList = new ArrayList();
        for (Object obj : clips) {
            Clip clip = (Clip) obj;
            TimelineView timelineView = this.timeline;
            if (timelineView == null) {
                Intrinsics.throwUninitializedPropertyAccessException("timeline");
                timelineView = null;
            }
            if (!timelineView.getThumbs().containsKey(clip.getId())) {
                arrayList.add(obj);
            }
        }
        ArrayList arrayList2 = arrayList;
        if (arrayList2.isEmpty()) {
            return;
        }
        new Thread(() -> new EditorActivity$loadThumbs$1(arrayList2, this).invoke()).start();
    }

    public final void move(int i, int d) {
        Project project;
        Project copy;
        int i2 = i + d;
        if (i2 >= 0) {
            Project project2 = this.project;
            if (project2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
                project2 = null;
            }
            if (i2 >= project2.getClips().size()) {
                return;
            }
            Project project3 = this.project;
            if (project3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
                project3 = null;
            }
            List mutableList = CollectionsKt.toMutableList((Collection) project3.getClips());
            Clip clip = (Clip) mutableList.get(i);
            mutableList.set(i, mutableList.get(i2));
            mutableList.set(i2, clip);
            Project project4 = this.project;
            if (project4 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
                project = null;
            } else {
                project = project4;
            }
            copy = project.copy(project.id, project.name, mutableList, project.captions, project.captionStyle, project.captionLang, project.musicUri, project.originalVolume, project.filter, project.templateId, project.updatedAt);
            commit(copy);
        }
    }

    public static final void musicLib$lambda$2(EditorActivity this$0, ActivityResult activityResult) {
        String stringExtra;
        Project copy;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intent data = activityResult.getData();
        if (data != null && activityResult.getResultCode() == -1) {
            String stringExtra2 = data.getStringExtra(MusicActivity.EXTRA_TRACK);
            if (stringExtra2 != null) {
                stringExtra = Music.INSTANCE.uri(this$0, stringExtra2).toString();
            } else {
                stringExtra = data.getStringExtra(MusicActivity.EXTRA_URI);
                if (stringExtra == null) {
                    return;
                }
            }
            String str = stringExtra;
            Intrinsics.checkNotNull(str);
            Project project = this$0.project;
            if (project == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
                project = null;
            }
            copy = project.copy(project.id, project.name, project.clips, project.captions, project.captionStyle, project.captionLang, str, project.originalVolume, project.filter, project.templateId, project.updatedAt);
            this$0.commit(copy);
            this$0.analyseBeats();
            this$0.showTool(MimeTypes.BASE_TYPE_AUDIO);
        }
    }

    public static final void onCreate$lambda$4(EditorActivity this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.showTool(EXTRA_CAPTIONS);
    }

    public final void onTime(long t) {
        Object obj;
        TimelineView timelineView = this.timeline;
        HorizontalScrollView horizontalScrollView = null;
        if (timelineView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("timeline");
            timelineView = null;
        }
        timelineView.setPlayheadMs(t);
        TextView textView = this.timeLabel;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("timeLabel");
            textView = null;
        }
        String fmtTime = UiKt.fmtTime(t);
        Project project = this.project;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        textView.setText(fmtTime + " / " + UiKt.fmtTime(project.getTotalMs()));
        Timeline timeline = Timeline.INSTANCE;
        Project project2 = this.project;
        if (project2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project2 = null;
        }
        Iterator it = timeline.timedCaptions(project2).iterator();
        while (true) {
            if (!it.hasNext()) {
                obj = null;
                break;
            }
            obj = it.next();
            TimedCaption timedCaption = (TimedCaption) obj;
            if (t >= timedCaption.getStartMs() && t < timedCaption.getEndMs()) {
                break;
            }
        }
        TimedCaption timedCaption2 = (TimedCaption) obj;
        if (timedCaption2 == null) {
            TextView textView2 = this.captionView;
            if (textView2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("captionView");
                textView2 = null;
            }
            textView2.setVisibility(8);
        } else {
            TextView textView3 = this.captionView;
            if (textView3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("captionView");
                textView3 = null;
            }
            textView3.setVisibility(0);
            TextView textView4 = this.captionView;
            if (textView4 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("captionView");
                textView4 = null;
            }
            textView4.setText(timedCaption2.getText());
            TextView textView5 = this.captionView;
            if (textView5 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("captionView");
                textView5 = null;
            }
            textView5.setTypeface(CaptionRenderer.INSTANCE.fontFor(this, timedCaption2.getText()));
            Project project3 = this.project;
            if (project3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
                project3 = null;
            }
            String captionStyle = project3.getCaptionStyle();
            if (Intrinsics.areEqual(captionStyle, "diwali")) {
                TextView textView6 = this.captionView;
                if (textView6 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("captionView");
                    textView6 = null;
                }
                textView6.setTextColor(Color.parseColor("#1A1005"));
                TextView textView7 = this.captionView;
                if (textView7 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("captionView");
                    textView7 = null;
                }
                textView7.setBackground(UiKt.solid$default(Color.parseColor("#FFD84A"), UiKt.dp(EditorActivity.this, 8), 0, 0, 12, null));
            } else if (Intrinsics.areEqual(captionStyle, "reels")) {
                TextView textView8 = this.captionView;
                if (textView8 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("captionView");
                    textView8 = null;
                }
                textView8.setTextColor(-1);
                TextView textView9 = this.captionView;
                if (textView9 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("captionView");
                    textView9 = null;
                }
                textView9.setBackground(UiKt.gradient$default(C.INSTANCE.getSaffron(), C.INSTANCE.getPink(), UiKt.dp(EditorActivity.this, 8), null, 8, null));
            } else {
                TextView textView10 = this.captionView;
                if (textView10 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("captionView");
                    textView10 = null;
                }
                textView10.setTextColor(Color.parseColor("#FFC24A"));
                TextView textView11 = this.captionView;
                if (textView11 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("captionView");
                    textView11 = null;
                }
                textView11.setBackground(null);
                TextView textView12 = this.captionView;
                if (textView12 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("captionView");
                    textView12 = null;
                }
                textView12.setShadowLayer(8.0f, 0.0f, 0.0f, Color.parseColor("#7A1030"));
            }
        }
        Preview preview = this.preview;
        if (preview == null) {
            Intrinsics.throwUninitializedPropertyAccessException("preview");
            preview = null;
        }
        if (preview.getPlaying()) {
            TimelineView timelineView2 = this.timeline;
            if (timelineView2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("timeline");
                timelineView2 = null;
            }
            int playheadX = (int) timelineView2.playheadX();
            HorizontalScrollView horizontalScrollView2 = this.hscroll;
            if (horizontalScrollView2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("hscroll");
                horizontalScrollView2 = null;
            }
            if (playheadX >= horizontalScrollView2.getScrollX()) {
                HorizontalScrollView horizontalScrollView3 = this.hscroll;
                if (horizontalScrollView3 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("hscroll");
                    horizontalScrollView3 = null;
                }
                int scrollX = horizontalScrollView3.getScrollX();
                HorizontalScrollView horizontalScrollView4 = this.hscroll;
                if (horizontalScrollView4 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("hscroll");
                    horizontalScrollView4 = null;
                }
                if (playheadX <= (scrollX + horizontalScrollView4.getWidth()) - UiKt.dp(this, 40)) {
                    return;
                }
            }
            HorizontalScrollView horizontalScrollView5 = this.hscroll;
            if (horizontalScrollView5 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("hscroll");
                horizontalScrollView5 = null;
            }
            HorizontalScrollView horizontalScrollView6 = this.hscroll;
            if (horizontalScrollView6 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("hscroll");
            } else {
                horizontalScrollView = horizontalScrollView6;
            }
            horizontalScrollView5.scrollTo(Math.max(0, playheadX - (horizontalScrollView.getWidth() / 3)), 0);
        }
    }

    public final void onTool(String k) {
        Preview preview = null;
        switch (k.hashCode()) {
            case -1335458389:
                if (k.equals("delete")) {
                    final Clip selected = selected();
                    if (selected == null) {
                        toast("Tap a clip first");
                        return;
                    }
                    Project project = this.project;
                    if (project == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("project");
                        project = null;
                    }
                    if (project.getClips().size() == 1) {
                        toast("A project needs at least one clip");
                        return;
                    } else {
                        new AlertDialog.Builder(this).setMessage("Delete this clip?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                EditorActivity.onTool$lambda$43(EditorActivity.this, selected, dialogInterface, i);
                            }
                        }).setNegativeButton("Cancel", (DialogInterface.OnClickListener) null).show();
                        return;
                    }
                }
                showTool(k);
                return;
            case 96417:
                if (k.equals("add")) {
                    this.addMedia.launch(new String[]{"video/*", "image/*"});
                    return;
                }
                showTool(k);
                return;
            case 3556653:
                if (k.equals("text")) {
                    addText();
                    return;
                }
                showTool(k);
                return;
            case 109648666:
                if (k.equals("split")) {
                    Preview preview2 = this.preview;
                    if (preview2 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("preview");
                        preview2 = null;
                    }
                    preview2.pause();
                    Timeline timeline = Timeline.INSTANCE;
                    Project project2 = this.project;
                    if (project2 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("project");
                        project2 = null;
                    }
                    Preview preview3 = this.preview;
                    if (preview3 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("preview");
                    } else {
                        preview = preview3;
                    }
                    Project split = timeline.split(project2, preview.getTimelineMs());
                    if (split == null) {
                        toast("Move the line to the middle of a clip to split");
                        return;
                    } else {
                        commit(split);
                        return;
                    }
                }
                showTool(k);
                return;
            default:
                showTool(k);
                return;
        }
    }

    public static final void onTool$lambda$43(EditorActivity this$0, Clip c, DialogInterface dialogInterface, int i) {
        Project project;
        Project copy;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(c, "$c");
        Project project2 = this$0.project;
        Project project3 = null;
        if (project2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        } else {
            project = project2;
        }
        Project project4 = this$0.project;
        if (project4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project4 = null;
        }
        List<Clip> clips = project4.getClips();
        ArrayList arrayList = new ArrayList();
        for (Object obj : clips) {
            if (!Intrinsics.areEqual(((Clip) obj).getId(), c.getId())) {
                arrayList.add(obj);
            }
        }
        ArrayList arrayList2 = arrayList;
        Project project5 = this$0.project;
        if (project5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
        } else {
            project3 = project5;
        }
        List<Caption> captions = project3.getCaptions();
        ArrayList arrayList3 = new ArrayList();
        for (Object obj2 : captions) {
            if (!Intrinsics.areEqual(((Caption) obj2).getClipId(), c.getId())) {
                arrayList3.add(obj2);
            }
        }
        copy = project.copy(project.id, project.name, arrayList2, arrayList3, project.captionStyle, project.captionLang, project.musicUri, project.originalVolume, project.filter, project.templateId, project.updatedAt);
        this$0.commit(copy);
    }

    public static final void pickMusic$lambda$1(EditorActivity this$0, Uri uri) {
        Project copy;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (uri == null) {
            return;
        }
        Media.INSTANCE.persist(this$0, uri);
        Project project = this$0.project;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        copy = project.copy(project.id, project.name, project.clips, project.captions, project.captionStyle, project.captionLang, uri.toString(), project.originalVolume, project.filter, project.templateId, project.updatedAt);
        this$0.commit(copy);
        this$0.analyseBeats();
        this$0.showTool(MimeTypes.BASE_TYPE_AUDIO);
    }

    public final void refreshAll() {
        Preview preview = null;
        if (this.selectedId != null) {
            Project project = this.project;
            if (project == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
                project = null;
            }
            List<Clip> clips = project.getClips();
            if (!(clips instanceof Collection) || !clips.isEmpty()) {
                Iterator it = clips.iterator();
                while (it.hasNext()) {
                    if (Intrinsics.areEqual(((Clip) it.next()).getId(), this.selectedId)) {
                        break;
                    }
                }
            }
            Project project2 = this.project;
            if (project2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
                project2 = null;
            }
            Clip clip = (Clip) CollectionsKt.firstOrNull((List) project2.getClips());
            this.selectedId = clip != null ? clip.getId() : null;
        }
        TimelineView timelineView = this.timeline;
        if (timelineView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("timeline");
            timelineView = null;
        }
        Project project3 = this.project;
        if (project3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project3 = null;
        }
        timelineView.setProject(project3);
        TimelineView timelineView2 = this.timeline;
        if (timelineView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("timeline");
            timelineView2 = null;
        }
        timelineView2.setSelectedId(this.selectedId);
        TimelineView timelineView3 = this.timeline;
        if (timelineView3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("timeline");
            timelineView3 = null;
        }
        timelineView3.setBeats(this.beats);
        Preview preview2 = this.preview;
        if (preview2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("preview");
            preview2 = null;
        }
        Project project4 = this.project;
        if (project4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project4 = null;
        }
        preview2.setProject(project4);
        applyFilterPreview();
        Preview preview3 = this.preview;
        if (preview3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("preview");
        } else {
            preview = preview3;
        }
        onTime(preview.getTimelineMs());
        loadThumbs();
    }

    public final void renameProject() {
        EditorActivity editorActivity = this;
        final EditText editText = new EditText(editorActivity);
        Project project = this.project;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        editText.setText(project.getName());
        editText.setSingleLine();
        new AlertDialog.Builder(editorActivity).setTitle("Project name").setView(editText).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                EditorActivity.renameProject$lambda$35(editText, editorActivity, dialogInterface, i);
            }
        }).setNegativeButton("Cancel", (DialogInterface.OnClickListener) null).show();
    }

    public static final void renameProject$lambda$35(EditText et, EditorActivity this$0, DialogInterface dialogInterface, int i) {
        Project copy;
        Intrinsics.checkNotNullParameter(et, "$et");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        String obj = StringsKt.trim((CharSequence) et.getText().toString()).toString();
        String str = obj;
        if (str.length() > 0) {
            Project project = this$0.project;
            if (project == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
                project = null;
            }
            copy = project.copy(project.id, obj, project.clips, project.captions, project.captionStyle, project.captionLang, project.musicUri, project.originalVolume, project.filter, project.templateId, project.updatedAt);
            this$0.project = copy;
            TextView textView = this$0.titleView;
            if (textView == null) {
                Intrinsics.throwUninitializedPropertyAccessException("titleView");
                textView = null;
            }
            textView.setText(str);
            Store store = Store.INSTANCE;
            EditorActivity editorActivity = this$0;
            Project project2 = this$0.project;
            if (project2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
                project2 = null;
            }
            store.save(editorActivity, project2);
        }
    }

    public final HorizontalScrollView row(View... v) {
        EditorActivity editorActivity = this;
        LinearLayout linearLayout = new LinearLayout(editorActivity);
        linearLayout.setGravity(16);
        for (View view : v) {
            LinearLayout.LayoutParams lp$default = UiKt.lp$default(UiKt.getWRAP(), UiKt.getWRAP(), 0.0f, 4, null);
            lp$default.rightMargin = UiKt.dp(EditorActivity.this, 8);
            Unit unit = Unit.INSTANCE;
            linearLayout.addView(view, lp$default);
        }
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(editorActivity);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        horizontalScrollView.addView(linearLayout);
        horizontalScrollView.setPadding(0, UiKt.dp(editorActivity, 4), 0, UiKt.dp(editorActivity, 4));
        return horizontalScrollView;
    }

    public final void runBusy(String msg, Function0<Unit> work) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(() -> new EditorActivity$runBusy$1(work, this, progressDialog).invoke()).start();
    }

    public final void runCaptionJob(List<Clip> vids, Lang lang, boolean download) {
        String str;
        this.cancelJob = false;
        ProgressDialog progressDialog = new ProgressDialog(this);
        if (download) {
            str = "Getting " + lang.getLabel() + " language pack";
        } else {
            str = "Listening…";
        }
        progressDialog.setTitle(str);
        progressDialog.setMessage("Please keep Mukhda open");
        progressDialog.setProgressStyle(1);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setButton(-2, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                EditorActivity.runCaptionJob$lambda$76$lambda$75(EditorActivity.this, dialogInterface, i);
            }
        });
        this.progress = progressDialog;
        progressDialog.show();
        new Thread(() -> new EditorActivity$runCaptionJob$1(download, this, lang, vids, progressDialog).invoke()).start();
    }

    public static final void runCaptionJob$lambda$76$lambda$75(EditorActivity this$0, DialogInterface dialogInterface, int i) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.cancelJob = true;
    }

    public final Clip selected() {
        Project project = this.project;
        Object obj = null;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        Iterator it = project.getClips().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Object next = it.next();
            if (Intrinsics.areEqual(((Clip) next).getId(), this.selectedId)) {
                obj = next;
                break;
            }
        }
        return (Clip) obj;
    }

    public final void showTool(String k) {
        this.tool = k;
        LinearLayout linearLayout = this.panel;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("panel");
            linearLayout = null;
        }
        linearLayout.removeAllViews();
        switch (k.hashCode()) {
            case -854547461:
                if (k.equals("filters")) {
                    filtersPanel();
                    return;
                }
                return;
            case -50093235:
                if (k.equals(EXTRA_CAPTIONS)) {
                    captionsPanel();
                    return;
                }
                return;
            case 3568674:
                if (k.equals("trim")) {
                    trimPanel();
                    return;
                }
                return;
            case 93166550:
                if (k.equals(MimeTypes.BASE_TYPE_AUDIO)) {
                    audioPanel();
                    return;
                }
                return;
            case 109641799:
                if (k.equals("speed")) {
                    speedPanel();
                    return;
                }
                return;
            case -1833928446:
                if (k.equals("effects")) {
                    effectsPanel();
                    return;
                }
                return;
            default:
                return;
        }
    }

    public final void snap() {
        Project project = this.project;
        Project project2 = null;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        if (project.getMusicUri() == null) {
            toast("Add music first");
            return;
        }
        if (this.beats.isEmpty()) {
            toast("No beat found in this music");
            return;
        }
        Preview preview = this.preview;
        if (preview == null) {
            Intrinsics.throwUninitializedPropertyAccessException("preview");
            preview = null;
        }
        preview.pause();
        Timeline timeline = Timeline.INSTANCE;
        Project project3 = this.project;
        if (project3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
        } else {
            project2 = project3;
        }
        commit(timeline.snapToBeats(project2, this.beats));
        toast("Cuts now land on the beat");
    }

    public final void speedPanel() {
        final Clip selected = selected();
        LinearLayout linearLayout = null;
        if (selected == null) {
            LinearLayout linearLayout2 = this.panel;
            if (linearLayout2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("panel");
            } else {
                linearLayout = linearLayout2;
            }
            linearLayout.addView(UiKt.label$default(this, "Tap a clip on the timeline", 14.0f, C.INSTANCE.getDim(), false, 0, 24, null));
            return;
        }
        if (selected.isImage()) {
            LinearLayout linearLayout3 = this.panel;
            if (linearLayout3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("panel");
            } else {
                linearLayout = linearLayout3;
            }
            linearLayout.addView(UiKt.label$default(this, "Speed works on videos. Use Trim to change how long a photo stays.", 14.0f, C.INSTANCE.getDim(), false, 0, 24, null));
            return;
        }
        LinearLayout linearLayout4 = this.panel;
        if (linearLayout4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("panel");
            linearLayout4 = null;
        }
        EditorActivity editorActivity = this;
        linearLayout4.addView(UiKt.label$default(editorActivity, "Speed of this clip", 13.0f, C.INSTANCE.getDim(), false, 0, 24, null));
        List listOf = CollectionsKt.listOf(new Float[]{Float.valueOf(0.5f), Float.valueOf(0.75f), Float.valueOf(1.0f), Float.valueOf(1.5f), Float.valueOf(2.0f), Float.valueOf(3.0f)});
        LinearLayout linearLayout5 = this.panel;
        if (linearLayout5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("panel");
        } else {
            linearLayout = linearLayout5;
        }
        List list = listOf;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        Iterator it = list.iterator();
        while (it.hasNext()) {
            final float floatValue = ((Number) it.next()).floatValue();
            arrayList.add(UiKt.chip(editorActivity, StringsKt.replace(floatValue + "x", ".0x", "x", false), selected.getSpeed() == floatValue, new Function0<Unit>() {

                @Override
                public Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                public final void invoke2() {
                    Project project;
                    Project project2;
                    Project project3;
                    Project copy;
                    ArrayList arrayList2;
                    EditorActivity editorActivity2 = EditorActivity.this;
                    project = editorActivity2.project;
                    Project project4 = null;
                    if (project == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("project");
                        project2 = null;
                    } else {
                        project2 = project;
                    }
                    project3 = EditorActivity.this.project;
                    if (project3 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("project");
                    } else {
                        project4 = project3;
                    }
                    List<Clip> clips = project4.getClips();
                    Clip clip = selected;
                    float f = floatValue;
                    ArrayList arrayList3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(clips, 10));
                    for (Clip clip2 : clips) {
                        if (Intrinsics.areEqual(clip2.getId(), clip.getId())) {
                            clip2 = clip2.copy(clip2.id, clip2.uri, clip2.isImage, clip2.durationMs, clip2.startMs, clip2.endMs, f, clip2.zoom);
                            arrayList2 = arrayList3;
                        } else {
                            arrayList2 = arrayList3;
                        }
                        arrayList2.add(clip2);
                        arrayList3 = arrayList2;
                    }
                    copy = project2.copy(project2.id, project2.name, arrayList3, project2.captions, project2.captionStyle, project2.captionLang, project2.musicUri, project2.originalVolume, project2.filter, project2.templateId, project2.updatedAt);
                    editorActivity2.commit(copy);
                    EditorActivity.this.showTool("speed");
                }
            }));
        }
        TextView[] textViewArr = (TextView[]) arrayList.toArray(new TextView[0]);
        linearLayout.addView(row((View[]) Arrays.copyOf(textViewArr, textViewArr.length)));
    }

    public final void toast(String s) {
        Toast.makeText(this, s, 1).show();
    }

    public final void togglePlay() {
        if (this.templateView != null) {
            if (this.templateView.isPlaying()) {
                this.templateView.pause();
                if (this.templateMusic != null) {
                    try { this.templateMusic.pause(); } catch (Exception ignored) {}
                }
            } else {
                this.templateView.play();
                if (this.templateMusic != null) {
                    try { this.templateMusic.start(); } catch (Exception ignored) {}
                }
            }
            return;
        }
        Preview preview = this.preview;
        Preview preview2 = null;
        if (preview == null) {
            Intrinsics.throwUninitializedPropertyAccessException("preview");
            preview = null;
        }
        if (preview.getPlaying()) {
            Preview preview3 = this.preview;
            if (preview3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("preview");
            } else {
                preview2 = preview3;
            }
            preview2.pause();
            return;
        }
        Preview preview4 = this.preview;
        if (preview4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("preview");
        } else {
            preview2 = preview4;
        }
        preview2.play();
    }

    public final void trimPanel() {
        final Clip selected = selected();
        LinearLayout linearLayout = null;
        if (selected == null) {
            LinearLayout linearLayout2 = this.panel;
            if (linearLayout2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("panel");
            } else {
                linearLayout = linearLayout2;
            }
            linearLayout.addView(UiKt.label$default(this, "Tap a clip on the timeline", 14.0f, C.INSTANCE.getDim(), false, 0, 24, null));
            return;
        }
        EditorActivity editorActivity = this;
        final TextView label$default = UiKt.label$default(editorActivity, "", 13.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        if (selected.isImage()) {
            String format = String.format("%.1f", Arrays.copyOf(new Object[]{Double.valueOf((selected.getEndMs() - selected.getStartMs()) / 1000.0d)}, 1));
            Intrinsics.checkNotNullExpressionValue(format, "format(...)");
            label$default.setText("Photo length: " + format + "s");
            Slider slider = new Slider(editorActivity);
            slider.setValueFrom(1.0f);
            slider.setValueTo(10.0f);
            slider.setStepSize(0.5f);
            slider.setValue(RangesKt.coerceIn(((float) (selected.getEndMs() - selected.getStartMs())) / 1000.0f, 1.0f, 10.0f));
            slider.addOnChangeListener(new Slider.OnChangeListener() {
                @Override
                public final void onValueChange(Slider slider2, float f, boolean z) {
                    EditorActivity.trimPanel$lambda$49(label$default, slider2, f, z);
                }
            });
            slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
                @Override
                public void onStartTrackingTouch(Slider slider2) {
                    Intrinsics.checkNotNullParameter(slider2, "slider");
                }

                @Override
                public void onStopTrackingTouch(Slider slider2) {
                    Project project;
                    Project project2;
                    Project project3;
                    Project copy;
                    Intrinsics.checkNotNullParameter(slider2, "slider");
                    EditorActivity editorActivity2 = EditorActivity.this;
                    project = editorActivity2.project;
                    Project project4 = null;
                    if (project == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("project");
                        project2 = null;
                    } else {
                        project2 = project;
                    }
                    project3 = EditorActivity.this.project;
                    if (project3 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("project");
                    } else {
                        project4 = project3;
                    }
                    List<Clip> clips = project4.getClips();
                    Clip clip = selected;
                    ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(clips, 10));
                    for (Clip clip2 : clips) {
                        if (Intrinsics.areEqual(clip2.getId(), clip.getId())) {
                            float f = 1000;
                            clip2 = clip2.copy(clip2.id, clip2.uri, clip2.isImage, Math.max(clip2.getDurationMs(), (long)(slider2.getValue() * f)), clip2.startMs, (long)(slider2.getValue() * f), clip2.speed, clip2.zoom);
                        }
                        arrayList.add(clip2);
                    }
                    copy = project2.copy(project2.id, project2.name, arrayList, project2.captions, project2.captionStyle, project2.captionLang, project2.musicUri, project2.originalVolume, project2.filter, project2.templateId, project2.updatedAt);
                    editorActivity2.commit(copy);
                }
            });
            LinearLayout linearLayout3 = this.panel;
            if (linearLayout3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("panel");
                linearLayout3 = null;
            }
            linearLayout3.addView(label$default);
            LinearLayout linearLayout4 = this.panel;
            if (linearLayout4 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("panel");
                linearLayout4 = null;
            }
            linearLayout4.addView(slider);
        } else {
            label$default.setText("Keep " + UiKt.fmtTime(selected.getStartMs()) + " to " + UiKt.fmtTime(selected.getEndMs()) + " of " + UiKt.fmtTime(selected.getDurationMs()));
            RangeSlider rangeSlider = new RangeSlider(editorActivity);
            rangeSlider.setValueFrom(0.0f);
            rangeSlider.setValueTo((float) selected.getDurationMs());
            rangeSlider.setValues(Float.valueOf((float) selected.getStartMs()), Float.valueOf((float) selected.getEndMs()));
            rangeSlider.setMinSeparationValue(500.0f);
            rangeSlider.setLabelFormatter(new LabelFormatter() {
                @Override
                public final String getFormattedValue(float f) {
                    String trimPanel$lambda$51$lambda$50;
                    trimPanel$lambda$51$lambda$50 = EditorActivity.trimPanel$lambda$51$lambda$50(f);
                    return trimPanel$lambda$51$lambda$50;
                }
            });
            rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
                @Override
                public final void onValueChange(RangeSlider rangeSlider2, float f, boolean z) {
                    EditorActivity.trimPanel$lambda$52(label$default, selected, rangeSlider2, f, z);
                }
            });
            rangeSlider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
                @Override
                public void onStartTrackingTouch(RangeSlider slider2) {
                    Intrinsics.checkNotNullParameter(slider2, "slider");
                }

                @Override
                public void onStopTrackingTouch(RangeSlider slider2) {
                    Project project;
                    Project project2;
                    Project project3;
                    Project copy;
                    Clip clip;
                    long j;
                    long j2;
                    ArrayList arrayList;
                    Intrinsics.checkNotNullParameter(slider2, "slider");
                    long floatValue = (long) slider2.getValues().get(0).floatValue();
                    long floatValue2 = (long) slider2.getValues().get(1).floatValue();
                    EditorActivity editorActivity2 = EditorActivity.this;
                    project = editorActivity2.project;
                    Project project4 = null;
                    if (project == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("project");
                        project2 = null;
                    } else {
                        project2 = project;
                    }
                    project3 = EditorActivity.this.project;
                    if (project3 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("project");
                    } else {
                        project4 = project3;
                    }
                    List<Clip> clips = project4.getClips();
                    Clip clip2 = selected;
                    ArrayList arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(clips, 10));
                    for (Clip clip3 : clips) {
                        if (Intrinsics.areEqual(clip3.getId(), clip2.getId())) {
                            clip = clip2;
                            j = floatValue2;
                            j2 = floatValue;
                            clip3 = clip3.copy(clip3.id, clip3.uri, clip3.isImage, clip3.durationMs, floatValue, floatValue2, clip3.speed, clip3.zoom);
                            arrayList = arrayList2;
                        } else {
                            clip = clip2;
                            j = floatValue2;
                            j2 = floatValue;
                            arrayList = arrayList2;
                        }
                        arrayList.add(clip3);
                        arrayList2 = arrayList;
                        floatValue = j2;
                        clip2 = clip;
                        floatValue2 = j;
                    }
                    copy = project2.copy(project2.id, project2.name, arrayList2, project2.captions, project2.captionStyle, project2.captionLang, project2.musicUri, project2.originalVolume, project2.filter, project2.templateId, project2.updatedAt);
                    editorActivity2.commit(copy);
                }
            });
            LinearLayout linearLayout5 = this.panel;
            if (linearLayout5 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("panel");
                linearLayout5 = null;
            }
            linearLayout5.addView(label$default);
            LinearLayout linearLayout6 = this.panel;
            if (linearLayout6 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("panel");
                linearLayout6 = null;
            }
            linearLayout6.addView(rangeSlider);
        }
        Project project = this.project;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        Iterator<Clip> it = project.getClips().iterator();
        int i = 0;
        while (true) {
            if (!it.hasNext()) {
                i = -1;
                break;
            } else if (Intrinsics.areEqual(it.next().getId(), selected.getId())) {
                break;
            } else {
                i++;
            }
        }
        final int fi = i;
        LinearLayout linearLayout7 = this.panel;
        if (linearLayout7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("panel");
        } else {
            linearLayout = linearLayout7;
        }
        linearLayout.addView(row(UiKt.chip(editorActivity, "◀ Move left", false, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                EditorActivity.this.move(fi, -1);
            }
        }), UiKt.chip(editorActivity, "Move right ▶", false, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                EditorActivity.this.move(fi, 1);
            }
        }), UiKt.chip(editorActivity, "Duplicate", false, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                Project project2;
                Project project3;
                Project project4;
                Clip copy;
                Project project5;
                Project copy2;
                EditorActivity editorActivity2 = EditorActivity.this;
                project2 = editorActivity2.project;
                Project project6 = null;
                if (project2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("project");
                    project3 = null;
                } else {
                    project3 = project2;
                }
                project4 = EditorActivity.this.project;
                if (project4 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("project");
                    project4 = null;
                }
                List take = CollectionsKt.take(project4.getClips(), fi + 1);
                Clip clip = selected;
                String uuid = UUID.randomUUID().toString();
                Intrinsics.checkNotNullExpressionValue(uuid, "toString(...)");
                copy = clip.copy(uuid, clip.uri, clip.isImage, clip.durationMs, clip.startMs, clip.endMs, clip.speed, clip.zoom);
                List plus = CollectionsKt.plus((Collection<? extends Clip>) take, copy);
                project5 = EditorActivity.this.project;
                if (project5 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("project");
                } else {
                    project6 = project5;
                }
                copy2 = project3.copy(project3.id, project3.name, CollectionsKt.plus((Collection) plus, (Iterable) CollectionsKt.drop(project6.getClips(), fi + 1)), project3.captions, project3.captionStyle, project3.captionLang, project3.musicUri, project3.originalVolume, project3.filter, project3.templateId, project3.updatedAt);
                editorActivity2.commit(copy2);
            }
        })));
    }

    public static final void trimPanel$lambda$49(TextView name, Slider slider, float f, boolean z) {
        Intrinsics.checkNotNullParameter(name, "$name");
        Intrinsics.checkNotNullParameter(slider, "<anonymous parameter 0>");
        name.setText("Photo length: " + f + "s");
    }

    public static final String trimPanel$lambda$51$lambda$50(float f) {
        return UiKt.fmtTime((long) f);
    }

    public static final void trimPanel$lambda$52(TextView name, Clip c, RangeSlider sl, float f, boolean z) {
        Intrinsics.checkNotNullParameter(name, "$name");
        Intrinsics.checkNotNullParameter(c, "$c");
        Intrinsics.checkNotNullParameter(sl, "sl");
        name.setText("Keep " + UiKt.fmtTime((long) sl.getValues().get(0).floatValue()) + " to " + UiKt.fmtTime((long) sl.getValues().get(1).floatValue()) + " of " + UiKt.fmtTime(c.getDurationMs()));
    }

    @Override
    public void onCreate(Bundle b) {
        PlayerView playerView;
        super.onCreate(b);
        getWindow().addFlags(128);
        String stringExtra = getIntent().getStringExtra("id");
        TimelineView timelineView = null;
        Project load = stringExtra != null ? Store.INSTANCE.load(this, stringExtra) : null;
        if (load == null) {
            toast("Project not found");
            finish();
            return;
        }
        this.project = load;
        buildUi();
        EditorActivity editorActivity = this;
        PlayerView playerView2 = this.playerView;
        if (playerView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("playerView");
            playerView = null;
        } else {
            playerView = playerView2;
        }
        View findViewById = findViewById(R.id.dummy_image);
        Intrinsics.checkNotNullExpressionValue(findViewById, "findViewById(...)");
        Preview preview = new Preview(editorActivity, playerView, (ImageView) findViewById, new Function1<Long, Unit>() {

            @Override
            public Unit invoke(Long l) {
                invoke(l.longValue());
                return Unit.INSTANCE;
            }

            public final void invoke(long j) {
                EditorActivity.this.onTime(j);
            }
        }, new Function1<Boolean, Unit>() {

            @Override
            public Unit invoke(Boolean bool) {
                invoke(bool.booleanValue());
                return Unit.INSTANCE;
            }

            public final void invoke(boolean z) {
                TextView textView;
                textView = EditorActivity.this.playBtn;
                if (textView == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("playBtn");
                    textView = null;
                }
                textView.setText(z ? "❚❚" : "▶");
            }
        });
        this.preview = preview;
        Project project = this.project;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        preview.setProject(project);
        refreshAll();
        loadThumbs();
        Project project2 = this.project;
        if (project2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project2 = null;
        }
        if (project2.getMusicUri() != null) {
            analyseBeats();
        }
        Project project3 = this.project;
        if (project3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project3 = null;
        }
        Clip clip = (Clip) CollectionsKt.firstOrNull((List) project3.getClips());
        this.selectedId = clip != null ? clip.getId() : null;
        TimelineView timelineView2 = this.timeline;
        if (timelineView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("timeline");
        } else {
            timelineView = timelineView2;
        }
        timelineView.setSelectedId(this.selectedId);
        if (getIntent().getBooleanExtra(EXTRA_CAPTIONS, false)) {
            this.main.post(new Runnable() {
                @Override
                public final void run() {
                    EditorActivity.onCreate$lambda$4(EditorActivity.this);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.templateMusic != null) {
            try { this.templateMusic.stop(); this.templateMusic.release(); } catch (Exception ignored) {}
            this.templateMusic = null;
        }
        if (this.templateView != null) {
            this.templateView.stop();
        }
        Preview preview = this.preview;
        if (preview == null) {
            Intrinsics.throwUninitializedPropertyAccessException("preview");
            preview = null;
        }
        preview.release();
        this.cancelJob = true;
        ProgressDialog progressDialog = this.progress;
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.templateView != null) {
            this.templateView.pause();
        }
        if (this.templateMusic != null) {
            try { this.templateMusic.pause(); } catch (Exception ignored) {}
        }
        Preview preview = this.preview;
        Project project = null;
        if (preview == null) {
            Intrinsics.throwUninitializedPropertyAccessException("preview");
            preview = null;
        }
        preview.pause();
        Store store = Store.INSTANCE;
        EditorActivity editorActivity = this;
        Project project2 = this.project;
        if (project2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
        } else {
            project = project2;
        }
        store.save(editorActivity, project);
    }
}
