package com.locurasoft.aupresetor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.xpath.*;

final class PanelXmlUtils {

    private static final XPathFactory X_PATH_FACTORY = XPathFactory.newInstance();

    private PanelXmlUtils() {
        
    }

    static void setModulatorIntValue(Node node, int value) {
        Node modulatorValue = node.getAttributes().getNamedItem("modulatorValue");
        if (modulatorValue == null) {
            ((Element) node).setAttribute("modulatorValue", Integer.toString(value));
        } else {
            modulatorValue.setNodeValue(Integer.toString(value));
        }
    }

    static void setModulatorStringValue(Node node, String value) throws XPathExpressionException {
        XPathExpression expr = X_PATH_FACTORY.newXPath().compile("component");
        Node component = (Node) expr.evaluate(node, XPathConstants.NODE);
        Node modulatorValue = component.getAttributes().getNamedItem("uiLabelText");
        modulatorValue.setNodeValue(value);
    }

    static int getModulatorMax(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        Node modulatorMax = attributes.getNamedItem("modulatorMax");
        return Integer.parseInt(modulatorMax.getNodeValue());
    }

    static int getModulatorMin(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        Node modulatorMin = attributes.getNamedItem("modulatorMin");
        return Integer.parseInt(modulatorMin.getNodeValue());
    }

    static Node getNodeByVstIndex(Document document, int vstIndex) throws XPathExpressionException {
        XPathExpression expr = X_PATH_FACTORY.newXPath().compile(String.format("//modulator[@vstIndex=%d]", vstIndex));
        return (Node) expr.evaluate(document, XPathConstants.NODE);
    }

    static Node getNodeByCustIndex(Document document, int custIndex) throws XPathExpressionException {
        XPathExpression expr = X_PATH_FACTORY.newXPath().compile(String.format("//modulator[@modulatorCustomIndex=%d]", custIndex));
        return (Node) expr.evaluate(document, XPathConstants.NODE);
    }

    static Node getNodeByCustName(Document document, String custName) throws XPathExpressionException {
        XPathExpression expr = X_PATH_FACTORY.newXPath().compile(String.format("//modulator[@modulatorCustomName='%s']", custName));
        return (Node) expr.evaluate(document, XPathConstants.NODE);
    }

    static Node getNodeByName(Document document, String name) throws XPathExpressionException {
        XPathExpression expr = X_PATH_FACTORY.newXPath().compile(String.format("//modulator[@name='%s']", name));
        return (Node) expr.evaluate(document, XPathConstants.NODE);
    }

    static void cleanPanelTree(Document document) throws XPathExpressionException {
        XPath xpath = X_PATH_FACTORY.newXPath();
        XPathExpression expr = xpath.compile("/panel");
        Node panelNode = (Node) expr.evaluate(document, XPathConstants.NODE);
        NamedNodeMap attributes = panelNode.getAttributes();
        attributes.getNamedItem("panelMidiOutputDevice").setTextContent("-- None");
        attributes.getNamedItem("panelMidiInputDevice").setTextContent("-- None");
        attributes.getNamedItem("panelMidiControllerDevice").setTextContent("-- None");

        XPathExpression expr2 = xpath.compile("/panel/uiPanelEditor");
        Node uiPanelEditorNode = (Node) expr2.evaluate(document, XPathConstants.NODE);
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

    static void clonePanelResourcesToExport(Document document) throws XPathExpressionException {
        XPath xpath = X_PATH_FACTORY.newXPath();
        XPathExpression expr = xpath.compile("/panel");
        Node panelNode = (Node) expr.evaluate(document, XPathConstants.NODE);
        XPathExpression resourcesExpr = xpath.compile("/panel/panelResources");
        Node panelResourcesNode = (Node) resourcesExpr.evaluate(document, XPathConstants.NODE);
        Node cloneNode = panelResourcesNode.cloneNode(true);
        panelNode.appendChild(cloneNode);
        document.renameNode(cloneNode, null, "resourceExportList");
    }
}
