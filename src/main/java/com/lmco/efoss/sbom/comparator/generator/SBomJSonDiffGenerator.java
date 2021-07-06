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

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.log4j.Logger;
import org.cyclonedx.CycloneDxSchema;
import org.cyclonedx.generators.json.AbstractBomJsonGenerator;
import org.cyclonedx.generators.json.BomJsonGenerator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.lmco.efoss.sbom.comparator.dtos.SBomDiff;
import com.lmco.efoss.sbom.comparator.exceptions.SBomComparatorException;

/**
 * This class is used to produce JavaScript Object Notation (JSON) for a SBomDiff Object.
 * 
 * @author wrgoff
 * @since 07 June 2021
 */
public class SBomJSonDiffGenerator extends AbstractBomJsonGenerator implements BomJsonGenerator
{
	private static final Logger logger = Logger.getLogger(SBomJSonDiffGenerator.class.getName());
	
	private SBomDiff diff;
	
	/**
	 * (U) Constructor.
	 * 
	 * @param diff SBomDiff object to evaluate.
	 */
	public SBomJSonDiffGenerator(SBomDiff diff)
	{
		super();
		this.diff = diff;
	}
	
	/**
	 * (U) This method is used to get the schema version to use.
	 * 
	 * @return Version the CycloneDxSchema version we are using.
	 */
	@Override
	public Version getSchemaVersion()
	{
		return CycloneDxSchema.Version.VERSION_13;
	}
	
	/**
	 * (U) This method is used to generate the JSon String for the SBom Diff Object passed in.
	 * 
	 * @param diff SBomDiff object to produce the JSon String for.
	 * @return String the JSon representation of the SBomDiff Object passed in.
	 * @throws SBomComparatorException in the event we are unable to generate the JSon String for
	 *                                 the SBomDiff Object passed in.
	 */
	public String toJson(SBomDiff diff) throws SBomComparatorException
	{
		return (toJson(diff, false));
	}
	
	/**
	 * (U) This method is used to generate the JSon String for the SBom Diff Object passed in.
	 * 
	 * @param diff        SBomDiff object to produce the JSon String for.
	 * @param prettyPrint Boolean that tells us if we want to make the JSon String pretty (for human
	 *                    readable purpose).
	 * @return String the JSon representation of the SBomDiff Object passed in.
	 * @throws SBomComparatorException in the event we are unable to generate the JSon String for
	 *                                 the SBomDiff Object passed in.
	 */
	public String toJson(SBomDiff diff, final boolean prettyPrint)
			throws SBomComparatorException
	{
		String json = null;
		try
		{
			if (prettyPrint)
				json = getMapper().writer(new DefaultPrettyPrinter()).writeValueAsString(diff);
			else
				json = getMapper().writeValueAsString(diff);
		}
		catch (JsonProcessingException e)
		{
			logger.error("Unable to produce the JSon String for the SBomDiff " +
					"Object passed in!", e);
			throw new SBomComparatorException(e);
		}
		return json;
	}
	
	/**
	 * this method is used to get the JsonObject created from the SBom Diff Object created from the
	 * two Boms passed in to the constructor.
	 * 
	 * @return JsonObject JsonObject representation of the SBom Diff object, created from the two
	 *         Boms passed into the constructor.
	 */
	@Override
	public JsonObject toJsonObject()
	{
		JsonObject jsonObj = null;
		try
		{
			if (diff != null)
			{
				JsonReader reader = Json.createReader(new StringReader(toJson(
						this.diff, false)));
				
				jsonObj = reader.readObject();
			}
			else
				logger.error("No Diff Object to evaulate!");
		}
		catch (SBomComparatorException e)
		{
			String error = "Unexpected error occured while attempting to create " +
					"the JSonObject from the SBom Diff!";
			logger.error(error, e);
		}
		return jsonObj;
	}
	
	/**
	 * (U) This method is used to get a pretty print version of the JSon created from the SBom Diff
	 * Object.
	 * 
	 * @return String the pretty JSon String representation of the SBom Diff Object.
	 */
	@Override
	public String toJsonString()
	{
		String json = "";
		try
		{
			json = toJson(this.diff, true);
		}
		catch (SBomComparatorException e)
		{
			String error = "Unexpected error occured while attempting to generate the Pretty " +
					"JSon String for the SBom Diff Object!";
			logger.error(error, e);
		}
		return json;
	}
	
	/**
	 * (U) This method is used to get the minimized JSon String created from the SBom Diff Object.
	 * 
	 * @return String the JSon String representation of the SBom Diff Object.
	 */
	@Override
	public String toString()
	{
		String json = "";
		try
		{
			json = toJson(this.diff, false);
		}
		catch (SBomComparatorException e)
		{
			String error = "Unexpected error occured while attempting to generate the " +
					"JSon String for the SBom Diff Object!";
			logger.error(error, e);
		}
		return json;
	}
}
