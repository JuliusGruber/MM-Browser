import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;

import weka.clusterers.Clusterer;
import weka.clusterers.HierarchicalClusterer;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class WekaTest {

	public static void main(String[] args) {
//		HierarchicalClusterer clusterer = new HierarchicalClusterer();
		String matFilePath = "C:/Users/Julius/Desktop/SampleDatenbanken/snareGroups/MFCC_mean.mat";
		
		

		LinkedHashMap<String, double[]> featureData = new LinkedHashMap<String, double[]>();
		Map<String, MLArray> content = null; 
		MatFileReader mfr = null;;
		
		try {
			mfr = new MatFileReader(matFilePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		content = mfr.getContent();
//		System.out.println(content);
		MLCell featureDataMLCell = (MLCell) content.get("featureData");
		int numRows = featureDataMLCell.getDimensions()[0];
		int numColumns = featureDataMLCell.getDimensions()[1];
		int numFeatures = numColumns -1;
		
		
		
		
		
		
		
		for(int i = 0; i<numRows;i++){
			String sampleFilePath = featureDataMLCell.get(i, 0).contentToString().substring(7).replaceAll("'", "");//filepath
//			System.out.println("sampleFilePath: "+sampleFilePath);
			double[] featureValues = new double[numFeatures];
			for(int k = 1; k< numColumns;k++){
//				System.out.println("Without Substring: "+ new Double(featureDataMLCell.get(i, k).contentToString()));
				featureValues[k-1]= new Double (featureDataMLCell.get(i, k).contentToString());
				
//				System.out.println("featureValues:"+ featureDataMLCell.get(i, k).contentToString());
			}
			
			featureData.put(sampleFilePath, featureValues);
		
		}
		
//		%%%%%%%%%%%%% featureData and number of clusters will be the input to the weka method %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%		
		calculateClusterNumbers(featureData, 5);
		
	
		
	}
	
	
	
	private static void calculateClusterNumbers(LinkedHashMap<String, double[]>featureData, int numClusters){
		
		int numRows = featureData.size();
		Entry<String, double[]> entryLength = featureData.entrySet().iterator().next();
		double [] testLength = entryLength.getValue();
		int numFeatures = testLength.length;
		
		
//		System.out.println("numFeatures: " + numFeatures);
		
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for(int i = 0; i < numFeatures; i++){
			attributes.add(new Attribute("feature"+i));
		}
		
		
//		prepare Instances
		Instances instances = new Instances("myInstances", attributes , numRows);
		for (Entry<String, double[]> entry : featureData.entrySet()) {
		 	double [] value = entry.getValue();
		 	Instance singleInstance = new DenseInstance(numFeatures); 
		    
		    for(int l = 0; l< value.length; l++){
		    	singleInstance.setValue(l, value[l]);
		    }
			instances.add(singleInstance);
		}
		
		
		
//		clusterer setup
		HierarchicalClusterer clusterer = new HierarchicalClusterer();
		
		 String[] options = new String[2];
		 options[0] = "-L";
		 options[1] = "WARD";
		
		
		try {
			clusterer.setOptions(options);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		clusterer.setNumClusters(numClusters);
		
		try {
			clusterer.buildClusterer(instances);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} ;
		
//		System.out.println("Linkage Type:" +clusterer.getLinkType());
		
		
		
//		Do the clustering
		
		LinkedHashMap<String, int[]> filePathsClusterNumbers = new LinkedHashMap<String, int[]>();
	
		
		for (Entry<String, double[]> entry : featureData.entrySet()) {
		    String key = entry.getKey();// = filePath
		    double [] value = entry.getValue();
//		    System.out.println(key);
		    
		   
		    
		    Instance singleInstance = new DenseInstance(numFeatures); 
		    
		    for(int l = 0; l< value.length; l++){
		    	singleInstance.setValue(l, value[l]);
		    }
			
		    int clusterNumber = 0;
			try {
				clusterNumber = clusterer.clusterInstance(singleInstance);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    int [] singleClusterNumberArray = new int[1];
		    singleClusterNumberArray[0]= clusterNumber;
		    filePathsClusterNumbers.put(key, singleClusterNumberArray);
		}
		
// 		print the filePath clusterNumberPairs
		for (Entry<String, int[]> entry : filePathsClusterNumbers.entrySet()) {
			String filePath = entry.getKey();
			int [] clustNumArray = entry.getValue();
			int clustNum = clustNumArray[0];
			System.out.println(filePath+": "+clustNum);
		}
		
		
	}

	
	
	
	
	
}
