package com.locurasoft.panelcompiler;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.locurasoft.panelcompiler.CtrlrPanelCompiler.Configurations.DEBUG;

/**
 * Hello world!
 */
public class CtrlrPanelCompiler {

    private static final List<String> MODULATOR_LIST = Arrays.asList("m", "mod", "modulator");
    private static final List<String> COMPONENT_LIST = Arrays.asList("c", "comp", "component", "label", "lbl", "l");
    private static final List<String> VALUE_LIST = Arrays.asList("v", "val", "value", "tab", "index", "tabindex");
    private static final List<String> EVENT_LIST = Arrays.asList("e", "ev", "event");
    private static final List<String> FILE_LIST = Arrays.asList("f", "file");
    private static final List<String> SAMPLE_LIST = Arrays.asList("s", "sample", "samplename");
    private static final List<String> CONTENT_LIST = Arrays.asList("c", "cont", "content");
    private static final List<String> MIDI_LIST = Arrays.asList("mid", "midi");

    private final Document document;
    private final File panelFile;
    private final Node luaManagerMethods;
    private final Configurations config;
    private final Node uiPanelEditor;

    enum Configurations {
        DEBUG,
        RELEASE
    }

    private static final Pattern REQUIRE_PATTERN = Pattern.compile("^\\s*require");
    private static final List<String> GENERICS_ORDER = Arrays.asList("LuaObject.lua", "lutils.lua", "mutils.lua", "cutils.lua", "Dispatcher.lua",
            "json4ctrlr.lua", "Logger.lua", "SyxMsg.lua", "AbstractController.lua", "DefaultControllerBase.lua", "AbstractBank.lua", "Queue.lua",
            "EffectParamService.lua");
    private static final List<String> GROUP_ORDER = Arrays.asList("message", "model", "service", "controller");

    static class FileComparator implements Comparator<File> {
        private final List<String> nameList;

        FileComparator(List<String> nameList) {
            this.nameList = nameList;
        }

        @Override
        public int compare(File o1, File o2) {
            return nameList.indexOf(o1.getName()) - nameList.indexOf(o2.getName());
        }
    }

