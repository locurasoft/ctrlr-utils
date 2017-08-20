package com.locurasoft.panelcompiler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.locurasoft.panelcompiler.CtrlrPanelCompiler.Configurations.DEBUG;

/**
 * Hello world!
 */
public class CtrlrPanelCompiler {
    private final DocumentBuilder builder;
    private final Document document;
    private final XPath xPath;
    private final File panelFile;
    private final Node luaManagerMethods;

    enum Configurations {
        DEBUG,
        RELEASE
    }

    private static final Pattern REQUIRE_PATTERN = Pattern.compile("^\\s*require");
    private static final List<String> GENERICS_ORDER = Arrays.asList("LuaObject.lua", "lutils.lua", "mutils.lua", "cutils.lua", "Dispatcher.lua",
            "json4ctrlr.lua", "Logger.lua", "SyxMsg.lua", "AbstractController.lua", "AbstractBank.lua", "Queue.lua");
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

    public static void main(String[] args) throws Exception {
        CtrlrPanelCompiler ctrlrPanelCompiler = new CtrlrPanelCompiler(args[0]);

        Configurations config = DEBUG;
        if (args.length > 1) {
            config = Configurations.valueOf(args[1]);
        }

        ctrlrPanelCompiler.removeOldFunctions();
        ctrlrPanelCompiler.addGenerics(config);
        ctrlrPanelCompiler.addPanelFunctions(config);
        ctrlrPanelCompiler.saveDocument();
    }

    public CtrlrPanelCompiler(String xmlPath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        panelFile = new File(xmlPath);
        this.document = builder.parse(new FileInputStream(panelFile));
        xPath = XPathFactory.newInstance().newXPath();
        luaManagerMethods = (Node) xPath.compile("//luaManagerMethods").evaluate(document, XPathConstants.NODE);
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

    private Element newLuaMethod(String elementName, String name, Node parent) {
        parent.appendChild(document.createTextNode("\n"));
        Element luaMethod = document.createElement(elementName);
        luaMethod.setAttribute("uuid", newUuid());
        luaMethod.setAttribute("luaMethodName", name);
        luaMethod.setAttribute("luaMethodValid", "1");
        parent.appendChild(luaMethod);
        return luaMethod;
    }

    private Node newFileMethod(File file, Node group) {
        Element luaMethod = newLuaMethod("luaMethod", file.getName(), group);
        luaMethod.setAttribute("luaMethodSourcePath", file.getAbsolutePath());
        luaMethod.setAttribute("luaMethodSource", "1");
        return luaMethod;
    }

    private Node newTextMethod(String name, String code, Node group) {
        Element luaMethod = newLuaMethod("luaMethod", name, group);
        luaMethod.setAttribute("luaMethodCode", code);
        luaMethod.setAttribute("luaMethodSource", "0");
        return luaMethod;
    }

    private void newController(Configurations config, File file, Node group) throws IOException {
        String name = file.getName();
        String controllerClassName = name.substring(0, name.indexOf(".lua"));
        String controllerName = controllerClassName.substring(0, controllerClassName.indexOf("Controller")).toLowerCase();
        Node controllerGroup = newMethodGroup(controllerName, group);
        addMethod(config, file, controllerGroup);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));

            StringBuilder staticsBuilder = new StringBuilder();
            Pattern pattern = Pattern.compile(String.format("function %s:(on[^\\(]+)\\(([^\\)]*)\\)", controllerClassName), Pattern.DOTALL);
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(currentLine);
                if (matcher.find()) {
                    StringBuilder codeBuilder = new StringBuilder();
                    codeBuilder.append("function ").append(matcher.group(1)).append("(").append(matcher.group(2)).append(")\r\n");
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
                            codeBuilder.append("fine");
                            break;
                    }

