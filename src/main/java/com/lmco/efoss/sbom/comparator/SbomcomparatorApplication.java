/*
 * Copyright (c) 2018,2019 Lockheed Martin Corporation.
 *
 * This work is owned by Lockheed Martin Corporation. Lockheed Martin personnel are permitted to use and
 * modify this software.  Lockheed Martin personnel may also deliver this source code to any US Government
 * customer Agency under a "US Government Purpose Rights" license.
 *
 * See the LICENSE file distributed with this work for licensing and distribution terms
 */
package com.lmco.efoss.sbom.comparator;

import java.io.File;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.cyclonedx.model.Bom;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.lmco.efoss.sbom.commons.utils.DateUtils;
import com.lmco.efoss.sbom.commons.utils.FileOutputUtils;
import com.lmco.efoss.sbom.commons.utils.SBomCommons;
import com.lmco.efoss.sbom.commons.utils.SBomCommonsException;
import com.lmco.efoss.sbom.commons.utils.SBomFileUtils;
import com.lmco.efoss.sbom.comparator.builder.HtmlBuilder;
import com.lmco.efoss.sbom.comparator.dtos.SBomDiff;
import com.lmco.efoss.sbom.comparator.exceptions.SBomComparatorException;
import com.lmco.efoss.sbom.comparator.generator.SBomGenerator;
import com.lmco.efoss.sbom.comparator.utils.SBomCompareUtils;
import com.lmco.efoss.sbom.comparator.utils.SBomDiffFileUtils;

/**
 * (U) This is the main driver class for the Software Bill of Materials (SBom) comparator. Used to
 * compare two SBom (bom.xml) files.
 * 
 * @author wrgoff
 * @since 23 July 2020
 */
@SpringBootApplication
public class SbomcomparatorApplication implements ApplicationRunner
{
	private enum AVAILABLE_FORMATS
	{
		JSON, XML
	}
	
	private static final String OUTPUT = "output";
	private static final String HTMLOUTPUT = "htmloutput";
	private static final String FORMAT = "format";
	private static final String OUTPUT_BOM_FILE = "outputBomFile";
	private static final Logger logger = Logger
			.getLogger(SbomcomparatorApplication.class.getName());
	
	/**
	 * (U) This method is used to create the valid options for command line usage.
	 * 
	 * @return Options for use when running via command line.
	 */
	private static Options createCliOptions()
	{
		Options cliOptions = new Options();
		cliOptions.addOption(new Option("h", "help", false, "will print out the command line " +
				"options."));
		cliOptions.addOption(new Option("f1", "orgsbom", true, "original SBom file"));
		cliOptions.addOption(new Option("f2", "newsbom", true, "new SBom file"));
		cliOptions.addOption(new Option("o", OUTPUT, true,
				"(Optional) output file name, default is diff.json or diff.xml"));
		cliOptions.addOption(new Option("ob", OUTPUT_BOM_FILE, true,
				"(Optional) output file name of the diff bom, default is diffBom.json or diffBom.xml"));
		cliOptions.addOption(new Option("f", FORMAT, true,
				"(Optional) output file format, Valid values json, xml.  Default is xml"));
		cliOptions.addOption(new Option("t", HTMLOUTPUT, true,
				"(Optional) output html file name, default name is sbomcompared"));
		return cliOptions;
	}
	
	/**
	 * (U) This method is used to read the argument for a Software Bill of Materials (SBom) file,
	 * and read it into a Bom Object.
	 * 
	 * @param cli        CommandLine object to read the command line arguments from.
	 * @param sbomOption SBom option that indicates the SBom file to read in.
	 * @return Bom CycloneDx Bom object the file has been read into.
	 * @throws SBomComparatorException in the event the file can not be read into a Bom object.
	 * @throws SBomCommonsException    in the event we fail to create an SBom from the file's data.
	 */
	private static Bom getBomFile(CommandLine cli, String sbomOption)
			throws SBomComparatorException, SBomCommonsException
	{
		Bom bom = null;
		if (cli.hasOption(sbomOption))
		{
			String fileName = cli.getOptionValue(sbomOption);
			
			if (logger.isDebugEnabled())
				logger.debug("Attempting to load SBom (" + fileName + ")");
			
			if ((fileName != null) && (fileName.trim().length() > 0))
			{
				File file = new File(fileName);
				if ((file.exists()) && (file.canRead()))
					bom = SBomFileUtils.processFile(file);
				else if (file.exists())
					throw new SBomComparatorException("Unable to read SBom from file(" +
							fileName + ").");
				else
					throw new SBomComparatorException("File(" + fileName + ") does NOT exist!");
			}
			else
				throw new SBomComparatorException("No file name priveded for " + sbomOption + ".");
		}
		else
			throw new SBomComparatorException(
					"You must provide a file for us to read the sbom from!");
		
		return bom;
	}
	
