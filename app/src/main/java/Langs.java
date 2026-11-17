package com.mukhda.editor;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

public final class Langs {
    public static final Langs INSTANCE = new Langs();
    private static final List<Lang> all = CollectionsKt.listOf(new Lang[]{new Lang("gu", "ગુજરાતી", "vosk-model-small-gu-0.42", 103, false, 16, null), new Lang("hi", "हिंदी", "vosk-model-small-hi-0.22", 42, false, 16, null), new Lang("hinglish", "Hinglish", "vosk-model-small-hi-0.22", 42, true), new Lang("en", "English", "vosk-model-small-en-in-0.4", 36, false, 16, null)});

    private Langs() {
    }

    public final Lang get(String code) {
        Object obj;
        Intrinsics.checkNotNullParameter(code, "code");
        Iterator it = all.iterator();
        while (true) {
            if (!it.hasNext()) {
                obj = null;
                break;
            }
            obj = it.next();
            if (Intrinsics.areEqual(((Lang) obj).getCode(), code)) {
                break;
            }
        }
        Lang lang = (Lang) obj;
        return lang == null ? all.get(0) : lang;
    }

    public final List<Lang> getAll() {
        return all;
    }
}
