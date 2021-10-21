/*
 * Copyright (c) 2018,2019 Lockheed Martin Corporation.
 *
 * This work is owned by Lockheed Martin Corporation. Lockheed Martin personnel are permitted to use and
 * modify this software.  Lockheed Martin personnel may also deliver this source code to any US Government
 * customer Agency under a "US Government Purpose Rights" license.
 *
 * See the LICENSE file distributed with this work for licensing and distribution terms
 */
package com.lmco.efoss.sbom.comparator.dtos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * (U) This data transfer object (DTO) is used to hold the differences between
 * 2 Software Bill Of Materials.
 * 
 * @author wrgoff
 * @since 9 July 2020
 */
public class SBomDiff implements Serializable
{
	private static final long serialVersionUID = 7667375060373133784L;
	
	private List<CompareComponent> componentsAdded = new ArrayList<>();
	private List<CompareComponent> componentsRemoved = new ArrayList<>();
	private List<ModifiedComponent> modifiedComponents = new ArrayList<>();
	
	/**
	 * (U) Convenience method used to add a component to the list of new 
	 * components added.
	 * @param component Component to add to the list new components.
	 */
	public void addComponentAdded(CompareComponent component)
	{
		if(component != null)
			componentsAdded.add(component);
	}

	/**
	 * (U) Convenience method used to add a modified component to the list of  
	 * modified components.
	 * @param component ModifiedComponent to add to the list of modified 
	 * components.
	 */
	public void addModifiedComponent(ModifiedComponent component)
	{
		if(component != null)
			modifiedComponents.add(component);
	}
	
	/**
	 * (U) Convenience method used to create and add a modified component to 
	 * the list of modified components.
	 * @param orgComp The original component pre-updated, which was modified not added or removed
	 * @param newComp The new component post-update, which was modified not added or removed
	 */
	public void addModifiedComponent(CompareComponent orgComp, CompareComponent newComp)
	{
		if ((orgComp != null) && (newComp != null))
			modifiedComponents.add((new ModifiedComponent(orgComp, newComp)));
	}
	
	/**
	 * (U) Convenience method used to add a component to the list of deleted 
	 * components.
	 * @param component Component to add to the list deleted components.
	 */
	public void addComponentRemoved(CompareComponent component)
	{
		if(component != null)
			componentsRemoved.add(component);
	}

	/**
	 * (U) This method is used to get the list of components added.
	 * @return List of components added to the SBom.
	 */
	public List<CompareComponent> getComponentsAdded()
	{
		return (new ArrayList<>(componentsAdded));
	}
	
	/**
	 * (U) This method is to get the list of components that have been removed.
	 * @return List of components removed from the SBom.
	 */
	public List<CompareComponent> getComponentsRemoved()
	{
		return (new ArrayList<>(componentsRemoved));
	}
	
	/**
	 * (U) This method is used to get the List of Components that have been
	 * Modified.
	 * @return List of ModifiedComponets, which contain both the original and
	 * new components.
	 */
	public List<ModifiedComponent> getModifiedComponents()
	{
		return (new ArrayList<>(modifiedComponents));
	}
	
	/**
	 * (U) This method is used to give us a nice string of a Component.
	 * @param tabs String value for the tabs used for indentation.
	 * @param component Component to get the values from.
	 * @return String nice readable string value of a Component.
	 */
	private String printComponent(String tabs, CompareComponent component)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(tabs + "Publisher: " + component.getComponent().getPublisher() + "\n");
		sb.append(tabs + "Name: " + component.getComponent().getName() + "\n");
		sb.append(tabs + "Group: " + component.getComponent().getGroup() + "\n");
		sb.append(tabs + "Version: " + component.getComponent().getVersion() + "\n");
		
		return sb.toString();
	}
	
	/**
	 * (U) This method is used to set the components added list to the list
	 * passed in.
	 * @param componentsAdded List of components to set the components added 
	 * list to.
	 */
	public void setComponentsAdded(List<CompareComponent> componentsAdded)
	{
		if(componentsAdded != null)
			this.componentsAdded = new ArrayList<>(componentsAdded);
		else
			this.componentsAdded = new ArrayList<>();
	}
	
	/**
	 * (U) This method is used to set the components removed list to the list of
	 * components passed in.
	 * @param componentsRemoved List of components to set the components removed
	 * list to.
	 */
	public void setComponentsRemoved(List<CompareComponent> componentsRemoved)
	{
		if(componentsRemoved != null)
			this.componentsRemoved = new ArrayList<>(componentsRemoved);
		else
			this.componentsRemoved = new ArrayList<>();
	}
	
	/**
	 * (U) This method is used to set the list of modified components.
	 * @param modifiedComponents List of modified components to set the components
	 * modified list to.
	 */
	public void setModifiedComponents(List<ModifiedComponent> modifiedComponents)
	{
		if(modifiedComponents != null)
			this.modifiedComponents = new ArrayList<>(modifiedComponents);
		else
			this.modifiedComponents = new ArrayList<>();
	}
	
	/**
	 * (U) Method used to printout the values of this Class.
	 *
	 * @return String nice readable string value of this class.
	 */
	@Override
	public String toString()
	{
		return (toString(""));
	}

	/**
	 * (U) Convenience method to make the string printed out indent as intended.
	 *
	 * @param tabs String value for the tabs used for indentation.
	 * @return String nice readable string value of this class.
	 */
	public String toString(String tabs)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(tabs + "Components Added: \n");
		for(CompareComponent component: getComponentsAdded())
		{
			sb.append(printComponent(tabs + "	", component));
		}
		
		sb.append("\n");
		
		sb.append(tabs + "Components Removed: \n");
		for(CompareComponent component: getComponentsRemoved())
		{
			sb.append(printComponent(tabs + "	", component));
		}
		
		sb.append("\n");
		
		sb.append(tabs + "Components Modified: \n");
		
		for(ModifiedComponent component: getModifiedComponents())
		{
			sb.append(tabs + "	Original Component: \n");
			sb.append(printComponent(tabs + "		", component.getPreviousComponent()));
			sb.append(tabs + "	New Component: \n");
			sb.append(printComponent(tabs + "		", component.getNewComponent()));
		}
				
		return (sb.toString());
	}
}