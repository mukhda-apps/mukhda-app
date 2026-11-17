package com.mukhda.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TemplateSpec {
    public final String id;
    public final String title;
    public final String sub;
    public final String musicFile;
    public final int bpm;
    public final int photoCount;
    public final int durationSec;
    public final List<String> cats;
    public final int c1;
    public final int c2;
    public final String filter;
    public final String overlay;
    public final String frame;
    public final String layout;
    public final String mask;
    public final String beatFx;
    public final List<String> textLines;
    public final boolean isNew;

    public TemplateSpec(String id, String title, String sub, String musicFile, int bpm, int photoCount,
                        int durationSec, String cats, int c1, int c2, String filter, String overlay,
                        String frame, String beatFx, String textLines, boolean isNew) {
        this(id, title, sub, musicFile, bpm, photoCount, durationSec, cats, c1, c2, filter, overlay, frame, beatFx, textLines, isNew,
             photoCount == 2 ? "split2" : (photoCount == 4 ? "grid4" : (photoCount >= 6 ? "grid6" : "full")), "rounded");
    }

    public TemplateSpec(String id, String title, String sub, String musicFile, int bpm, int photoCount,
                        int durationSec, String cats, int c1, int c2, String filter, String overlay,
                        String frame, String beatFx, String textLines, boolean isNew, String layout, String mask) {
        this.layout = layout; this.mask = mask;
        this.id = id; this.title = title; this.sub = sub; this.musicFile = musicFile; this.bpm = bpm;
        this.photoCount = photoCount; this.durationSec = durationSec;
        this.cats = Arrays.asList(cats.split(","));
        this.c1 = c1; this.c2 = c2; this.filter = filter; this.overlay = overlay; this.frame = frame;
        this.beatFx = beatFx; this.textLines = Arrays.asList(textLines.split("\\|")); this.isNew = isNew;
    }

    public long durationMs() { return durationSec * 1000L; }

    public List<Long> beatsMs() {
        List<Long> out = new ArrayList<Long>();
        long beat = 60000L / bpm;
        for (long t = 0; t < durationMs(); t += beat) out.add(t);
        return out;
    }

    public static final List<TemplateSpec> ALL = new ArrayList<TemplateSpec>();
    static {
        ALL.add(new TemplateSpec("garba_night", "Garba Night", "Dhol rhythm, one photo", "dhol_dhamaka", 128, 1, 15,
                "For you,Gujarati,Festival", 0xFFFF8A00, 0xFFE91E63, "diya", "sparkles", "rounded", "punch", "Garba Night|ગરબા નાઇટ", true));
        ALL.add(new TemplateSpec("edm", "EDM Drop", "Flash cuts on the drop", "edm_drop", 126, 1, 12,
                "For you,English,Attitude", 0xFF7B1FA2, 0xFF311B92, "vivid", "leak", "none", "flash", "DROP", false));
        ALL.add(new TemplateSpec("attitude", "Attitude Status", "Hard cuts", "attitude_drop", 140, 2, 12,
                "For you,Attitude,Hindi", 0xFF212121, 0xFFB71C1C, "noir", "leak", "none", "glitch", "ATTITUDE|No Caption Needed", true));
        ALL.add(new TemplateSpec("south_mass", "South Mass", "Mass hero style", "south_mass", 132, 2, 12,
                "South,Attitude", 0xFFFF6F00, 0xFF263238, "vivid", "leak", "none", "shake", "MASS", false));
        ALL.add(new TemplateSpec("bhangra", "Bhangra Beat", "Dhol energy", "punjabi_bhangra", 120, 2, 15,
                "Punjabi,Festival", 0xFFFF8F00, 0xFF6A1B9A, "vivid", "confetti", "rounded", "punch", "Bhangra", false));
        ALL.add(new TemplateSpec("dil_se", "Dil Se Love", "Soft romantic", "hindi_romance", 90, 4, 20,
                "For you,Love,Hindi", 0xFFEC407A, 0xFF7B1FA2, "rose", "hearts", "rounded", "pulse", "Dil Se|Love Forever", true));
        ALL.add(new TemplateSpec("diwali", "Diwali Wishes", "Warm glow", "diwali_glow", 100, 4, 20,
                "Festival,Gujarati,Hindi", 0xFFFFB300, 0xFFBF360C, "diya", "sparkles", "rounded", "pulse", "દિવાળીની શુભકામના|Happy Diwali", true));
        ALL.add(new TemplateSpec("birthday", "Happy Birthday", "Party beat", "party_anthem", 118, 4, 15,
                "For you,Birthday,English", 0xFFFF4081, 0xFFFFD740, "vivid", "confetti", "rounded", "punch", "Happy Birthday|Party Time", true));
        ALL.add(new TemplateSpec("anniversary", "Anniversary", "Golden moments", "romantic_piano", 84, 4, 20,
                "For you,Anniversary,Love", 0xFFD4A017, 0xFF5D4037, "warm", "hearts", "rounded", "pulse", "Happy Anniversary|Forever Together", true, "full", "polaroid"));
        ALL.add(new TemplateSpec("holi", "Holi Dhamaal", "Colourful, fast", "holi_dhamaal", 124, 4, 15,
                "Festival,Hindi", 0xFFE91E63, 0xFF00BCD4, "vivid", "confetti", "none", "shake", "હોળીની શુભકામના|Happy Holi", false));
        ALL.add(new TemplateSpec("navratri", "Navratri Special", "Garba beats", "garba_beat", 130, 6, 20,
                "For you,Gujarati,Festival", 0xFFFF5722, 0xFF880E4F, "diya", "sparkles", "rounded", "punch", "નવરાત્રિની શુભકામના|Navratri", true));
        ALL.add(new TemplateSpec("love_story", "Love Story", "Dreamy slow cuts", "romantic_piano", 84, 6, 24,
                "Love,Hindi", 0xFFF06292, 0xFF4A148C, "rose", "hearts", "rounded", "pulse", "Love Story|You & Me", false));
        ALL.add(new TemplateSpec("wedding", "Wedding Invite", "Slow, graceful", "shaadi_sur", 92, 6, 30,
                "Wedding,Gujarati,Hindi", 0xFFFFD54F, 0xFF3E2723, "warm", "sparkles", "rounded", "pulse", "શુભ લગ્ન|Wedding", false, "full", "circle"));
        ALL.add(new TemplateSpec("party", "Party Mode", "Fast party cuts", "party_anthem", 122, 10, 20,
                "English,Birthday", 0xFF00E676, 0xFF1A237E, "vivid", "confetti", "none", "flash", "PARTY|All Night", false));
        ALL.add(new TemplateSpec("family_story", "Family Story", "Every moment", "shaadi_sur", 96, 15, 30,
                "For you,Hindi,Gujarati", 0xFFFFB74D, 0xFF4E342E, "warm", "sparkles", "rounded", "pulse", "Family|Moments Forever", true));
    }

    public static final List<String> CATEGORIES = Arrays.asList(
            "For you", "New", "Love", "Birthday", "Anniversary", "Festival", "Gujarati", "Hindi",
            "Punjabi", "South", "Wedding", "Attitude", "English");

    public static TemplateSpec byId(String id) {
        for (TemplateSpec t : ALL) if (t.id.equals(id)) return t;
        return null;
    }

    public static List<TemplateSpec> inCategory(String c) {
        List<TemplateSpec> out = new ArrayList<TemplateSpec>();
        for (TemplateSpec t : ALL) {
            if (c.equals("New")) { if (t.isNew) out.add(t); }
            else if (t.cats.contains(c)) out.add(t);
        }
        return out;
    }
}
