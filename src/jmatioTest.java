import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;


import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;


public class jmatioTest  {

	public static void main(String[] args) {
		//String fileName = "C:/Users/Julius/Desktop" + "/" + "nxd.mat";
		String fileName = "C:/Users/Julius/Desktop/SampleDatenbanken/snareGroups/MFCC_mean.mat";
		 try {
//			MatFileReader mfr = new MatFileReader(fileName);
//			Map<String, MLArray> content = mfr.getContent();
//			System.out.println(content);
//			MLCell featureData = (MLCell) content.get("featureData");
//			System.out.println(featureData.get(0, 0).toString());
//			System.out.println(featureData.get(0, 0).contentToString());
//			System.out.println(featureData.get(1, 0).contentToString());
//			System.out.println(featureData.get(0, 1).contentToString());
//			
//			int [] dim = featureData.getDimensions();
//			System.out.println("dim1: "+ dim[0]);
//			System.out.println("dim2: "+ dim[1]);
//			
//			
//			System.out.println("numFeatures: "+ (featureData.getDimensions()[1] -1));
//			
//			
//			int numRows = featureData.getDimensions()[0];
//			int numColumns = featureData.getDimensions()[1];
//			int numFeatures = numColumns -1;
//			
//			for(int i = 0; i<numRows;i++){
//				System.out.println(i +": "+featureData.get(i, 0).contentToString());
//			}
			
			
//		%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			
		
			LinkedHashMap<String, double[]> featureData = new LinkedHashMap<String, double[]>();
			 
			MatFileReader mfr = new MatFileReader(fileName);
			Map<String, MLArray> content = mfr.getContent();
//			System.out.println(content);
			MLCell featureDataMLCell = (MLCell) content.get("featureData");
			int numRows = featureDataMLCell.getDimensions()[0];
			int numColumns = featureDataMLCell.getDimensions()[1];
			int numFeatures = numColumns -1;
			
//			String [] returnAtomArray = new String[(numRows*numColumns)+2];
//			returnAtomArray[0]= viewName;
//			returnAtomArray[1]= Integer.toString(numFeatures);
			
//			int atomLoopCounter = 2;
//			for(int i = 0; i<numRows;i++){
//				returnAtomArray[atomLoopCounter]=featureData.get(i, 0).contentToString();//filepath
//				for(int k = 1; k< numColumns;k++){
//					Double.parseDouble(featureData.get(i, k).contentToString());
//				}
//				atomLoopCounter = atomLoopCounter+numColumns;
//			}
			
			
			for(int i = 0; i<numRows;i++){
				String sampleFilePath = featureDataMLCell.get(i, 0).contentToString().substring(7).replaceAll("'", "");//filepath
				System.out.println("sampleFilePath:"+sampleFilePath);
				double [] featureValues = new double [numFeatures];
				for(int k = 1; k< numColumns;k++){
					
//					System.out.println("Is Double Value: "+featureDataMLCell.get(i, k).isDouble());
//					System.out.println("Is Char Value: "+featureDataMLCell.get(i, k).isChar());
					System.out.println("Without Substring:"+featureDataMLCell.get(i, k).contentToString());
					
					featureValues[k-1]= new Double(featureDataMLCell.get(i, k).contentToString().substring(6));
					
					
					
//					System.out.println("featureValues: "+ featureValues.toString());
				}
				
				featureData.put(sampleFilePath, featureValues);
				
				double[] storedValues = featureData.get(sampleFilePath);
				for(int l = 0;l < storedValues.length; l++){
					System.out.println(storedValues[l]);
				}
			}
			
			
			
			
			
			
					} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
