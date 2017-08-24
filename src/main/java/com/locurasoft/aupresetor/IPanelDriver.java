package com.locurasoft.aupresetor;

import java.io.File;
import java.util.Map;

public interface IPanelDriver {

    void setInputFolder(String inputFolder);

    void setOutputFolder(String outputFolder);

    Map<File, String> generatePanels() throws Exception;
}
