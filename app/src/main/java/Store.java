package com.mukhda.editor;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.mukhda.editor.Project;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.io.FilesKt;
import kotlin.text.Charsets;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.json.JSONObject;

public final class Store {
    public static final Store INSTANCE = new Store();

    private Store() {
    }

    private final File dir(Context ctx) {
        File file = new File(ctx.getFilesDir(), "projects");
        file.mkdirs();
        return file;
    }

    public static final boolean list$lambda$1(File file) {
        String name = file.getName();
        Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
        return StringsKt.endsWith(name, ".json", false);
    }

    private final SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences("mukhda", 0);
    }

    public final void delete(Context ctx, String id) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(id, "id");
        new File(dir(ctx), id + ".json").delete();
    }

    public final List<Project> list(Context ctx) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        File[] listFiles = dir(ctx).listFiles(new FileFilter() {
            @Override
            public final boolean accept(File file) {
                boolean list$lambda$1;
                list$lambda$1 = Store.list$lambda$1(file);
                return list$lambda$1;
            }
        });
        if (listFiles == null) {
            listFiles = new File[0];
        }
        ArrayList arrayList = new ArrayList();
        for (File file : listFiles) {
            Project project = null;
            try {
                Project.Companion companion = Project.INSTANCE;
                Intrinsics.checkNotNull(file);
                project = companion.fromJson(new JSONObject(FilesKt.readText(file, Charsets.UTF_8)));
            } catch (Exception unused) {
            }
            if (project != null) {
                arrayList.add(project);
            }
        }
        return CollectionsKt.sortedWith(arrayList, new Comparator() {
            @Override
            public final int compare(Object t, Object t2) {
                return ComparisonsKt.compareValues(Long.valueOf(((Project) t2).getUpdatedAt()), Long.valueOf(((Project) t).getUpdatedAt()));
            }
        });
    }

    public final boolean lite(Context ctx) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        return prefs(ctx).getBoolean("lite", false);
    }

    public final Project load(Context ctx, String id) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(id, "id");
        try {
            return Project.INSTANCE.fromJson(new JSONObject(FilesKt.readText(new File(dir(ctx), id + ".json"), Charsets.UTF_8)));
        } catch (Exception unused) {
            return null;
        }
    }

    public final void save(Context ctx, Project p) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(p, "p");
        File file = new File(dir(ctx), p.getId() + ".json");
        File file2 = new File(dir(ctx), p.getId() + ".tmp");
        String jSONObject = p.toJson().toString();
        Intrinsics.checkNotNullExpressionValue(jSONObject, "toString(...)");
        FilesKt.writeText(file2, jSONObject, Charsets.UTF_8);
        file2.renameTo(file);
    }

    public final void setLite(Context ctx, boolean v) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        prefs(ctx).edit().putBoolean("lite", v).apply();
    }

    public final void setUserName(Context ctx, String n) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(n, "n");
        prefs(ctx).edit().putString("name", StringsKt.trim((CharSequence) n).toString()).apply();
    }

    public final String userName(Context ctx) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        String string = prefs(ctx).getString("name", "");
        return string == null ? "" : string;
    }
}
