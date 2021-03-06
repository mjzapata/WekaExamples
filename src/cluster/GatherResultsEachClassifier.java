package cluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import parsers.NewRDPParserFileLine;

public class GatherResultsEachClassifier
{
	public static final String SPREADSHEETS_DIR = "/nobackup/afodor_research/arffMerged/spreadsheets";
	
	public static void main(String[] args) throws Exception
	{
		for(int x=1; x< NewRDPParserFileLine.TAXA_ARRAY.length; x++)
		{
			String taxa = NewRDPParserFileLine.TAXA_ARRAY[x];
			
			HashMap<String, Holder> map =getResultsForALevel(taxa);
			writeResults(map, taxa);
		}
	}
	
	private static class Holder
	{
		List<Double> notScrambled = new ArrayList<Double>();
		List<Double> scrambled = new ArrayList<Double>();
	}
	
	private static void writeResults( HashMap<String, Holder> map, String level) throws Exception
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter( 
				SPREADSHEETS_DIR + File.separator + "allClassifiers_" + level + ".txt"));
		
		List<String> list = new ArrayList<String>(map.keySet());
		Collections.sort(list);
		
		writer.write("index");
		
		for( String s : list)
			writer.write("\t" + s + "\t" + s + "_scrambled");
		
		writer.write("\n");
		
		int length = map.get(list.get(0)).scrambled.size();
		
		for( int x=0; x < length; x++)
		{
			writer.write("" + (x+1));
			
			for(String s : list)
				writer.write("\t" + map.get(s).notScrambled.get(x) + "\t" + map.get(s).scrambled.get(x));
			
			writer.write("\n");
		}
		
		writer.flush();  writer.close();
	}
	
	private static int getNumLines(File file ) throws Exception
	{
		int i =0;
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		for(String s= reader.readLine();s != null; s= reader.readLine())
			i++;
		
		reader.close();
		
		return i;
	}
	
	private static HashMap<String, Holder> getResultsForALevel(String level) throws Exception
	{
		HashMap<String, Holder>  map = new HashMap<String,Holder>();
		
		String[] files = EvaluateAClassifier.OUTPUT_DIR.list();
		
		for( String s : files)
		{
			if( s.indexOf(level + ".txt") != -1)
			{
				
				File file = new File(EvaluateAClassifier.OUTPUT_DIR.getAbsolutePath() + File.separator + s);
				
				if( getNumLines(file)!= EvaluateEachClassifier.numPermutations + 1)
				{
					System.out.println("Expecting " +  (EvaluateEachClassifier.numPermutations + 1) + 
							" but got "  + getNumLines(file) + " skipping " + file.getAbsolutePath() );
				}
				else
				{
					BufferedReader reader = new BufferedReader(new FileReader(file));
					
					s = s.replace(level + ".txt", "").replace("projectDescriptors.", "")
							.replace("ProjectDescriptor", "");
					
					if(map.containsKey(s))
						throw new Exception("Duplicate key " + s);
					
					Holder h =new Holder();
					map.put(s,h);
					
					reader.readLine();
					
					for( String s2 = reader.readLine(); s2 != null; s2= reader.readLine())
					{
						String[] splits =s2.split("\t");
						
						if( splits.length != 2)
							throw new Exception("Parsing error");
						
						h.notScrambled.add(Double.parseDouble(splits[0]));
						h.scrambled.add(Double.parseDouble(splits[1]));
					}
					
					reader.close();			
				}
			}
		}
		
		return map;
		
	}
}
