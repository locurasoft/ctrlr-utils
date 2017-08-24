package com.locurasoft.aupresetor;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static com.locurasoft.aupresetor.PanelXmlUtils.*;

public abstract class AbstractPanelDriver implements IPanelDriver {
    protected static final String SYX = ".syx";
    protected static final String PANEL = ".panel";
    private final DocumentBuilder builder;
    private final Map<File, String> generatedFiles;
    private File inputFolder;
    private File outputFolder;

    public AbstractPanelDriver() throws ParserConfigurationException, XPathExpressionException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        generatedFiles = new HashMap<>();
    }

    Data newData(File input) throws IOException, SAXException {
        Class<? extends AbstractPanelDriver> klass = getClass();
        Document doc = builder.parse(klass.getResourceAsStream("/" + klass.getSimpleName() + ".panel"));
        return new Data(input, doc);
    }

    public void setInputFolder(String inputFolder) {
        this.inputFolder = new File(inputFolder);
        if (!this.inputFolder.exists() || !this.inputFolder.isDirectory()) {
            throw new IllegalArgumentException("Invalid input folder");
        }
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = new File(outputFolder);
        if (!this.outputFolder.exists() || !this.outputFolder.isDirectory()) {
            throw new IllegalArgumentException("Invalid output folder");
        }
    }

    @Override
    public Map<File, String> generatePanels() throws Exception {
        File[] files = inputFolder.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.getName().endsWith("syx")) {
                Data data = newData(file);
                generateFxp(data);
            }
        }
        return generatedFiles;
    }

    private void saveFile(Data data, File folder, String filename, String patchName) throws TransformerException, IOException, InterruptedException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Failed to create parent folder!");
        }
        File panelFile = new File(folder, filename);
        Result panel = new StreamResult(panelFile);
        Source xml = new DOMSource(data.document);

        transformer.transform(xml, panel);

        generatedFiles.put(panelFile, patchName);
    }

    protected void saveFile(Data data, String filename) throws InterruptedException, TransformerException, IOException {
        saveFile(data, outputFolder, filename, "Default CTRLR program");
    }

    protected void saveFile(Data data, String bankName, String filename, String patchName) throws InterruptedException, TransformerException, IOException {
        saveFile(data, new File(outputFolder, bankName), filename, patchName);
    }

    protected void saveFile(Data data, String filename, String patchName) throws TransformerException, IOException, InterruptedException {
        saveFile(data, outputFolder, filename, patchName);
    }

    protected abstract void generateFxp(Data data) throws Exception;

    protected void setModIntValue(Node node, int value) {
        setModulatorIntValue(node, value);
    }

    protected void setModStringValue(Node node, String value) throws XPathExpressionException {
        setModulatorStringValue(node, value);
    }

    protected int modulatorMax(Node node) {
        return getModulatorMax(node);
    }

    protected int modulatorMin(Node node) {
        return getModulatorMin(node);
    }

    protected Node nodeByVstIndex(Data data, int vstIndex) throws XPathExpressionException {
        return getNodeByVstIndex(data.document, vstIndex);
    }

    protected Node nodeByCustIndex(Data data, int custIndex) throws XPathExpressionException {
        return getNodeByCustIndex(data.document, custIndex);
    }

    protected Node nodeByCustName(Data data, String custName) throws XPathExpressionException {
        return getNodeByCustName(data.document, custName);
    }

    protected Node nodeByName(Data data, String name) throws XPathExpressionException {
        return getNodeByName(data.document, name);
    }

    protected void cleanTree(Data data) throws XPathExpressionException {
        cleanPanelTree(data.document);
    }

    protected void cloneResourcesToExport(Data data) throws XPathExpressionException {
        clonePanelResourcesToExport(data.document);
    }

    public static class Data {
        private final File input;
        private final Document document;

        public Data(File input, Document document) {
            this.input = input;
            this.document = document;
        }

        public byte[] getInputBytes() throws IOException {
            return IOUtils.toByteArray(new FileInputStream(input));
        }

        public File input() {
            return input;
        }

        public String toString() {
            try {
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                StreamResult result = new StreamResult(new StringWriter());
                DOMSource source = new DOMSource(document);
                transformer.transform(source, result);
                return result.getWriter().toString();
            } catch (TransformerException e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }
    }

}
