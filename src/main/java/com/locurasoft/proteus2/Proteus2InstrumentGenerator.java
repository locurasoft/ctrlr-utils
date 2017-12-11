package com.locurasoft.proteus2;

public class Proteus2InstrumentGenerator {

    private static String[] instNames = {
            "None", "Arco Basses", "Arco Celli", "Arco Violas", "ArcoViolins", "Dark Basses", "Dark Celli",
            "Dark Violas", "DarkViolins", "Low Tremolo", "HighTremolo", "Tremolande", "Strings 1", "Strings 2",
            "Strings 3", "Solo Cello", "Solo Viola", "Solo Violin", "Quartet 1", "Quartet 2", "Quartet 3",
            "Quartet 4", "Gambambo", "Pizz Basses", "Pizz Celli", "Pizz Violas", "Pizz Violin", "Pizzicombo",
            "Flute w/Vib", "Flute noVib", "Alt. Flute", "Piccolo", "Bass Clar.", "Clarinet", "B.Clar/Clar",
            "Cntrbassoon", "Bassoon", "EnglishHorn", "Oboe w/Vib", "Oboe noVib", "Alt. Oboe", "Woodwinds",
            "Hi Trombone", "Lo Trombone", "mf Trumpet", "ff Trumpet", "Harmon Mute", "mf Fr. Horn", "ff Fr. Horn",
            "Tuba", "ff Brass", "mf Brass", "Harp", "Xylophone", "Celesta", "Triangle", "Bass Drum",
            "Snare Drum+", "Piatti", "TempleBlock", "Glocknspiel", "Percussion1", "Percussion2", "Low Perc 2",
            "High Perc 2", "TubularBell", "Timpani", "Timp/T.Bell", "Tamborine", "Tam Tam", "Percussion3",
            "Special FX", "Oct 1 Sine", "Oct 2 All", "Oct 3 All", "Oct 4 All", "Oct 5 All", "Oct 6 All",
            "Oct 7 All", "Oct 2 Odd", "Oct 3 Odd", "Oct 4 Odd", "Oct 5 Odd", "Oct 6 Odd", "Oct 7 Odd",
            "Oct 2 Even", "Oct 3 Even", "Oct 4 Even", "Oct 5 Even", "Oct 6 Even", "Oct 7 Even", "Low Odds",
            "Low Evens", "FourOctaves", "Sine Wave", "Tri Wave", "Square Wave", "Pulse 33%", "Pulse 25%",
            "Pulse 10%", "Sawtooth", "SawOddGone", "Ramp", "RampEveOnly", "Vio Essence", "Buzzoon",
            "Brassy Wave", "Reedy Buzz", "Growl Wave", "HarpsiWave", "Fuzzy Gruzz", "Power 5ths", "Filt Saw",
            "Ice Bell", "Bronze Age", "Iron Plate", "Aluminum", "Lead Beam", "SteelXtract", "WinterGlass",
            "TwnBellWash", "Orch Bells", "Tubular SE", "SoftBellWav", "Swirly", "Tack Attack", "ShimmerWave",
            "Moog Lead", "B3 SE", "Mild Tone", "Piper", "Ah Wave", "Vocal Wave", "Fuzzy Clav",
            "Electrhode", "Whine 1", "Whine 2", "Metal Drone", "Silver Race", "MetalAttack", "Filter Bass",
            "UprightPizz", "NylonPluck1", "NylonPluck2", "PluckedBass"
    };

    private static String[] instValues = {"2-0", "2-1", "2-2", "2-3", "2-4", "2-5", "2-6", "2-7", "2-8", "2-9", "2-10", "2-13", "2-11", "2-12",
            "2-14", "4-1", "4-2", "4-3", "4-5", "4-6", "4-7", "4-8", "4-4", "4-9", "4-10", "4-11", "4-12", "4-13", "2-15",
            "2-63", "2-65", "2-16", "4-14", "4-15", "4-16", "4-17", "4-18", "4-19", "4-20", "4-30", "4-79", "4-21", "2-17", "2-64",
            "2-18", "2-19", "4-22", "2-20", "2-21", "2-22", "2-23", "2-24", "2-25", "2-26", "2-27", "2-28", "2-29", "2-30", "2-31", "2-32",
            "2-33", "2-34", "2-35", "2-36", "2-37", "4-23", "4-24", "4-25", "4-26", "4-27", "4-28", "4-29", "2-38", "2-39", "2-40",
            "2-41", "2-42", "2-43", "2-44", "2-45", "2-46", "2-47", "2-48", "2-49", "2-50", "2-51", "2-52", "2-53", "2-54", "2-55",
            "2-56", "2-57", "2-58", "2-59", "4-32", "4-33", "4-34", "4-35", "4-36", "4-37", "4-38", "4-39", "4-40", "4-41", "4-42",
            "4-43", "4-44", "4-45", "4-46", "4-47", "4-48", "4-49", "4-50", "4-51", "4-52", "4-53", "4-54", "4-55", "4-56", "4-57",
            "4-58", "4-59", "4-60", "4-61", "4-62", "4-63", "4-64", "4-65", "4-66", "4-67", "4-68", "4-69", "4-70", "4-71", "4-72",
            "4-73", "4-74", "4-75", "4-76", "4-77", "4-78", "4-31", "2-60", "2-61", "2-62"};

    public static void main(String[] args) {
        for (int i = 0; i < instNames.length; i++) {
            String instName = instNames[i];
            String instValue = instValues[i];

            String[] split = instValue.split("-");
            int val = Integer.parseInt(split[0]) * 128 + Integer.parseInt(split[1]);
            System.out.println(String.format("%s=%d", instName, val));
        }
    }
}
