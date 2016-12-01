package projectDescriptors;

import java.io.File;

import utils.ConfigReader;

public class Divitriculosis2015ProjectDescriptor extends AbstractProjectDescription
{
	@Override
	public String getProjectName()
	{
		return "Divitriculosis2015";
	}
	
	@Override
	public String getArffIndiviudalFileFromRDP(String taxa) throws Exception
	{
		return ConfigReader.getMergedArffDir() + File.separator + "Adenomas2015" 
						+ File.separator + taxa +
						"pivoted_" + taxa + "asColumnsLogNormalPlusMetadata.arff";
		 
	}
}
