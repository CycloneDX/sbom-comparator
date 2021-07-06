/*
 * Copyright (c) 2018,2019, 2020, 2021 Lockheed Martin Corporation.
 *
 * This work is owned by Lockheed Martin Corporation. Lockheed Martin personnel are permitted to use and
 * modify this software.  Lockheed Martin personnel may also deliver this source code to any US Government
 * customer Agency under a "US Government Purpose Rights" license.
 *
 * See the LICENSE file distributed with this work for licensing and distribution terms
 */
package com.lmco.efoss.sbom.comparator.generator;

import java.util.Date;

import javax.json.JsonObject;

import org.cyclonedx.model.Bom;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmco.efoss.sbom.commons.test.utils.Log4JTestWatcher;
import com.lmco.efoss.sbom.commons.test.utils.TestUtils;
import com.lmco.efoss.sbom.commons.utils.DateUtils;
import com.lmco.efoss.sbom.comparator.dtos.SBomDiff;
import com.lmco.efoss.sbom.comparator.utils.SBomCompareUtils;

/**
 * @author wrgoff
 *
 * @since 08 June 2021
 */
class SBomJSonDiffGeneratorTest
{
	private static final String LOG4J_FILE = "SBomJSonDiffGeneratorAppender.xml";
	
	@Rule
	public static Log4JTestWatcher watcher = new Log4JTestWatcher(LOG4J_FILE,
			"SBomJSonDiffGeneratorTest");
	
	/**
	 * (U) This method is used to test the JSon created from the SBomJSonDiffGenerator.
	 */
	@Test
	void testGenerateJson()
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
			
			int orgCompAdded = diff.getComponentsAdded().size();
			int orgCompDeleted = diff.getComponentsRemoved().size();
			int orgCompModified = diff.getModifiedComponents().size();
			
			SBomJSonDiffGenerator generator = new SBomJSonDiffGenerator(diff);
			String genJson = generator.toString();
			
			ObjectMapper objectMapper = new ObjectMapper();
			SBomDiff newDiff = objectMapper.readValue(genJson, SBomDiff.class);
			
			int newCompAdded = newDiff.getComponentsAdded().size();
			int newCompDeleted = newDiff.getComponentsRemoved().size();
			int newCompModified = newDiff.getModifiedComponents().size();
			
			Assert.assertTrue(((orgCompAdded == newCompAdded) &&
					(orgCompDeleted == newCompDeleted) &&
					(orgCompModified == newCompModified)));
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while attempt to produce JSon from an Object!";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) This method is used to test the JSon created from the SBomJSonDiffGenerator.
	 */
	@Test
	void testGenerateJson2()
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
			
			int orgCompAdded = diff.getComponentsAdded().size();
			int orgCompDeleted = diff.getComponentsRemoved().size();
			int orgCompModified = diff.getModifiedComponents().size();
			
			SBomJSonDiffGenerator generator = new SBomJSonDiffGenerator(diff);
			String genJson = generator.toJson(diff);
			
			ObjectMapper objectMapper = new ObjectMapper();
			SBomDiff newDiff = objectMapper.readValue(genJson, SBomDiff.class);
			
			int newCompAdded = newDiff.getComponentsAdded().size();
			int newCompDeleted = newDiff.getComponentsRemoved().size();
			int newCompModified = newDiff.getModifiedComponents().size();
			
			Assert.assertTrue(((orgCompAdded == newCompAdded) &&
					(orgCompDeleted == newCompDeleted) &&
					(orgCompModified == newCompModified)));
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while attempt to produce JSon from an Object!";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) This method is used to test the JSon created from the SBomJSonDiffGenerator.
	 */
	@Test
	void testGenerateJsonObject()
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
			
			int orgCompAdded = diff.getComponentsAdded().size();
			int orgCompDeleted = diff.getComponentsRemoved().size();
			int orgCompModified = diff.getModifiedComponents().size();
			
			SBomJSonDiffGenerator generator = new SBomJSonDiffGenerator(diff);
			JsonObject jsonObj = generator.toJsonObject();
			String genJson = jsonObj.toString();
			
			ObjectMapper objectMapper = new ObjectMapper();
			SBomDiff newDiff = objectMapper.readValue(genJson, SBomDiff.class);
			
			int newCompAdded = newDiff.getComponentsAdded().size();
			int newCompDeleted = newDiff.getComponentsRemoved().size();
			int newCompModified = newDiff.getModifiedComponents().size();
			
			Assert.assertTrue(((orgCompAdded == newCompAdded) &&
					(orgCompDeleted == newCompDeleted) &&
					(orgCompModified == newCompModified)));
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while attempt to produce JSon from an Object!";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
}
