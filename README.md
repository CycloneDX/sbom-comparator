# sbom-comparator
Lockheed Martin developed utility to compare two SBOMs

The SBomComparator is used to compare two Software Bill Of Materials (SBOM) commonly known as SBoms or Boms.  
It uses the CycloneDx Schema, and can consume SBoms in either JSon or XML, and produce a difference report in either JSon or XML. 
The difference report can be viewed as an html display which is automatically generated. 

## Prerequisites
- Open JDK11
- Apache Maven 3.6.3 or greater installed 
- (Recommended) java IDE Eclipse with Subclipse 4.3.0 plug-in
- Two Software Bill of Materials.

## Usage:

### Build artifact via maven.
### Maven Command
mvn clean package

### Run
To run as a standalone java application, you can look at the "example.sh" shell script for an example.
You can also use the provided "compare.sh" script as a pass through to the jar.  It assumes all the basic settings.

### Help is available.   
<pre>
./compare.sh -h
</pre>

### Help Output shows options for running the SBomComparator application.
<pre>
usage: help
    -f,     --format        &lt;arg&gt; (Optional) output file format, Valid values json, xml.  Default is xml
    -f1,    --orgsbom       &lt;arg&gt; original SBom file
    -f2,    --newsbom       &lt;arg&gt; new SBom file
    -h,     --help            will print out the command line options.
    -o,     --output        &lt;arg&gt; (Optional) output file name, default is diff.json or diff.xml
    -ob,    --outputBomFile &lt;arg&gt; (Optional) output file of the diff bom,  default is diffBom.xml
    -t,     --htmloutput    &lt;arg&gt; (Optional) output html file name, default name is sbomcompared
</pre>

### Running SBomComparator.

./compare.sh -f1 ./test/OrgSbom.xml -f2 ./test/ModifiedSbom.xml -o ./test/output -f xml -t ./test/output -ob ./test/newBom

# API:
## You can also pull in the API and run it inside your application.
### From reading in a CycloneDx bom.xml or bom.json file via.
<pre>
Bom bom = SBomFileUtils.processFile(new File(fileName));
</pre>

### To compare two SBoms.
<pre>
SBomDiff diff = SBomCompareUtils.compareComponents(originalBom, newBom);
</pre>

### Difference Report HTML
The Difference Report automatically generates a graphical display. If the user does not give a location with "-t", the file will be created at the root of the project with the name "sbomcompared.html" 

## Sample HTML output

![](htmlexample.png)

## License
[licenses](./LICENSE)

