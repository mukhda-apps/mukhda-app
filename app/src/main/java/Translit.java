package com.mukhda.editor;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Map;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

public final class Translit {
    public static final Translit INSTANCE = new Translit();
    private static final Map<Character, String> vowels = MapsKt.mapOf(TuplesKt.to((char) 2309, "a"), TuplesKt.to((char) 2310, "aa"), TuplesKt.to((char) 2311, "i"), TuplesKt.to((char) 2312, "ee"), TuplesKt.to((char) 2313, "u"), TuplesKt.to((char) 2314, "oo"), TuplesKt.to((char) 2315, "ri"), TuplesKt.to((char) 2319, "e"), TuplesKt.to((char) 2320, "ai"), TuplesKt.to((char) 2323, "o"), TuplesKt.to((char) 2324, "au"), TuplesKt.to((char) 2321, "o"));
    private static final Map<Character, String> matras = MapsKt.mapOf(TuplesKt.to((char) 2366, "aa"), TuplesKt.to((char) 2367, "i"), TuplesKt.to((char) 2368, "ee"), TuplesKt.to((char) 2369, "u"), TuplesKt.to((char) 2370, "oo"), TuplesKt.to((char) 2371, "ri"), TuplesKt.to((char) 2375, "e"), TuplesKt.to((char) 2376, "ai"), TuplesKt.to((char) 2379, "o"), TuplesKt.to((char) 2380, "au"), TuplesKt.to((char) 2377, "o"));
    private static final Map<Character, String> cons = MapsKt.mapOf(TuplesKt.to((char) 2325, "k"), TuplesKt.to((char) 2326, "kh"), TuplesKt.to((char) 2327, "g"), TuplesKt.to((char) 2328, "gh"), TuplesKt.to((char) 2329, "n"), TuplesKt.to((char) 2330, "ch"), TuplesKt.to((char) 2331, "chh"), TuplesKt.to((char) 2332, "j"), TuplesKt.to((char) 2333, "jh"), TuplesKt.to((char) 2334, "n"), TuplesKt.to((char) 2335, "t"), TuplesKt.to((char) 2336, "th"), TuplesKt.to((char) 2337, "d"), TuplesKt.to((char) 2338, "dh"), TuplesKt.to((char) 2339, "n"), TuplesKt.to((char) 2340, "t"), TuplesKt.to((char) 2341, "th"), TuplesKt.to((char) 2342, "d"), TuplesKt.to((char) 2343, "dh"), TuplesKt.to((char) 2344, "n"), TuplesKt.to((char) 2346, "p"), TuplesKt.to((char) 2347, "f"), TuplesKt.to((char) 2348, "b"), TuplesKt.to((char) 2349, "bh"), TuplesKt.to((char) 2350, "m"), TuplesKt.to((char) 2351, "y"), TuplesKt.to((char) 2352, "r"), TuplesKt.to((char) 2354, "l"), TuplesKt.to((char) 2357, "v"), TuplesKt.to((char) 2358, "sh"), TuplesKt.to((char) 2359, "sh"), TuplesKt.to((char) 2360, "s"), TuplesKt.to((char) 2361, "h"), TuplesKt.to((char) 2355, "l"));

    private Translit() {
    }

    public final String word(String w) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < w.length()) {
            char charAt = w.charAt(i);
            String str = cons.get(Character.valueOf(charAt));
            if (str != null) {
                sb.append(str);
                int i2 = i + 1;
                Character orNull = StringsKt.getOrNull(w, i2);
                if (orNull != null) {
                    Map<Character, String> map = matras;
                    if (map.containsKey(orNull)) {
                        sb.append(map.get(orNull));
                        i = i2;
                    }
                }
                if (orNull == null || orNull.charValue() != 2381) {
                    if (i != w.length() - 1) {
                        sb.append('a');
                    }
                }
                i = i2;
            } else {
                Map<Character, String> map2 = vowels;
                if (map2.containsKey(Character.valueOf(charAt))) {
                    sb.append(map2.get(Character.valueOf(charAt)));
                } else if (charAt == 2306 || charAt == 2305) {
                    sb.append('n');
                } else if (charAt == 2307) {
                    sb.append('h');
                } else if (charAt == 2404) {
                    sb.append('.');
                } else if (charAt != 2364 && charAt != 2381) {
                    sb.append(charAt);
                }
            }
            i++;
        }
        String sb2 = sb.toString();
        Intrinsics.checkNotNullExpressionValue(sb2, "toString(...)");
        return sb2;
    }

    public final String toRoman(String text) {
        Intrinsics.checkNotNullParameter(text, "text");
        return CollectionsKt.joinToString(StringsKt.split(text, new String[]{" "}, false, 0), " ", "", "", -1, "...", new Function1<String, CharSequence>() {
            @Override
            public final CharSequence invoke(String it) {
                String word;
                Intrinsics.checkNotNullParameter(it, "it");
                word = Translit.INSTANCE.word(it);
                return word;
            }
        });
    }
}
