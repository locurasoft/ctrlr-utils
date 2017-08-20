package com.locurasoft.aupresetor;

import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

public interface IDriver {

    void setInputFolder(String inputFolder);

    void setOutputFolder(String outputFolder);

    void generateFxps() throws Exception;

}
