package com.mukhda.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TemplateSpec {
    public final String id;
    public final String title;
    public final String sub;
    public final String musicFile;
    public final int bpm;
    public final int photoCount;
    public final int durationSec;
    public final List<String> cats;
    public final String group;
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

    private TemplateSpec(String id, String title, String sub, String musicFile, int bpm, int photoCount,
                         int durationSec, String cats, String group, int c1, int c2, String filter,
                         String overlay, String beatFx, String layout, String mask, String textLines,
                         boolean isNew) {
        this.id = id; this.title = title; this.sub = sub; this.musicFile = musicFile; this.bpm = bpm;
        this.photoCount = photoCount; this.durationSec = durationSec;
        this.cats = Arrays.asList(cats.split(","));
        this.group = group;
        this.c1 = c1; this.c2 = c2; this.filter = filter; this.overlay = overlay; this.frame = "none";
        this.layout = layout; this.mask = mask;
        this.beatFx = beatFx; this.textLines = Arrays.asList(textLines.split("\\|")); this.isNew = isNew;
    }

    public long durationMs() { return durationSec * 1000L; }

    public List<Long> beatsMs() {
        List<Long> out = new ArrayList<Long>();
        long beat = 60000L / bpm;
        for (long t = 0; t < durationMs(); t += beat) out.add(t);
        return out;
    }

    private static final Map<String, Integer> BPM = new HashMap<String, Integer>();
    static {
        BPM.put("bollywood_dance", 118); BPM.put("hindi_romance", 84); BPM.put("romantic_piano", 78);
        BPM.put("retro_90s", 106); BPM.put("sad_acoustic", 68); BPM.put("garba_beat", 120);
        BPM.put("dhol_dhamaka", 120); BPM.put("diwali_glow", 104); BPM.put("shaadi_sur", 92);
        BPM.put("holi_dhamaal", 140); BPM.put("bhajan_sur", 90); BPM.put("punjabi_bhangra", 100);
        BPM.put("punjabi_swag", 108); BPM.put("attitude_drop", 128); BPM.put("party_anthem", 128);
        BPM.put("edm_drop", 126); BPM.put("reels_pop", 110); BPM.put("lofi_chill", 84);
        BPM.put("epic_cinematic", 96); BPM.put("desi_trap", 140); BPM.put("south_mass", 145);
        BPM.put("south_beat", 128); BPM.put("south_melody", 92);
    }

    public static final List<TemplateSpec> ALL = new ArrayList<TemplateSpec>();

    private static String autoLayout(int photos) {
        return photos == 1 ? "full" : (photos == 2 ? "split2" : (photos == 4 ? "grid4" : "grid6"));
    }

    /** cat, group, id, title, song, photos, durationSec, filter, overlay, beatFx, layout(null=auto), mask, c1, c2, textLines, forYou */
    private static void a(String cat, String grp, String id, String title, String song, int photos,
                          int dur, String filter, String overlay, String fx, String layout, String mask,
                          int c1, int c2, String texts, boolean forYou) {
        String lay = layout == null ? autoLayout(photos) : layout;
        String msk = "full".equals(lay) ? mask : "rounded";
        String cats = forYou ? cat + ",For you" : cat;
        ALL.add(new TemplateSpec(id, title, photos + " Photos", song, BPM.get(song), photos, dur,
                cats, grp, c1, c2, filter, overlay, fx, lay, msk, texts, "New".equals(cat)));
    }

    static {
        // ---------- New ----------
        a("New", "Face Swap", "new_facepop", "Face Pop", "reels_pop", 1, 12, "vivid", "sparkles", "zoompulse", "full", "circle", 0xFF8E2DE2, 0xFF4A00E0, "FACE POP|Trending Now", true);
        a("New", "Face Swap", "new_faceduo", "Face Duo Swap", "desi_trap", 2, 12, "cool", "leak", "glitch", null, "rounded", 0xFF00BCD4, 0xFF1A237E, "SWAP STYLE|Double Fun", true);
        a("New", "Mirror Trend", "new_mirror2", "Mirror Trend", "reels_pop", 4, 15, "aqua", "sparkles", "shine", "grid4", "rounded", 0xFF11998E, 0xFF38EF7D, "MIRROR ME|Trend Alert", true);
        a("New", "Mirror Trend", "new_mirrorx", "Mirror Magic", "edm_drop", 4, 12, "vivid", "leak", "flash", "grid4", "rounded", 0xFF373B44, 0xFF4286F4, "MIRROR MAGIC", true);
        a("New", "Cute Emoji", "new_emoji1", "Cute Emoji Love", "reels_pop", 1, 12, "rose", "hearts", "zoompulse", "full", "heart", 0xFFF857A6, 0xFFFF5858, "SO CUTE|Smile Please", true);
        a("New", "Cute Emoji", "new_emoji2", "Emoji Fun", "bollywood_dance", 2, 12, "vivid", "confetti", "punch", null, "rounded", 0xFFFC4A1A, 0xFFF7B733, "EMOJI FUN|Hasti Raho", true);
        a("New", "", "new_viral", "Viral Reel", "reels_pop", 4, 15, "vivid", "leak", "punch", null, "rounded", 0xFFFF5F6D, 0xFFFFC371, "VIRAL|Trending Reel", true);
        a("New", "", "new_fresh", "Fresh Beat", "desi_trap", 6, 15, "cool", "sparkles", "shake", null, "rounded", 0xFF0F2027, 0xFF2C5364, "FRESH|New Drop", false);
        // ---------- Love ----------
        a("Love", "Your Name", "love_names", "Naam Wala Love", "hindi_romance", 2, 15, "rose", "hearts", "zoompulse", null, "rounded", 0xFFEC407A, 0xFF7B1FA2, "A + P|Forever", true);
        a("Love", "Your Name", "love_initials", "Love Initials", "romantic_piano", 2, 15, "rose", "hearts", "shine", null, "rounded", 0xFFF06292, 0xFF4A148C, "P <3 R|Made For Each Other", false);
        a("Love", "Your Name", "love_couplename", "Couple Naam", "hindi_romance", 4, 20, "warm", "hearts", "zoompulse", null, "rounded", 0xFFD81B60, 0xFF5E35B1, "Tera Naam|Mera Naam", false);
        a("Love", "Single Pic", "love_single", "Ek Photo Pyar", "romantic_piano", 1, 15, "rose", "hearts", "shine", "full", "heart", 0xFFEC407A, 0xFF880E4F, "Only You|Meri Jaan", true);
        a("Love", "Single Pic", "love_heart1", "Dil Frame", "hindi_romance", 1, 12, "rose", "hearts", "zoompulse", "full", "heart", 0xFFF06292, 0xFF6A1B9A, "Dil Se|Sirf Tum", false);
        a("Love", "Single Pic", "love_circle", "Circle of Love", "romantic_piano", 1, 15, "warm", "sparkles", "shine", "full", "circle", 0xFFFF8A80, 0xFF4A148C, "You & Me|Hamesha", false);
        a("Love", "", "dil_se", "Dil Se Love", "hindi_romance", 4, 20, "rose", "hearts", "zoompulse", null, "rounded", 0xFFEC407A, 0xFF7B1FA2, "Dil Se|Love Forever", true);
        a("Love", "", "love_story", "Love Story", "romantic_piano", 6, 24, "rose", "hearts", "shine", null, "rounded", 0xFFF06292, 0xFF4A148C, "Love Story|You & Me", true);
        a("Love", "", "love_ishq", "Ishq Wala Love", "hindi_romance", 4, 15, "sunset", "hearts", "zoompulse", null, "rounded", 0xFFFF5F6D, 0xFF8E2DE2, "Ishq Wala Love|Bas Tum", false);
        a("Love", "", "love_mohabbat", "Mohabbat", "romantic_piano", 10, 25, "rose", "sparkles", "shine", null, "rounded", 0xFFC2185B, 0xFF311B92, "Mohabbat|Dil Ki Baat", false);
        // ---------- Birthday ----------
        a("Birthday", "Single Pic", "bday_single", "Birthday Star", "party_anthem", 1, 12, "vivid", "confetti", "punch", "full", "circle", 0xFFFF4081, 0xFFFFD740, "Happy Birthday|Party Time", true);
        a("Birthday", "Single Pic", "bday_hero", "Bday Hero", "bollywood_dance", 1, 12, "warm", "confetti", "zoompulse", "full", "rounded", 0xFFFC4A1A, 0xFFF7B733, "Janamdin Mubarak|Hero No.1", false);
        a("Birthday", "Single Pic", "bday_queen", "Birthday Queen", "reels_pop", 1, 15, "rose", "confetti", "shine", "full", "heart", 0xFFF857A6, 0xFFFF5858, "Birthday Queen|Shine On", false);
        a("Birthday", "", "birthday", "Happy Birthday", "party_anthem", 4, 15, "vivid", "confetti", "punch", null, "rounded", 0xFFFF4081, 0xFFFFD740, "Happy Birthday|Party Time", true);
        a("Birthday", "", "bday_cake", "Cake Cutting", "bollywood_dance", 4, 15, "warm", "confetti", "punch", null, "rounded", 0xFFFF6F00, 0xFFE91E63, "Cake Time|Make A Wish", true);
        a("Birthday", "", "bday_balloon", "Balloon Party", "party_anthem", 6, 20, "vivid", "confetti", "shake", null, "rounded", 0xFF00BCD4, 0xFF7B1FA2, "Balloon Party|Celebrate", false);
        a("Birthday", "", "bday_family", "Family Bday", "shaadi_sur", 6, 20, "warm", "sparkles", "zoompulse", null, "rounded", 0xFFFFB74D, 0xFF4E342E, "Happy Birthday|With Family", false);
        a("Birthday", "", "bday_bash", "Bday Bash", "edm_drop", 10, 20, "vivid", "confetti", "flash", null, "rounded", 0xFF00E676, 0xFF1A237E, "BDAY BASH|All Night", false);
        // ---------- Anniversary ----------
        a("Anniversary", "", "anniversary", "Anniversary Gold", "romantic_piano", 4, 20, "warm", "hearts", "shine", "full", "polaroid", 0xFFD4A017, 0xFF5D4037, "Happy Anniversary|Forever Together", true);
        a("Anniversary", "", "anni_rose", "Rose Anniversary", "hindi_romance", 2, 15, "rose", "hearts", "zoompulse", null, "rounded", 0xFFAD1457, 0xFF4A148C, "Saalgirah Mubarak|You & Me", false);
        a("Anniversary", "", "anni_heart", "Heart Anniversary", "romantic_piano", 1, 15, "rose", "hearts", "shine", "full", "heart", 0xFFEC407A, 0xFF880E4F, "Anniversary|Dil Se Dil Tak", true);
        a("Anniversary", "", "anni_journey", "Humdum Journey", "shaadi_sur", 6, 25, "warm", "sparkles", "zoompulse", null, "rounded", 0xFFD4A017, 0xFF3E2723, "Humdum|Sath Sath", false);
        a("Anniversary", "", "anni_couple", "Couple Goals", "hindi_romance", 4, 20, "sunset", "hearts", "shine", null, "rounded", 0xFFFF7043, 0xFF880E4F, "Couple Goals|Anniversary", false);
        a("Anniversary", "", "anni_years", "Years of Love", "romantic_piano", 10, 30, "warm", "hearts", "zoompulse", null, "rounded", 0xFF8D6E63, 0xFF311B92, "Years of Love|Many More", false);
        a("Anniversary", "", "anni_vows", "Vows Forever", "shaadi_sur", 2, 15, "sepia", "sparkles", "shine", null, "rounded", 0xFFBCAAA4, 0xFF4E342E, "Vows Forever|Shadi Ki Saalgirah", false);
        a("Anniversary", "", "anni_dance", "Anniversary Dance", "retro_90s", 4, 15, "vivid", "hearts", "punch", null, "rounded", 0xFFAD1457, 0xFF311B92, "Anniversary|Dance Night", false);
        // ---------- Friends ----------
        a("Friends", "Bestie", "fr_bestie1", "Bestie Forever", "reels_pop", 2, 12, "vivid", "sparkles", "punch", null, "rounded", 0xFFF857A6, 0xFFFF5858, "BESTIE|Forever Wali", true);
        a("Friends", "Bestie", "fr_bestie2", "Bestie Goals", "bollywood_dance", 4, 15, "rose", "confetti", "shake", null, "rounded", 0xFFEC407A, 0xFF00BCD4, "Bestie Goals|Partner In Crime", false);
        a("Friends", "Bro", "fr_bro1", "Bro Code", "desi_trap", 2, 12, "cool", "leak", "glitch", null, "rounded", 0xFF373B44, 0xFF4286F4, "BRO CODE|No Rules", true);
        a("Friends", "Bro", "fr_bro2", "Yaar Beli", "punjabi_bhangra", 4, 15, "vivid", "leak", "punch", null, "rounded", 0xFFFF8F00, 0xFF6A1B9A, "Yaar Beli|Jigri Yaar", false);
        a("Friends", "", "fr_yaari", "Yaari Dosti", "reels_pop", 4, 15, "vivid", "confetti", "punch", null, "rounded", 0xFF11998E, 0xFF38EF7D, "Yaari Dosti|Dil Se", true);
        a("Friends", "", "fr_gang", "Gang Story", "desi_trap", 6, 15, "cool", "leak", "shake", null, "rounded", 0xFF0F2027, 0xFF2C5364, "THE GANG|Full Power", false);
        a("Friends", "", "fr_memories", "Friendship Memories", "lofi_chill", 6, 20, "fade", "sparkles", "shine", null, "rounded", 0xFF355C7D, 0xFFC06C84, "Memories|Friends Forever", false);
        a("Friends", "", "fr_masti", "Dosti Masti", "bollywood_dance", 10, 20, "vivid", "confetti", "punch", null, "rounded", 0xFFFF5F6D, 0xFFFFC371, "Dosti|Masti Non Stop", false);
        // ---------- God ----------
        a("God", "Shiva", "god_shiva1", "Har Har Mahadev", "bhajan_sur", 1, 15, "cool", "sparkles", "shine", "full", "rounded", 0xFF1A237E, 0xFF00BCD4, "Har Har Mahadev|Bhole Nath", true);
        a("God", "Shiva", "god_shiva2", "Shiv Shakti", "epic_cinematic", 4, 20, "cool", "leak", "zoompulse", null, "rounded", 0xFF0D47A1, 0xFF311B92, "Shiv Shakti|Om Namah Shivaya", false);
        a("God", "Ganesha", "god_ganesh1", "Ganpati Bappa", "bhajan_sur", 1, 15, "warm", "sparkles", "shine", "full", "rounded", 0xFFFF8F00, 0xFFBF360C, "Ganpati Bappa|Morya", true);
        a("God", "Ganesha", "god_ganesh2", "Bappa Blessings", "diwali_glow", 4, 15, "diya", "sparkles", "zoompulse", null, "rounded", 0xFFFFB300, 0xFF880E4F, "Bappa|Sukh Karta", false);
        a("God", "Krishna", "god_krishna1", "Jai Shree Krishna", "bhajan_sur", 1, 15, "aqua", "sparkles", "shine", "full", "circle", 0xFF1565C0, 0xFF6A1B9A, "Jai Shree Krishna|Radhe Radhe", true);
        a("God", "Krishna", "god_krishna2", "Kanha Love", "bhajan_sur", 2, 15, "cool", "hearts", "zoompulse", null, "rounded", 0xFF283593, 0xFF00897B, "Kanha|Murli Wala", false);
        a("God", "Mahadev", "god_mahadev", "Mahadev Status", "epic_cinematic", 2, 15, "noir", "leak", "shake", null, "rounded", 0xFF212121, 0xFF01579B, "MAHADEV|Bam Bam Bhole", false);
        a("God", "Ram", "god_ram", "Jai Shri Ram", "bhajan_sur", 1, 15, "diya", "sparkles", "shine", "full", "rounded", 0xFFFF6F00, 0xFFBF360C, "Jai Shri Ram|Ayodhya", false);
        a("God", "", "god_bhakti", "Bhakti Sagar", "bhajan_sur", 4, 20, "diya", "sparkles", "zoompulse", null, "rounded", 0xFFFFB300, 0xFF4E342E, "Bhakti|Shraddha", false);
        a("God", "", "god_mandir", "Mandir Darshan", "bhajan_sur", 6, 20, "warm", "sparkles", "shine", null, "rounded", 0xFF8D6E63, 0xFFBF360C, "Darshan|Jai Ho", false);
        // ---------- Family ----------
        a("Family", "Brother Sister", "fam_brosis1", "Bhai Behen", "shaadi_sur", 2, 15, "warm", "hearts", "zoompulse", null, "rounded", 0xFFFFB74D, 0xFF6A1B9A, "Bhai Behen|Rishta Pakka", true);
        a("Family", "Brother Sister", "fam_brosis2", "Sibling Love", "retro_90s", 4, 15, "vivid", "sparkles", "punch", null, "rounded", 0xFFFF5F6D, 0xFF8E2DE2, "Sibling Love|Tom & Jerry", false);
        a("Family", "Baby", "fam_baby1", "Cute Baby", "lofi_chill", 1, 12, "rose", "hearts", "zoompulse", "full", "heart", 0xFFF8BBD0, 0xFF7B1FA2, "Cute Baby|Nanha Mehmaan", true);
        a("Family", "Baby", "fam_baby2", "Baby Smiles", "reels_pop", 4, 15, "warm", "hearts", "punch", null, "rounded", 0xFFFFCC80, 0xFFAD1457, "Baby Smiles|Gudda Guddi", false);
        a("Family", "Mom", "fam_mom1", "Maa", "sad_acoustic", 1, 15, "warm", "hearts", "shine", "full", "circle", 0xFFFFB74D, 0xFF880E4F, "MAA|Duniya Meri", true);
        a("Family", "Mom", "fam_mom2", "Meri Maa", "bhajan_sur", 4, 20, "warm", "sparkles", "zoompulse", null, "rounded", 0xFFD4A017, 0xFF5D4037, "Meri Maa|Pyar Bhara", false);
        a("Family", "Dad", "fam_dad1", "Papa Hero", "retro_90s", 1, 12, "sepia", "sparkles", "shine", "full", "rounded", 0xFF8D6E63, 0xFF263238, "PAPA|Mera Hero", true);
        a("Family", "Dad", "fam_dad2", "Papa Pride", "epic_cinematic", 2, 15, "warm", "leak", "zoompulse", null, "rounded", 0xFF5D4037, 0xFF1A237E, "Papa Pride|Strong Man", false);
        a("Family", "", "fam_story", "Family Story", "shaadi_sur", 15, 30, "warm", "sparkles", "zoompulse", null, "rounded", 0xFFFFB74D, 0xFF4E342E, "Family|Moments Forever", true);
        a("Family", "", "fam_together", "Parivaar", "shaadi_sur", 6, 20, "warm", "hearts", "shine", null, "rounded", 0xFFBCAAA4, 0xFF4E342E, "Parivaar|Sab Sath", false);
        // ---------- YoungSwag ----------
        a("YoungSwag", "Baby Dialogue", "ys_dialog1", "Baby Dialogue", "reels_pop", 1, 12, "vivid", "sparkles", "punch", "full", "rounded", 0xFFFC4A1A, 0xFFF7B733, "Baby Bol|Dialogue King", true);
        a("YoungSwag", "Baby Dialogue", "ys_dialog2", "Chhotu Star", "bollywood_dance", 2, 12, "vivid", "confetti", "shake", null, "rounded", 0xFFFF5F6D, 0xFFFFC371, "Chhotu|Bada Dialogue", false);
        a("YoungSwag", "Baby Dance", "ys_dance1", "Baby Dance", "bollywood_dance", 1, 12, "vivid", "confetti", "shake", "full", "rounded", 0xFF11998E, 0xFF38EF7D, "Baby Dance|Full Jhakaas", true);
        a("YoungSwag", "Baby Dance", "ys_dance2", "Dhamaka Dance", "dhol_dhamaka", 4, 15, "vivid", "confetti", "punch", null, "rounded", 0xFFFF8A00, 0xFFE91E63, "DHAMAKA|Dance Baby Dance", false);
        a("YoungSwag", "Baby Showtime", "ys_show1", "Baby Showtime", "reels_pop", 4, 15, "cool", "sparkles", "zoompulse", null, "rounded", 0xFF373B44, 0xFF4286F4, "SHOWTIME|Little Star", false);
        a("YoungSwag", "Baby Showtime", "ys_show2", "Swag Kid", "punjabi_swag", 6, 15, "vivid", "leak", "shake", null, "rounded", 0xFF0F2027, 0xFF2C5364, "SWAG KID|Attitude Full", false);
        a("YoungSwag", "Baby Dance", "ys_dance3", "Baby Beat Drop", "edm_drop", 2, 12, "cool", "confetti", "flash", null, "rounded", 0xFF00BCD4, 0xFF1A237E, "BEAT DROP|Baby Style", false);
        a("YoungSwag", "Baby Showtime", "ys_show3", "Stage Star Baby", "party_anthem", 1, 12, "rose", "confetti", "zoompulse", "full", "heart", 0xFFF857A6, 0xFFFF5858, "STAGE STAR|Baby Showtime", false);
        // ---------- Bhojpuri ----------
        a("Bhojpuri", "", "bhoj_swag", "Bhojpuri Swag", "dhol_dhamaka", 2, 12, "vivid", "leak", "punch", null, "rounded", 0xFFFF6F00, 0xFF880E4F, "BHOJPURI SWAG|Jila Top", true);
        a("Bhojpuri", "", "bhoj_dance", "Lollypop Dance", "desi_trap", 4, 15, "vivid", "confetti", "shake", null, "rounded", 0xFF00E676, 0xFF1A237E, "Dance Dhamaka|Bhojpuri Style", false);
        a("Bhojpuri", "", "bhoj_star", "Bhojpuri Star", "bollywood_dance", 1, 12, "warm", "sparkles", "zoompulse", "full", "rounded", 0xFFFC4A1A, 0xFFF7B733, "Star Ho|Khesari Feel", false);
        a("Bhojpuri", "", "bhoj_dhamaka", "Bhojpuriya Dhamaka", "dhol_dhamaka", 6, 15, "diya", "leak", "punch", null, "rounded", 0xFFFF8F00, 0xFF6A1B9A, "DHAMAKA|Bhojpuriya", false);
        a("Bhojpuri", "", "bhoj_rang", "Rang Barse Bhojpuri", "holi_dhamaal", 4, 12, "vivid", "confetti", "shake", null, "rounded", 0xFFE91E63, 0xFF00BCD4, "RANG BARSE|Bhojpuri Beat", false);
        a("Bhojpuri", "", "bhoj_single", "Bhojpuri Hero", "south_mass", 1, 12, "warm", "leak", "punch", "full", "rounded", 0xFFBF360C, 0xFF212121, "BHOJPURI HERO|Full Mass", false);
        a("Bhojpuri", "", "bhoj_mela", "Mela Dhamaka", "garba_beat", 10, 20, "diya", "confetti", "punch", null, "rounded", 0xFFFF8F00, 0xFFBF360C, "MELA|Dhamaka Night", false);
        a("Bhojpuri", "", "bhoj_love", "Bhojpuri Romance", "retro_90s", 2, 15, "rose", "hearts", "zoompulse", null, "rounded", 0xFFAD1457, 0xFF4A148C, "Bhojpuri Romance|Dil Ke Kareeb", false);
        // ---------- Sad ----------
        a("Sad", "Sad Love", "sad_love1", "Judai", "sad_acoustic", 1, 15, "bw", "leak", "shine", "full", "rounded", 0xFF37474F, 0xFF880E4F, "Judai|Dil Tod Diya", true);
        a("Sad", "Sad Love", "sad_love2", "Bewafa", "sad_acoustic", 2, 15, "noir", "leak", "glitch", null, "rounded", 0xFF212121, 0xFF37474F, "Bewafa|Kyunnn", false);
        a("Sad", "Sad Love", "sad_love3", "Tute Dil", "romantic_piano", 4, 20, "fade", "hearts", "shine", null, "rounded", 0xFF455A64, 0xFF4A148C, "Tute Dil|Yaadein", false);
        a("Sad", "Alone", "sad_alone1", "Akelapan", "sad_acoustic", 1, 15, "noir", "leak", "shine", "full", "rounded", 0xFF263238, 0xFF0F2027, "Akelapan|Koi Nahi", true);
        a("Sad", "Alone", "sad_alone2", "Tanhayi", "lofi_chill", 1, 12, "bw", "sparkles", "shine", "full", "circle", 0xFF37474F, 0xFF311B92, "Tanhayi|Bas Main", false);
        a("Sad", "Alone", "sad_alone3", "Alone Walk", "lofi_chill", 4, 20, "fade", "leak", "shine", null, "rounded", 0xFF355C7D, 0xFF2C3E50, "Alone|Khamoshi", false);
        a("Sad", "", "sad_status", "Sad Status", "sad_acoustic", 2, 15, "noir", "leak", "shine", null, "rounded", 0xFF212121, 0xFF37474F, "Sad Status|Dard Bhara", false);
        a("Sad", "", "sad_rain", "Barsaat Sad", "sad_acoustic", 6, 20, "cool", "leak", "shine", null, "rounded", 0xFF1A237E, 0xFF37474F, "Barsaat|Aansu", false);
        // ---------- AI Magic ----------
        a("AI Magic", "AI Baby", "ai_baby1", "AI Baby Look", "reels_pop", 1, 12, "vivid", "sparkles", "zoompulse", "full", "circle", 0xFF8E2DE2, 0xFF00BCD4, "AI BABY|Magic Look", true);
        a("AI Magic", "AI Baby", "ai_baby2", "Baby Magic", "lofi_chill", 2, 12, "aqua", "hearts", "shine", null, "rounded", 0xFF11998E, 0xFF38EF7D, "Baby Magic|AI Style", false);
        a("AI Magic", "AI Baby", "ai_baby3", "Little AI Star", "bollywood_dance", 4, 15, "vivid", "confetti", "punch", null, "rounded", 0xFFF857A6, 0xFFFF5858, "Little Star|AI Touch", false);
        a("AI Magic", "Magic Face", "ai_face1", "Magic Face", "edm_drop", 1, 12, "cool", "leak", "glitch", "full", "heart", 0xFF7B1FA2, 0xFF311B92, "MAGIC FACE|Wow Look", true);
        a("AI Magic", "Magic Face", "ai_face2", "Face Glow", "reels_pop", 1, 12, "rose", "sparkles", "shine", "full", "circle", 0xFFEC407A, 0xFF4A148C, "FACE GLOW|AI Magic", false);
        a("AI Magic", "Magic Face", "ai_face3", "Neon Face", "desi_trap", 2, 12, "vivid", "leak", "flash", null, "rounded", 0xFF00BCD4, 0xFF1A237E, "NEON FACE|Future Look", false);
        a("AI Magic", "AI Baby", "ai_baby4", "AI Cutie Reel", "reels_pop", 6, 15, "aqua", "hearts", "punch", null, "rounded", 0xFF11998E, 0xFF8E2DE2, "AI CUTIE|Magic Reel", false);
        a("AI Magic", "Magic Face", "ai_face4", "Glitter Face", "edm_drop", 4, 12, "rose", "sparkles", "flash", null, "rounded", 0xFFEC407A, 0xFF311B92, "GLITTER FACE|AI Sparkle", false);
        // ---------- Attitude ----------
        a("Attitude", "Swagger", "att_swag1", "Swag Wala", "punjabi_swag", 1, 12, "vivid", "leak", "punch", "full", "rounded", 0xFF212121, 0xFFFF8F00, "SWAG WALA|Level High", true);
        a("Attitude", "Swagger", "att_swag2", "Royal Swag", "desi_trap", 2, 12, "cool", "leak", "glitch", null, "rounded", 0xFF0F2027, 0xFF2C5364, "ROYAL SWAG|Nawabi Style", false);
        a("Attitude", "Swagger", "att_swag3", "Swag Mode On", "attitude_drop", 4, 15, "noir", "leak", "shake", null, "rounded", 0xFF212121, 0xFFB71C1C, "SWAG MODE|Always On", false);
        a("Attitude", "Powerful", "att_power1", "Power Star", "south_mass", 1, 12, "vivid", "leak", "shake", "full", "rounded", 0xFFFF6F00, 0xFF263238, "POWER STAR|Mass Look", true);
        a("Attitude", "Powerful", "att_power2", "Rowdy Feel", "south_beat", 2, 12, "diya", "leak", "punch", null, "rounded", 0xFFBF360C, 0xFF212121, "ROWDY|Full Power", false);
        a("Attitude", "Powerful", "att_power3", "Boss Entry", "epic_cinematic", 4, 15, "cinema", "leak", "zoompulse", null, "rounded", 0xFF1A237E, 0xFF000000, "BOSS ENTRY|Mass Scene", false);
        a("Attitude", "", "attitude", "Attitude Status", "attitude_drop", 2, 12, "noir", "leak", "glitch", null, "rounded", 0xFF212121, 0xFFB71C1C, "ATTITUDE|No Caption Needed", true);
        a("Attitude", "", "att_killer", "Killer Look", "desi_trap", 6, 15, "noir", "leak", "shake", null, "rounded", 0xFF37474F, 0xFF000000, "KILLER LOOK|Danger Zone", false);
        // ---------- Reels ----------
        a("Reels", "Triple Screen", "reel_triple1", "Triple Screen", "reels_pop", 6, 15, "vivid", "sparkles", "punch", "split3", "rounded", 0xFFFF5F6D, 0xFFFFC371, "TRIPLE SCREEN|Reel Star", true);
        a("Reels", "Triple Screen", "reel_triple2", "Teen Screen Fun", "bollywood_dance", 6, 15, "aqua", "confetti", "shake", "split3", "rounded", 0xFF11998E, 0xFF38EF7D, "3X FUN|Triple Dhamaka", false);
        a("Reels", "3D Effects", "reel_3d1", "3D Zoom Reel", "edm_drop", 4, 12, "vivid", "leak", "zoompulse", null, "rounded", 0xFF8E2DE2, 0xFF4A00E0, "3D ZOOM|Next Level", true);
        a("Reels", "3D Effects", "reel_3d2", "3D Glitch", "desi_trap", 4, 12, "cool", "leak", "glitch", null, "rounded", 0xFF00BCD4, 0xFF1A237E, "3D GLITCH|Viral FX", false);
        a("Reels", "Dual Photo", "reel_dual1", "Dual Photo Reel", "reels_pop", 2, 12, "rose", "sparkles", "punch", null, "rounded", 0xFFEC407A, 0xFF7B1FA2, "DUAL PHOTO|Trending", true);
        a("Reels", "Dual Photo", "reel_dual2", "Do Photo Style", "hindi_romance", 2, 15, "warm", "hearts", "zoompulse", null, "rounded", 0xFFFF7043, 0xFF880E4F, "Do Photo|Ek Reel", false);
        a("Reels", "Horizontal", "reel_horz1", "Cinema Reel", "epic_cinematic", 4, 15, "cinema", "leak", "shine", null, "rounded", 0xFF212121, 0xFF37474F, "CINEMA REEL|Movie Feel", false);
        a("Reels", "Horizontal", "reel_horz2", "Widescreen Vibe", "lofi_chill", 4, 15, "fade", "sparkles", "shine", null, "rounded", 0xFF355C7D, 0xFFC06C84, "WIDESCREEN|Aesthetic", false);
        a("Reels", "", "reel_trend", "Trending Reel", "reels_pop", 6, 15, "vivid", "leak", "punch", null, "rounded", 0xFFFF5F6D, 0xFF8E2DE2, "TRENDING|Reel It", true);
        a("Reels", "", "reel_fast", "Fast Cut Reel", "edm_drop", 10, 15, "vivid", "confetti", "flash", null, "rounded", 0xFF00E676, 0xFF1A237E, "FAST CUTS|Non Stop", false);
        // ---------- Nature ----------
        a("Nature", "", "nat_green", "Nature Love", "south_melody", 4, 20, "aqua", "sparkles", "shine", null, "rounded", 0xFF11998E, 0xFF38EF7D, "Nature|Sukoon", true);
        a("Nature", "", "nat_sunset", "Sunset Vibes", "lofi_chill", 4, 15, "sunset", "sparkles", "shine", null, "rounded", 0xFFFC4A1A, 0xFFF7B733, "Sunset Vibes|Golden Hour", false);
        a("Nature", "", "nat_rain", "Rain Mood", "sad_acoustic", 2, 15, "cool", "leak", "shine", null, "rounded", 0xFF1A237E, 0xFF37474F, "Rain Mood|Baarish", false);
        a("Nature", "", "nat_flower", "Phool Bahar", "romantic_piano", 6, 20, "rose", "hearts", "zoompulse", null, "rounded", 0xFFF06292, 0xFF6A1B9A, "Phool Bahar|Khushboo", false);
        a("Nature", "", "nat_mount", "Mountain Trip", "epic_cinematic", 6, 20, "cool", "leak", "zoompulse", null, "rounded", 0xFF0F2027, 0xFF2C5364, "Mountains|Safar", false);
        a("Nature", "", "nat_sea", "Sea Side", "lofi_chill", 2, 15, "aqua", "leak", "shine", null, "rounded", 0xFF0277BD, 0xFF01579B, "Sea Side|Lehrein", false);
        a("Nature", "", "nat_village", "Gaon Ki Yaad", "south_melody", 4, 20, "sepia", "sparkles", "shine", null, "rounded", 0xFF8D6E63, 0xFF33691E, "Gaon Ki Yaad|Mitti", false);
        a("Nature", "", "nat_sky", "Aasman", "epic_cinematic", 1, 12, "cool", "sparkles", "zoompulse", "full", "rounded", 0xFF1565C0, 0xFF4A148C, "AASMAN|Udaan", false);
        // ---------- Funny ----------
        a("Funny", "", "fun_comedy", "Comedy Time", "bollywood_dance", 2, 12, "vivid", "confetti", "shake", null, "rounded", 0xFFFF5F6D, 0xFFFFC371, "COMEDY|Hansi Non Stop", true);
        a("Funny", "", "fun_meme", "Meme Reel", "reels_pop", 4, 12, "vivid", "leak", "glitch", null, "rounded", 0xFF00BCD4, 0xFF7B1FA2, "MEME TIME|LOL", false);
        a("Funny", "", "fun_masti", "Full Masti", "dhol_dhamaka", 4, 15, "warm", "confetti", "punch", null, "rounded", 0xFFFC4A1A, 0xFFF7B733, "FULL MASTI|Haso Yaar", false);
        a("Funny", "", "fun_joker", "Joker Mood", "desi_trap", 6, 15, "vivid", "leak", "shake", null, "rounded", 0xFF8E2DE2, 0xFF311B92, "JOKER MOOD|Crazy", false);
        a("Funny", "", "fun_laugh", "Hasi Ka Dose", "party_anthem", 6, 15, "sunset", "confetti", "punch", null, "rounded", 0xFFFF8A00, 0xFFE91E63, "HASI KA DOSE|Daily", false);
        a("Funny", "", "fun_dance", "Funny Dance", "punjabi_bhangra", 2, 12, "vivid", "confetti", "shake", null, "rounded", 0xFFFF8F00, 0xFF6A1B9A, "FUNNY DANCE|Has Has Ke", false);
        a("Funny", "", "fun_face", "Funny Faces", "reels_pop", 1, 12, "aqua", "sparkles", "glitch", "full", "circle", 0xFF11998E, 0xFF38EF7D, "FUNNY FACES|Hahaha", false);
        a("Funny", "", "fun_bloopers", "Bloopers Reel", "edm_drop", 10, 15, "vivid", "leak", "flash", null, "rounded", 0xFF00E676, 0xFF1A237E, "BLOOPERS|Oops Moment", false);
        // ---------- Greeting ----------
        a("Greeting", "Good Morning", "grt_morning1", "Good Morning", "bhajan_sur", 1, 12, "sunset", "sparkles", "shine", "full", "rounded", 0xFFFFB300, 0xFFFF7043, "GOOD MORNING|Suprabhat", true);
        a("Greeting", "Good Morning", "grt_morning2", "Morning Wishes", "south_melody", 2, 12, "warm", "sparkles", "zoompulse", null, "rounded", 0xFFFFCC80, 0xFFAD1457, "Shubh Prabhat|Nice Day", false);
        a("Greeting", "Good Night", "grt_night1", "Good Night", "lofi_chill", 1, 12, "cool", "sparkles", "shine", "full", "rounded", 0xFF1A237E, 0xFF311B92, "GOOD NIGHT|Sweet Dreams", true);
        a("Greeting", "Good Night", "grt_night2", "Shubh Ratri", "sad_acoustic", 2, 12, "noir", "sparkles", "shine", null, "rounded", 0xFF263238, 0xFF0F2027, "Shubh Ratri|Chanda Mama", false);
        a("Greeting", "", "grt_wishes", "Best Wishes", "bhajan_sur", 4, 15, "warm", "sparkles", "zoompulse", null, "rounded", 0xFFD4A017, 0xFF5D4037, "Best Wishes|Shubhkamnayein", false);
        a("Greeting", "", "grt_bless", "Blessings", "bhajan_sur", 4, 15, "diya", "hearts", "shine", null, "rounded", 0xFFFF8F00, 0xFF880E4F, "Blessings|Aashirwad", false);
        a("Greeting", "Good Morning", "grt_morning3", "Chai Morning", "retro_90s", 4, 12, "warm", "sparkles", "punch", null, "rounded", 0xFFBCAAA4, 0xFFBF360C, "Good Morning|Chai Ready", false);
        a("Greeting", "Good Night", "grt_night3", "Starry Night", "lofi_chill", 4, 15, "cool", "sparkles", "shine", null, "rounded", 0xFF0F2027, 0xFF2C5364, "Starry Night|So Jao", false);
        // ---------- Festival ----------
        a("Festival", "Diwali", "diwali", "Diwali Wishes", "diwali_glow", 4, 20, "diya", "sparkles", "shine", null, "rounded", 0xFFFFB300, 0xFFBF360C, "Shubh Deepavali|Happy Diwali", true);
        a("Festival", "Diwali", "fest_diya", "Diya Night", "diwali_glow", 2, 15, "diya", "sparkles", "zoompulse", null, "rounded", 0xFFFF8A00, 0xFF880E4F, "Diya Jale|Diwali Night", false);
        a("Festival", "Navratri", "navratri", "Navratri Special", "garba_beat", 6, 20, "diya", "sparkles", "punch", null, "rounded", 0xFFFF5722, 0xFF880E4F, "Navratri|Garba Time", true);
        a("Festival", "Navratri", "garba_night", "Garba Night", "dhol_dhamaka", 1, 15, "diya", "sparkles", "punch", "full", "rounded", 0xFFFF8A00, 0xFFE91E63, "Garba Night|Dandiya Raas", true);
        a("Festival", "Holi", "holi", "Holi Dhamaal", "holi_dhamaal", 4, 15, "vivid", "confetti", "shake", null, "rounded", 0xFFE91E63, 0xFF00BCD4, "Holi Hai|Rang Barse", true);
        a("Festival", "Holi", "fest_rang", "Rang Utsav", "holi_dhamaal", 6, 15, "vivid", "confetti", "punch", null, "rounded", 0xFF8E2DE2, 0xFF38EF7D, "Rang Utsav|Happy Holi", false);
        a("Festival", "", "fest_shaadi", "Shaadi Season", "shaadi_sur", 6, 25, "warm", "sparkles", "zoompulse", null, "rounded", 0xFFFFD54F, 0xFF3E2723, "Shubh Vivah|Wedding Bells", false);
        a("Festival", "", "fest_uttarayan", "Uttarayan", "garba_beat", 4, 15, "aqua", "confetti", "punch", null, "rounded", 0xFF00BCD4, 0xFFFF8F00, "Uttarayan|Kai Po Che", false);
    }

    public static final List<String> CATEGORIES = Arrays.asList(
            "For you", "New", "Love", "Birthday", "Anniversary", "Friends", "God", "Family",
            "YoungSwag", "Bhojpuri", "Sad", "AI Magic", "Attitude", "Reels", "Nature", "Funny",
            "Greeting", "Festival");

    /** Sub-chip labels per category (without "All"; UI prepends it). */
    public static final Map<String, List<String>> SUBCHIPS = new LinkedHashMap<String, List<String>>();
    static {
        SUBCHIPS.put("New", Arrays.asList("Face Swap", "Mirror Trend", "Cute Emoji"));
        SUBCHIPS.put("Love", Arrays.asList("Your Name", "Single Pic"));
        SUBCHIPS.put("Birthday", Arrays.asList("Single Pic"));
        SUBCHIPS.put("Friends", Arrays.asList("Bestie", "Bro"));
        SUBCHIPS.put("God", Arrays.asList("Shiva", "Ganesha", "Krishna", "Mahadev", "Ram"));
        SUBCHIPS.put("Family", Arrays.asList("Brother Sister", "Baby", "Mom", "Dad"));
        SUBCHIPS.put("YoungSwag", Arrays.asList("Baby Dialogue", "Baby Dance", "Baby Showtime"));
        SUBCHIPS.put("Sad", Arrays.asList("Sad Love", "Alone"));
        SUBCHIPS.put("AI Magic", Arrays.asList("AI Baby", "Magic Face"));
        SUBCHIPS.put("Attitude", Arrays.asList("Swagger", "Powerful"));
        SUBCHIPS.put("Reels", Arrays.asList("Triple Screen", "3D Effects", "Dual Photo", "Horizontal"));
        SUBCHIPS.put("Greeting", Arrays.asList("Good Morning", "Good Night"));
        SUBCHIPS.put("Festival", Arrays.asList("Diwali", "Navratri", "Holi"));
    }

    public static List<String> groupsOf(String category) {
        List<String> g = SUBCHIPS.get(category);
        return g == null ? new ArrayList<String>() : g;
    }

    public static TemplateSpec byId(String id) {
        for (TemplateSpec t : ALL) if (t.id.equals(id)) return t;
        return null;
    }

    public static List<TemplateSpec> inCategory(String c) {
        List<TemplateSpec> out = new ArrayList<TemplateSpec>();
        for (TemplateSpec t : ALL) {
            if (t.cats.contains(c)) out.add(t);
        }
        return out;
    }

    public static List<TemplateSpec> inGroup(String category, String group) {
        List<TemplateSpec> out = new ArrayList<TemplateSpec>();
        for (TemplateSpec t : inCategory(category)) {
            if (group == null || group.length() == 0 || "All".equals(group) || group.equals(t.group)) out.add(t);
        }
        return out;
    }
}