	/**
	 * (U) This method is used to pull the output file name from the command line arguments.
	 * 
	 * @param cli CommandLine to pull the output file name from.
	 * @return String the output file name.
	 */
	private static String getOutputFileName(CommandLine cli)
	{
		String filename = "diff";
		
		if (cli.hasOption(OUTPUT))
		{
			filename = cli.getOptionValue(OUTPUT);
		}
		return filename;
	}
	
	/**
	 * (U) Used to get the new Bom File name.
	 * 
	 * @param cli Command Line to pull the output bom file name from.
	 * @return String the output bom file name.
	 */
	private static String getOutputBomFileName(CommandLine cli)
	{
		String fileName = "diffBom";
		if (cli.hasOption(OUTPUT_BOM_FILE))
		{
			fileName = cli.getOptionValue(OUTPUT_BOM_FILE);
		}
		return fileName;
	}
	
	/**
	 * (U) This method is used to get the output format the user wants. If the user didn't specify
	 * we return XML.
	 * 
	 * @param cli CommandLine arguments to read in what the user wants.
	 * @return AVAILABLE_FORMATS enumeration that tell us what the format of the output file will be
	 *         in.
	 * @throws SBomComparatorException in the event the user supplied a format we do NOT support.
	 */
	private static AVAILABLE_FORMATS getOutputFormat(CommandLine cli) throws SBomComparatorException
	{
		AVAILABLE_FORMATS format = AVAILABLE_FORMATS.XML;
		
		String formatString = null;
		if (cli.hasOption(FORMAT))
		{
			formatString = cli.getOptionValue(FORMAT);
			if (formatString.equalsIgnoreCase("xml"))
				format = AVAILABLE_FORMATS.XML;
			else if (formatString.equalsIgnoreCase("json"))
				format = AVAILABLE_FORMATS.JSON;
			else
			{
				String error = "Unrecognized or unsupported output file format.  Valid values " +
						"are xml, json.";
				logger.error("User provided a format of " + formatString + ", which is invalid.");
				throw new SBomComparatorException(error);
			}
		}
		else
			logger.info("User didn't supply a format, returning XML");
		
		return format;
	}
	
	/**
	 * (U) Main method.
	 * 
	 * @param args The main method; an array of sequence of characters (Strings) that are passed to
	 *             the "main" function
	 */
	public static void main(String[] args)
	{
		SpringApplication.run(SbomcomparatorApplication.class, args);
	}
	
	/**
	 * (U) This method runs the actual Comparator.
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception
	{
		Date startDate = DateUtils.rightNowDate();
		boolean failed = false;
		
		CommandLineParser cliParser = new DefaultParser();
		Options cliOptions = createCliOptions();
		boolean runningHelp = false;
		try
		{
			CommandLine cli = cliParser.parse(cliOptions, args.getSourceArgs());
			if (cli.hasOption("help"))
			{
				runningHelp = true;
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("help", cliOptions);
			}
			else
			{
				AVAILABLE_FORMATS outputFormat = getOutputFormat(cli);
				String outputFileName = getOutputFileName(cli);
				
				Bom originalBom = getBomFile(cli, "orgsbom");
				Bom newBom = getBomFile(cli, "newsbom");
				
				SBomDiff diff = SBomCompareUtils.compareComponents(originalBom, newBom);
				
				HtmlBuilder htmlBuilder = new HtmlBuilder();
				htmlBuilder.generateHTMLFile(diff, cli);
				SBomDiffFileUtils.generateOutputFile(diff, outputFormat.toString(),
						outputFileName);
				
				SBomGenerator sbomGenerator = new SBomGenerator();
				Bom diffBom = sbomGenerator.geneateDiffBom(originalBom, newBom, diff);
				
				String output;
				SBomCommons.AVAILABLE_FORMATS format = SBomCommons.AVAILABLE_FORMATS.XML;
				
				if (outputFormat.equals(AVAILABLE_FORMATS.JSON))
					format = SBomCommons.AVAILABLE_FORMATS.JSON;
				
				output = SBomCommons.generateOutputString(diffBom, format);
				
				FileOutputUtils.generateOutputFile(output, "XML", getOutputBomFileName(cli));
			}
		}
		catch (Exception e)
		{
			failed = true;
			logger.error("Failed to compare the two SBoms!", e);
			throw e;
		}
		finally
		{
			if (logger.isInfoEnabled())
			{
				StringBuilder msg = new StringBuilder("It took " + DateUtils.computeDiff(startDate,
						DateUtils.rightNowDate()));
				if (failed)
					msg.append(" to fail, ");
				else
					msg.append(" to successfully, ");
				
				if (runningHelp)
					msg.append("to show the usage.");
				else
					msg.append("compare two SBom.");
				logger.info(msg.toString());
			}
		}
	}
}
