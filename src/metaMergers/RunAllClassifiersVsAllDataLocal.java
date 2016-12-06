package metaMergers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import examples.TestClassify;
import parsers.NewRDPParserFileLine;
import projectDescriptors.AbstractProjectDescription;
import utils.ConfigReader;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.meta.Bagging;
import weka.classifiers.rules.OneR;
import weka.classifiers.trees.RandomForest;

public class RunAllClassifiersVsAllDataLocal
{
	public static final int NUM_PERMUTATIONS = 100;
	
	public static List<Classifier> getClassifiers() throws Exception
	{
		List<Classifier> list = new ArrayList<Classifier>();
		
		list.add(new RandomForest());
		list.add(new OneR());
		list.add(new NaiveBayes());
		list.add(new SMO());
		list.add(new AdaBoostM1());
		//list.add(new AdditiveRegression());
		list.add(new AttributeSelectedClassifier());
		list.add(new Bagging());
		list.add(new BayesNet());
		return list;
	}
	
	public static void main(String[] args) throws Exception
	{
		
		for( Classifier c : getClassifiers())
		for( AbstractProjectDescription apd : BringIntoOneNameSpace.getAllProjects())
		{
			// just genus
			for( int x=NewRDPParserFileLine.TAXA_ARRAY.length -1; 
							x < NewRDPParserFileLine.TAXA_ARRAY.length; x++)
			{
				String taxa = NewRDPParserFileLine.TAXA_ARRAY[x];
				File outFile = new File(ConfigReader.getMergedArffDir() + File.separator + 
						"allVsallMerged" + File.separator + 
						apd.getClass().getName() + "_" + c.getClass().getName() + "_" + taxa +".txt" );
				System.out.println(outFile.getAbsolutePath());
				
				if( ! outFile.exists())
				{
					
					File inFile = new File( apd.getArffMergedFileFromRDP(taxa));
					
					// single-threaded because memory is constraining on the cluster
					List<Double> unscrambled= 
							TestClassify.plotRocUsingMultithread(inFile, NUM_PERMUTATIONS, false, null, 
									c.getClass().getName(), null);
					
					List<Double> scrambled= 
							TestClassify.plotRocUsingMultithread(inFile, NUM_PERMUTATIONS, true, null, 
									c.getClass().getName(), null);
					
					BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
					writer.write("unscrambled\tscrambled\n");
					
					for(int y=0; y < NUM_PERMUTATIONS; y++)
						writer.write(unscrambled.get(y) + "\t" + scrambled.get(y) + "\n");
					
					writer.flush();  writer.close();
					
				}
			}
		}
	}
}