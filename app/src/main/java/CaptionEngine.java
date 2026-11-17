package com.mukhda.editor;

import android.content.Context;
import android.net.Uri;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.mukhda.editor.CaptionEngine;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.ranges.RangesKt;
import org.json.JSONArray;
import org.json.JSONObject;
import org.vosk.Model;
import org.vosk.Recognizer;

public final class CaptionEngine {
    public static final CaptionEngine INSTANCE = new CaptionEngine();

    public static final class Word {
        private final long endMs;
        private final long startMs;
        private final String text;

        public Word(String text, long j, long j2) {
            Intrinsics.checkNotNullParameter(text, "text");
            this.text = text;
            this.startMs = j;
            this.endMs = j2;
        }

        public static Word copy$default(Word word, String str, long j, long j2, int i, Object obj) {
            if ((i & 1) != 0) {
                str = word.text;
            }
            if ((i & 2) != 0) {
                j = word.startMs;
            }
            long j3 = j;
            if ((i & 4) != 0) {
                j2 = word.endMs;
            }
            return word.copy(str, j3, j2);
        }

        public final String getText() {
            return this.text;
        }

        public final long getStartMs() {
            return this.startMs;
        }

        public final long getEndMs() {
            return this.endMs;
        }

        public final Word copy(String text, long startMs, long endMs) {
            Intrinsics.checkNotNullParameter(text, "text");
            return new Word(text, startMs, endMs);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Word)) {
                return false;
            }
            Word word = (Word) other;
            return Intrinsics.areEqual(this.text, word.text) && this.startMs == word.startMs && this.endMs == word.endMs;
        }

        public int hashCode() {
            return (((this.text.hashCode() * 31) + Long.hashCode(this.startMs)) * 31) + Long.hashCode(this.endMs);
        }

        public String toString() {
            return "Word(text=" + this.text + ", startMs=" + this.startMs + ", endMs=" + this.endMs + ")";
        }
    }

    private CaptionEngine() {
    }

    private static final void group$flush(Ref.ObjectRef<ArrayList<Word>> objectRef, boolean z, ArrayList<Caption> arrayList, String str) {
        if (objectRef.element.isEmpty()) {
            return;
        }
        String joinToString$default = CollectionsKt.joinToString(objectRef.element, " ", "", "", -1, "...", new Function1<Word, CharSequence>() {
            @Override
            public final CharSequence invoke(CaptionEngine.Word it) {
                Intrinsics.checkNotNullParameter(it, "it");
                return it.getText();
            }
        });
        if (z) {
            joinToString$default = Translit.INSTANCE.toRoman(joinToString$default);
        }
        arrayList.add(new Caption(str, ((Word) CollectionsKt.first((List) objectRef.element)).getStartMs(), 150 + ((Word) CollectionsKt.last((List) objectRef.element)).getEndMs(), joinToString$default));
        objectRef.element = new ArrayList();
    }

    public static final void transcribe$collect(ArrayList<Word> arrayList, Clip clip, String str) {
        JSONArray optJSONArray;
        try {
            optJSONArray = new JSONObject(str).optJSONArray("result");
        } catch (org.json.JSONException e) { return; }
        if (optJSONArray == null) {
            return;
        }
        int length = optJSONArray.length();
        for (int i = 0; i < length; i++) {
            try {
                JSONObject jSONObject = optJSONArray.getJSONObject(i);
                String string = jSONObject.getString("word");
                Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                double d = 1000;
                arrayList.add(new Word(string, clip.getStartMs() + ((long) (jSONObject.getDouble("start") * d)), clip.getStartMs() + ((long) (jSONObject.getDouble("end") * d))));
            } catch (org.json.JSONException e) { continue; }
        }
    }

    public final List<Caption> group(String clipId, List<Word> words, boolean roman) {
        int i;
        Intrinsics.checkNotNullParameter(clipId, "clipId");
        Intrinsics.checkNotNullParameter(words, "words");
        ArrayList arrayList = new ArrayList();
        Ref.ObjectRef objectRef = new Ref.ObjectRef();
        objectRef.element = new ArrayList();
        Iterator<Word> it = words.iterator();
        while (true) {
            i = 0;
            if (!it.hasNext()) {
                break;
            }
            Word next = it.next();
            if (!((Collection) objectRef.element).isEmpty()) {
                long startMs = next.getStartMs() - ((Word) CollectionsKt.last((List) objectRef.element)).getEndMs();
                Iterator it2 = ((Iterable) objectRef.element).iterator();
                while (it2.hasNext()) {
                    i += ((Word) it2.next()).getText().length() + 1;
                }
                if (startMs > 800 || ((ArrayList) objectRef.element).size() >= 6 || i + next.getText().length() > 30 || next.getEndMs() - ((Word) CollectionsKt.first((List) objectRef.element)).getStartMs() > 3500) {
                    group$flush(objectRef, roman, arrayList, clipId);
                }
            }
            ((ArrayList) objectRef.element).add(next);
        }
        group$flush(objectRef, roman, arrayList, clipId);
        ArrayList arrayList2 = arrayList;
        ArrayList arrayList3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(arrayList2, 10));
        for (Object obj : arrayList2) {
            int i2 = i + 1;
            if (i < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            Caption caption = (Caption) obj;
            if (i2 < arrayList.size() && caption.getSrcEndMs() > ((Caption) arrayList.get(i2)).getSrcStartMs()) {
                caption = Caption.copy$default(caption, null, 0L, ((Caption) arrayList.get(i2)).getSrcStartMs(), null, 11, null);
            }
            arrayList3.add(caption);
            i = i2;
        }
        return arrayList3;
    }

    public final List<Caption> transcribe(Context ctx, final Clip clip, Lang lang, Function0<Boolean> isCancelled, final Function1<? super Float, Unit> onProgress) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(clip, "clip");
        Intrinsics.checkNotNullParameter(lang, "lang");
        Intrinsics.checkNotNullParameter(isCancelled, "isCancelled");
        Intrinsics.checkNotNullParameter(onProgress, "onProgress");
        if (clip.isImage()) {
            return CollectionsKt.emptyList();
        }
        Model model;
        Recognizer recognizerTmp;
        try {
            model = new Model(ModelStore.INSTANCE.dir(ctx, lang.getModel()).getAbsolutePath());
            recognizerTmp = new Recognizer(model, 16000.0f);
        } catch (Exception e) { return CollectionsKt.emptyList(); }
        final Recognizer recognizer = recognizerTmp;
        recognizer.setWords(true);
        final ArrayList arrayList = new ArrayList();
        try {
            final long coerceAtLeast = RangesKt.coerceAtLeast(clip.getEndMs() - clip.getStartMs(), 1L);
            final Ref.LongRef longRef = new Ref.LongRef();
            final byte[] bArr = new byte[32000];
            AudioDecoder audioDecoder = AudioDecoder.INSTANCE;
            Uri parse = Uri.parse(clip.getUri());
            Intrinsics.checkNotNullExpressionValue(parse, "parse(...)");
            if (!audioDecoder.decode(ctx, parse, clip.getStartMs(), clip.getEndMs(), isCancelled, (Function2) new Function2<short[], Integer, Unit>() {

                @Override
                public Unit invoke(short[] sArr, Integer num) {
                    invoke(sArr, num.intValue());
                    return Unit.INSTANCE;
                }

                public final void invoke(short[] chunk, int i) {
                    Intrinsics.checkNotNullParameter(chunk, "chunk");
                    int i2 = 0;
                    while (i2 < i) {
                        int min = Math.min(i - i2, bArr.length / 2);
                        for (int i3 = 0; i3 < min; i3++) {
                            short s = chunk[i2 + i3];
                            byte[] bArr2 = bArr;
                            int i4 = i3 * 2;
                            bArr2[i4] = (byte) (s & 255);
                            bArr2[i4 + 1] = (byte) (s >> 8);
                        }
                        if (recognizer.acceptWaveForm(bArr, min * 2)) {
                            ArrayList<CaptionEngine.Word> arrayList2 = arrayList;
                            Clip clip2 = clip;
                            String result = recognizer.getResult();
                            Intrinsics.checkNotNullExpressionValue(result, "getResult(...)");
                            CaptionEngine.transcribe$collect(arrayList2, clip2, result);
                        }
                        i2 += min;
                        longRef.element += min;
                    }
                    onProgress.invoke(Float.valueOf(RangesKt.coerceIn(((((float) longRef.element) * 1000.0f) / 16000) / ((float) coerceAtLeast), 0.0f, 1.0f)));
                }
            })) {
                return CollectionsKt.emptyList();
            }
            String finalResult = recognizer.getFinalResult();
            Intrinsics.checkNotNullExpressionValue(finalResult, "getFinalResult(...)");
            transcribe$collect(arrayList, clip, finalResult);
            recognizer.close();
            model.close();
            ArrayList arrayList2 = new ArrayList();
            for (Object obj : arrayList) {
                if (((Word) obj).getStartMs() < clip.getEndMs()) {
                    arrayList2.add(obj);
                }
            }
            return group(clip.getId(), arrayList2, lang.getRoman());
        } finally {
            recognizer.close();
            model.close();
        }
    }
}
