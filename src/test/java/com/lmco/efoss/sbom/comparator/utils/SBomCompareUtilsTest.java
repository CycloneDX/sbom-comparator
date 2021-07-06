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

import java.util.Date;

import org.cyclonedx.model.Bom;
import org.cyclonedx.model.Component;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.lmco.efoss.sbom.commons.test.utils.Log4JTestWatcher;
import com.lmco.efoss.sbom.commons.test.utils.TestUtils;
import com.lmco.efoss.sbom.commons.utils.DateUtils;
import com.lmco.efoss.sbom.comparator.dtos.SBomDiff;


/**
 * (U) This class contains the Unit test methods for the Software Bill of 
 * Materials (SBOM) utilities.
 * 
 * @author wrgoff
 * @since 9 July 2020
 */
public class SBomCompareUtilsTest
{
	private static final String LOG4J_FILE = "SBomCompareUtilsAppender.xml";
	
	@Rule
	public static Log4JTestWatcher watcher = new Log4JTestWatcher(LOG4J_FILE, 
			"SBomCompareUtilsTest");
	
	private static Bom originalSbom = null;
	private static Bom newSbom = null;
	private static Bom modifiedSbom = null;

	/**
	 * (U) For performance reasons, we are going to load the SBoms.
	 */
	@BeforeAll
	private static void setUp() throws Exception
	{
		TestUtils testUtils = new TestUtils();
		
		if(watcher.getLogger().isDebugEnabled())
			watcher.getLogger().debug("loading SBoms.");
		
		originalSbom = testUtils.readSbomFile("OrgSbom.xml", watcher.getLogger());
		newSbom = testUtils.readSbomFile("bom.xml", watcher.getLogger());
		modifiedSbom = testUtils.readSbomFile("ModifiedSbom.xml", watcher.getLogger());
	}
	
