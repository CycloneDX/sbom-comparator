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

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.cyclonedx.CycloneDxSchema;
import org.cyclonedx.exception.GeneratorException;
import org.cyclonedx.generators.xml.AbstractBomXmlGenerator;
import org.cyclonedx.generators.xml.BomXmlGenerator;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;
import com.lmco.efoss.sbom.comparator.dtos.SBomDiff;

/**
 * This class is used to produce a extensible markup language (XML) String for a SBomDiff Object.
 * 
 * @author wrgoff
 * @since 08 June 2021
 */
public class SBomXmlDiffGenerator extends AbstractBomXmlGenerator implements BomXmlGenerator
{
	
	private SBomDiff diff = null;
	
	/**
	 * (U) Base Constructor.
	 */
	public SBomXmlDiffGenerator()
	{
		super();
	}
	
	/**
	 * (U) Constructor.
	 * 
	 * @param diff SBomDiff Object to evaulate.
	 */
	public SBomXmlDiffGenerator(SBomDiff diff)
	{
		super();
		this.diff = diff;
	}
	
	/**
	 * Creates a CycloneDX BoM from a set of Components.
	 * 
	 * @return an XML Document representing a CycloneDX BoM
	 * @since 5.0.0
	 */
	public Document generate() throws ParserConfigurationException
	{
		return generateDocument(this.diff);
	}
	
	/**
	 * (U) This method is used to get the document, that is used to evaluate the SBomDiff Object as
	 * an XML String.
	 * 
	 * @param diff SBomDiff Object to evaluate.
	 * @return Document that contains the XML evaluate of the SBomDiff Object passed in.
	 * @throws ParserConfigurationException in the event we are unable to parse the SBomDiff Object
	 *                                      passed in.
	 */
	protected Document generateDocument(final SBomDiff diff)
			throws ParserConfigurationException
	{
		Document doc = null;
		try
		{
			final DocumentBuilder docBuilder = buildSecureDocumentBuilder();
			
			doc = docBuilder.parse(new InputSource(new StringReader(toXML(diff, false))));
			
			doc.setXmlStandalone(true);
		}
		catch (SAXException | ParserConfigurationException | IOException | GeneratorException ex)
		{
			throw new ParserConfigurationException(ex.toString());
		}
		return doc;
	}
	
	/**
	 * Returns the version of the CycloneDX schema used by this instance
	 * 
	 * @return a CycloneDxSchemaVersion enum
	 */
	@Override
	public Version getSchemaVersion()
	{
		return CycloneDxSchema.Version.VERSION_13;
	}
	
	/**
	 * (U) This method is used to produce an XML String for the SBomDiff Object passed in.
	 * 
	 * @param diff SBomDiff Object to produce the XML String for.
	 * @return String the XML representation of the SBomDiff passed in.
	 * @throws GeneratorException in the event we are unable to parse the SBomDiff Object into an
	 *                            XML String.
	 */
	public String toXML(SBomDiff diff) throws GeneratorException
	{
		return (toXML(diff, false));
	}
	
	/**
	 * (U) This method is used to produce an XML String for the SBomDiff Object passed in.
	 * 
	 * @param diff        SBomDiff Object to produce the XML String for.
	 * @param prettyPrint boolean that tells us if we should produce a nice human readable XML
	 *                    String. True to produce the XML in a nicely spaced XML String for human
	 *                    readable purposes. False to produce a minimized XML String.
	 * @return String the XML representation of the SBomDiff passed in.
	 * @throws GeneratorException in the event we are unable to parse the SBomDiff Object into an
	 *                            XML String.
	 */
	public String toXML(SBomDiff diff, boolean prettyPrint) throws GeneratorException
	{
		String xml = null;
		try
		{
			if (prettyPrint)
			{
				xml = PROLOG + System.lineSeparator() +
						getMapper().writer(new DefaultXmlPrettyPrinter()).writeValueAsString(diff);
			}
			else
				xml = PROLOG + getMapper().writeValueAsString(diff);
		}
		catch (JsonProcessingException ex)
		{
			throw new GeneratorException(ex);
		}
		return xml;
	}
	
	/**
	 * This method is used to get the XML String for the Diff object loaded in the constructor.
	 */
	public String toXmlString() throws GeneratorException
	{
		return toXML(this.diff, true);
	}
}
