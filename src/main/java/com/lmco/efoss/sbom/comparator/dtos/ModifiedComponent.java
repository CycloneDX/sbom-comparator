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

/**
 * (U) This data transfer object (DTO) is used to capture the data of a component, and what it
 * changed from.
 * 
 * @author wrgoff
 * @since 9 July 2020
 */
public class ModifiedComponent implements Serializable
{
	private static final long serialVersionUID = 3267195617402274461L;
	private CompareComponent newComponent;
	private CompareComponent previousComponent;
	
	/**
	 * (U) Base Constructor.
	 */
	public ModifiedComponent()
	{
	}
	
	/**
	 * (U) Convenience Constructor used to create a Modified Component.
	 * 
	 * @param previousComponent Component that was the original Component.
	 * @param newComponent      Component that is the new component.
	 */
	public ModifiedComponent(CompareComponent previousComponent,
			CompareComponent newComponent)
	{
		this.previousComponent = previousComponent;
		this.newComponent = newComponent;
	}
	
	/**
	 * (U) This method is used to get the new Component.
	 * 
	 * @return Component the new component.
	 */
	public CompareComponent getNewComponent()
	{
		return newComponent;
	}
	
	/**
	 * (U) This method is used to get the original or previous Component.
	 * 
	 * @return Component that is the original Component.
	 */
	public CompareComponent getPreviousComponent()
	{
		return previousComponent;
	}
	
	/**
	 * (U) This method is used to set the new component.
	 * 
	 * @param newComponent Component to set the new Component to.
	 */
	public void setNewComponent(CompareComponent newComponent)
	{
		this.newComponent = newComponent;
	}
	
	/**
	 * (U) This method is used to set the previous Component.
	 * 
	 * @param previousComponent Component to set the original or previous component to.
	 */
	public void setPreviousComponent(CompareComponent previousComponent)
	{
		this.previousComponent = previousComponent;
	}
}