    static class ProcessFileComparator implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            int i1 = "Process.lua".equals(o1.getName()) ? 1 : 0;
            int i2 = "Process.lua".equals(o2.getName()) ? 1 : 0;
            return i2 - i1;
        }
    }

    private static final String[] PANELS = {
            "AkaiS2000/Akai-S2000.panel",
            "BehringerModulizer1200DSP/Behringer-Modulizer-1200DSP.panel",
            "EmuProteus2/Emu-Proteus2.panel",
            "EnsoniqEsq1/Ensoniq-ESQ1.panel",
            "RolandD50/Roland-D50.panel",
            "RolandJV1080/Roland-JV1080.panel",
            "YamahaCS1x/Yamaha-CS1x.panel",
            "YamahaDX7/Yamaha-DX7.panel"
    };

    /**
     * Main method
     */
    public static void main(String[] args) throws Exception {
        Configurations config = DEBUG;
        if (args.length > 1) {
            config = Configurations.valueOf(args[1]);
        }

        if (args[0].endsWith(".panel")) {
            compilePanel(args[0], config);
        } else {
            for (String panelPath : PANELS) {
                Path path = Paths.get(args[0], panelPath);
                compilePanel(path.toFile().getAbsolutePath(), config);
            }
        }
    }

    private static void compilePanel(String panelPath, Configurations config) throws Exception {
        System.out.println("Compiling " + panelPath);
        CtrlrPanelCompiler ctrlrPanelCompiler = new CtrlrPanelCompiler(panelPath, config);

        ctrlrPanelCompiler.removeOldFunctions();
        ctrlrPanelCompiler.addGenerics();
        ctrlrPanelCompiler.addPanelFunctions();
        ctrlrPanelCompiler.saveDocument();
    }

    private CtrlrPanelCompiler(String xmlPath, Configurations config) throws Exception {
        this.config = config;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        panelFile = new File(xmlPath);
        this.document = builder.parse(new FileInputStream(panelFile));
        XPath xPath = XPathFactory.newInstance().newXPath();
        luaManagerMethods = (Node) xPath.compile("//luaManagerMethods").evaluate(document, XPathConstants.NODE);
        uiPanelEditor = (Node) xPath.compile("//uiPanelEditor").evaluate(document, XPathConstants.NODE);
    }

    private void removeOldFunctions() {
        NodeList childNodes = luaManagerMethods.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.hasAttributes()) {
                continue;
            }
            Node name = item.getAttributes().getNamedItem("name");
            if (name == null || !"Built-In".equalsIgnoreCase(name.getTextContent())) {
                luaManagerMethods.removeChild(item);
            }
        }
    }

    private String newUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }

    private Node newMethodGroup(String name) {
        return newMethodGroup(name, luaManagerMethods);
    }

    private Node newMethodGroup(String name, Node parent) {
        parent.appendChild(document.createTextNode("\n"));
        Element luaMethodGroup = document.createElement("luaMethodGroup");
        luaMethodGroup.setAttribute("uuid", newUuid());
        luaMethodGroup.setAttribute("name", name);
        parent.appendChild(luaMethodGroup);
        return luaMethodGroup;
    }

    private Element newLuaMethod(String name, Node parent) {
        parent.appendChild(document.createTextNode("\n"));
        Element luaMethod = document.createElement("luaMethod");
        luaMethod.setAttribute("uuid", newUuid());
        luaMethod.setAttribute("luaMethodName", name);
        luaMethod.setAttribute("luaMethodValid", "1");
        parent.appendChild(luaMethod);
        return luaMethod;
    }

    private void newFileMethod(File file, Node group) {
        Element luaMethod = newLuaMethod(file.getName(), group);
        luaMethod.setAttribute("luaMethodSourcePath", file.getAbsolutePath());
        luaMethod.setAttribute("luaMethodSource", "1");
    }

    private void newTextMethod(String name, String code, Node group) {
        Element luaMethod = newLuaMethod(name, group);
        luaMethod.setAttribute("luaMethodCode", code);
        luaMethod.setAttribute("luaMethodSource", "0");
    }

    private void addGenerics() throws IOException {
        Node generic = newMethodGroup("generic");
        List<File> files = getFolderList(getGenericSrcDir());
        files.sort(new FileComparator(GENERICS_ORDER));
        for (File file : files) {
            System.out.println("Adding " + file.getAbsolutePath());
            addMethod(file, generic);
        }
    }

    private File getGenericSrcDir() {
        File panelFolder = panelFile.getParentFile();
        File rootFolder = panelFolder.getParentFile();
        return Paths.get(rootFolder.getAbsolutePath(), "Generic", "src").toFile();
    }

    private void addMethod(File file, Node group) throws IOException {
        switch (config) {
            case DEBUG:
                newFileMethod(file, group);
                break;
            case RELEASE:
                String fileContents = getFileContents(file);
                String methodName = getMethodName(file);
                newTextMethod(methodName, fileContents, group);
                break;
        }
    }

    private String getMethodName(File file) {
        String name = file.getName();
        return name.substring(0, name.indexOf(".lua"));
    }

    private String getFileContents(File file) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String currentLine;
            StringBuilder codeBuilder = new StringBuilder();
            while ((currentLine = reader.readLine()) != null) {
                switch (config) {
                    case DEBUG:
                        codeBuilder.append(currentLine).append("\r\n");
                        break;
                    case RELEASE:
                        Matcher matcher = REQUIRE_PATTERN.matcher(currentLine);
                        if (!matcher.find()) {
                            codeBuilder.append(currentLine).append("\r\n");
                        }
                        break;
                }
            }
            return codeBuilder.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private void addStaticMethod(File file, Node group) throws IOException {
        String methodName = getMethodName(file);
        String fileContents = getFileContents(file);
        newTextMethod(methodName, fileContents, group);
    }

    private List<File> getFolderList(File dir) {
        File[] listFiles = dir.listFiles();
        assert listFiles != null;
        return Arrays.asList(listFiles);
    }

    private void addPanelFunctions() throws IOException {
        File panelFolder = panelFile.getParentFile();
        File srcFolder = Paths.get(panelFolder.getAbsolutePath(), "src").toFile();
        List<File> folders = getFolderList(srcFolder);
        folders.sort(new FileComparator(GROUP_ORDER));
        for (File folder : folders) {
            addPanelFolder(folder);
        }
    }

    private void addPanelFolder(File folder) throws IOException {
        System.out.println("Adding " + folder.getAbsolutePath());
        Node node = newMethodGroup(folder.getName());
        List<File> files = getFolderList(folder);
        if ("process".equals(folder.getName())) {
            files.sort(new ProcessFileComparator());
        }
        for (File file : files) {
            if (file.isDirectory()) {
                addPanelFolder(file);
            } else if ("controller".equals(folder.getName())) {
                if (file.getName().endsWith("Controller.lua")) {
                    new ControllerParser(file, node).parse();
                } else if (file.getName().endsWith(".lua")
                        && !file.getName().endsWith("Autogen.lua")) {
                    // Static method
                    addStaticMethod(file, node);
                }
            } else {
                addMethod(file, node);
            }
        }
    }

    private void saveDocument() throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StreamResult result = new StreamResult(new File(panelFile.getParentFile(), panelFile.getName().replace(".panel", "-new.panel")));
        DOMSource domSource = new DOMSource(document);
        transformer.transform(domSource, result);
    }

    class ControllerParser {

        private final File file;
        private final Node parentNode;
        private final String controllerName;
        private final String controllerClassName;
        private final Pattern callbackPattern;
        private final Pattern loadFromfilePattern;
        private final Pattern defaultControllerBasePattern;
        private final Pattern dcbCallbackPattern;
        private final Pattern dcbLoadFromfilePattern;

        ControllerParser(File file, Node parent) {
            this.file = file;
            this.parentNode = parent;
            String name = file.getName();
            controllerClassName = name.substring(0, name.indexOf(".lua"));
            controllerName = controllerClassName.substring(0, controllerClassName.indexOf("Controller")).toLowerCase();
            callbackPattern = Pattern.compile(String.format("function %s:(on[^\\(]+)\\(([^\\)]*)\\)", controllerClassName), Pattern.DOTALL);
            dcbCallbackPattern = Pattern.compile("function DefaultControllerBase:(on[^(]+)\\(([^)]*)\\)", Pattern.DOTALL);
            loadFromfilePattern = Pattern.compile(String.format("function %s:(loadVoiceFromFile[^\\(]*)\\(file\\)", controllerClassName), Pattern.DOTALL);
            dcbLoadFromfilePattern = Pattern.compile("function DefaultControllerBase:(loadVoiceFromFile[^(]*)\\(file\\)", Pattern.DOTALL);
            defaultControllerBasePattern = Pattern.compile("^\\s*__index\\s*=\\s*DefaultControllerBase");
        }

        private void parse() throws IOException {
            Node controllerGroup = newMethodGroup(controllerName, parentNode);
            addMethod(file, controllerGroup);
            BufferedReader reader = null;
            HashSet<String> addedFunctions = new HashSet<>();
            try {
                reader = new BufferedReader(new FileReader(file));

                StringBuilder staticsBuilder = new StringBuilder();
                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    Matcher callbackMatcher = callbackPattern.matcher(currentLine);
                    if (callbackMatcher.find()) {
                        addCallback(addedFunctions, callbackMatcher, controllerGroup, staticsBuilder);
                    } else if (loadFromfilePattern.matcher(currentLine).find()) {
                        appendOnFilesDropped(addedFunctions, controllerClassName, parentNode, staticsBuilder);
                    } else if (defaultControllerBasePattern.matcher(currentLine).find()) {
                        BufferedReader dcbReader = new BufferedReader(new FileReader(new File(getGenericSrcDir(), "DefaultControllerBase.lua")));
                        String currentLine2;
                        while ((currentLine2 = dcbReader.readLine()) != null) {
                            Matcher callbackMatcher2 = dcbCallbackPattern.matcher(currentLine2);
                            if (callbackMatcher2.find()) {
                                addCallback(addedFunctions, callbackMatcher2, controllerGroup, staticsBuilder);
                            } else if (dcbLoadFromfilePattern.matcher(currentLine2).find()) {
                                appendOnFilesDropped(addedFunctions, controllerClassName, parentNode, staticsBuilder);
                            }
                        }
                    }
                }
                if (!staticsBuilder.toString().isEmpty()) {
                    FileOutputStream fos = new FileOutputStream(new File(file.getParentFile(), file.getName().replace(".lua", "Autogen.lua")));
                    fos.write(staticsBuilder.toString().getBytes());
                    fos.flush();
                    fos.close();
                }
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }

        private void addCallback(Set<String> addedFunctions, Matcher callbackMatcher, Node controllerGroup, StringBuilder staticsBuilder) {
            if (addedFunctions.contains(callbackMatcher.group(1))) {
                return;
            }
            String methodString = getMethodString(controllerClassName, callbackMatcher.group(1), callbackMatcher.group(2));
            newTextMethod(callbackMatcher.group(1), methodString, controllerGroup);
            staticsBuilder.append(methodString).append("\r\n\r\n");
            addedFunctions.add(callbackMatcher.group(1));
        }

        private void appendOnFilesDropped(Set<String> addedFunctions, String controllerClassName, Node group, StringBuilder staticsBuilder) {
            String codeBuilder = "---\r\n-- Callback to indicate that the user has dropped the files onto this panel\r\n" +
                    "--\r\n-- @files   - StringArray object that has the file paths\r\n-- @x       - x coordinate where the event occured" +
                    "-- @y       - y coordinate where the event occured\r\n" +
                    "function onFilesDroppedToPanel(files, x, y)\r\n    if files:size() > 0 then\r\n        local f = File(files:get(0))\r\n" +
                    String.format("        %s%s:loadVoiceFromFile(f)\r\n", controllerClassName.substring(0, 1).toLowerCase(), controllerClassName.substring(1)) + "    end\r\nend\r\n";
            newTextMethod("onFilesDroppedToPanel", codeBuilder, group);
            NamedNodeMap attributes = uiPanelEditor.getAttributes();
            attributes.getNamedItem("luaPanelFileDragDropHandler").setTextContent("onFilesDroppedToPanel");
            staticsBuilder.append(codeBuilder).append("\r\n\r\n");
            addedFunctions.add("onFilesDroppedToPanel");
        }

        private final List<String> RETURN_CALLBACKS = Arrays.asList("onGetValueForMIDI", "onGetValueFromMIDI");

        private boolean isReturnCallback(String methodName) {
            return RETURN_CALLBACKS.contains(methodName);
        }

        private String getMethodString(String controllerClassName, String methodName, String methodArgs) {
            StringBuilder codeBuilder = new StringBuilder();
            codeBuilder.append("function ").append(methodName).append("(").append(methodArgs).append(")\r\n");
            codeBuilder.append("    -- This variable stops index issues during panel bootup\r\n");
            codeBuilder.append("    if panel:getBootstrapState() or panel:getProgramState() then\r\n");
            codeBuilder.append("      return\r\n");
            codeBuilder.append("    end\r\n");
            codeBuilder.append("\r\n");
            codeBuilder.append("    LOGGER:");
            switch (config) {
                case DEBUG:
                    codeBuilder.append("info");
                    break;
                case RELEASE:
                    codeBuilder.append("warn");
//                    codeBuilder.append("fine");
                    break;
            }

            codeBuilder.append("(").append(getLogJson(methodName, methodArgs)).append(")\r\n");
            codeBuilder.append("\r\n");
            codeBuilder.append("    ");
            if (isReturnCallback(methodName)) {
                codeBuilder.append("return ");
            }
            codeBuilder.append(controllerClassName.substring(0, 1).toLowerCase())
                    .append(controllerClassName.substring(1)).append(":").append(methodName).append("(")
                    .append(methodArgs).append(")\r\n");
            codeBuilder.append("end\r\n");
            return codeBuilder.toString();
        }

        private String getLogJson(String methodName, String args) {
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("\"\\\"{").append("'methodName':'").append(methodName).append("'");

            ArrayList<String> varargs = new ArrayList<>();
            String[] argList = args.split(",");
            for (String arg : argList) {
                String trimmedArg = arg.trim();
                if (MODULATOR_LIST.contains(trimmedArg.toLowerCase())) {
                    jsonBuilder.append(", 'modulator':'%s'");
                    varargs.add(trimmedArg + ":getProperty('name')");
                } else if (COMPONENT_LIST.contains(trimmedArg.toLowerCase())) {
                    jsonBuilder.append(", 'component':'%s'");
                    if (methodName.equals("onPadSelected")) {
                        varargs.add(trimmedArg + ":getProperty('componentGroupName'):sub(0, " + trimmedArg +
                                ":getProperty('componentGroupName'):find(\"-grp\") - 1)");
                    } else {
                        varargs.add(trimmedArg + ":getOwner():getProperty('name')");
                    }
                } else if (VALUE_LIST.contains(trimmedArg.toLowerCase())) {
                    jsonBuilder.append(", 'value':%s");
                    varargs.add(trimmedArg);
                } else if (EVENT_LIST.contains(trimmedArg.toLowerCase())) {
                    // TODO
                } else if (MIDI_LIST.contains(trimmedArg.toLowerCase())) {
                    jsonBuilder.append(", 'midi':'%s'");
                    varargs.add(trimmedArg + ":getData():toHexString(1)");
                } else if (FILE_LIST.contains(trimmedArg.toLowerCase())) {
                    jsonBuilder.append(", 'file':'%s'");
                    varargs.add(trimmedArg + ":getFullPathName()");
                } else if (SAMPLE_LIST.contains(trimmedArg.toLowerCase())) {
                    jsonBuilder.append(", 'sample':'%s'");
                    varargs.add(trimmedArg);
                } else if (CONTENT_LIST.contains(trimmedArg.toLowerCase())) {
                    jsonBuilder.append(", 'content':'%s'");
                    varargs.add(trimmedArg);
                } else {
                    throw new IllegalArgumentException("Invalid arg: " + trimmedArg);
                }
            }
            jsonBuilder.append("},\\\" ..\"");
            for (String vararg : varargs) {
                jsonBuilder.append(", ").append(vararg);
            }
            return jsonBuilder.toString();
        }

    }
}
