package com.locurasoft.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ZipUtils {
    private final String srcFolder;
    private List<String> fileList;

    private ZipUtils(File srcFolder) {
        fileList = new ArrayList<>();
        this.srcFolder = srcFolder.getAbsolutePath();
    }

    public static File zipFolder(File srcFolder, String outputFilename) {
        return zipFolder(srcFolder, null, outputFilename);
    }

    public static File zipFolder(File srcFolder, FileFilter filter, String outputFilename) {
        ZipUtils zipUtils = new ZipUtils(srcFolder);
        zipUtils.generateFileList(srcFolder, filter);
        File output = new File(srcFolder.getParent(), outputFilename);
        zipUtils.zipIt(output.getAbsolutePath());
        return output;
    }

    /**
     * Zip it
     *
     * @param zipFile output ZIP file location
     */
    void zipIt(String zipFile) {

        byte[] buffer = new byte[1024];

        try {

            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            System.out.println("Output to Zip : " + zipFile);

            for (String file : this.fileList) {

                System.out.println("File Added : " + file);
                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);

                FileInputStream in =
                        new FileInputStream(srcFolder + File.separator + file);

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                in.close();
            }

            zos.closeEntry();
            //remember close it
            zos.close();

            System.out.println("Done");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Traverse a directory and get all files,
     * and add the file into fileList
     *
     * @param node file or directory
     * @param filter
     */
    void generateFileList(File node, FileFilter filter) {

        //add file only
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
        }

        if (node.isDirectory()) {
            File[] subNote = node.listFiles(filter);
            assert subNote != null;
            for (File file : subNote) {
                generateFileList(new File(node, file.getName()), filter);
            }
        }

    }

    /**
     * Format the file path for zip
     *
     * @param file file path
     * @return Formatted file path
     */
    private String generateZipEntry(String file) {
        return file.substring(srcFolder.length() + 1, file.length());
    }
}
