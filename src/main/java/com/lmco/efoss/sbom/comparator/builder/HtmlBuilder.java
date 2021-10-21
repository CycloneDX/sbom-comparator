/*
 * Copyright (c) 2018,2019 Lockheed Martin Corporation.
 *
 * This work is owned by Lockheed Martin Corporation. Lockheed Martin personnel are permitted to use and
 * modify this software.  Lockheed Martin personnel may also deliver this source code to any US Government
 * customer Agency under a "US Government Purpose Rights" license.
 *
 * See the LICENSE file distributed with this work for licensing and distribution terms
 */

package com.lmco.efoss.sbom.comparator.builder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.log4j.Logger;
import org.cyclonedx.model.Property;

import com.lmco.efoss.sbom.commons.utils.StringUtils;
import com.lmco.efoss.sbom.comparator.dtos.CompareComponent;
import com.lmco.efoss.sbom.comparator.dtos.HtmlTableValue;
import com.lmco.efoss.sbom.comparator.dtos.ModifiedComponent;
import com.lmco.efoss.sbom.comparator.dtos.SBomDiff;
import com.lmco.efoss.sbom.comparator.exceptions.SBomComparatorException;

/**
 * (U) This class is used to build the static HTML which shows the differences between the two
 * compared files. The generated HTML is always being create (as of September 2020) and is located
 * at the root of this project directory.
 * 
 * @author e275735
 * @since 23 September 2020
 */
public class HtmlBuilder
{
	private static final Logger logger = Logger.getLogger(HtmlBuilder.class.getName());
	
	private static final String ADDED = "Added";
	private static final String END_TD_TD = "</td><td>";
	private static final String HTMLOUTPUT = "htmloutput";
	private static final String MODIFIED = "Modified";
	private static final String REMOVED = "Removed";
	
	/**
	 * (U) Hidden base constructor.
	 */
	public HtmlBuilder()
	{}
	
	/**
	 * (U) This method is used to calculate the HTML table's values which will be set later.
	 * 
	 * @param diffs SBomDiff Object that contains the differences between the two SBOMs
	 * @return List of HtmlTableValue that contains the HtmlTableValue DTO of the data for this HTML
	 *         table
	 */
	public List<HtmlTableValue> adaptComponents(SBomDiff diffs)
	{
		List<HtmlTableValue> values = new ArrayList<>();
		List<CompareComponent> addedComponents = diffs.getComponentsAdded();
		List<CompareComponent> removedComponents = diffs.getComponentsRemoved();
		List<ModifiedComponent> modifiedComponents = diffs.getModifiedComponents();
		
		for (CompareComponent addedComponent : addedComponents)
		{
			HtmlTableValue value = new HtmlTableValue();
			value.setEfossStatus(getComponentEfossStatus(addedComponent));
			value.setName(addedComponent.getComponent().getName());
			value.setGroup(addedComponent.getComponent().getGroup());
			value.setStatus(ADDED);
			value.setVersionNew(addedComponent.getComponent().getVersion());
			value.setVersionOld("");
			values.add(value);
		}
		
		for (CompareComponent removedComponent : removedComponents)
		{
			HtmlTableValue value = new HtmlTableValue();
			value.setEfossStatus(getComponentEfossStatus(removedComponent));
			value.setName(removedComponent.getComponent().getName());
			value.setGroup(removedComponent.getComponent().getGroup());
			value.setStatus(REMOVED);
			value.setVersionNew("");
			value.setVersionOld(removedComponent.getComponent().getVersion());
			values.add(value);
		}
		
		for (ModifiedComponent modifiedComponent : modifiedComponents)
		{
			HtmlTableValue value = new HtmlTableValue();
			value.setEfossStatus(getComponentEfossStatus(modifiedComponent.getNewComponent()));
			value.setName(modifiedComponent.getNewComponent().getComponent().getName());
			value.setGroup(modifiedComponent.getNewComponent().getComponent().getGroup());
			value.setStatus(MODIFIED);
			value.setVersionNew(
					modifiedComponent.getNewComponent().getComponent().getVersion().trim());
			value.setVersionOld(
					modifiedComponent.getPreviousComponent().getComponent().getVersion().trim());
			values.add(value);
		}
		Collections.sort(values);
		return values;
	}
	
