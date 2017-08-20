package com.locurasoft.vstindexreducer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VstIndexReducer {
    public static void main(String[] args) throws IOException, IllegalAccessException {
        Path path = Paths.get("c:\\ctrlr\\Panels\\pascalc\\Roland - D-50_1_0.panel");
//        moveCustomIndexToVstIndex(path);
        verifyUniqueVstIndexes(path);
//        moveNameToCustomName(path);
    }

    static void moveCustomIndexToVstIndex(Path path) throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        Pattern pattern = Pattern.compile("modulatorVstExported=\"\\d\"([^>]+)vstIndex=\"[^\"]*\"([^>]+)modulatorIsStatic=\"\\d\"([^>]+)modulatorCustomName=\"Voice(\\d+)\"");
        String content = new String(Files.readAllBytes(path), charset);
        Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();

        System.out.println(sb.toString());
        while (matcher.find()) {
            String replacement = "modulatorVstExported=\"1\"" + matcher.group(1) + "vstIndex=\"" + matcher.group(4) + "\"" + matcher.group(2) +
                    "modulatorIsStatic=\"0\"" + matcher.group(3) + "modulatorCustomName=\"Voice" + matcher.group(4) + "\"";
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        Files.write(path, sb.toString().getBytes(charset));
    }

    static void replaceVstIndex(Path path) throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        Pattern pattern = Pattern.compile("vstIndex=\"(\\d+)\"");
        String content = new String(Files.readAllBytes(path), charset);
        Matcher matcher = pattern.matcher(content);
        int end = 0;
        while (matcher.find(end)) {
            int value = Integer.parseInt(matcher.group(1));
            String string = "vstIndex=\"" + (value - 392) + "\"";
            end = matcher.end();
            if (value < 392) {
                continue;
            }
            content = content.replace(matcher.group(), string);
        }
        Files.write(path, content.getBytes(charset));
    }

    static void verifyUniqueVstIndexes(Path path) throws IOException, IllegalAccessException {
        ArrayList<Integer> indeces = new ArrayList<Integer>();
        Charset charset = StandardCharsets.UTF_8;
        Pattern pattern = Pattern.compile("vstIndex=\"(\\d+)\"");
        String content = new String(Files.readAllBytes(path), charset);
        Matcher matcher = pattern.matcher(content);
        ArrayList<Integer> duplicates = new ArrayList<Integer>();
        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            if (indeces.contains(value)) {
                duplicates.add(value);
            } else {
                indeces.add(value);
            }
        }

        if (duplicates.isEmpty()) {
            System.out.println("No duplicates were found!");
        } else {
            Collections.sort(indeces);
            System.out.println(String.format("Duplicates were found: %s. Max vst index is %d",
                    Arrays.toString(duplicates.toArray(new Integer[duplicates.size()])), indeces.get(indeces.size() - 1)));
        }
    }

    static void fixName(Path path) throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        Pattern pattern = Pattern.compile(" name=\"([^\"]+)\" ([^>]+)>([^>]+) componentVisibleName=\"([^\"]+)\"");
        String content = new String(Files.readAllBytes(path), charset);
        Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();

        System.out.println(sb.toString());
        while (matcher.find()) {
            if (matcher.group(1).startsWith("Voice")) {
                String group = matcher.group(4);
                String replacement = " name=\"" + group.replaceAll("\\s+", "") + "\" " + matcher.group(2) + ">" +
                        matcher.group(3) + " componentVisibleName=\"" + matcher.group(4) + "\"";
                matcher.appendReplacement(sb, replacement);
            }
        }
        matcher.appendTail(sb);
        Files.write(path, sb.toString().getBytes(charset));
    }

    static void moveNameToCustomName(Path path) throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        Pattern pattern = Pattern.compile("modulatorCustomName=\"\" ([^>]+) name=\"([^\"]+)\"");
        String content = new String(Files.readAllBytes(path), charset);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            content = matcher.replaceAll("modulatorCustomName=\"$2\" $1 name=\"$2\"");
        } else {
            System.out.println("Could not find pattern");
        }
        Files.write(path, content.getBytes(charset));
    }
}
