package com.mukhda.editor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.concurrent.ThreadsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

public final class MainActivity extends AppCompatActivity {
    public FrameLayout content;
    public Intent forcedSong;
    public final ActivityResultLauncher<Intent> musicLib;
    public List<? extends TextView> navItems;
    public List<Clip> pendingClips;
    public String pendingLang;
    public Template pendingTemplate;
    public TemplateSpec pendingSpec;
    public String specQuery = "";
    public final ActivityResultLauncher<String[]> pickLong;
    public final ActivityResultLauncher<String[]> pickMedia;
    public boolean started;
    public int tab;
    public String category = "For you";
    public final Handler main = new Handler(Looper.getMainLooper());
    public String pendingMode = "project";

    public MainActivity() {
        ActivityResultLauncher<String[]> registerForActivityResult = registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), new ActivityResultCallback() {
            @Override
            public final void onActivityResult(Object obj) {
                MainActivity.pickMedia$lambda$0(MainActivity.this, (List) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult, "registerForActivityResult(...)");
        this.pickMedia = registerForActivityResult;
        ActivityResultLauncher<String[]> registerForActivityResult2 = registerForActivityResult(new ActivityResultContracts.OpenDocument(), new ActivityResultCallback() {
            @Override
            public final void onActivityResult(Object obj) {
                MainActivity.pickLong$lambda$1(MainActivity.this, (Uri) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult2, "registerForActivityResult(...)");
        this.pickLong = registerForActivityResult2;
        ActivityResultLauncher<Intent> registerForActivityResult3 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() {
            @Override
            public final void onActivityResult(Object obj) {
                MainActivity.musicLib$lambda$67(MainActivity.this, (ActivityResult) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult3, "registerForActivityResult(...)");
        this.musicLib = registerForActivityResult3;
    }

    public final View buildNav() {
        MainActivity mainActivity = this;
        LinearLayout linearLayout = new LinearLayout(mainActivity);
        linearLayout.setBackgroundColor(C.INSTANCE.getCard());
        linearLayout.setGravity(17);
        linearLayout.setPadding(0, UiKt.dp(mainActivity, 6), 0, UiKt.dp(mainActivity, 10));
        List<String> listOf = CollectionsKt.listOf(new String[]{"Home", "Templates", "", "Music", "Me"});
        ArrayList arrayList = new ArrayList();
        final int i = 0;
        for (String str : listOf) {
            int i2 = i + 1;
            if (i == 2) {
                TextView textView = new TextView(mainActivity);
                textView.setText("+");
                textView.setTextSize(30.0f);
                textView.setGravity(17);
                textView.setTextColor(Color.parseColor("#1A1005"));
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                textView.setBackground(UiKt.gradient$default(C.INSTANCE.getSaffron(), C.INSTANCE.getPink(), UiKt.dp(mainActivity, 16), null, 8, null));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        MainActivity.buildNav$lambda$10$lambda$9(MainActivity.this, view);
                    }
                });
                LinearLayout.LayoutParams lp$default = UiKt.lp$default(UiKt.dp(mainActivity, 58), UiKt.dp(mainActivity, 50), 0.0f, 4, null);
                lp$default.setMargins(UiKt.dp(mainActivity, 8), UiKt.dp(mainActivity, 0), UiKt.dp(mainActivity, 8), UiKt.dp(mainActivity, 0));
                Unit unit = Unit.INSTANCE;
                linearLayout.addView(textView, lp$default);
                arrayList.add(new TextView(mainActivity));
            } else {
                TextView label = UiKt.label(mainActivity, str, 12.0f, C.INSTANCE.getDim(), true, 17);
                label.setPadding(0, UiKt.dp(mainActivity, 12), 0, UiKt.dp(mainActivity, 8));
                label.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        MainActivity.buildNav$lambda$13$lambda$12(MainActivity.this, i, view);
                    }
                });
                arrayList.add(label);
                linearLayout.addView(label, UiKt.lp(0, UiKt.getWRAP(), 1.0f));
            }
            // i = i2;
        }
        this.navItems = arrayList;
        return linearLayout;
    }

    public static final void buildNav$lambda$10$lambda$9(MainActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.pendingMode = "project";
        this$0.pickMedia.launch(new String[]{"video/*", "image/*"});
    }

    public static final void buildNav$lambda$13$lambda$12(MainActivity this$0, int i, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.show(i);
    }

    public final void buildTemplateProject(List<Clip> clips, Intent song) {
        String stringExtra;
        Template template = this.pendingTemplate;
        if (template == null) {
            template = (Template) CollectionsKt.first((List) Templates.INSTANCE.getAll());
        }
        Track byFile = (song == null || (stringExtra = song.getStringExtra(MusicActivity.EXTRA_TRACK)) == null) ? null : Music.INSTANCE.byFile(stringExtra);
        String stringExtra2 = song != null ? song.getStringExtra(MusicActivity.EXTRA_URI) : null;
        if (byFile != null && !Intrinsics.areEqual(byFile.getFile(), template.getMusic())) {
            template = Templates.INSTANCE.withTrack(template, byFile);
        }
        MainActivity mainActivity = this;
        Project build = Templates.INSTANCE.build(mainActivity, template, clips);
        if (stringExtra2 != null) {
            build = build.copy(build.id, build.name, build.clips, build.captions, build.captionStyle, build.captionLang, stringExtra2, build.originalVolume, build.filter, build.templateId, build.updatedAt);
        }
        Store.INSTANCE.save(mainActivity, build);
        openEditor(build, false);
    }

