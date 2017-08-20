package com.locurasoft.vstindexreducer;

public class MuteModulatorGenerator {

    public static final String STUB = "  <modulator modulatorVstExported=\"0\" modulatorMax=\"127\" vstIndex=\"\" modulatorIsStatic=\"1\"\n" +
            "             modulatorGlobalVariable=\"-1\" modulatorMuteOnStart=\"1\" modulatorMute=\"0\"\n" +
            "             modulatorExcludeFromSnapshot=\"0\" modulatorValueExpression=\"modulatorValue\"\n" +
            "             modulatorValueExpressionReverse=\"midiValue\" modulatorControllerExpression=\"value\"\n" +
            "             luaModulatorGetValueForMIDI=\"-- None\" luaModulatorGetValueFromMIDI=\"-- None\"\n" +
            "             modulatorLinkedToPanelProperty=\"-- None\" modulatorLinkedToModulatorProperty=\"-- None\"\n" +
            "             modulatorLinkedToModulator=\"-- None\" modulatorLinkedToModulatorSource=\"1\"\n" +
            "             modulatorLinkedToComponent=\"0\" modulatorBaseValue=\"0\" modulatorCustomIndex=\"0\"\n" +
            "             modulatorCustomName=\"Voice54\" modulatorCustomIndexGroup=\"0\"\n" +
            "             modulatorCustomNameGroup=\"\" modulatorVstNameFormat=\"%n\" luaModulatorValueChange=\"-- None\"\n" +
            "             name=\"Voice54\" modulatorMin=\"0\" modulatorValue=\"0\">\n" +
            "    <midi midiMessageType=\"5\" midiMessageChannelOverride=\"0\" midiMessageChannel=\"1\"\n" +
            "          midiMessageCtrlrNumber=\"1\" midiMessageCtrlrValue=\"0\" midiMessageMultiList=\"\"\n" +
            "          midiMessageSysExFormula=\"F0 41 00 14 12 00 00 36 xx z4 F7\"/>\n" +
            "    <component componentLabelPosition=\"top\" componentLabelJustification=\"center\"\n" +
            "               componentLabelHeight=\"14\" componentLabelWidth=\"0\" componentLabelVisible=\"1\"\n" +
            "               componentLabelAlwaysOnTop=\"1\" componentSentBack=\"0\" componentLabelColour=\"0xff000000\"\n" +
            "               componentLabelFont=\"&lt;Sans-Serif&gt;;12;0;0;0;0;1;3\" componentVisibleName=\"Voice54\"\n" +
            "               componentMouseCursor=\"2\" componentGroupName=\"Voice_globalPatchControls\"\n" +
            "               componentGroupped=\"1\" componentSnapSize=\"0\" componentIsLocked=\"0\"\n" +
            "               componentDisabled=\"0\" componentRadioGroupId=\"0\" componentRadioGroupNotifyMidi=\"1\"\n" +
            "               componentVisibility=\"0\" componentEffect=\"0\" componentEffectRadius=\"1\"\n" +
            "               componentEffectColour=\"0xff000000\" componentEffectOffsetX=\"0\"\n" +
            "               componentEffectOffsetY=\"0\" componentExcludedFromLabelDisplay=\"0\"\n" +
            "               componentValueDecimalPlaces=\"0\" componentLuaMouseMoved=\"-- None\"\n" +
            "               componentLuaMouseDown=\"-- None\" componentLuaMouseDrag=\"-- None\"\n" +
            "               componentLuaMouseDoubleClick=\"-- None\" uiSliderStyle=\"LinearVertical\"\n" +
            "               uiSliderMin=\"0\" uiSliderMax=\"127\" uiSliderInterval=\"1\" uiSliderDoubleClickEnabled=\"1\"\n" +
            "               uiSliderDoubleClickValue=\"0\" uiSliderValuePosition=\"4\" uiSliderValueHeight=\"12\"\n" +
            "               uiSliderValueWidth=\"64\" uiSliderTrackCornerSize=\"5\" uiSliderThumbCornerSize=\"3\"\n" +
            "               uiSliderThumbWidth=\"0\" uiSliderThumbHeight=\"0\" uiSliderThumbFlatOnLeft=\"0\"\n" +
            "               uiSliderThumbFlatOnRight=\"0\" uiSliderThumbFlatOnTop=\"0\" uiSliderThumbFlatOnBottom=\"0\"\n" +
            "               uiSliderValueTextColour=\"0xff000000\" uiSliderValueBgColour=\"ffffff\"\n" +
            "               uiSliderRotaryOutlineColour=\"ff000000\" uiSliderRotaryFillColour=\"ff000000\"\n" +
            "               uiSliderThumbColour=\"0xffff0000\" uiSliderValueHighlightColour=\"0xFF89A997\"\n" +
            "               uiSliderValueOutlineColour=\"0xffffffff\" uiSliderTrackColour=\"0xff0f0f0f\"\n" +
            "               uiSliderIncDecButtonColour=\"0xFF89A997\" uiSliderIncDecTextColour=\"0xffffffff\"\n" +
            "               uiSliderValueFont=\"&lt;Sans-Serif&gt;;12;1;0;0;0;1;3\" uiSliderValueTextJustification=\"centred\"\n" +
            "               uiSliderVelocitySensitivity=\"1\" uiSliderVelocityThreshold=\"1\"\n" +
            "               uiSliderVelocityOffset=\"0\" uiSliderVelocityMode=\"0\" uiSliderVelocityModeKeyTrigger=\"1\"\n" +
            "               uiSliderSpringMode=\"0\" uiSliderSpringValue=\"0\" uiSliderMouseWheelInterval=\"1\"\n" +
            "               uiSliderPopupBubble=\"0\" componentBubbleBackgroundColour1=\"0x9cffffff\"\n" +
            "               componentBubbleBackgroundColour2=\"0xbab9b9b9\" componentBubbleBackgroundGradientType=\"1\"\n" +
            "               componentBubbleNameColour=\"0xff000000\" componentBubbleNameFont=\"&lt;Sans-Serif&gt;;14;0;0;0;0;1;3\"\n" +
            "               componentBubbleNameJustification=\"centred\" componentBubbleRoundAngle=\"10\"\n" +
            "               componentBubbleValueColour=\"0xff000000\" componentBubbleValueFont=\"&lt;Sans-Serif&gt;;14;0;0;0;0;1;3\"\n" +
            "               componentBubbleValueJustification=\"centred\" componentLayerUid=\"6fafe60984010000f0f63200a8000000\"\n" +
            "               componentRectangle=\"0 0 31 73\" uiType=\"uiSlider\"/>\n" +
            "  </modulator>\n";

//    10:  54 (0), 118 (0), 246 (1), 310 (2)
//    16: 176 (1), 368 (2)
//    27: 421 (3)
    public static void main(String[] args) {
        int startIndex = Integer.parseInt(args[0]);
        int length = Integer.parseInt(args[1]);
        int offset = Integer.parseInt(args[2]);

        for (int i = startIndex; i < startIndex + length; i++) {
            String name = String.format("Voice%d", i);
//            F0 41 00 14 12 00 00 75 xx z4 F7
//            F0 41 00 14 12 00 01 75 xx z4 F7
//            F0 41 00 14 12 00 02 35 xx z4 F7
            int hexValue = i - offset * 128;
            String sysex = String.format("F0 41 00 14 12 00 0%d %s xx z4 F7", offset, Integer.toHexString(hexValue));
            String result = STUB.replaceAll("Voice54", name).replaceAll("F0 41 00 14 12 00 00 36 xx z4 F7", sysex);
            System.out.println(result);
        }
    }
}
