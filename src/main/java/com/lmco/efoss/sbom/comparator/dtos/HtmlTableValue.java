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

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * (U) This Data Transfer Object (DTO) is used so the HTML file can be created.
 * 
 * @author e275735
 * @since 23 September 2020
 */
public class HtmlTableValue implements Comparable<HtmlTableValue>
{
	
	private String efossStatus;
	private String group;
	private String name;
	private String status;
	private String versionNew;
	private String versionOld;
	
	@Override
	public int compareTo(HtmlTableValue o)
	{
		return this.getName().toLowerCase(Locale.ENGLISH)
				.compareTo(o.getName().toLowerCase(Locale.ENGLISH));
	}
	
	@Override
	public boolean equals(Object o)
	{
		boolean same = false;
		
		if (o instanceof HtmlTableValue)
		{
			HtmlTableValue other = (HtmlTableValue) o;
			if (StringUtils.equalsIgnoreCase(this.getName(), other.getName()))
				same = true;
		}
		return same;
	}
	
	public String getEfossStatus()
	{
		return efossStatus;
	}
	
	public String getGroup()
	{
		return group;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public String getVersionNew()
	{
		return versionNew;
	}
	
	public String getVersionOld()
	{
		return versionOld;
	}
	
	@Override
	public int hashCode()
	{
		return (this.getName().toLowerCase(Locale.ENGLISH).hashCode());
	}
	
	public void setEfossStatus(String efossStatus)
	{
		this.efossStatus = efossStatus;
	}
	
	public void setGroup(String group)
	{
		this.group = group;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setStatus(String status)
	{
		this.status = status;
	}
	
	public void setVersionNew(String versionNew)
	{
		this.versionNew = versionNew;
	}
	
	public void setVersionOld(String versionOld)
	{
		this.versionOld = versionOld;
	}
	
	public String toString(String tabs)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(tabs + "Name: " + getName() + "\n");
		sb.append(tabs + "Group " + getGroup() + "\n");
		sb.append(tabs + "Deployment Specifics: " + getVersionOld() + "\n");
		sb.append(tabs + "Need Date: " + getVersionNew() + "\n");
		sb.append(tabs + "System: " + getStatus() + "\n");
		sb.append(tabs + "Efoss Status: " + getEfossStatus() + "\n");
		return (sb.toString());
	}
	
}