	/**
	 * (U) This method is used to build a static HTML with the HtmlTableValue DTO.
	 * 
	 * @param diffs SBomDiff Object that contains the differences between the two Software Build of
	 *              Materials (SBOMs)
	 * @param cli   CommandLine object to read the command line arguments from, particularly needed
	 *              for the file names of the compared SBOMs
	 * @throws SBomComparatorException if we are unable to produce the JSon String from the Object
	 *                                 passed in.
	 */
	public void generateHTMLFile(SBomDiff diffs, CommandLine cli)
			throws SBomComparatorException
	{
		List<HtmlTableValue> values = adaptComponents(diffs);
		
		Option[] splitCli = cli.getOptions();
		String encoding = "UTF-8";
		
		String htmloutputFileName = getHtmlOutputFileName(cli);
		
		if (!StringUtils.isValid(htmloutputFileName))
			htmloutputFileName = "sbomcompared";
		
		try (FileOutputStream fos = new FileOutputStream(htmloutputFileName + ".html");
				OutputStreamWriter out = new OutputStreamWriter(fos, encoding);)
		{
			writeHeaderHTML(splitCli, out);
			writeTableHTML(values, out);
		}
		catch (Exception e)
		{
			String error = "Failed to write output to file (HTML).";
			logger.error(error, e);
			throw new SBomComparatorException(error, e);
		}
	}
	
	/**
	 * (U) This method is used to write the HTML data that is not the table rows, to include the
	 * table headers, date of compare, and the two files being compared.
	 * 
	 * @param splitCli the options of the cli down to access the file names
	 * @param out      OutputStreamWriter used to write the content static HTML
	 * @throws SBomComparatorException if we are unable to produce the JSon String from the Object
	 *                                 passed in.
	 */
	public void writeHeaderHTML(Option[] splitCli, OutputStreamWriter out)
			throws SBomComparatorException
	{
		try
		{
			String fristLongName = splitCli[0].getValue();
			String[] arrOfStr = fristLongName.split("/");
			String originalName = arrOfStr[arrOfStr.length - 1];
			
			String secondLongName = splitCli[1].getValue();
			arrOfStr = secondLongName.split("/");
			String secondName = arrOfStr[arrOfStr.length - 1];
			
			String headingColor = "<tr bgcolor=\"#e6e6e6\">";
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Date date = new Date();
			String dateFormatted = formatter.format(date);
			
			out.write("<html><head>" +
					"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
					"<style>table, th, td {border: 1px solid black;" +
					"border-collapse: collapse;}table.center {margin-left:auto;" +
					"margin-right:auto;}body{padding: 3%;}h1 {text-align: center;}" +
					"p {text-align: center; margin-left: 30%;}</style></head>" +
					"<body><h1>Compared Sbom Results</h1>" +
					"<table class=\"center\" border ='1' >" + headingColor +
					"<td>Date Created" + END_TD_TD + "First SBom" + END_TD_TD +
					"Second SBom</td></tr><tr><td>" + dateFormatted + END_TD_TD +
					originalName + END_TD_TD + secondName + "</td></tr></table><br><br>" +
					"<table class=\"center\" border ='1'>" + headingColor +
					"<td>Name" + END_TD_TD + "Group" + END_TD_TD + "Version Old" + END_TD_TD +
					"Version New" + END_TD_TD + "Status" + END_TD_TD + "EFoss Status</td></tr>");
		}
		catch (IOException e)
		{
			String error = "Failed to write output to file (HTML).";
			logger.error(error, e);
			throw new SBomComparatorException(error, e);
		}
	}
	
	/**
	 * (U) This method sets the HTML table values of the actual SBOM differences from the data
	 * transformed off of the DTO.
	 * 
	 * @param values List of HtmlTableValue DTO which contains the SBOM diff information for the
	 *               HTML
	 * @param out    OutputStreamWriter used to write the content static HTML
	 * @throws SBomComparatorException if we are unable to produce the JSon String from the Object
	 *                                 passed in.
	 */
	public void writeTableHTML(List<HtmlTableValue> values, OutputStreamWriter out)
			throws SBomComparatorException
	{
		try
		{
			String rowcolor = null;
			String name = null;
			String group = null;
			String versionNew = null;
			String versionOld = null;
			String status = null;
			String efossStatus = null;
			
			for (HtmlTableValue value : values)
			{
				name = value.getName();
				
				group = "";
				if (StringUtils.isValid(value.getGroup()))
					group = value.getGroup();
				
				versionNew = value.getVersionNew();
				versionOld = value.getVersionOld();
				status = value.getStatus();
				efossStatus = value.getEfossStatus();
				rowcolor = getRowColor(status);
				
				out.write("<tr><td>");
				out.write(name);
				out.write(END_TD_TD);
				out.write(group);
				out.write(END_TD_TD);
				out.write(versionOld);
				out.write(END_TD_TD);
				if (status.equalsIgnoreCase(MODIFIED))
				{
					out.write(buildModifiedString(versionOld, versionNew));
				}
				else
				{
					out.write(versionNew);
				}
				out.write("</td>" + rowcolor);
				out.write(status + "</font></td>");
				out.write(generateEfossRow(efossStatus));
				
				out.write("</tr>");
			}
			out.write("</table>" + "</body>" + "</html>");
			
		}
		catch (IOException e)
		{
			String error = "Failed to write the Table of the HTML with the DTO information.";
			logger.error(error, e);
			throw new SBomComparatorException(error, e);
		}
	}
	
