/*
 * Copyright (c) 2018,2019 Lockheed Martin Corporation.
 *
 * This work is owned by Lockheed Martin Corporation. Lockheed Martin personnel are permitted to use and
 * modify this software.  Lockheed Martin personnel may also deliver this source code to any US Government
 * customer Agency under a "US Government Purpose Rights" license.
 *
 * See the LICENSE file distributed with this work for licensing and distribution terms
 */
package com.lmco.efoss.sbom.comparator;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.lmco.efoss.sbom.commons.test.utils.Log4JTestWatcher;
import com.lmco.efoss.sbom.commons.test.utils.TestUtils;
import com.lmco.efoss.sbom.commons.utils.DateUtils;
import com.lmco.efoss.sbom.comparator.dtos.SBomDiff;
import com.lmco.efoss.sbom.comparator.generator.SBomXmlDiffGenerator;

/**
 * (U) This unit test tests the main driver class for our Software Bill of Materials (SBOM)
 * Comparator.
 * 
 * @author wrgoff
 * @since 27 July 2020
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("sandbox")
class SbomcomparatorApplicationTest
{
	private static final String LOG4J_FILE = "SbomcomparatorApplicationAppender.xml";
	
	@Rule
	public static Log4JTestWatcher watcher = new Log4JTestWatcher(LOG4J_FILE,
			SbomcomparatorApplicationTest.class.getName());
	
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	
	/**
	 * Turns on stdErr output capture
	 */
	private void captureErr()
	{
		System.setErr(new PrintStream(errContent));
	}
	
	/**
	 * Turns on stdOut output capture
	 */
	private void captureOut()
	{
		System.setOut(new PrintStream(outContent));
	}
	
	/**
	 * Turns off stdErr capture and returns the contents that have been captured
	 *
	 * @return
	 */
	private String getErr()
	{
		System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		return errContent.toString().replaceAll("\r", "");
	}
	
	/**
	 * Turns off stdOut capture and returns the contents that have been captured
	 *
	 * @return String the contents of our stdOut.
	 */
	private String getOut()
	{
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		return outContent.toString().replaceAll("\r", "");
	}
	
	/**
	 * (U) This unit test tests for invalid usage.
	 */
	@Test
	void testBadFile()
	{
		String methodName = new Object()
		{}.getClass().getEnclosingMethod().getName();
		
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());
		
		String[] args = new String[]
		{ "-f1", "./test/BadFile.json", "-f2", "./test/ModifiedSbom.json" };
		try
		{
			Exception exception = Assert.assertThrows(IllegalStateException.class, () ->
			{
				SbomcomparatorApplication.main(args);
			});
			
			String expectedMessage = "File(./test/BadFile.json) does NOT exist!";
			String actualMessage = exception.getCause().getMessage();
			
			watcher.getLogger().debug("Actual Message: " + actualMessage);
			
			if (actualMessage.contains(expectedMessage))
				watcher.getLogger().debug("Failed as expected!");
			
			Assert.assertTrue(actualMessage.contains(expectedMessage));
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while running testInvalidUsage!";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) This method is used to test to make sure our help menu works. It does NOT check the
	 * contents, but check to make sure it begins "usage: help".
	 */
	@Test
	void testHelp()
	{
		String methodName = new Object()
		{}.getClass().getEnclosingMethod().getName();
		
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());
		
		String helpUsageText = "usage: help";
		
		try
		{
			captureOut();
			
			String[] args = new String[]
			{ "-h" };
			SbomcomparatorApplication.main(args);
			
			String theOutput = getOut();
			
			Assert.assertTrue(theOutput.contains(helpUsageText));
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while attempting to check to make sure our " +
					"help menu works!";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) This unit test tests for invalid output.
	 */
	@Test
	void testInvalidOutput()
	{
		String methodName = new Object()
		{}.getClass().getEnclosingMethod().getName();
		
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());
		
		String outputFileNamePrefix = "./test/diff";
		
		String[] args = new String[]
		{ "-f1", "./test/OrgSbom.json", "-f2", "./test/ModifiedSbom.json", "-o",
				outputFileNamePrefix, "-f", "js" };
		
		try
		{
			Exception exception = Assert.assertThrows(IllegalStateException.class, () ->
			{
				SbomcomparatorApplication.main(args);
			});
			
			String expectedMessage = "Unrecognized or unsupported output file format.  " +
					"Valid values are xml, json.";
			
			String actualMessage = exception.getCause().getMessage();
			
			watcher.getLogger().debug("Actual Message: " + actualMessage);
			
			if (actualMessage.contains(expectedMessage))
				watcher.getLogger().debug("Failed as expected!");
			
			Assert.assertTrue(actualMessage.contains(expectedMessage));
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while attempting to create a JSon SBom diff " +
					"file from two JSon SBoms.";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) This unit test tests the creation of a JSon SBom diff file from two JSon input files.
	 */
	@Test
	void testJSonSbomComparator()
	{
		String methodName = new Object()
		{}.getClass().getEnclosingMethod().getName();
		
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());
		
		String outputFileNamePrefix = "./test/diff";
		
		String[] args = new String[]
		{ "-f1", "./test/OrgSbom.json", "-f2", "./test/ModifiedSbom.json", "-o",
				outputFileNamePrefix, "-f", "json" };
		
		String outputFileName = outputFileNamePrefix + ".json";
		
		long expectedComponentsAdded = 73;
		long expectedComponentsRemoved = 2;
		long expectedComponentsModified = 12;
		
		String fileContent = null;
		try
		{
			SbomcomparatorApplication.main(args);
			
			fileContent = Files.readString(Paths.get(outputFileName), StandardCharsets.UTF_8);
			
			ObjectMapper mapper = new ObjectMapper();
			SBomDiff diff = mapper.readValue(fileContent, SBomDiff.class);
			
			if ((expectedComponentsAdded == diff.getComponentsAdded().size()) &&
					(expectedComponentsRemoved == diff.getComponentsRemoved().size()) &&
					(expectedComponentsModified == diff.getModifiedComponents().size()))
			{
				StringBuilder sb = new StringBuilder("Got Expected Results: \n");
				sb.append("	Components Added: " + expectedComponentsAdded + "\n");
				sb.append("	Components Removed: " + expectedComponentsRemoved + "\n");
				sb.append("	Components Modified: " + expectedComponentsModified + "\n");
				watcher.getLogger().debug(sb.toString());
			}
			else
			{
				StringBuilder sb = new StringBuilder("Did NOT get Expected Results: \n");
				sb.append("	Components Added (" + diff.getComponentsAdded().size() +
						"), expected: " + expectedComponentsAdded + "\n");
				sb.append("	Components Removed (" + diff.getComponentsRemoved().size() +
						"), expected: " + expectedComponentsRemoved + "\n");
				sb.append("	Components Modified (" + diff.getModifiedComponents().size() +
						"), expected: " + expectedComponentsModified + "\n");
				watcher.getLogger().error(sb.toString());
			}
			Assert.assertEquals("Components Added", expectedComponentsAdded,
					diff.getComponentsAdded().size());
			Assert.assertEquals("Components Removed", expectedComponentsRemoved,
					diff.getComponentsRemoved().size());
			Assert.assertEquals("Components Modified", expectedComponentsModified,
					diff.getModifiedComponents().size());
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while attempting to create a JSon SBom diff " +
					"file from two JSon SBoms.";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			try
			{
				Files.delete(Paths.get(outputFileName));
				Files.delete(Paths.get("sbomcompared.html"));
			}
			catch (Exception e)
			{
				watcher.getLogger().warn("Filed to cleanup output file (" + outputFileName + ").");
			}
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) This unit test tests the creation of a JSon SBom diff file from one Json and one XML
	 * input files.
	 */
//	@Test
	void testMixedSbomComparator()
	{
		String methodName = new Object()
		{}.getClass().getEnclosingMethod().getName();
		
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());
		
		String outputFileNamePrefix = "./test/diffMixed";
		
		String[] args = new String[]
		{ "-f1", "./test/OrgSbom.xml", "-f2", "./test/ModifiedSbom.json", "-o",
				outputFileNamePrefix, "-f", "json" };
		
		String outputFileName = outputFileNamePrefix + ".json";
		
		long expectedComponentsAdded = 73;
		long expectedComponentsRemoved = 2;
		long expectedComponentsModified = 12;
		
		String fileContent = null;
		try
		{
			SbomcomparatorApplication.main(args);
			
			fileContent = Files.readString(Paths.get(outputFileName), StandardCharsets.UTF_8);
			
			ObjectMapper mapper = new ObjectMapper();
			SBomDiff diff = mapper.readValue(fileContent, SBomDiff.class);
			
			if ((expectedComponentsAdded == diff.getComponentsAdded().size()) &&
					(expectedComponentsRemoved == diff.getComponentsRemoved().size()) &&
					(expectedComponentsModified == diff.getModifiedComponents().size()))
			{
				StringBuilder sb = new StringBuilder("Got Expected Results: \n");
				sb.append("	Components Added: " + expectedComponentsAdded + "\n");
				sb.append("	Components Removed: " + expectedComponentsRemoved + "\n");
				sb.append("	Components Modified: " + expectedComponentsModified + "\n");
				watcher.getLogger().debug(sb.toString());
			}
			else
			{
				StringBuilder sb = new StringBuilder("Did NOT get Expected Results: \n");
				sb.append("	Components Added (" + diff.getComponentsAdded().size() +
						"), expected: " + expectedComponentsAdded + "\n");
				sb.append("	Components Removed (" + diff.getComponentsRemoved().size() +
						"), expected: " + expectedComponentsRemoved + "\n");
				sb.append("	Components Modified (" + diff.getModifiedComponents().size() +
						"), expected: " + expectedComponentsModified + "\n");
				watcher.getLogger().error(sb.toString());
			}
			Assert.assertEquals("Components Added", expectedComponentsAdded,
					diff.getComponentsAdded().size());
			Assert.assertEquals("Components Removed", expectedComponentsRemoved,
					diff.getComponentsRemoved().size());
			Assert.assertEquals("Components Modified", expectedComponentsModified,
					diff.getModifiedComponents().size());
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while attempting to create a JSon SBom diff " +
					"file from two JSon SBoms.";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			try
			{
				Files.delete(Paths.get(outputFileName));
				Files.delete(Paths.get("sbomcompared.html"));
			}
			catch (Exception e)
			{
				watcher.getLogger().warn("Filed to cleanup output file (" + outputFileName + ").");
			}
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) This unit test tests for invalid usage.
	 */
	@Test
	void testNoFile()
	{
		String methodName = new Object()
		{}.getClass().getEnclosingMethod().getName();
		
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());
		
		String[] args = new String[]
		{ "-f1", "./test/OrgSbom.json", "-f2", "" };
		try
		{
			Exception exception = Assert.assertThrows(IllegalStateException.class, () ->
			{
				SbomcomparatorApplication.main(args);
			});
			
			String expectedMessage = "No file name priveded for";
			String actualMessage = exception.getCause().getMessage();
			
			watcher.getLogger().debug("Actual Message: " + actualMessage);
			
			if (actualMessage.contains(expectedMessage))
				watcher.getLogger().debug("Failed as expected!");
			
			Assert.assertTrue(actualMessage.contains(expectedMessage));
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while running test to throw error of " +
					"NO file!";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) This unit test tests for invalid usage.
	 */
	@Test
	void testNoFileOption()
	{
		String methodName = new Object()
		{}.getClass().getEnclosingMethod().getName();
		
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());
		
		String[] args = new String[]
		{ "-f1", "./test/OrgSbom.json" };
		try
		{
			Exception exception = Assert.assertThrows(IllegalStateException.class, () ->
			{
				SbomcomparatorApplication.main(args);
			});
			
			String expectedMessage = "You must provide a file for us to read the sbom from!";
			String actualMessage = exception.getCause().getMessage();
			
			watcher.getLogger().debug("Actual Message: " + actualMessage);
			
			if (actualMessage.contains(expectedMessage))
				watcher.getLogger().debug("Failed as expected!");
			
			Assert.assertTrue(actualMessage.contains(expectedMessage));
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while running test to throw error of " +
					"NO file!";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) This unit test tests for invalid usage. Fails in Docker container.
	 *
	 * @Test void testUnreadableFile() { String methodName = new Object()
	 *       {}.getClass().getEnclosingMethod().getName();
	 * 
	 *       Date startDate = DateUtils.rightNowDate();
	 * 		
	 *       TestUtils.logTestStart(methodName, watcher.getLogger());
	 * 
	 *       String unreadableFile = "./test/unreadableSbom.xml";
	 * 		
	 *       Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("-w--w--w-");
	 * 		
	 *       String[] args = new String[] { "-f1", unreadableFile, "-f2", "./test/ModifiedSbom.json"
	 *       };
	 * 
	 *       try { Files.setPosixFilePermissions(Paths.get(unreadableFile), permissions);
	 * 		
	 *       Exception exception = Assert.assertThrows(IllegalStateException.class, () -> {
	 *       SbomcomparatorApplication.main(args); });
	 * 
	 *       String expectedMessage = "Unable to read SBom from file"; String actualMessage =
	 *       exception.getCause().getMessage();
	 * 
	 *       watcher.getLogger().debug("Actual Message: " + actualMessage);
	 * 		
	 *       if (actualMessage.contains(expectedMessage)) watcher.getLogger().debug("Failed as
	 *       expected!");
	 * 
	 *       Assert.assertTrue(actualMessage.contains(expectedMessage)); } catch (Exception e) {
	 *       String error = "Unexpected error occured while running test to throw error of " +
	 *       "unreadable file!"; watcher.getLogger().error(error, e); Assert.fail(error); } finally
	 *       { try { // Reset Permissions permissions =
	 *       PosixFilePermissions.fromString("rw-rw-rw-");
	 *       Files.setPosixFilePermissions(Paths.get(unreadableFile), permissions); } catch
	 *       (Exception e) { watcher.getLogger().warn("Failed to rest the files permissions!"); }
	 *       TestUtils.logTestFinish(methodName, startDate, watcher.getLogger()); } }
	 */
	
	/**
	 * (U) This unit test tests the creation of an XML SBom diff file from two XML input files.
	 */
	@Test
	void testXmlSbomComparator()
	{
		String methodName = new Object()
		{}.getClass().getEnclosingMethod().getName();
		
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());
		
		String outputFileNamePrefix = "./test/diff";
		
		String[] args = new String[]
		{ "-f1", "./test/OrgSbom.xml", "-f2", "./test/ModifiedSbom.xml", "-o",
				outputFileNamePrefix, "-f", "xml" };
		
		String outputFileName = outputFileNamePrefix + ".xml";
		
		long expectedComponentsAdded = 73;
		long expectedComponentsRemoved = 2;
		long expectedComponentsModified = 12;
		
		String fileContent = null;
		try
		{
			SbomcomparatorApplication.main(args); // Generate the XML SBom Diff file.
			
			fileContent = Files.readString(Paths.get(outputFileName), StandardCharsets.UTF_8);
			
			XmlMapper mapper = new XmlMapper();
			SBomDiff diff = mapper.readValue(fileContent, SBomDiff.class);
			
			if ((expectedComponentsAdded == diff.getComponentsAdded().size()) &&
					(expectedComponentsRemoved == diff.getComponentsRemoved().size()) &&
					(expectedComponentsModified == diff.getModifiedComponents().size()))
			{
				StringBuilder sb = new StringBuilder("Got Expected Results: \n");
				sb.append("	Components Added: " + expectedComponentsAdded + "\n");
				sb.append("	Components Removed: " + expectedComponentsRemoved + "\n");
				sb.append("	Components Modified: " + expectedComponentsModified + "\n");
				watcher.getLogger().debug(sb.toString());
			}
			else
			{
				StringBuilder sb = new StringBuilder("Did NOT get Expected Results: \n");
				sb.append("	Components Added (" + diff.getComponentsAdded().size() +
						"), expected: " + expectedComponentsAdded + "\n");
				sb.append("	Components Removed (" + diff.getComponentsRemoved().size() +
						"), expected: " + expectedComponentsRemoved + "\n");
				sb.append("	Components Modified (" + diff.getModifiedComponents().size() +
						"), expected: " + expectedComponentsModified + "\n");
				watcher.getLogger().error(sb.toString());
			}
			Assert.assertEquals("Components Added", expectedComponentsAdded,
					diff.getComponentsAdded().size());
			Assert.assertEquals("Components Removed", expectedComponentsRemoved,
					diff.getComponentsRemoved().size());
			Assert.assertEquals("Components Modified", expectedComponentsModified,
					diff.getModifiedComponents().size());
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while attempting to create an XML SBom diff " +
					"file from two XML SBoms.";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			try
			{
				Files.delete(Paths.get(outputFileName));
				Files.delete(Paths.get("sbomcompared.html"));
			}
			catch (Exception e)
			{
				watcher.getLogger().warn("Filed to cleanup output file (" + outputFileName + ").");
			}
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) This unit test tests the creation of an XML SBom diff file from two XML input files with
	 * the html Output File Name set.
	 */
	@Test
	void when_cli_has_html_name_should_output_html_with_name()
	{
		String methodName = new Object()
		{}.getClass().getEnclosingMethod().getName();
		
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());
		
		String outputFileNamePrefix = "./test/diff";
		String htmlOutputFileNamePrefix = "./test/testHTMLdiff";
		
		String[] args = new String[]
		{ "-f1", "./test/OrgSbom.xml", "-f2", "./test/ModifiedSbom.xml", "-o",
				outputFileNamePrefix, "-f", "xml", "-t", htmlOutputFileNamePrefix };
		
		String outputFileName = outputFileNamePrefix + ".xml";
		String htmlOutputFileName = htmlOutputFileNamePrefix + ".html";
		
		long expectedComponentsAdded = 73;
		long expectedComponentsRemoved = 2;
		long expectedComponentsModified = 12;
		
		String fileContent = null;
		try
		{
			SbomcomparatorApplication.main(args); // Generate the XML SBom Diff file.
			
			fileContent = Files.readString(Paths.get(outputFileName), StandardCharsets.UTF_8);
			
			SBomXmlDiffGenerator generator = new SBomXmlDiffGenerator();
			
			ObjectMapper mapper = generator.getMapper();
			SBomDiff diff = mapper.readValue(fileContent, SBomDiff.class);
			
			if ((expectedComponentsAdded == diff.getComponentsAdded().size()) &&
					(expectedComponentsRemoved == diff.getComponentsRemoved().size()) &&
					(expectedComponentsModified == diff.getModifiedComponents().size()))
			{
				StringBuilder sb = new StringBuilder("Got Expected Results: \n");
				sb.append("	Components Added: " + expectedComponentsAdded + "\n");
				sb.append("	Components Removed: " + expectedComponentsRemoved + "\n");
				sb.append("	Components Modified: " + expectedComponentsModified + "\n");
				watcher.getLogger().debug(sb.toString());
			}
			else
			{
				StringBuilder sb = new StringBuilder("Did NOT get Expected Results: \n");
				sb.append("	Components Added (" + diff.getComponentsAdded().size() +
						"), expected: " + expectedComponentsAdded + "\n");
				sb.append("	Components Removed (" + diff.getComponentsRemoved().size() +
						"), expected: " + expectedComponentsRemoved + "\n");
				sb.append("	Components Modified (" + diff.getModifiedComponents().size() +
						"), expected: " + expectedComponentsModified + "\n");
				watcher.getLogger().error(sb.toString());
			}
			Assert.assertEquals("Components Added", expectedComponentsAdded,
					diff.getComponentsAdded().size());
			Assert.assertEquals("Components Removed", expectedComponentsRemoved,
					diff.getComponentsRemoved().size());
			Assert.assertEquals("Components Modified", expectedComponentsModified,
					diff.getModifiedComponents().size());
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while attempting to create an XML SBom diff " +
					"file from two XML SBoms with HTML filename set.";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			try
			{
				Files.delete(Paths.get(outputFileName));
				Files.delete(Paths.get(htmlOutputFileName));
			}
			catch (Exception e)
			{
				watcher.getLogger().warn("Filed to cleanup output file (" + outputFileName + ").");
			}
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
}
