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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.cyclonedx.model.Bom;
import org.cyclonedx.model.Component;
import org.cyclonedx.model.ExternalReference;
import org.cyclonedx.model.Metadata;
import org.cyclonedx.model.Property;
import org.cyclonedx.model.Tool;

import com.lmco.efoss.sbom.commons.utils.DateUtils;
import com.lmco.efoss.sbom.commons.utils.ToolsUtils;
import com.lmco.efoss.sbom.comparator.dtos.ModifiedComponent;
import com.lmco.efoss.sbom.comparator.dtos.SBomDiff;
import com.lmco.efoss.sbom.comparator.exceptions.SBomComparatorException;

/**
 * (U) This class is used to generate an SBom containing only what has been added or modified from
 * the original SBom to the new SBom.
 * 
 * @author wrgoff
 * @since 24 February 2021
 */
public class SBomGenerator
{
	private static final Logger logger = Logger.getLogger(SBomGenerator.class.getName());
	
	private Bom newBom = new Bom();
	
	/**
	 * Constructor.
	 */
	public SBomGenerator()
	{}
	
	/**
	 * (U) This method is used to generate a new SBom containing the differences between the two
	 * SBom. It includes the metadata for the tools combined and the SBom Diff passed in.
	 * 
	 * @param bom1    Original SBom.
	 * @param bom2    New SBom.
	 * @param diffBom SBomDiff containing the changes between the two SBoms.
	 * @return Bom the newly created SBom, with only the new components, and or modified components.
	 *         It also contains metadata taken from both SBoms, such as the Tools.
	 * @throws SBomComparatorException in the event we are unable to generate the new SBom.
	 */
	public Bom geneateDiffBom(Bom bom1, Bom bom2, SBomDiff diffBom)
			throws SBomComparatorException
	{
		newBom.setSerialNumber(bom2.getSerialNumber());
		newBom.setVersion(bom2.getVersion());
		newBom.setXmlns(bom2.getXmlns());
		
		newBom.setMetadata(buildMetadata(bom1, bom2));
		
		addComponents(diffBom.getComponentsAdded());
		addModifiedComponents(diffBom.getModifiedComponents());
		
		return newBom;
	}
	
	/**
	 * (U) This method is used to add the added components to the new SBom.
	 * 
	 * @param components List of Diff Components to add to the new SBom.
	 */
	private void addComponents(
			List<com.lmco.efoss.sbom.comparator.dtos.CompareComponent> components)
	{
		Component component;
		for (com.lmco.efoss.sbom.comparator.dtos.CompareComponent comp : components)
		{
			component = comp.getComponent();
			if (component.getExternalReferences() == null)
				component.setExternalReferences(new ArrayList<ExternalReference>());
			
			component.setProperties(appendDiffProperty(component, "added"));
			
			newBom.addComponent(component);
		}
	}
	
	/**
	 * (U) This method is used to add the modified components to the SBom passed in.
	 * 
	 * @param components List of ModifiedComponents to add to the SBom passed in.
	 */
	private void addModifiedComponents(List<ModifiedComponent> components)
	{
		Component component;
		for (ModifiedComponent comp : components)
		{
			component = comp.getNewComponent().getComponent();
			if (component.getExternalReferences() == null)
				component.setExternalReferences(new ArrayList<ExternalReference>());
			
			component.setProperties(appendDiffProperty(component, "modified"));
			
			newBom.addComponent(component);
		}
	}
	
	/**
	 * Convenience method to append the Diff Property to a component's list of properties.
	 * 
	 * @param component Component to get the list of properties from.
	 * @param value     String value for the new diff property.
	 * @return List of Properties to append back on the Component.
	 */
	private List<Property> appendDiffProperty(Component component, String value)
	{
		List<Property> properties = component.getProperties();
		if (properties == null)
			properties = new ArrayList<>();
		properties.add(createDiffProperty(value));
		
		return properties;
	}
	
	/**
	 * (U) This method is used to build the metadata that will be used in the new SBom.
	 * 
	 * @param orgBom Original SBom, used to pull the Tools from.
	 * @param newBom Newer SBom, used to get component from, and merge the Tools with the original
	 *               SBom.
	 * @return Metadata for the diff SBom.
	 */
	private Metadata buildMetadata(Bom orgBom, Bom newBom)
	{
		Metadata metadata = new Metadata();
		metadata.setTimestamp(DateUtils.rightNowDate());
		
		List<Tool> tools = new ArrayList<>();
		
		tools.add(buildSBomTool("SBomComparator", "sbomcomparator-"));
		
		List<Tool> orgTools = new ArrayList<>();
		
		if (orgBom.getMetadata() != null)
		{
			orgTools = orgBom.getMetadata().getTools();
		}
		Component newComp = null;
		List<Tool> newTools = new ArrayList<>();
		
		if (newBom.getMetadata() != null)
		{
			newComp = newBom.getMetadata().getComponent();
			newTools = newBom.getMetadata().getTools();
		}
		
		tools = ToolsUtils.addUniqueTools(tools, newTools);
		tools = ToolsUtils.addUniqueTools(tools, orgTools);
		
		if (newComp != null)
		{
			metadata.setComponent(newComp);
		}
		metadata.setTools(tools);
		return metadata;
	}
	
	/**
	 * (U) This method is used to build this Tool.
	 * 
	 * @param name       String name of the tool.
	 * @param jarSubName String value of the prefix for the tools jar file name.
	 * @return newly created tool to represent this tool
	 */
	private Tool buildSBomTool(String name, String jarSubName)
	{
		Tool tool = new Tool();
		tool.setName(name);
		tool.setVendor("Lockheed Martin");
		
		String version = "unknown";
		try
		{
			String path = SBomGenerator.class.getProtectionDomain().getCodeSource().getLocation()
					.getPath();
			
			int index = path.indexOf(jarSubName);
			int endIndex = path.indexOf(".jar");
			
			version = path.substring(index + jarSubName.length(), endIndex);
			
			logger.debug("Version: " + version);
		}
		catch (Exception e)
		{
			logger.warn("Unable to determine verison of Tool!  Setting to " + version + ".");
		}
		tool.setVersion(version);
		return tool;
	}
	
	/**
	 * Convenience method to create the diff property.
	 * 
	 * @param value String value to assign to the diff property.
	 * @return Property newly created diff property.
	 */
	private Property createDiffProperty(String value)
	{
		Property diffProp = new Property();
		diffProp.setName("diff reason");
		diffProp.setValue(value);
		return diffProp;
	}
}
