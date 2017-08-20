package com.locurasoft.aupresetor;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
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

public abstract class AbstractDriver implements IDriver {
    public static final String SYX = ".syx";
    public static final String PANEL = ".panel";
    private final DocumentBuilder builder;
    private final XPath xpath;
    private File inputFolder;
    private File outputFolder;

    private static final String MOD_VAL_EXPR = "";
    private static final String MOD_MIN_EXPR = "";
    private static final String MOD_MAX_EXPR = "";
    private static final String MOD_VST_INDEX_EXPR = "";
    private static final String MOD_CUST_INDEX_EXPR = "";

    public AbstractDriver() throws ParserConfigurationException, XPathExpressionException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        XPathFactory xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
    }

    Data newData(File input) throws IOException, SAXException {
        Class<? extends AbstractDriver> klass = getClass();
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

    public void generateFxps() throws Exception {
        File[] files = inputFolder.listFiles();
        for (File file : files) {
            if (file.getName().endsWith("syx")) {
                Data data = newData(file);
                generateFxp(data);
            }
        }
    }

    protected void saveFile(Data data, String filename) throws TransformerException, IOException, InterruptedException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        File outputFile = new File(outputFolder, filename);
        Result output = new StreamResult(outputFile);
        Source input = new DOMSource(data.document);

        transformer.transform(input, output);

        String[] cmd = {"cmd.exe", "/C", String.format("Ctrlr-Debug-Win32.exe --panelFile=\"%s\"", outputFile)};
        if (ProcessUtil.execProcess(cmd)) {
            System.out.println("Done!");
        } else {
            System.out.println("fxp2aupreset failed!");
        }

    }

    protected abstract void generateFxp(Data data) throws IOException, XPathExpressionException, TransformerException, InterruptedException, Exception;

    protected void setModulatorIntValue(Node node, int value) {
        Node modulatorValue = node.getAttributes().getNamedItem("modulatorValue");
        if (modulatorValue == null) {
            ((Element) node).setAttribute("modulatorValue", Integer.toString(value));
        } else {
            modulatorValue.setNodeValue(Integer.toString(value));
        }
    }

    protected void setModulatorStringValue(Node node, String value) throws XPathExpressionException {
        XPathExpression expr = xpath.compile("component");
        Node component = (Node) expr.evaluate(node, XPathConstants.NODE);
        Node modulatorValue = component.getAttributes().getNamedItem("uiLabelText");
        modulatorValue.setNodeValue(value);
    }

    protected int getModulatorMax(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        Node modulatorMax = attributes.getNamedItem("modulatorMax");
        return Integer.parseInt(modulatorMax.getNodeValue());
    }

    protected int getModulatorMin(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        Node modulatorMin = attributes.getNamedItem("modulatorMin");
        return Integer.parseInt(modulatorMin.getNodeValue());
    }

    protected Node getNodeByVstIndex(Data data, int vstIndex) throws XPathExpressionException {
        XPathExpression expr = xpath.compile(String.format("//modulator[@vstIndex=%d]", vstIndex));
        return (Node) expr.evaluate(data.document, XPathConstants.NODE);
    }

    protected Node getNodeByCustIndex(Data data, int custIndex) throws XPathExpressionException {
        XPathExpression expr = xpath.compile(String.format("//modulator[@modulatorCustomIndex=%d]", custIndex));
        return (Node) expr.evaluate(data.document, XPathConstants.NODE);
    }

    protected Node getNodeByCustName(Data data, String custName) throws XPathExpressionException {
        XPathExpression expr = xpath.compile(String.format("//modulator[@modulatorCustomName='%s']", custName));
        return (Node) expr.evaluate(data.document, XPathConstants.NODE);
    }

    protected Node getNodeByName(Data data, String name) throws XPathExpressionException {
        XPathExpression expr = xpath.compile(String.format("//modulator[@name='%s']", name));
        return (Node) expr.evaluate(data.document, XPathConstants.NODE);
    }

    protected void cleanTree(Data data) throws XPathExpressionException {
        XPathExpression expr = xpath.compile("/panel");
        Node panelNode = (Node) expr.evaluate(data.document, XPathConstants.NODE);
        NamedNodeMap attributes = panelNode.getAttributes();
        attributes.getNamedItem("panelMidiOutputDevice").setTextContent("-- None");
        attributes.getNamedItem("panelMidiInputDevice").setTextContent("-- None");
        attributes.getNamedItem("panelMidiControllerDevice").setTextContent("-- None");

        XPathExpression expr2 = xpath.compile("/panel/uiPanelEditor");
        Node uiPanelEditorNode = (Node) expr.evaluate(data.document, XPathConstants.NODE);
        if (uiPanelEditorNode != null) {
            attributes = uiPanelEditorNode.getAttributes();
            Node uiPanelMenuBarVisible = attributes.getNamedItem("uiPanelMenuBarVisible");
            if (uiPanelMenuBarVisible == null) {
                ((Element)uiPanelEditorNode).setAttribute("uiPanelMenuBarVisible", "0");
            } else {
                uiPanelMenuBarVisible.setTextContent("0");
            }

        }
    }

    protected void cloneResourcesToExport(Data data) throws XPathExpressionException {
        XPathExpression expr = xpath.compile("/panel");
        Node panelNode = (Node) expr.evaluate(data.document, XPathConstants.NODE);
        XPathExpression resourcesExpr = xpath.compile("/panel/panelResources");
        Node panelResourcesNode = (Node) resourcesExpr.evaluate(data.document, XPathConstants.NODE);
        Node cloneNode = panelResourcesNode.cloneNode(true);
        panelNode.appendChild(cloneNode);
        data.document.renameNode(cloneNode, null, "resourceExportList");
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