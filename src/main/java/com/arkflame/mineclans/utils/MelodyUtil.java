package com.arkflame.mineclans.utils;

import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;

public class MelodyUtil {

    private static boolean isLegacy;

    static {
        try {
            Class.forName("org.bukkit.Note$Tone");
            isLegacy = false;
        } catch (ClassNotFoundException e) {
            isLegacy = true;
        }
    }

    private MelodyUtil() {
        // Private constructor to prevent instantiation
    }

    public enum Melody {
        BUFF_ACTIVE(new ToneOctavePair[] {
                new ToneOctavePair(Note.Tone.G, 0),
                new ToneOctavePair(Note.Tone.B, 0),
                new ToneOctavePair(Note.Tone.D, 1)
        }, 4, Instrument.PIANO),

        EVENT_END_MELODY(new ToneOctavePair[] {
                new ToneOctavePair(Note.Tone.C, 0),
                new ToneOctavePair(Note.Tone.E, 0),
                new ToneOctavePair(Note.Tone.G, 0),
                new ToneOctavePair(Note.Tone.C, 1),
                new ToneOctavePair(Note.Tone.E, 1),
                new ToneOctavePair(Note.Tone.G, 1),
                new ToneOctavePair(Note.Tone.C, 1)
        }, 4, Instrument.PIANO),

        EVENT_START_MELODY(new ToneOctavePair[] {
                new ToneOctavePair(Note.Tone.F, 0),
                new ToneOctavePair(Note.Tone.A, 0),
                new ToneOctavePair(Note.Tone.C, 1),
                new ToneOctavePair(Note.Tone.F, 1),
                new ToneOctavePair(Note.Tone.A, 1),
                new ToneOctavePair(Note.Tone.C, 1)
        }, 4, Instrument.PIANO),

        FACTION_JOIN_MELODY(new ToneOctavePair[] {
                new ToneOctavePair(Note.Tone.D, 0),
                new ToneOctavePair(Note.Tone.F, 0),
                new ToneOctavePair(Note.Tone.A, 0),
                new ToneOctavePair(Note.Tone.D, 1),
                new ToneOctavePair(Note.Tone.F, 1),
                new ToneOctavePair(Note.Tone.A, 1),
                new ToneOctavePair(Note.Tone.D, 1)
        }, 4, Instrument.PIANO),

        FACTION_LEAVE_MELODY(new ToneOctavePair[] {
                new ToneOctavePair(Note.Tone.E, 0),
                new ToneOctavePair(Note.Tone.G, 0),
                new ToneOctavePair(Note.Tone.B, 0),
                new ToneOctavePair(Note.Tone.E, 1),
                new ToneOctavePair(Note.Tone.G, 1),
                new ToneOctavePair(Note.Tone.B, 1),
                new ToneOctavePair(Note.Tone.E, 1)
        }, 4, Instrument.PIANO),

        KILL_REWARD_MELODY(new ToneOctavePair[] {
                new ToneOctavePair(Note.Tone.C, 1),
                new ToneOctavePair(Note.Tone.E, 1),
                new ToneOctavePair(Note.Tone.G, 1)
        }, 4, Instrument.PIANO),

        FACTION_CREATE_MELODY(new ToneOctavePair[] {
                new ToneOctavePair(Note.Tone.C, 1),
                new ToneOctavePair(Note.Tone.D, 1),
                new ToneOctavePair(Note.Tone.E, 1),
                new ToneOctavePair(Note.Tone.F, 1),
                new ToneOctavePair(Note.Tone.G, 1),
                new ToneOctavePair(Note.Tone.A, 1),
                new ToneOctavePair(Note.Tone.B, 1),
                new ToneOctavePair(Note.Tone.C, 1)
        }, 4, Instrument.PIANO),

        FACTION_DISBAND_MELODY(new ToneOctavePair[] {
                new ToneOctavePair(Note.Tone.C, 0),
                new ToneOctavePair(Note.Tone.B, 0),
                new ToneOctavePair(Note.Tone.G, 0),
                new ToneOctavePair(Note.Tone.E, 0),
                new ToneOctavePair(Note.Tone.C, 0),
                new ToneOctavePair(Note.Tone.A, 0),
                new ToneOctavePair(Note.Tone.G, 0),
                new ToneOctavePair(Note.Tone.E, 0)
        }, 4, Instrument.PIANO),

        ERROR(new ToneOctavePair[] {
                new ToneOctavePair(Note.Tone.C, 0),
        }, 1, Instrument.BASS_GUITAR);

        private final ToneOctavePair[] notes;
        private final int tickInterval;
        private final Instrument instrument;

        Melody(ToneOctavePair[] notes, int tickInterval, Instrument instrument) {
            this.notes = notes;
            this.tickInterval = tickInterval;
            this.instrument = instrument;
        }

        public Note[] getNotes() {
            return createNotes(notes);
        }

        public int getTickInterval() {
            return tickInterval;
        }

        public Instrument getInstrument() {
            return instrument;
        }

        private static Note[] createNotes(ToneOctavePair... pairs) {
            Note[] notes = new Note[pairs.length];
            for (int i = 0; i < pairs.length; i++) {
                ToneOctavePair pair = pairs[i];
                notes[i] = isLegacy ? getLegacyNote(pair.octave, pair.tone) : Note.natural(pair.octave, pair.tone);
            }
            return notes;
        }

        private static Note getLegacyNote(int octave, Note.Tone tone) {
            try {
                Method method = Note.class.getMethod("getNote", int.class, Note.Tone.class, boolean.class);
                return (Note) method.invoke(null, octave, tone, false);
            } catch (Exception e) {
                e.printStackTrace();
                return Note.natural(0, Note.Tone.C);
            }
        }
    }

    private static class ToneOctavePair {
        final Note.Tone tone;
        final int octave;

        ToneOctavePair(Note.Tone tone, int octave) {
            this.tone = tone;
            this.octave = octave;
        }
    }

    public static void playMelody(Plugin plugin, Player player, Melody melody) {
        playMelody(plugin, player, melody.getNotes(), melody.getTickInterval(), melody.getInstrument());
    }

    public static void playMelody(Plugin plugin, Melody melody) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            playMelody(plugin, player, melody);
        }
    }

    private static void playMelody(Plugin plugin, Player player, Note[] melody, int tickInterval,
            Instrument instrument) {
        for (int i = 0; i < melody.length; i++) {
            final Note note = melody[i];
            int delay = i * tickInterval;

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.playNote(player.getLocation(), instrument, note);
                }
            }.runTaskLater(plugin, delay);
        }
    }

    public static String getAvailableMelodies() {
        StringBuilder availableMelodies = new StringBuilder();
        Melody[] melodies = Melody.values();
        for (int i = 0; i < melodies.length; i++) {
            availableMelodies.append(melodies[i].name().toLowerCase());
            if (i < melodies.length - 1) {
                availableMelodies.append(", ");
            }
        }
        return availableMelodies.toString();
    }

}