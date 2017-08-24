package com.locurasoft.aupresetor.drivers;

import com.google.common.io.Files;
import com.locurasoft.aupresetor.AbstractPanelDriver;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Ignore
public class RolanD50Test {

    @Test
    public void testGenerateFxp_Bank() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        URL resource = RolanD50Test.class.getResource("/BobbyBluz_1.syx");
        Document doc = builder.parse(RolanD50Test.class.getResourceAsStream("/RolandD50.panel"));

        AbstractPanelDriver.Data data = new AbstractPanelDriver.Data(Paths.get(resource.toURI()).toFile(), doc);
        RolandD50 tested = new RolandD50();
        File myTempDir = Files.createTempDir();
        System.out.println("Writing to " + myTempDir.getAbsolutePath());
        tested.setOutputFolder(myTempDir.getAbsolutePath());
        tested.generateFxp(data);
        assertEquals(64*2, myTempDir.list().length);
    }

    @Test
    public void testGenerateFxp_Patch() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        URL resource = RolanD50Test.class.getResource("/5th of 50.syx");
        Document doc = builder.parse(RolanD50Test.class.getResourceAsStream("/RolandD50.panel"));

        AbstractPanelDriver.Data data = new AbstractPanelDriver.Data(Paths.get(resource.toURI()).toFile(), doc);
        RolandD50 tested = new RolandD50();
        File myTempDir = Files.createTempDir();
        System.out.println("Writing to " + myTempDir.getAbsolutePath());
        tested.setOutputFolder(myTempDir.getAbsolutePath());
        tested.generateFxp(data);
        assertEquals(1*2, myTempDir.list().length);
    }

}
