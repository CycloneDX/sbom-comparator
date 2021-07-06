/*
 * Copyright (c) 2018,2019 Lockheed Martin Corporation.
 *
 * This work is owned by Lockheed Martin Corporation. Lockheed Martin personnel are permitted to use and
 * modify this software.  Lockheed Martin personnel may also deliver this source code to any US Government
 * customer Agency under a "US Government Purpose Rights" license.
 *
 * See the LICENSE file distributed with this work for licensing and distribution terms
 */
package com.lmco.efoss.sbom.comparator.builder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmco.efoss.sbom.commons.test.utils.Log4JTestWatcher;
import com.lmco.efoss.sbom.commons.test.utils.TestUtils;
import com.lmco.efoss.sbom.commons.utils.DateUtils;
import com.lmco.efoss.sbom.comparator.SbomcomparatorApplication;
import com.lmco.efoss.sbom.comparator.dtos.SBomDiff;

class HtmlBuilderTest
{
	private HtmlBuilder htmlBuilder = new HtmlBuilder();
	
	private static final String LOG4J_FILE = "HtmlBuilderAppender.xml";
	private static final String HTMLOUTPUT = "htmloutput";
	private static final String FORMAT = null;
	private static final String OUTPUT = null;
	
	@Rule
	public static Log4JTestWatcher watcher = new Log4JTestWatcher(LOG4J_FILE,
			HtmlBuilderTest.class.getName());
	
	/**
	 * (U) This unit test tests the creation of an SBom diff against the HTML file created in the
	 * HtmlBuilder class. More of a inegrations test since it does not test actual methods. Also
	 * tests that the html file was made with custom name
	 */
	@Test
	void when_main_method_ran_with_correct_inputs_HTML_Builder_should_create_HTML_wth_same_amount_of_components()
	{
		String methodName = new Object()
		{}.getClass().getEnclosingMethod().getName();
		
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());
		
		String outputFileNamePrefix = "./test/diff";
		
		String[] args = new String[]
		{ "-f1", "./test/OrgSbom.json", "-f2", "./test/ModifiedSbom.json", "-o",
				outputFileNamePrefix, "-f", "json", "-t", outputFileNamePrefix };
		
		String outputFileName = outputFileNamePrefix + ".json";
		
		String fileContent = null;
		try
		{
			SbomcomparatorApplication.main(args);
			
			fileContent = Files.readString(Paths.get(outputFileName), StandardCharsets.UTF_8);
			
			ObjectMapper mapper = new ObjectMapper();
			SBomDiff diff = mapper.readValue(fileContent, SBomDiff.class);
			
			FileReader fr = new FileReader(outputFileNamePrefix + ".html");
			BufferedReader br = new BufferedReader(fr);
			
			StringBuilder content = new StringBuilder();
			String s;
			
			while ((s = br.readLine()) != null)
			{
				content.append(s);
			}
			s = content.toString();
			
			int added = StringUtils.countOccurrencesOf(s, "Added</");
			int modified = StringUtils.countOccurrencesOf(s, "Modified<");
			int removed = StringUtils.countOccurrencesOf(s, "Removed<");
			
			Assert.assertEquals("Components Added", added, diff.getComponentsAdded().size());
			Assert.assertEquals("Components Removed", removed, diff.getComponentsRemoved().size());
			Assert.assertEquals("Components Modified", modified,
					diff.getModifiedComponents().size());
			Assert.assertNotNull(outputFileNamePrefix + ".html");
			br.close();
			fr.close();
			
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
				Files.delete(Paths.get(outputFileNamePrefix + ".html"));
			}
			catch (Exception e)
			{
				watcher.getLogger().warn("Filed to cleanup output file (" + outputFileName + ").");
			}
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) Tests the name retrieval of the the html file, in this case that when no name is entered
	 * that the default is used
	 */
	@Test
	void when_no_name_for_HTML_files_should_return_default_HTML_name()
	{
		String methodName = new Object()
		{}.getClass().getEnclosingMethod().getName();
		
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());
		
		Options cliOptions = new Options();
		cliOptions.addOption(
				new Option("h", "help", false, "will print out the command line " + "options."));
		cliOptions.addOption(new Option("f1", "orgsbom", true, "original SBom file"));
		cliOptions.addOption(new Option("f2", "newsbom", true, "new SBom file"));
		cliOptions.addOption(
				new Option("o", OUTPUT, true,
						"(Optional) output file name, default is diff.json or diff.xml"));
		cliOptions.addOption(new Option("f", FORMAT, true,
				"(Optional) output file format, Valid values json, xml.  Default is xml"));
		cliOptions.addOption(
				new Option("t", HTMLOUTPUT, true,
						"(Optional) output html file name, default name is sbomcompared"));
		
		String[] testArgs = new String[]
		{ "-f1", "./test/OrgSbom.xml", "-f2", "./test/ModifiedSbom.xml" };
		CommandLineParser cliParser = new DefaultParser();
		CommandLine cli = null;
		try
		{
			cli = cliParser.parse(cliOptions, testArgs);
			String htmlName = htmlBuilder.getHtmlOutputFileName(cli);
			Assert.assertEquals(htmlName, "sbomcompared");
		}
		catch (ParseException e)
		{
			watcher.getLogger().error("Filed to HTMLName test default case with error :" + e);
			Assert.fail();
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) Tests the name retrieval of the the html file, in this case that when a name is entered
	 * that the name is used
	 */
	@Test
	void when_name_for_HTML_files_should_return_correct_HTML_name()
	{
		String methodName = new Object()
		{}.getClass().getEnclosingMethod().getName();
		
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());
		
		Options cliOptions = new Options();
		cliOptions.addOption(
				new Option("h", "help", false, "will print out the command line " + "options."));
		cliOptions.addOption(new Option("f1", "orgsbom", true, "original SBom file"));
		cliOptions.addOption(new Option("f2", "newsbom", true, "new SBom file"));
		cliOptions.addOption(
				new Option("o", OUTPUT, true,
						"(Optional) output file name, default is diff.json or diff.xml"));
		cliOptions.addOption(new Option("f", FORMAT, true,
				"(Optional) output file format, Valid values json, xml.  Default is xml"));
		cliOptions.addOption(
				new Option("t", HTMLOUTPUT, true,
						"(Optional) output html file name, default name is sbomcompared"));
		String htmlOutputFileNamePrefix = "./test/htmlDiff";
		
		String[] testArgs = new String[]
		{ "-f1", "./test/OrgSbom.xml", "-f2", "./test/ModifiedSbom.xml", "-t",
				htmlOutputFileNamePrefix };
		
		CommandLineParser cliParser = new DefaultParser();
		CommandLine cli = null;
		try
		{
			cli = cliParser.parse(cliOptions, testArgs);
			
			String htmlName = htmlBuilder.getHtmlOutputFileName(cli);
			Assert.assertEquals(htmlName, htmlOutputFileNamePrefix);
		}
		catch (ParseException e)
		{
			watcher.getLogger().error("Filed to HTMLName test dynamic case with error :" + e);
			Assert.fail();
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
}
