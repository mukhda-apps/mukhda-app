package com.mukhda.editor;

import android.content.Context;
import android.net.Uri;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.location.LocationRequestCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.io.ByteStreamsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;

public final class Music {
    public static final Music INSTANCE = new Music();
    private static final List<String> languages = CollectionsKt.listOf(new String[]{"Hindi", "Gujarati", "Punjabi", "English", "Bollywood", "South"});
    private static final List<Track> all = CollectionsKt.listOf(new Track[]{new Track("bollywood_dance", "Bollywood Dance", CollectionsKt.listOf(new String[]{"Bollywood", "Hindi"}), "Dance", 118), new Track("hindi_romance", "Dil Se (Romantic)", CollectionsKt.listOf(new String[]{"Hindi", "Bollywood"}), "Love", 84), new Track("romantic_piano", "Pyaar Piano", CollectionsKt.listOf("Hindi"), "Love", 78), new Track("retro_90s", "Retro 90s Filmy", CollectionsKt.listOf(new String[]{"Bollywood", "Hindi"}), "Retro", 106), new Track("sad_acoustic", "Judaai (Sad)", CollectionsKt.listOf("Hindi"), "Sad", 68), new Track("garba_beat", "Garba Beat", CollectionsKt.listOf("Gujarati"), "Festival", 120), new Track("dhol_dhamaka", "Dhol Dhamaka", CollectionsKt.listOf("Gujarati"), "Dance", 120), new Track("diwali_glow", "Diwali Glow", CollectionsKt.listOf(new String[]{"Gujarati", "Hindi"}), "Festival", 104), new Track("shaadi_sur", "Shaadi Sur", CollectionsKt.listOf(new String[]{"Gujarati", "Hindi"}), "Wedding", 92), new Track("holi_dhamaal", "Holi Dhamaal", CollectionsKt.listOf(new String[]{"Gujarati", "Hindi"}), "Festival", 140), new Track("bhajan_sur", "Bhajan Sur", CollectionsKt.listOf(new String[]{"Gujarati", "Hindi"}), "Devotional", 90), new Track("punjabi_bhangra", "Punjabi Bhangra", CollectionsKt.listOf("Punjabi"), "Dance", 100), new Track("punjabi_swag", "Punjabi Swag", CollectionsKt.listOf("Punjabi"), "Attitude", 108), new Track("attitude_drop", "Attitude Drop", CollectionsKt.listOf(new String[]{"Punjabi", "English"}), "Attitude", 128), new Track("party_anthem", "Party Anthem", CollectionsKt.listOf("English"), "Party", 128), new Track("edm_drop", "EDM Drop", CollectionsKt.listOf("English"), "Party", 126), new Track("reels_pop", "Reels Pop", CollectionsKt.listOf(new String[]{"English", "Hindi"}), "Trending", 110), new Track("lofi_chill", "Lo-fi Chill", CollectionsKt.listOf("English"), "Chill", 84), new Track("epic_cinematic", "Epic Cinematic", CollectionsKt.listOf(new String[]{"English", "South"}), "Epic", 96), new Track("desi_trap", "Desi Trap", CollectionsKt.listOf(new String[]{"English", "Punjabi"}), "Hip-hop", 140), new Track("south_mass", "South Mass", CollectionsKt.listOf("South"), "Mass", 145), new Track("south_beat", "South Beat", CollectionsKt.listOf("South"), "Dance", 128), new Track("south_melody", "South Melody", CollectionsKt.listOf("South"), "Melody", 92)});

    private Music() {
    }

    public final Track byFile(String f) {
        Object obj;
        Iterator it = all.iterator();
        while (true) {
            if (!it.hasNext()) {
                obj = null;
                break;
            }
            obj = it.next();
            if (Intrinsics.areEqual(((Track) obj).getFile(), f)) {
                break;
            }
        }
        return (Track) obj;
    }

    public final List<Track> getAll() {
        return all;
    }

    public final List<String> getLanguages() {
        return languages;
    }

    public final List<Track> inLanguage(String l) {
        Intrinsics.checkNotNullParameter(l, "l");
        List<Track> list = all;
        ArrayList arrayList = new ArrayList();
        for (Object obj : list) {
            if (((Track) obj).getLangs().contains(l)) {
                arrayList.add(obj);
            }
        }
        return arrayList;
    }

    public final Uri uri(Context ctx, String file) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(file, "file");
        File file2 = new File(ctx.getFilesDir(), "music/" + file + ".m4a");
        if (!file2.exists()) {
            File parentFile = file2.getParentFile();
            if (parentFile != null) {
                parentFile.mkdirs();
            }
            try {
                java.io.InputStream in = ctx.getAssets().open("music/" + file + ".m4a");
                FileOutputStream out = new FileOutputStream(file2);
                try {
                    ByteStreamsKt.copyTo(in, out, 8192);
                } finally {
                    try { in.close(); } catch (Exception ignored) {}
                    try { out.close(); } catch (Exception ignored) {}
                }
            } catch (Exception e) { throw new RuntimeException(e); }
        }
        Uri fromFile = Uri.fromFile(file2);
        Intrinsics.checkNotNullExpressionValue(fromFile, "fromFile(...)");
        return fromFile;
    }
}
