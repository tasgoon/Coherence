package org.tasgo.coherence.client;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasgo.coherence.Coherence;
import org.tasgo.coherence.Library;

import cpw.mods.fml.common.FMLCommonHandler;

public class PostCohere { //This class undoes everything Cohering did to revert it to a normal state
	private final Logger logger = LogManager.getLogger("Coherence");
	
	public PostCohere() {
		try {
			startCohereUndoer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getJarFilename() {
		String codeSource = Coherence.class.getProtectionDomain().getCodeSource().getLocation().toString();
    	codeSource = codeSource.replace("jar:file:/", "");
    	return codeSource.substring(0, codeSource.indexOf("!"));
    	
	}
	
	public static JavaCommandBuilder getCohereUndoer() throws IOException {
		JavaCommandBuilder cmdBuilder = new JavaCommandBuilder();
        String[] approvedClassFiles = {"commons-io", "logging", "gson"};
        for (String classFile : ManagementFactory.getRuntimeMXBean().getClassPath().split(";")) { //Load commons-io and logging
        	for (String approvedClassFile : approvedClassFiles) {
        		if (classFile.contains(approvedClassFile))
        			cmdBuilder.classPath.add(classFile);
        	}
        }
        cmdBuilder.classPath.add(getJarFilename());
        
        cmdBuilder.mainClass = CohereUndoer.class.getName();
        cmdBuilder.programArgs.add(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        
        return cmdBuilder;
	}
	
	public static Process startCohereUndoer() throws IOException {
		return getCohereUndoer().launch();
	}
	
	public static void setupTestEnvironment() throws IOException {
		String cmd = getCohereUndoer().constructCommand();
		System.out.println(cmd);
		StringSelection selection = new StringSelection(cmd);
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(selection, selection);
	}
}