    public final void createFrom(List<? extends Uri> uris) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Preparing…");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(() -> new MainActivity$createFrom$1(this, uris, progressDialog, this.pendingMode, this.pendingTemplate).invoke()).start();
    }

    public final void downloadModel(Lang l) {
        Object systemService = getSystemService("connectivity");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.net.ConnectivityManager");
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) systemService).getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            Toast.makeText(this, "Connect to the internet first", 1).show();
            return;
        }
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading " + l.getLabel());
        progressDialog.setProgressStyle(1);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(() -> new MainActivity$downloadModel$1(this, l, progressDialog).invoke()).start();
    }

    public final void home(LinearLayout col) {
        MainActivity mainActivity = this;
        String userName = Store.INSTANCE.userName(mainActivity);
        col.addView(UiKt.label$default(mainActivity, userName.length() == 0 ? "નમસ્તે 👋" : "નમસ્તે, " + userName, 14.0f, C.INSTANCE.getDim(), false, 0, 24, null));
        LinearLayout linearLayout = new LinearLayout(mainActivity);
        linearLayout.setGravity(16);
        ImageView imageView = new ImageView(mainActivity);
        try {
            InputStream open = getAssets().open("logo.png");
            Bitmap decodeStream = BitmapFactory.decodeStream(open);
            CloseableKt.closeFinally(open, null);
            imageView.setImageBitmap(decodeStream);
            linearLayout.addView(imageView, UiKt.lp$default(UiKt.dp(mainActivity, 30), UiKt.dp(mainActivity, 32), 0.0f, 4, null));
            TextView textView = new TextView(mainActivity);
            textView.setText("Mukhda");
            textView.setTextSize(28.0f);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTextColor(C.INSTANCE.getSaffron());
            textView.setPadding(UiKt.dp(mainActivity, 8), 0, 0, 0);
            linearLayout.addView(textView);
            LinearLayout.LayoutParams lp$default = UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null);
            lp$default.topMargin = UiKt.dp(mainActivity, 4);
            Unit unit = Unit.INSTANCE;
            col.addView(linearLayout, lp$default);
            FrameLayout frameLayout = new FrameLayout(mainActivity);
            frameLayout.setBackground(UiKt.gradient(C.INSTANCE.getSaffron(), Color.parseColor("#C03AA8"), UiKt.dp(mainActivity, 22), GradientDrawable.Orientation.TL_BR));
            frameLayout.setPadding(UiKt.dp(mainActivity, 20), UiKt.dp(mainActivity, 18), UiKt.dp(mainActivity, 20), UiKt.dp(mainActivity, 18));
            LinearLayout linearLayout2 = new LinearLayout(mainActivity);
            linearLayout2.setOrientation(1);
            int i = 0;
            linearLayout2.addView(UiKt.label$default(mainActivity, "Photo to Video", 22.0f, -1, true, 0, 16, null));
            TextView label$default = UiKt.label$default(mainActivity, "Pick photos. Video with music in 2 taps", 12.0f, -1, false, 0, 24, null);
            label$default.setPadding(0, UiKt.dp(mainActivity, 4), UiKt.dp(mainActivity, 60), 0);
            linearLayout2.addView(label$default);
            frameLayout.addView(linearLayout2);
            TextView textView2 = new TextView(mainActivity);
            textView2.setText("+");
            textView2.setTextSize(26.0f);
            textView2.setGravity(17);
            textView2.setTextColor(C.INSTANCE.getOrange());
            textView2.setBackground(UiKt.solid$default(-1, UiKt.dp(mainActivity, 24), 0, 0, 12, null));
            frameLayout.addView(textView2, new FrameLayout.LayoutParams(UiKt.dp(mainActivity, 48), UiKt.dp(mainActivity, 48), 8388629));
            frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    MainActivity.home$lambda$24(MainActivity.this, view);
                }
            });
            LinearLayout.LayoutParams lp$default2 = UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null);
            int i2 = 14;
            lp$default2.topMargin = UiKt.dp(mainActivity, 14);
            Unit unit2 = Unit.INSTANCE;
            col.addView(frameLayout, lp$default2);
            LinearLayout linearLayout3 = new LinearLayout(mainActivity);
            for (final String str : TemplateSpec.CATEGORIES) {
                boolean areEqual = Intrinsics.areEqual(str, this.category);
                TextView textView3 = new TextView(mainActivity);
                textView3.setText(str);
                textView3.setTextSize(13.0f);
                textView3.setTextColor(areEqual ? -1 : C.INSTANCE.getDim());
                textView3.setPadding(UiKt.dp(mainActivity, i2), UiKt.dp(mainActivity, 7), UiKt.dp(mainActivity, i2), UiKt.dp(mainActivity, 7));
                textView3.setBackground(areEqual ? UiKt.gradient$default(C.INSTANCE.getSaffron(), C.INSTANCE.getPink(), UiKt.dp(mainActivity, 18), null, 8, null) : UiKt.solid(C.INSTANCE.getCard(), UiKt.dp(mainActivity, 18), C.INSTANCE.getStroke(), UiKt.dp(mainActivity, 1)));
                textView3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        MainActivity.home$lambda$27$lambda$26(MainActivity.this, str, view);
                    }
                });
                LinearLayout.LayoutParams lp$default3 = UiKt.lp$default(UiKt.getWRAP(), UiKt.getWRAP(), 0.0f, 4, null);
                lp$default3.rightMargin = UiKt.dp(mainActivity, 8);
                Unit unit3 = Unit.INSTANCE;
                linearLayout3.addView(textView3, lp$default3);
                i2 = 14;
            }
            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(mainActivity);
            horizontalScrollView.setHorizontalScrollBarEnabled(false);
            horizontalScrollView.addView(linearLayout3);
            LinearLayout.LayoutParams lp$default4 = UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null);
            lp$default4.topMargin = UiKt.dp(mainActivity, 16);
            Unit unit4 = Unit.INSTANCE;
            col.addView(horizontalScrollView, lp$default4);
            int i3 = 2;
            Iterator it = CollectionsKt.chunked(specList(this.category), 2).iterator();
            while (true) {
                float f = 1.0f;
                if (!it.hasNext()) {
                    break;
                }
                List list = (List) it.next();
                LinearLayout linearLayout4 = new LinearLayout(mainActivity);
                int i4 = i;
                while (i4 < i3) {
                    TemplateSpec template = (TemplateSpec) CollectionsKt.getOrNull(list, i4);
                    View templateCard = template != null ? specCard(template) : new View(mainActivity);
                    LinearLayout.LayoutParams lp = UiKt.lp(i, UiKt.getWRAP(), f);
                    lp.setMargins(UiKt.dp(mainActivity, 5), UiKt.dp(mainActivity, 6), UiKt.dp(mainActivity, 5), UiKt.dp(mainActivity, 6));
                    Unit unit5 = Unit.INSTANCE;
                    linearLayout4.addView(templateCard, lp);
                    i4++;
                    f = 1.0f;
                    i3 = 2;
                    i = 0;
                }
                col.addView(linearLayout4, UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
                i3 = 2;
                i = 0;
            }
            LinearLayout linearLayout5 = new LinearLayout(mainActivity);
            View tile = tile("Auto Captions", "ગુજરાતી · हिंदी · Hinglish", C.INSTANCE.getSaffron(), new Function0<Unit>() {

                @Override
                public Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                public final void invoke2() {
                    ActivityResultLauncher activityResultLauncher;
                    MainActivity.this.pendingMode = EditorActivity.EXTRA_CAPTIONS;
                    activityResultLauncher = MainActivity.this.pickMedia;
                    activityResultLauncher.launch(new String[]{"video/*"});
                }
            });
            LinearLayout.LayoutParams lp2 = UiKt.lp(0, UiKt.getWRAP(), 1.0f);
            lp2.rightMargin = UiKt.dp(mainActivity, 8);
            Unit unit6 = Unit.INSTANCE;
            linearLayout5.addView(tile, lp2);
            View tile2 = tile("Status Ready", "WhatsApp 30s auto-split", C.INSTANCE.getTeal(), new Function0<Unit>() {

                @Override
                public Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                public final void invoke2() {
                    MainActivity.this.show(5);
                }
            });
            LinearLayout.LayoutParams lp3 = UiKt.lp(0, UiKt.getWRAP(), 1.0f);
            lp3.leftMargin = UiKt.dp(mainActivity, 8);
            Unit unit7 = Unit.INSTANCE;
            linearLayout5.addView(tile2, lp3);
            LinearLayout.LayoutParams lp$default5 = UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null);
            lp$default5.topMargin = UiKt.dp(mainActivity, 18);
            Unit unit8 = Unit.INSTANCE;
            col.addView(linearLayout5, lp$default5);
            float f2 = 1.0f;
            TextView pillButton$default = UiKt.pillButton$default(mainActivity, "+  Blank project (editor)", C.INSTANCE.getCard2(), C.INSTANCE.getCard2(), 0, new Function0<Unit>() {

                @Override
                public Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                public final void invoke2() {
                    ActivityResultLauncher activityResultLauncher;
                    MainActivity.this.pendingMode = "project";
                    activityResultLauncher = MainActivity.this.pickMedia;
                    activityResultLauncher.launch(new String[]{"video/*", "image/*"});
                }
            }, 8, null);
            LinearLayout.LayoutParams lp$default6 = UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null);
            lp$default6.topMargin = UiKt.dp(mainActivity, 12);
            Unit unit9 = Unit.INSTANCE;
            col.addView(pillButton$default, lp$default6);
            TextView label$default2 = UiKt.label$default(mainActivity, "My Projects", 18.0f, C.INSTANCE.getText(), true, 0, 16, null);
            label$default2.setPadding(0, UiKt.dp(mainActivity, 24), 0, UiKt.dp(mainActivity, 10));
            col.addView(label$default2);
            List<Project> list2 = Store.INSTANCE.list(mainActivity);
            if (list2.isEmpty()) {
                col.addView(UiKt.label$default(mainActivity, "Your projects appear here.", 14.0f, C.INSTANCE.getDim(), false, 0, 24, null));
                return;
            }
            int i5 = 2;
            for (List list3 : CollectionsKt.chunked(list2, 2)) {
                LinearLayout linearLayout6 = new LinearLayout(mainActivity);
                int i6 = 0;
                while (i6 < i5) {
                    Project project = (Project) CollectionsKt.getOrNull(list3, i6);
                    View projectCard = project != null ? projectCard(project) : new View(mainActivity);
                    LinearLayout.LayoutParams lp4 = UiKt.lp(0, UiKt.getWRAP(), f2);
                    lp4.setMargins(UiKt.dp(mainActivity, 6), UiKt.dp(mainActivity, 6), UiKt.dp(mainActivity, 6), UiKt.dp(mainActivity, 6));
                    Unit unit10 = Unit.INSTANCE;
                    linearLayout6.addView(projectCard, lp4);
                    i6++;
                    f2 = 1.0f;
                    i5 = 2;
                }
                col.addView(linearLayout6, UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
                f2 = 1.0f;
                i5 = 2;
            }
        } catch (Exception ignored) {
        }
    }

    public static final void home$lambda$24(MainActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.pendingMode = "template";
        this$0.pendingTemplate = (Template) CollectionsKt.first((List) Templates.INSTANCE.inCategory("Trending"));
        this$0.pickMedia.launch(new String[]{"video/*", "image/*"});
    }

    public static final void home$lambda$27$lambda$26(MainActivity this$0, String c, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(c, "$c");
        this$0.category = c;
        this$0.show(0);
    }

    public final void me(LinearLayout col) {
        String str;
        MainActivity mainActivity = this;
        col.addView(UiKt.label$default(mainActivity, "Me", 26.0f, C.INSTANCE.getText(), true, 0, 16, null));
        String userName = Store.INSTANCE.userName(mainActivity);
        if (userName.length() == 0) {
            userName = "tap to add";
        }
        TextView label$default = UiKt.label$default(mainActivity, "Your name:  " + ((Object) userName), 15.0f, C.INSTANCE.getText(), false, 0, 24, null);
        int i = 16;
        label$default.setPadding(0, UiKt.dp(mainActivity, 20), 0, UiKt.dp(mainActivity, 16));
        label$default.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MainActivity.me$lambda$75$lambda$74(MainActivity.this, view);
            }
        });
        col.addView(label$default);
        Switch r2 = new Switch(mainActivity);
        r2.setText("Lite Mode (smooth editing and export on 4 GB phones)");
        r2.setTextColor(C.INSTANCE.getText());
        r2.setChecked(Store.INSTANCE.lite(mainActivity));
        r2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                MainActivity.me$lambda$77$lambda$76(MainActivity.this, compoundButton, z);
            }
        });
        col.addView(r2);
        TextView label$default2 = UiKt.label$default(mainActivity, "Caption language packs", 16.0f, C.INSTANCE.getText(), true, 0, 16, null);
        label$default2.setPadding(0, UiKt.dp(mainActivity, 24), 0, UiKt.dp(mainActivity, 6));
        col.addView(label$default2);
        col.addView(UiKt.label$default(mainActivity, "Downloaded once, then captions work offline.", 12.0f, C.INSTANCE.getDim(), false, 0, 24, null));
        HashSet hashSet = new HashSet();
        for (final Lang lang : Langs.INSTANCE.getAll()) {
            if (hashSet.add(lang.getModel())) {
                final boolean isReady = ModelStore.INSTANCE.isReady(mainActivity, lang.getModel());
                List<Lang> all = Langs.INSTANCE.getAll();
                ArrayList arrayList = new ArrayList();
                for (Object obj : all) {
                    if (Intrinsics.areEqual(((Lang) obj).getModel(), lang.getModel())) {
                        arrayList.add(obj);
                    }
                }
                String joinToString$default = CollectionsKt.joinToString(arrayList, " / ", "", "", -1, "...", new Function1<Lang, CharSequence>() {
                    @Override
                    public final CharSequence invoke(Lang it) {
                        Intrinsics.checkNotNullParameter(it, "it");
                        return it.getLabel();
                    }
                });
                LinearLayout linearLayout = new LinearLayout(mainActivity);
                linearLayout.setGravity(i);
                linearLayout.setPadding(0, UiKt.dp(mainActivity, 10), 0, UiKt.dp(mainActivity, 10));
                linearLayout.addView(UiKt.label$default(mainActivity, joinToString$default + "  ·  " + lang.getSizeMb() + " MB", 14.0f, C.INSTANCE.getText(), false, 0, 24, null), UiKt.lp(0, UiKt.getWRAP(), 1.0f));
                linearLayout.addView(UiKt.chip(mainActivity, isReady ? "Remove" : "Download", false, new Function0<Unit>() {

                    @Override
                    public Unit invoke() {
                        invoke2();
                        return Unit.INSTANCE;
                    }

                    public final void invoke2() {
                        if (!isReady) {
                            MainActivity.this.downloadModel(lang);
                        } else {
                            ModelStore.INSTANCE.delete(MainActivity.this, lang.getModel());
                            MainActivity.this.show(4);
                        }
                    }
                }));
                col.addView(linearLayout);
                i = 16;
            }
        }
        TextView label$default3 = UiKt.label$default(mainActivity, "About", 16.0f, C.INSTANCE.getText(), true, 0, 16, null);
        label$default3.setPadding(0, UiKt.dp(mainActivity, 24), 0, UiKt.dp(mainActivity, 6));
        col.addView(label$default3);
        try {
            str = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Throwable unused) {
            str = "";
        }
        col.addView(UiKt.label$default(mainActivity, "Mukhda " + str + "\nFree video editor made in Gujarat. Everything is edited and exported on your phone.", 13.0f, C.INSTANCE.getDim(), false, 0, 24, null));
    }

    public static final void me$lambda$75$lambda$74(final MainActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        MainActivity mainActivity = this$0;
        final EditText editText = new EditText(mainActivity);
        editText.setText(Store.INSTANCE.userName(mainActivity));
        editText.setSingleLine();
        new AlertDialog.Builder(mainActivity).setTitle("Your name").setView(editText).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.me$lambda$75$lambda$74$lambda$73(mainActivity, editText, dialogInterface, i);
            }
        }).setNegativeButton("Cancel", (DialogInterface.OnClickListener) null).show();
    }

    public static final void me$lambda$75$lambda$74$lambda$73(MainActivity this$0, EditText et, DialogInterface dialogInterface, int i) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(et, "$et");
        Store.INSTANCE.setUserName(this$0, et.getText().toString());
        this$0.show(4);
    }

    public static final void me$lambda$77$lambda$76(MainActivity this$0, CompoundButton compoundButton, boolean z) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Store.INSTANCE.setLite(this$0, z);
    }

    public final void musicFirst(String lang) {
        this.musicLib.launch(new Intent(this, (Class<?>) MusicActivity.class).putExtra(MusicActivity.EXTRA_TITLE, "Pick a song").putExtra(MusicActivity.EXTRA_LANG, lang));
    }

    public static final void musicLib$lambda$67(MainActivity this$0, ActivityResult activityResult) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intent data = activityResult.getData();
        List<Clip> list = this$0.pendingClips;
        if (list != null) {
            Intrinsics.checkNotNull(list);
            this$0.pendingClips = null;
            if (activityResult.getResultCode() != -1) {
                data = null;
            }
            this$0.buildTemplateProject(list, data);
            return;
        }
        if (activityResult.getResultCode() != -1 || data == null) {
            return;
        }
        this$0.forcedSong = data;
        this$0.pendingMode = "template";
        this$0.pendingTemplate = (Template) CollectionsKt.first((List) Templates.INSTANCE.inCategory("Trending"));
        this$0.pickMedia.launch(new String[]{"video/*", "image/*"});
    }

    public final void musicTab(LinearLayout col) {
        MainActivity mainActivity = this;
        col.addView(UiKt.label$default(mainActivity, "Music", 26.0f, C.INSTANCE.getText(), true, 0, 16, null));
        TextView label$default = UiKt.label$default(mainActivity, "Free songs for your videos. Pick one, then add photos. Or use a song from your phone.", 13.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        label$default.setPadding(0, UiKt.dp(mainActivity, 4), 0, UiKt.dp(mainActivity, 14));
        col.addView(label$default);
        col.addView(UiKt.pillButton$default(mainActivity, "📁  Use a song from my phone", C.INSTANCE.getCard2(), C.INSTANCE.getCard2(), 0, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                MainActivity.this.musicFirst(null);
            }
        }, 8, null), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        for (final String str : Music.INSTANCE.getLanguages()) {
            int size = Music.INSTANCE.inLanguage(str).size();
            LinearLayout linearLayout = new LinearLayout(mainActivity);
            linearLayout.setGravity(16);
            linearLayout.setPadding(UiKt.dp(mainActivity, 18), UiKt.dp(mainActivity, 16), UiKt.dp(mainActivity, 18), UiKt.dp(mainActivity, 16));
            linearLayout.setBackground(UiKt.solid(C.INSTANCE.getCard(), UiKt.dp(mainActivity, 16), C.INSTANCE.getStroke(), UiKt.dp(mainActivity, 1)));
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    MainActivity.musicTab$lambda$62$lambda$61(MainActivity.this, str, view);
                }
            });
            LinearLayout linearLayout2 = new LinearLayout(mainActivity);
            linearLayout2.setOrientation(1);
            linearLayout2.addView(UiKt.label$default(mainActivity, str, 17.0f, C.INSTANCE.getText(), true, 0, 16, null));
            TextView label$default2 = UiKt.label$default(mainActivity, size + " songs", 12.0f, C.INSTANCE.getDim(), false, 0, 24, null);
            label$default2.setPadding(0, UiKt.dp(mainActivity, 2), 0, 0);
            linearLayout2.addView(label$default2);
            linearLayout.addView(linearLayout2, UiKt.lp(0, UiKt.getWRAP(), 1.0f));
            linearLayout.addView(UiKt.label$default(mainActivity, "▶", 18.0f, C.INSTANCE.getSaffron(), true, 0, 16, null));
            LinearLayout.LayoutParams lp$default = UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null);
            lp$default.topMargin = UiKt.dp(mainActivity, 10);
            Unit unit = Unit.INSTANCE;
            col.addView(linearLayout, lp$default);
        }
        TextView label$default3 = UiKt.label$default(mainActivity, "These are original tracks made for Mukhda, free to use. Real film songs are not included because of copyright. Use your own song from your phone for those.", 11.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        label$default3.setPadding(0, UiKt.dp(mainActivity, 16), 0, 0);
        col.addView(label$default3);
    }

    public static final void musicTab$lambda$62$lambda$61(MainActivity this$0, String l, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(l, "$l");
        this$0.musicFirst(l);
    }

    public final void openEditor(Project p, boolean captions) {
        startActivity(new Intent(this, (Class<?>) EditorActivity.class).putExtra("id", p.getId()).putExtra(EditorActivity.EXTRA_CAPTIONS, captions));
    }

    public static final void pickLong$lambda$1(MainActivity this$0, Uri uri) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (uri == null) {
            return;
        }
        MainActivity mainActivity = this$0;
        Media.INSTANCE.persist(mainActivity, uri);
        this$0.startActivity(new Intent(mainActivity, (Class<?>) StatusActivity.class).setData(uri));
    }

    public static final void pickMedia$lambda$0(MainActivity this$0, List list) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        List list2 = list;
        if (list2 == null || list2.isEmpty()) {
            return;
        }
        Intrinsics.checkNotNull(list);
        if (this$0.pendingSpec != null) {
            int need = this$0.pendingSpec.photoCount;
            ArrayList adjusted = new ArrayList();
            for (int i = 0; i < need; i++) {
                adjusted.add(list.get(i % list.size()));
            }
            if (list.size() != need) {
                Toast.makeText(this$0, this$0.pendingSpec.title + " needs " + need + " photos - filled in for you", 1).show();
            }
            list = adjusted;
        }
        this$0.createFrom(list);
    }

    public final View projectCard(final Project p) {
        MainActivity mainActivity = this;
        LinearLayout linearLayout = new LinearLayout(mainActivity);
        linearLayout.setOrientation(1);
        ImageView imageView = new ImageView(mainActivity);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setBackground(UiKt.gradient$default(C.INSTANCE.getOrange(), C.INSTANCE.getPink(), UiKt.dp(mainActivity, 14), null, 8, null));
        imageView.setClipToOutline(true);
        FrameLayout frameLayout = new FrameLayout(mainActivity);
        frameLayout.addView(imageView, new FrameLayout.LayoutParams(UiKt.getMATCH(), UiKt.dp(mainActivity, 140)));
        TextView label$default = UiKt.label$default(mainActivity, UiKt.fmtTime(p.getTotalMs()), 11.0f, -1, true, 0, 16, null);
        label$default.setPadding(UiKt.dp(mainActivity, 8), UiKt.dp(mainActivity, 3), UiKt.dp(mainActivity, 8), UiKt.dp(mainActivity, 3));
        label$default.setBackground(UiKt.solid$default(Color.argb(170, 0, 0, 0), UiKt.dp(mainActivity, 10), 0, 0, 12, null));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(UiKt.getWRAP(), UiKt.getWRAP(), 8388693);
        layoutParams.setMargins(0, 0, UiKt.dp(mainActivity, 8), UiKt.dp(mainActivity, 8));
        Unit unit = Unit.INSTANCE;
        frameLayout.addView(label$default, layoutParams);
        Clip clip = (Clip) CollectionsKt.firstOrNull((List) p.getClips());
        if (clip != null) {
            new Thread(() -> new MainActivity$projectCard$3$1(this, clip, imageView).invoke()).start();
        }
        linearLayout.addView(frameLayout);
        TextView label$default2 = UiKt.label$default(mainActivity, p.getName(), 13.0f, C.INSTANCE.getText(), true, 0, 16, null);
        label$default2.setPadding(UiKt.dp(mainActivity, 2), UiKt.dp(mainActivity, 6), 0, 0);
        label$default2.setMaxLines(1);
        linearLayout.addView(label$default2);
        String format = new SimpleDateFormat("d MMM, h:mm a", Locale.getDefault()).format(new Date(p.getUpdatedAt()));
        Intrinsics.checkNotNullExpressionValue(format, "format(...)");
        TextView label$default3 = UiKt.label$default(mainActivity, format, 11.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        label$default3.setPadding(UiKt.dp(mainActivity, 2), UiKt.dp(mainActivity, 2), 0, 0);
        linearLayout.addView(label$default3);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MainActivity.projectCard$lambda$55(MainActivity.this, p, view);
            }
        });
        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public final boolean onLongClick(View view) {
                boolean projectCard$lambda$57;
                projectCard$lambda$57 = MainActivity.projectCard$lambda$57(MainActivity.this, p, view);
                return projectCard$lambda$57;
            }
        });
        return linearLayout;
    }

    public static final void projectCard$lambda$55(MainActivity this$0, Project p, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(p, "$p");
        this$0.startActivity(new Intent(this$0, (Class<?>) EditorActivity.class).putExtra("id", p.getId()));
    }

    public static final boolean projectCard$lambda$57(final MainActivity this$0, final Project p, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(p, "$p");
        new AlertDialog.Builder(this$0).setMessage("Delete \"" + p.getName() + "\"? Exported videos stay in your gallery.").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.projectCard$lambda$57$lambda$56(this$0, p, dialogInterface, i);
            }
        }).setNegativeButton("Cancel", (DialogInterface.OnClickListener) null).show();
        return true;
    }

    public static final void projectCard$lambda$57$lambda$56(MainActivity this$0, Project p, DialogInterface dialogInterface, int i) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(p, "$p");
        Store.INSTANCE.delete(this$0, p.getId());
        this$0.show(0);
    }

    public final void show(int i) {
        this.tab = i;
        List<? extends TextView> list = this.navItems;
        FrameLayout frameLayout = null;
        if (list == null) {
            Intrinsics.throwUninitializedPropertyAccessException("navItems");
            list = null;
        }
        int i2 = 0;
        for (TextView textView : list) {
            int i3 = i2 + 1;
            if (i2 != 2) {
                textView.setTextColor(i2 == i ? C.INSTANCE.getSaffron() : C.INSTANCE.getDim());
            }
            i2 = i3;
        }
        FrameLayout frameLayout2 = this.content;
        if (frameLayout2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("content");
            frameLayout2 = null;
        }
        frameLayout2.removeAllViews();
        MainActivity mainActivity = this;
        ScrollView scrollView = new ScrollView(mainActivity);
        LinearLayout linearLayout = new LinearLayout(mainActivity);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(UiKt.dp(mainActivity, 18), UiKt.dp(mainActivity, 22), UiKt.dp(mainActivity, 18), UiKt.dp(mainActivity, 24));
        scrollView.addView(linearLayout);
        FrameLayout frameLayout3 = this.content;
        if (frameLayout3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("content");
        } else {
            frameLayout = frameLayout3;
        }
        frameLayout.addView(scrollView);
        if (i == 0) {
            home(linearLayout);
            return;
        }
        if (i == 1) {
            templates(linearLayout);
            return;
        }
        if (i == 3) {
            musicTab(linearLayout);
        } else if (i == 4) {
            me(linearLayout);
        } else {
            if (i != 5) {
                return;
            }
            status(linearLayout);
        }
    }

    public final void showError(String title, String trace) {
        MainActivity mainActivity = this;
        LinearLayout linearLayout = new LinearLayout(mainActivity);
        linearLayout.setOrientation(1);
        linearLayout.setBackgroundColor(Color.parseColor("#0F0F14"));
        linearLayout.setPadding(40, 80, 40, 40);
        TextView textView = new TextView(mainActivity);
        textView.setText(title);
        textView.setTextSize(18.0f);
        textView.setTextColor(-1);
        linearLayout.addView(textView);
        Button button = new Button(mainActivity);
        button.setText("Phir se try karo");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MainActivity.showError$lambda$6$lambda$5(MainActivity.this, view);
            }
        });
        linearLayout.addView(button);
        ScrollView scrollView = new ScrollView(mainActivity);
        TextView textView2 = new TextView(mainActivity);
        textView2.setText(StringsKt.take(trace, PathInterpolatorCompat.MAX_NUM_POINTS));
        textView2.setTextSize(11.0f);
        textView2.setTextColor(Color.parseColor("#FF9F2E"));
        scrollView.addView(textView2);
        linearLayout.addView(scrollView);
        setContentView(linearLayout);
    }

    public static final void showError$lambda$6$lambda$5(MainActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        CrashLog.INSTANCE.clear(this$0);
        this$0.recreate();
    }

    public final void status(LinearLayout col) {
        MainActivity mainActivity = this;
        col.addView(UiKt.label$default(mainActivity, "Status Ready", 26.0f, C.INSTANCE.getText(), true, 0, 16, null));
        TextView label$default = UiKt.label$default(mainActivity, "Long video?", 20.0f, C.INSTANCE.getText(), true, 0, 16, null);
        label$default.setPadding(0, UiKt.dp(mainActivity, 16), 0, 0);
        col.addView(label$default);
        col.addView(UiKt.label$default(mainActivity, "WhatsApp Status ready in one tap.", 20.0f, C.INSTANCE.getGreen(), true, 0, 16, null));
        TextView label$default2 = UiKt.label$default(mainActivity, "Mukhda splits your video into 30 second parts at the pauses in speech, never in the middle of a sentence. Then it saves the parts and opens WhatsApp.", 14.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        label$default2.setPadding(0, UiKt.dp(mainActivity, 12), 0, UiKt.dp(mainActivity, 20));
        col.addView(label$default2);
        col.addView(UiKt.pillButton$default(mainActivity, "Choose a long video", C.INSTANCE.getGreen(), C.INSTANCE.getTeal(), 0, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                ActivityResultLauncher activityResultLauncher;
                activityResultLauncher = MainActivity.this.pickLong;
                activityResultLauncher.launch(new String[]{"video/*"});
            }
        }, 8, null), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
    }

    public final View templateCard(final Template t) {
        String str;
        MainActivity mainActivity = this;
        FrameLayout frameLayout = new FrameLayout(mainActivity);
        frameLayout.setBackground(UiKt.gradient$default(t.getC1(), t.getC2(), UiKt.dp(mainActivity, 18), null, 8, null));
        frameLayout.setClipToOutline(true);
        frameLayout.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        frameLayout.addView(new TemplatePreviewView(mainActivity, t), new FrameLayout.LayoutParams(UiKt.getMATCH(), UiKt.dp(mainActivity, 190)));
        LinearLayout linearLayout = new LinearLayout(mainActivity);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(UiKt.dp(mainActivity, 12), UiKt.dp(mainActivity, 8), UiKt.dp(mainActivity, 12), UiKt.dp(mainActivity, 10));
        linearLayout.setBackgroundColor(Color.argb(150, 0, 0, 0));
        TextView label$default = UiKt.label$default(mainActivity, t.getTitle(), 14.0f, -1, true, 0, 16, null);
        label$default.setMaxLines(1);
        label$default.setEllipsize(TextUtils.TruncateAt.END);
        linearLayout.addView(label$default);
        Track byFile = Music.INSTANCE.byFile(t.getMusic());
        if (byFile == null || (str = byFile.getTitle()) == null) {
            str = "";
        }
        TextView label$default2 = UiKt.label$default(mainActivity, "♪ " + str, 11.0f, Color.argb(230, 255, 255, 255), false, 0, 24, null);
        label$default2.setMaxLines(1);
        label$default2.setEllipsize(TextUtils.TruncateAt.END);
        label$default2.setPadding(0, UiKt.dp(mainActivity, 2), 0, 0);
        linearLayout.addView(label$default2);
        frameLayout.addView(linearLayout, new FrameLayout.LayoutParams(UiKt.getMATCH(), UiKt.getWRAP(), 80));
        if (t.getTrending()) {
            TextView label$default3 = UiKt.label$default(mainActivity, "HOT", 10.0f, -1, true, 0, 16, null);
            label$default3.setPadding(UiKt.dp(mainActivity, 8), UiKt.dp(mainActivity, 3), UiKt.dp(mainActivity, 8), UiKt.dp(mainActivity, 3));
            label$default3.setBackground(UiKt.solid$default(Color.argb(150, 0, 0, 0), UiKt.dp(mainActivity, 8), 0, 0, 12, null));
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(UiKt.getWRAP(), UiKt.getWRAP(), 8388661);
            layoutParams.setMargins(0, UiKt.dp(mainActivity, 8), UiKt.dp(mainActivity, 8), 0);
            Unit unit = Unit.INSTANCE;
            frameLayout.addView(label$default3, layoutParams);
        }
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MainActivity.templateCard$lambda$44(MainActivity.this, t, view);
            }
        });
        return frameLayout;
    }

    public static final void templateCard$lambda$44(MainActivity this$0, Template t, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(t, "$t");
        this$0.pendingMode = "template";
        this$0.pendingTemplate = t;
        this$0.pickMedia.launch(new String[]{"video/*", "image/*"});
    }

    public final void templates(LinearLayout col) {
        final MainActivity mainActivity = this;
        col.addView(UiKt.label$default(mainActivity, "Templates", 26.0f, C.INSTANCE.getText(), true, 0, 16, null));
        TextView label$default = UiKt.label$default(mainActivity, "Each template has its own effects and song. Just add your photos.", 13.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        label$default.setPadding(0, UiKt.dp(mainActivity, 4), 0, UiKt.dp(mainActivity, 12));
        col.addView(label$default);
        final android.widget.EditText search = new android.widget.EditText(mainActivity);
        search.setHint("Search templates");
        search.setSingleLine(true);
        search.setTextColor(C.INSTANCE.getText());
        search.setHintTextColor(C.INSTANCE.getDim());
        search.setBackground(UiKt.solid(C.INSTANCE.getCard(), UiKt.dp(mainActivity, 14), C.INSTANCE.getStroke(), UiKt.dp(mainActivity, 1)));
        search.setPadding(UiKt.dp(mainActivity, 14), 0, UiKt.dp(mainActivity, 14), 0);
        search.setText(specQuery);
        col.addView(search, UiKt.lp$default(UiKt.getMATCH(), UiKt.dp(mainActivity, 44), 0.0f, 4, null));
        LinearLayout chipRow = new LinearLayout(mainActivity);
        for (final String str : TemplateSpec.CATEGORIES) {
            boolean sel = Intrinsics.areEqual(str, this.category);
            TextView chip = new TextView(mainActivity);
            chip.setText(str);
            chip.setTextSize(13.0f);
            chip.setTextColor(sel ? -1 : C.INSTANCE.getDim());
            chip.setPadding(UiKt.dp(mainActivity, 14), UiKt.dp(mainActivity, 7), UiKt.dp(mainActivity, 14), UiKt.dp(mainActivity, 7));
            chip.setBackground(sel ? UiKt.gradient$default(C.INSTANCE.getSaffron(), C.INSTANCE.getPink(), UiKt.dp(mainActivity, 18), null, 8, null) : UiKt.solid(C.INSTANCE.getCard(), UiKt.dp(mainActivity, 18), C.INSTANCE.getStroke(), UiKt.dp(mainActivity, 1)));
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    mainActivity.category = str;
                    mainActivity.refreshSpecGrid();
                    show(1);
                }
            });
            LinearLayout.LayoutParams clp = UiKt.lp$default(UiKt.getWRAP(), UiKt.getWRAP(), 0.0f, 4, null);
            clp.rightMargin = UiKt.dp(mainActivity, 8);
            chipRow.addView(chip, clp);
        }
        HorizontalScrollView chipScroll = new HorizontalScrollView(mainActivity);
        chipScroll.setHorizontalScrollBarEnabled(false);
        chipScroll.addView(chipRow);
        LinearLayout.LayoutParams cslp = UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null);
        cslp.topMargin = UiKt.dp(mainActivity, 12);
        col.addView(chipScroll, cslp);
        final LinearLayout grid = new LinearLayout(mainActivity);
        grid.setOrientation(1);
        LinearLayout.LayoutParams glp = UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null);
        glp.topMargin = UiKt.dp(mainActivity, 8);
        col.addView(grid, glp);
        fillSpecGrid(grid);
        search.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence c, int a, int b, int d) {}
            @Override
            public void onTextChanged(CharSequence c, int a, int b, int d) {}
            @Override
            public void afterTextChanged(android.text.Editable e) {
                mainActivity.specQuery = e == null ? "" : e.toString();
                mainActivity.fillSpecGrid(grid);
            }
        });
    }

    public final void refreshSpecGrid() {
    }

    public final List<TemplateSpec> specList(String cat) {
        List<TemplateSpec> base = TemplateSpec.inCategory(cat);
        String q = specQuery == null ? "" : specQuery.trim().toLowerCase();
        if (q.length() == 0) return base;
        ArrayList<TemplateSpec> out = new ArrayList<TemplateSpec>();
        for (TemplateSpec t : base) {
            if (t.title.toLowerCase().contains(q) || t.sub.toLowerCase().contains(q)) out.add(t);
        }
        return out;
    }

    public final void fillSpecGrid(LinearLayout grid) {
        MainActivity mainActivity = this;
        grid.removeAllViews();
        for (List list : CollectionsKt.chunked(specList(this.category), 2)) {
            LinearLayout linearLayout = new LinearLayout(mainActivity);
            for (int i = 0; i < 2; i++) {
                TemplateSpec template = (TemplateSpec) CollectionsKt.getOrNull(list, i);
                View templateCard = template != null ? specCard(template) : new View(mainActivity);
                LinearLayout.LayoutParams lp = UiKt.lp(0, UiKt.getWRAP(), 1.0f);
                lp.setMargins(UiKt.dp(mainActivity, 5), UiKt.dp(mainActivity, 6), UiKt.dp(mainActivity, 5), UiKt.dp(mainActivity, 6));
                Unit unit = Unit.INSTANCE;
                linearLayout.addView(templateCard, lp);
            }
            grid.addView(linearLayout, UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        }
    }

    public final Template specToTemplate(TemplateSpec sp) {
        Track tr = Music.INSTANCE.byFile(sp.musicFile);
        return new Template(sp.id, sp.title, sp.sub, tr != null ? tr.getBpm() : sp.bpm, sp.musicFile, 2, sp.durationSec, sp.filter, "reels", sp.textLines.isEmpty() ? sp.title : sp.textLines.get(0), sp.c1, sp.c2, sp.cats, false, sp.beatFx);
    }

    public final View specCard(final TemplateSpec sp) {
        final MainActivity mainActivity = this;
        FrameLayout frameLayout = new FrameLayout(mainActivity);
        frameLayout.setBackground(UiKt.solid(C.INSTANCE.getCard(), UiKt.dp(mainActivity, 18), C.INSTANCE.getStroke(), UiKt.dp(mainActivity, 1)));
        frameLayout.setClipToOutline(true);
        frameLayout.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        TemplateView pv = new TemplateView(mainActivity);
        pv.setTemplate(sp, null);
        pv.play();
        frameLayout.addView(pv, new FrameLayout.LayoutParams(UiKt.getMATCH(), UiKt.dp(mainActivity, 190)));
        LinearLayout linearLayout = new LinearLayout(mainActivity);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(UiKt.dp(mainActivity, 12), UiKt.dp(mainActivity, 8), UiKt.dp(mainActivity, 12), UiKt.dp(mainActivity, 10));
        linearLayout.setBackgroundColor(Color.argb(150, 0, 0, 0));
        TextView label$default = UiKt.label$default(mainActivity, sp.title, 14.0f, -1, true, 0, 16, null);
        label$default.setMaxLines(1);
        label$default.setEllipsize(TextUtils.TruncateAt.END);
        linearLayout.addView(label$default);
        Track byFile = Music.INSTANCE.byFile(sp.musicFile);
        String str = byFile == null ? "" : byFile.getTitle();
        if (str == null) str = "";
        TextView label$default2 = UiKt.label$default(mainActivity, "\u266a " + str, 11.0f, Color.argb(230, 255, 255, 255), false, 0, 24, null);
        label$default2.setMaxLines(1);
        label$default2.setEllipsize(TextUtils.TruncateAt.END);
        label$default2.setPadding(0, UiKt.dp(mainActivity, 2), 0, 0);
        linearLayout.addView(label$default2);
        frameLayout.addView(linearLayout, new FrameLayout.LayoutParams(UiKt.getMATCH(), UiKt.getWRAP(), 80));
        TextView badge = UiKt.label$default(mainActivity, sp.photoCount + " photos", 10.0f, -1, true, 0, 16, null);
        badge.setPadding(UiKt.dp(mainActivity, 8), UiKt.dp(mainActivity, 3), UiKt.dp(mainActivity, 8), UiKt.dp(mainActivity, 3));
        badge.setBackground(UiKt.solid$default(Color.argb(180, 0, 0, 0), UiKt.dp(mainActivity, 8), 0, 0, 12, null));
        FrameLayout.LayoutParams blp = new FrameLayout.LayoutParams(UiKt.getWRAP(), UiKt.getWRAP(), 8388661);
        blp.setMargins(0, UiKt.dp(mainActivity, 8), UiKt.dp(mainActivity, 8), 0);
        frameLayout.addView(badge, blp);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                mainActivity.pendingMode = "template";
                mainActivity.pendingSpec = sp;
                mainActivity.pendingTemplate = mainActivity.specToTemplate(sp);
                mainActivity.pickMedia.launch(new String[]{"image/*"});
            }
        });
        return frameLayout;
    }

    public final View tile(String title, String sub, int accent, final Function0<Unit> onClick) {
        MainActivity mainActivity = this;
        LinearLayout linearLayout = new LinearLayout(mainActivity);
        linearLayout.setOrientation(1);
        linearLayout.setBackground(UiKt.solid(C.INSTANCE.getCard(), UiKt.dp(mainActivity, 18), C.INSTANCE.getStroke(), UiKt.dp(mainActivity, 1)));
        linearLayout.setPadding(UiKt.dp(mainActivity, 16), UiKt.dp(mainActivity, 16), UiKt.dp(mainActivity, 16), UiKt.dp(mainActivity, 16));
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MainActivity.tile$lambda$46$lambda$45(onClick, view);
            }
        });
        linearLayout.addView(UiKt.label$default(mainActivity, title, 15.0f, accent, true, 0, 16, null));
        TextView label$default = UiKt.label$default(mainActivity, sub, 11.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        label$default.setPadding(0, UiKt.dp(mainActivity, 4), 0, 0);
        linearLayout.addView(label$default);
        return linearLayout;
    }

    public static final void tile$lambda$46$lambda$45(Function0 onClick, View view) {
        Intrinsics.checkNotNullParameter(onClick, "$onClick");
        onClick.invoke();
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        MainActivity mainActivity = this;
        String read = CrashLog.INSTANCE.read(mainActivity);
        if (read != null) {
            showError("Pichli baar app band ho gayi. Ye screenshot bhej do:", read);
            return;
        }
        try {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(1);
            linearLayout.setBackgroundColor(C.INSTANCE.getBg());
            FrameLayout frameLayout = new FrameLayout(this);
            this.content = frameLayout;
            linearLayout.addView(frameLayout, UiKt.lp(UiKt.getMATCH(), 0, 1.0f));
            linearLayout.addView(buildNav());
            setContentView(linearLayout);
            show(0);
            this.started = true;
        } catch (Throwable th) {
            CrashLog.INSTANCE.write(mainActivity, th);
            String read2 = CrashLog.INSTANCE.read(mainActivity);
            if (read2 == null) {
                read2 = th.toString();
            }
            showError("Mukhda start nahi ho paya. Ye screenshot bhej do:", read2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.started && this.tab == 0) {
            show(0);
        }
    }
}
