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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.cyclonedx.exception.GeneratorException;

import com.lmco.efoss.sbom.commons.utils.StringUtils;
import com.lmco.efoss.sbom.comparator.dtos.SBomDiff;
import com.lmco.efoss.sbom.comparator.exceptions.SBomComparatorException;
import com.lmco.efoss.sbom.comparator.generator.SBomJSonDiffGenerator;
import com.lmco.efoss.sbom.comparator.generator.SBomXmlDiffGenerator;

/**
 * (U) This class is used to handle interactions with a file and an SBomDiff Object.
 * 
 * @author wrgoff
 * @since 23 July 2020
 */
public class SBomDiffFileUtils
{
	private static final Logger logger = Logger.getLogger(SBomDiffFileUtils.class.getName());
	
	/**
	 * Constructor.
	 */
	private SBomDiffFileUtils()
	{}
	
	/**
	 * (U) This method is used to generate the output file.
	 * 
	 * @param diff           SBomDiff object to produce the file from.
	 * @param outputFormat   String value that tells us what format the data in the file will be.
	 *                       Either JSon or XML.
	 * @param outputFileName String value of the file to put the data in.
	 * @throws SBomComparatorException in the event we are unable to produce the desired format, and
	 *                                 write it to the file.
	 */
	public static void generateOutputFile(SBomDiff diff, String outputFormat,
			String outputFileName) throws SBomComparatorException
	{
		String outputFormatString = outputFormat.toLowerCase(Locale.ENGLISH);
		
		if (!StringUtils.isValid(outputFileName))
			outputFileName = "diff";
		
		// Append the file suffix. If it is NOT already there.
		if (!outputFileName.endsWith(outputFormatString))
			outputFileName = outputFileName + "." + outputFormatString;
		
		String output = null;
		if (outputFormatString.equalsIgnoreCase("json"))
		{
			SBomJSonDiffGenerator generator = new SBomJSonDiffGenerator(diff);
			output = generator.toJsonString();
		}
		else // Its XML.
		{
			try
			{
				SBomXmlDiffGenerator generator = new SBomXmlDiffGenerator(diff);
				generator.generate();
				output = generator.toXmlString();
			}
			catch (ParserConfigurationException | GeneratorException e)
			{
				String error = "Unable to create XML from SBom Diff!";
				logger.error(error, e);
				throw new SBomComparatorException(error);
			}
		}
		try
		{
			Files.writeString(Paths.get(outputFileName), output);
		}
		catch (IOException e)
		{
			String error = "Failed to write output to file (" + outputFileName + ").";
			logger.error(error, e);
			throw new SBomComparatorException(error, e);
		}
	}
}