	/**
	 * (U) This test case is used to test to the Compare Components method of
	 * the SBomUtils class.  
	 */
	@Test
	void testCompareComponentsAdded()
	{
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());		
		try
		{
			if(watcher.getLogger().isDebugEnabled())
			{
				watcher.getLogger().debug("Org SBom contains " + 
						originalSbom.getComponents().size() + " components.");
				watcher.getLogger().debug("New SBon contains " + 
						newSbom.getComponents().size() + " components.");
			}
			
			SBomDiff diff = SBomCompareUtils.compareComponents(originalSbom, newSbom);
			
			if(watcher.getLogger().isDebugEnabled())
				watcher.getLogger().debug("Diff: " + diff.toString());
			
			int expected = (newSbom.getComponents().size() - 
					originalSbom.getComponents().size());
			
			if(watcher.getLogger().isDebugEnabled())
			{
				watcher.getLogger().debug("Should see " + expected + 
						" added Components in the diff (" + 
						diff.getComponentsAdded().size() + ")");
			}
			
			Assert.assertEquals(expected, diff.getComponentsAdded().size());			
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while attempt to compare " +
					"2 Sbom Files";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) This test case is used to test to the Compare Components method of
	 * the SBomUtils class.  
	 */
	@Test
	void testCompareComponentsChanged()
	{
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());		
		try
		{
			if(watcher.getLogger().isDebugEnabled())
			{
				watcher.getLogger().debug("SBom contains " + 
						originalSbom.getComponents().size() + " components.");
				watcher.getLogger().debug("Changed SBom contains " + 
						modifiedSbom.getComponents().size() + " components.");
			}
			
			SBomDiff diff = SBomCompareUtils.compareComponents(newSbom, 
					modifiedSbom);
			
			if(watcher.getLogger().isDebugEnabled())
				watcher.getLogger().debug("Diff: " + diff.toString());
			
			int expected = 20;
			
			if(watcher.getLogger().isDebugEnabled())
			{
				watcher.getLogger().debug("Should see " + expected + 
						" Modified Components in the diff (" + 
						diff.getModifiedComponents().size() + ")");
			}
			
			Assert.assertEquals(expected, diff.getModifiedComponents().size());			
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while attempt to compare " +
					"2 Sbom Files";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) This test case is used to test to the Compare Components method of
	 * the SBomUtils class.  
	 */
	@Test
	void testCompareComponentsDeleted()
	{
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());		
		try
		{
			if(watcher.getLogger().isDebugEnabled())
			{
				watcher.getLogger().debug("New SBon contains " + 
						newSbom.getComponents().size() + " components.");
				
				watcher.getLogger().debug("Org SBom contains " + 
						originalSbom.getComponents().size() + " components.");
			}
			
			//Switch the order and they become deleted.
			SBomDiff diff = SBomCompareUtils.compareComponents(newSbom, originalSbom);
			
			if(watcher.getLogger().isDebugEnabled())
				watcher.getLogger().debug("Diff: " + diff.toString());
			
			int expected = (newSbom.getComponents().size() - 
					originalSbom.getComponents().size());
			
			if(watcher.getLogger().isDebugEnabled())
			{
				watcher.getLogger().debug("Should see " + expected + 
						" deleted Components in the diff (" + 
						diff.getComponentsRemoved().size() + ")");
			}
			
			Assert.assertEquals(expected, diff.getComponentsRemoved().size());			
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while attempt to compare " +
					"2 Sbom Files";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) This test case is used to test that a list of components contains
	 * a specific Component.
	 */
	@Test
	void testComponentInList()
	{		
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());		
		try
		{
			Component component = new Component();
			component.setName("log4j");
			component.setVersion("1.2.12");
			component.setGroup("log4j");
						
			if (SBomCompareUtils.componentInList(component, 
					originalSbom.getComponents()))
			{
				Assert.fail("Component should be in List from Sbom!");
			}
			else
				watcher.getLogger().debug("Component in list, as expected!");
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while attempt to check " +
					"that component is in the list of componts from file!";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) This test case is used to test that a list of components does NOT 
	 * contain a specific Component.
	 */
	@Test
	void testComponentNotInList()
	{		
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());		
		try
		{
			Component component = new Component();
			component.setName("log4j");
			component.setVersion("1.2.10");  //We have 1.2.12.
			component.setGroup("log4j");
						
			if (SBomCompareUtils.componentInList(component, 
					originalSbom.getComponents()))
				Assert.fail("Component should NOT be in List from Sbom!");
			else
				watcher.getLogger().debug("Component not in list, as expected!");
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while attempt to check " +
					"that component is NOT in the list of componts from file!";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) This test case is used to test that two components are equal, if
	 * and only if their group, and name.
	 */
	@Test
	void testComponentsDoNotMatch()
	{		
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());		
		try
		{
			Component component1 = new Component();
			component1.setName("log4j");
			component1.setVersion("1.2.12");
			component1.setGroup("log4j");
			
			Component component2 = new Component();
			component2.setName("slf4j");
			component2.setVersion("1.2.12");
			component2.setGroup("slf4j");
			component2.setDescription("Component number 2");
			
			if (SBomCompareUtils.componentsEqual(component1, component2))
				Assert.fail("Components should NOT be equal!");
			else
				watcher.getLogger().debug("Components do NOT match, as expected!");
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while attempt to check " +
					"that components do NOT match!";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
	
	/**
	 * (U) This test case is used to test that two components are equal, if
	 * and only if their group, name, and version match.
	 */
	@Test
	void testComponentsMatch()
	{		
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		Date startDate = DateUtils.rightNowDate();
		
		TestUtils.logTestStart(methodName, watcher.getLogger());		
		try
		{
			Component component1 = new Component();
			component1.setName("log4j");
			component1.setVersion("1.2.12");
			component1.setGroup("log4j");
			
			Component component2 = new Component();
			component2.setName("log4j");
			component2.setVersion("1.2.12");
			component2.setGroup("log4j");
			component2.setDescription("Component number 2");
			
			if (SBomCompareUtils.componentsEqual(component1, component2))
				watcher.getLogger().debug("Components are equal, as expected!");
			else
			{
				Assert.fail("Components should be equal!");
			}
		}
		catch (Exception e)
		{
			String error = "Unexpected error occured while attempt to check " +
					"if components match.";
			watcher.getLogger().error(error, e);
			Assert.fail(error);
		}
		finally
		{
			TestUtils.logTestFinish(methodName, startDate, watcher.getLogger());
		}
	}
}