                    codeBuilder.append("(").append(getLogJson(matcher.group(1), matcher.group(2))).append(")\r\n");
                    codeBuilder.append("\r\n");
                    codeBuilder.append("    ").append(controllerClassName.substring(0, 1).toLowerCase())
                            .append(controllerClassName.substring(1)).append(":").append(matcher.group(1)).append("(")
                            .append(matcher.group(2)).append(")\r\n");
                    codeBuilder.append("end\r\n");
                    newTextMethod(matcher.group(1), codeBuilder.toString(), controllerGroup);
                    staticsBuilder.append(codeBuilder.toString()).append("\r\n\r\n");
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

    private static final List<String> MODULATOR_LIST = Arrays.asList("m", "mod", "modulator");
    private static final List<String> COMPONENT_LIST = Arrays.asList("c", "comp", "component", "label", "lbl", "l");
    private static final List<String> VALUE_LIST = Arrays.asList("v", "val", "value", "tab", "index", "tabindex");
    private static final List<String> EVENT_LIST = Arrays.asList("e", "ev", "event");
    private static final List<String> FILE_LIST = Arrays.asList("f", "file");
    private static final List<String> SAMPLE_LIST = Arrays.asList("s", "sample", "samplename");
    private static final List<String> CONTENT_LIST = Arrays.asList("c", "cont", "content");
    private static final List<String> MIDI_LIST = Arrays.asList("mid", "midi");

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

    private void addGenerics(Configurations config) throws IOException {
        Node generic = newMethodGroup("generic");
        File panelFolder = panelFile.getParentFile();
        File rootFolder = panelFolder.getParentFile();
        File srcFolder = Paths.get(rootFolder.getAbsolutePath(), "Generic", "src").toFile();
        List<File> files = Arrays.asList(srcFolder.listFiles());
        Collections.sort(files, new FileComparator(GENERICS_ORDER));
        for (File file : files) {
            System.out.println("Adding " + file.getAbsolutePath());
            addMethod(config, file, generic);
        }
    }

    private void addMethod(Configurations config, File file, Node group) throws IOException {
        switch (config) {
            case DEBUG:
                newFileMethod(file, group);
                break;
            case RELEASE:
                String fileContents = getFileContents(config, file);
                String methodName = getMethodName(file);
                newTextMethod(methodName, fileContents, group);
                break;
        }
    }

    private String getMethodName(File file) {
        String name = file.getName();
        return name.substring(0, name.indexOf(".lua"));
    }

    private String getFileContents(Configurations config, File file) throws IOException {
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

    private void addStaticMethod(Configurations config, File file, Node group) throws IOException {
        String methodName = getMethodName(file);
        String fileContents = getFileContents(config, file);
        newTextMethod(methodName, fileContents, group);
    }

    private void addPanelFunctions(Configurations config) throws IOException {
        File panelFolder = panelFile.getParentFile();
        File srcFolder = Paths.get(panelFolder.getAbsolutePath(), "src").toFile();
        List<File> folders = Arrays.asList(srcFolder.listFiles());
        Collections.sort(folders, new FileComparator(GROUP_ORDER));
        for (File folder : folders) {
            addPanelFolder(config, folder);
        }
    }

    private void addPanelFolder(Configurations config, File folder) throws IOException {
        System.out.println("Adding " + folder.getAbsolutePath());
        Node node = newMethodGroup(folder.getName());
        List<File> files = Arrays.asList(folder.listFiles());
        if ("process".equals(folder.getName())) {
            Collections.sort(files, new ProcessFileComparator());
        }
        for (File file : files) {
            if (file.isDirectory()) {
                addPanelFolder(config, file);
            } else if ("controller".equals(folder.getName())) {
                if (file.getName().endsWith("Autogen.lua")) {
                    continue;
                } else if (file.getName().endsWith("Controller.lua")) {
                    newController(config, file, node);
                } else if (file.getName().endsWith(".lua")) {
                    // Static method
                    addStaticMethod(config, file, node);
                }
            } else {
                addMethod(config, file, node);
            }
        }
    }

    private void saveDocument() throws IOException, TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StreamResult result = new StreamResult(new File(panelFile.getParentFile(), panelFile.getName().replace(".panel", "-new.panel")));
        DOMSource domSource = new DOMSource(document);
        transformer.transform(domSource, result);
    }
}
