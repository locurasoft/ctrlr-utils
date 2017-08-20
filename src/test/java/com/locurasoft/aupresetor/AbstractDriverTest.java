package com.locurasoft.aupresetor;

import com.locurasoft.aupresetor.drivers.BehringerModulizer1200DSP;
import com.locurasoft.aupresetor.drivers.RolandD50;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AbstractDriverTest {
    private final DocumentBuilder builder;

    public AbstractDriverTest() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
    }

    @Test
    public void testGetNodeByVstIndex() throws Exception {
        Document doc = builder.parse(getClass().getResourceAsStream("/BehringerModulizer1200DSP.panel"));
        AbstractDriver.Data data = new AbstractDriver.Data(new File(""), doc);
        BehringerModulizer1200DSP tested = new BehringerModulizer1200DSP();
        Node nodeByVstIndex = tested.getNodeByVstIndex(data, 1);
        assertNotNull(nodeByVstIndex);
        NamedNodeMap attributes = nodeByVstIndex.getAttributes();
        assertNotNull(attributes);
        Node vstIndex = attributes.getNamedItem("vstIndex");
        assertNotNull(vstIndex);
        assertEquals("1", vstIndex.getNodeValue());
    }

    @Test
    public void testGetNodeByCustName() throws Exception {
        Document doc = builder.parse(getClass().getResourceAsStream("/RolandD50.panel"));
        AbstractDriver.Data data = new AbstractDriver.Data(new File(""), doc);
        RolandD50 tested = new RolandD50();
        Node nodeByCustName = tested.getNodeByCustName(data, "Voice384");
        assertNotNull(nodeByCustName);
        NamedNodeMap attributes = nodeByCustName.getAttributes();
        assertNotNull(attributes);
        Node custIndex = attributes.getNamedItem("modulatorCustomName");
        assertNotNull(custIndex);
        assertEquals("Voice384", custIndex.getNodeValue());
    }

    @Test
    public void testGetNodeByName() throws Exception {
        Document doc = builder.parse(getClass().getResourceAsStream("/RolandD50.panel"));
        AbstractDriver.Data data = new AbstractDriver.Data(new File(""), doc);
        RolandD50 tested = new RolandD50();
        Node nodeByName = tested.getNodeByName(data, "Voice384");
        assertNotNull(nodeByName);
        NamedNodeMap attributes = nodeByName.getAttributes();
        assertNotNull(attributes);
        Node name = attributes.getNamedItem("name");
        assertNotNull(name);
        assertEquals("Voice384", name.getNodeValue());
    }

    @Test
    public void testGetModulatorMaxMin() throws Exception {
        Document doc = builder.parse(getClass().getResourceAsStream("/RolandD50.panel"));
        AbstractDriver.Data data = new AbstractDriver.Data(new File(""), doc);
        RolandD50 tested = new RolandD50();
        Node nodeByCustName = tested.getNodeByCustName(data, "Voice384");

        int modulatorMax = tested.getModulatorMax(nodeByCustName);
        assertEquals(63, modulatorMax);
        int modulatorMin = tested.getModulatorMin(nodeByCustName);
        assertEquals(0, modulatorMin);
    }

}
