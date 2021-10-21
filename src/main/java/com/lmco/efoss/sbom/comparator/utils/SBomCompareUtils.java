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
import java.util.ArrayList;
import java.util.List;

import org.cyclonedx.model.Bom;

import com.lmco.efoss.sbom.commons.utils.StringUtils;
import com.lmco.efoss.sbom.comparator.dtos.CompareComponent;
import com.lmco.efoss.sbom.comparator.dtos.ModifiedComponent;
import com.lmco.efoss.sbom.comparator.dtos.SBomDiff;

/**
 * (U) This class is the Software Bill Of Materials (SBOM) utility class.
 * 
 * @author wrgoff
 * @since 9 July 2020
 */
public class SBomCompareUtils
{
	/**
	 * Base Constructor.
	 */
	private SBomCompareUtils()
	{}
	
	/**
	 * (U) This method is used to compare the components of two Software Bill Of Materials (SBoms).
	 * 
	 * @param orgSbom Bill of Materials (BOM) to compare to the next one passed in.
	 * @param newSbom Bill of Materials (BOM) to compare to the first one passed in.
	 * @return SBomDiff Object that contains the differences between the two SBoms.
	 */
	public static SBomDiff compareComponents(Bom orgSbom, Bom newSbom)
	{
		SBomDiff diff = new SBomDiff();
		
		List<org.cyclonedx.model.Component> orgComponents = orgSbom.getComponents();
		List<org.cyclonedx.model.Component> newComponents = newSbom.getComponents();
		
		diff.setComponentsAdded(componentsNotInList2(newComponents, orgComponents));
		diff.setComponentsRemoved(componentsNotInList2(orgComponents, newComponents));
		diff.setModifiedComponents(componentsModified(newComponents, orgComponents));
		
		return diff;
	}
	
	/**
	 * (U) This method is used to check to see if a Component exists in the list of components
	 * passed in.
	 * 
	 * @param componentToFind Component Object that we are attempting to see if is in the list of
	 *                        components passed in.
	 * @param componentList   List of Components to look for the component in.
	 * @return boolean return true if the component passed in is inside the list of components pass
	 *         in.
	 */
	public static boolean componentInList(org.cyclonedx.model.Component componentToFind,
			List<org.cyclonedx.model.Component> componentList)
	{
		for (org.cyclonedx.model.Component component : componentList)
		{
			if (componentsEqual(componentToFind, component))
				return true;
		}
		return false;
	}
	
	/**
	 * (U) This method is used to check the components to see if they are the same artifact. Ie
	 * Name, Group. We don't look at version here. That will be looked at separately.
	 * 
	 * @param comp1 Component 1 to compare to component 2.
	 * @param comp2 Component 2 to compare to component 1.
	 * @return boolean true if the two components have the same Name, and group.
	 */
	public static boolean componentsEqual(org.cyclonedx.model.Component comp1,
			org.cyclonedx.model.Component comp2)
	{
		boolean areEqual = false;
		
		String comp1Name = comp1.getName();
		String comp1Group = comp1.getGroup();
		
		String comp2Name = comp2.getName();
		String comp2Group = comp2.getGroup();
		
		if ((StringUtils.equals(comp1Name, comp2Name)) &&
				(StringUtils.equals(comp1Group, comp2Group)))
		{
			areEqual = true;
		}
		return areEqual;
	}
	
	/**
	 * (U) This method is used to get the list of components that have been modified.
	 * 
	 * @param newComponents List of new components to compare to the list of original components.
	 * @param orgComponents List of original components to compare to the list of new components.
	 * @return List of components that are in both lists (based on Name and Group), but their
	 *         version has changed.
	 */
	public static List<ModifiedComponent> componentsModified(
			List<org.cyclonedx.model.Component> newComponents,
			List<org.cyclonedx.model.Component> orgComponents)
	{
		List<ModifiedComponent> modifiedComps = new ArrayList<>();
		
		ModifiedComponent modifiedComp;
		org.cyclonedx.model.Component orgComponent;
		for (org.cyclonedx.model.Component newComponent : newComponents)
		{
			orgComponent = getComponentFromList(newComponent, orgComponents);
			if ((orgComponent != null) && (!StringUtils.equals(newComponent.getVersion(),
					orgComponent.getVersion())))
			{
					modifiedComp = new ModifiedComponent(new CompareComponent(orgComponent),
							new CompareComponent(newComponent));
					modifiedComps.add(modifiedComp);
			}
		}
		return modifiedComps;
	}
	
	/**
	 * (U) This method is used to get the list of components that are in List 1, but not in list 2.
	 * 
	 * @param list1 List of components to compare to the next list of components.
	 * @param list2 List of components to compare to the first list.
	 * @return List of components that are in List 1, but Not in list2.
	 */
	public static List<CompareComponent> componentsNotInList2(List<org.cyclonedx.model.Component> list1,
			List<org.cyclonedx.model.Component> list2)
	{
		List<CompareComponent> componentsNotInBothLists = new ArrayList<>();
		
		for (org.cyclonedx.model.Component component : list1)
		{
			if (!componentInList(component, list2))
				componentsNotInBothLists.add(new CompareComponent(component));
		}
		return componentsNotInBothLists;
	}
	
	/**
	 * (U) This method is used to pull a component from the list that has the same Name and Group as
	 * the component passed in.
	 * 
	 * @param componentWanted Component that contains the Name, and Group of the component we are
	 *                        looking for.
	 * @param components      List of Components that we are looking for the component in.
	 * @return Component that has the same name and group as the Component passed in. If we didn't
	 *         find the component it will return null.
	 */
	public static org.cyclonedx.model.Component getComponentFromList(
			org.cyclonedx.model.Component componentWanted,
			List<org.cyclonedx.model.Component> components)
	{
		for (org.cyclonedx.model.Component component : components)
		{
			if (componentsEqual(component, componentWanted))
				return component;
		}
		return null;
	}
}
