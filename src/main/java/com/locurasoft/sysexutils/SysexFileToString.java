package com.locurasoft.sysexutils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SysexFileToString {

	public static void main(String[] args) throws IOException, URISyntaxException {
		File file = new File(args[0]);
		Path path = Paths.get(file.getAbsolutePath());
		byte[] bytes = Files.readAllBytes(path);
		System.out.println(SysexUtils.sysexToString(bytes));
	}
}