	/**
	 * (U) This method is used to high light the changes in the version.
	 * 
	 * @param versionOld String value of the old version.
	 * @param versionNew String value of the new version.
	 * @return String to appear in the HTML table highlighting the differences in the old versus new
	 *         versions.
	 */
	private String buildModifiedString(String versionOld, String versionNew)
	{
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < versionNew.length(); i++)
		{
			if (getCharAt(versionNew, i) == getCharAt(versionOld, i))
			{
				sb.append("<l>" + versionNew.charAt(i) + "</l>");
			}
			else
			{
				sb.append("<l><mark>" + versionNew.charAt(i) + "</mark></l>");
			}
		}
		return sb.toString();
	}
	
	/**
	 * (U) This method is used to generate the EFoss Status Row of the HTML.
	 * 
	 * @param efossStatus String value of the EFoss Status.
	 * @return String the html used to show the efoss status.
	 */
	private String generateEfossRow(String efossStatus)
	{
		StringBuilder htmlRow = new StringBuilder("<td ");
		
		switch (efossStatus)
		{
			case "APPROVAL_RECOMMENDED":
			case "APPROVED":
				htmlRow.append("bgcolor=\"#03AC13\"><font color=\"black\">");
				break;
			case "UNDER_REVIEW":
			case "LEGAL_REVIEW_HOLD":
				htmlRow.append("bgcolor=\"yellow\"><font color=\"black\">");
				break;
			case "DENIED":
				htmlRow.append("bgcolor=\"red\"><font color=\"white\">");
				break;
			default:
				htmlRow.append("bgcolor=\"white\"><font color=\"black\">");
				break;
		}
		htmlRow.append(efossStatus + "</font></td>");
		
		return htmlRow.toString();
	}
	
	/**
	 * (U) This method is used to return the character at a specific position.  It will either return an empty character to the
	 * character from the string passed in.
	 * @param str String to get the character at a specific position.
	 * @param index integer that the character within the string that we want.
	 * @return char either an empty character or the character at the position identified by the index passed in.
	 */
	private char getCharAt(String str, int index)
	{
		char myChar = ' ';
		
		if(str.length() >= (index + 1))
			myChar = str.charAt(index);
		
		return myChar;
	}
	
	/**
	 * (U) This method is used to get the Efoss Status of the component passed in.
	 * 
	 * @param comp Component to get the Efoss Status for.
	 * @return String the efoss Status of the component.
	 */
	private String getComponentEfossStatus(CompareComponent comp)
	{
		String efossStatus = "";
		
		List<Property> properties = comp.getComponent().getProperties();
		
		if ((properties != null) && (!properties.isEmpty()))
		{
			for (Property property : properties)
			{
				if ((property.getName().equalsIgnoreCase("efossStatus")) ||
						(property.getName().equalsIgnoreCase("efoss status")))
				{
					efossStatus = property.getValue();
					break;
				}
			}
		}
		return efossStatus;
	}
	
	/**
	 * (U) Get the color for the row based on the status.
	 * 
	 * @param status String value of the status.
	 * @return String to contain the HTML that will determine the color of the row.
	 * @throws SBomComparatorException if the status is invalid.
	 */
	private String getRowColor(String status) throws SBomComparatorException
	{
		String rowColor = "";
		
		switch (status)
		{
			case ADDED:
				rowColor = " <td bgcolor=\"#03AC13\"><font color=\"black\">";
				break;
			case REMOVED:
				rowColor = " <td bgcolor=\"red\"><font color=\"white\">";
				break;
			case MODIFIED:
				rowColor = " <td bgcolor=\"white\"><font color=\"black\">";
				break;
			default:
				throw new SBomComparatorException("Unknown status field: " + status);
		}
		return rowColor;
	}
	
	/**
	 * (U) This method is used to pull the html output file name from the command line arguments.
	 * 
	 * @param cli CommandLine to pull the html output file name from.
	 * @return String the html output file name.
	 */
	protected String getHtmlOutputFileName(CommandLine cli)
	{
		String htmlfilename = "sbomcompared";
		
		if (cli.hasOption(HTMLOUTPUT))
		{
			htmlfilename = cli.getOptionValue(HTMLOUTPUT);
		}
		
		return htmlfilename;
	}
}
