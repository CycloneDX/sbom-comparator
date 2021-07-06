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
 * (U) This Data Transfer Object (DTO) is used so the "component" appears correctly in our XML/JSon.
 * 
 * @author wrgoff
 * @since 5 Aug 2020
 */
public class CompareComponent implements Serializable
{
	private static final long serialVersionUID = 7302698178952661549L;
	private org.cyclonedx.model.Component component;

	/**
	 * (U) Base Constructor.
	 */
	public CompareComponent()
	{}
	
	/**
	 * (U) Convenience constructor used to create our Component.
	 * 
	 * @param newComp Cyclone DX Component we are putting inside ours, so it comes out correctly in
	 *                the XML/JSon.
	 */
	public CompareComponent(org.cyclonedx.model.Component newComp)
	{
		component = newComp;
	}
	
	/**
	 * (U) This method is used to get the Cyclone DX Component.
	 * 
	 * @return Component the CycloneDx Component.
	 */
	public org.cyclonedx.model.Component getComponent()
	{
		return component;
	}

	/**
	 * (U) This method is used to set the Cyclone DX Component.
	 * 
	 * @param innerComponent Cyclone DX Component used to set ours to it.
	 */
	public void setComponent(org.cyclonedx.model.Component innerComponent)
	{
		this.component = innerComponent;
	}
}
