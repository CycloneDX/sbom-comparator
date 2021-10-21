/*
 * Copyright (c) 2018,2019 Lockheed Martin Corporation.
 *
 * This work is owned by Lockheed Martin Corporation. Lockheed Martin personnel are permitted to use and
 * modify this software.  Lockheed Martin personnel may also deliver this source code to any US Government
 * customer Agency under a "US Government Purpose Rights" license.
 *
 * See the LICENSE file distributed with this work for licensing and distribution terms
 */
package com.lmco.efoss.sbom.comparator.utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.cyclonedx.model.Bom;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmco.efoss.sbom.commons.test.utils.Log4JTestWatcher;
import com.lmco.efoss.sbom.commons.test.utils.TestUtils;
import com.lmco.efoss.sbom.commons.utils.DateUtils;
import com.lmco.efoss.sbom.comparator.dtos.SBomDiff;
import com.lmco.efoss.sbom.comparator.generator.SBomJSonDiffGenerator;
import com.lmco.efoss.sbom.comparator.generator.SBomXmlDiffGenerator;

/**
 * (U) This class contains our test cases for the SBom File Utilities.
 * 
 * @author wrgoff
 * @since 23 July 2020
 */
class SBomDiffFileUtilsTest
{
	private static final String LOG4J_FILE = "SBomDiffFileUtilsAppender.xml";
	
	@Rule
	public static Log4JTestWatcher watcher = new Log4JTestWatcher(LOG4J_FILE,
			"SBomDiffFileUtilsTest");
		
	/**
	 * (U) This method is used to test printing a SBomDiff Object to a file.
	 */
	@Test
	void sBomDiffFileUtilsJsonTest()
	{
		String methodName = new Object()
		{}.getClass().getEnclosingMethod().getName();
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());
		try
		{
			TestUtils testUtils = new TestUtils();
			
			Bom originalSbom = testUtils.readSbomFile("OrgSbom.xml", watcher.getLogger());
			Bom newSbom = testUtils.readSbomFile("ModifiedSbom.xml", watcher.getLogger());
			
			SBomDiff diff = SBomCompareUtils.compareComponents(originalSbom, newSbom);
			
			String sbomDiffFileName = "./test/tempSbomDiff";
			SBomDiffFileUtils.generateOutputFile(diff, "json", sbomDiffFileName);
			String content = Files.readString(Paths.get(sbomDiffFileName + ".json"),
					StandardCharsets.UTF_8);
			
			int orgCompAdded = diff.getComponentsAdded().size();
			int orgCompDeleted = diff.getComponentsRemoved().size();
			int orgCompModified = diff.getModifiedComponents().size();
			
			SBomJSonDiffGenerator generator = new SBomJSonDiffGenerator(diff);
			
			ObjectMapper objectMapper = generator.getMapper();
			SBomDiff newDiff = objectMapper.readValue(content, SBomDiff.class);
			
			int newCompAdded = newDiff.getComponentsAdded().size();
			int newCompDeleted = newDiff.getComponentsRemoved().size();
			int newCompModified = newDiff.getModifiedComponents().size();
			
			Assert.assertTrue(((orgCompAdded == newCompAdded) &&
					(orgCompDeleted == newCompDeleted) &&
					(orgCompModified == newCompModified)));
			
			Files.delete(Paths.get(sbomDiffFileName + ".json")); // Clean up.
		}
		catch (Exception e)
		{
			String error = "Unexpected error occurred while attempting to write an SBomDiff to a file!";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) This method is used to test printing a SBomDiff Object to a file.
	 */
	@Test
	void sBomDiffFileUtilsNoNameJsonTest()
	{
		String methodName = new Object()
		{}.getClass().getEnclosingMethod().getName();
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());
		try
		{
			String sbomDiffFileName = null;
			
			TestUtils testUtils = new TestUtils();
			
			Bom originalSbom = testUtils.readSbomFile("OrgSbom.xml", watcher.getLogger());
			Bom newSbom = testUtils.readSbomFile("ModifiedSbom.xml", watcher.getLogger());
			
			SBomDiff diff = SBomCompareUtils.compareComponents(originalSbom, newSbom);
			
			SBomDiffFileUtils.generateOutputFile(diff, "json", sbomDiffFileName);
			String content = Files.readString(Paths.get("./diff.json"),
					StandardCharsets.UTF_8);
			
			int orgCompAdded = diff.getComponentsAdded().size();
			int orgCompDeleted = diff.getComponentsRemoved().size();
			int orgCompModified = diff.getModifiedComponents().size();
			
			SBomJSonDiffGenerator generator = new SBomJSonDiffGenerator(diff);
			
			ObjectMapper objectMapper = generator.getMapper();
			SBomDiff newDiff = objectMapper.readValue(content, SBomDiff.class);
			
			int newCompAdded = newDiff.getComponentsAdded().size();
			int newCompDeleted = newDiff.getComponentsRemoved().size();
			int newCompModified = newDiff.getModifiedComponents().size();
			
			Assert.assertTrue(((orgCompAdded == newCompAdded) &&
					(orgCompDeleted == newCompDeleted) &&
					(orgCompModified == newCompModified)));
			
			Files.delete(Paths.get("diff.json")); // Clean up.
		}
		catch (Exception e)
		{
			String error = "Unexpected error occurred while attempting to write an SBomDiff to a file!";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) This method is used to test printing a SBomDiff Object to a file.
	 */
	@Test
	void sBomDiffFileUtilsXmlTest()
	{
		String methodName = new Object()
		{}.getClass().getEnclosingMethod().getName();
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());
		try
		{
			String sbomDiffFileName = "./test/tempSbomDiff";
			
			TestUtils testUtils = new TestUtils();
			
			Bom originalSbom = testUtils.readSbomFile("OrgSbom.xml", watcher.getLogger());
			Bom newSbom = testUtils.readSbomFile("ModifiedSbom.xml", watcher.getLogger());
			
			SBomDiff diff = SBomCompareUtils.compareComponents(originalSbom, newSbom);
			
			SBomDiffFileUtils.generateOutputFile(diff, "xml", sbomDiffFileName);
			String content = Files.readString(Paths.get(sbomDiffFileName + ".xml"),
					StandardCharsets.UTF_8);
			
			int orgCompAdded = diff.getComponentsAdded().size();
			int orgCompDeleted = diff.getComponentsRemoved().size();
			int orgCompModified = diff.getModifiedComponents().size();
			
			SBomXmlDiffGenerator generator = new SBomXmlDiffGenerator(diff);
			
			ObjectMapper mapper = generator.getMapper();
			SBomDiff newDiff = mapper.readValue(content, SBomDiff.class);
			
			int newCompAdded = newDiff.getComponentsAdded().size();
			int newCompDeleted = newDiff.getComponentsRemoved().size();
			int newCompModified = newDiff.getModifiedComponents().size();
			
			Assert.assertTrue(((orgCompAdded == newCompAdded) &&
					(orgCompDeleted == newCompDeleted) &&
					(orgCompModified == newCompModified)));
			
			Files.delete(Paths.get(sbomDiffFileName + ".xml")); // Clean up.
		}
		catch (Exception e)
		{
			String error = "Unexpected error occurred while attempting to write an SBomDiff to an XML file!";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
}
