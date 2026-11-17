package com.mukhda.editor;

import android.app.Activity;
import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

public final class Ads {
    public static final Ads INSTANCE = new Ads();

    private Ads() {
    }

    public static void onBreak$default(Ads ads, Activity activity, String str, Function0 function0, int i, Object obj) {
        if ((i & 4) != 0) {
            function0 = new Function0<Unit>() {
                @Override
                public Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                public final void invoke2() {
                }
            };
        }
        ads.onBreak(activity, str, function0);
    }

    public final void init(Context ctx) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
    }

    public final void onBreak(Activity activity, String place, Function0<Unit> then) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(place, "place");
        Intrinsics.checkNotNullParameter(then, "then");
        then.invoke();
    }
}
