

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.FileUtils;



import com.cycling74.max.Atom;
import com.cycling74.max.MaxBox;
import com.cycling74.max.MaxObject;
import com.cycling74.max.MaxPatcher;
import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;
//import com.mathworks.toolbox.javabuilder.MWArray;
//import com.mathworks.toolbox.javabuilder.MWClassID;
//import com.mathworks.toolbox.javabuilder.MWException;
//import com.mathworks.toolbox.javabuilder.MWNumericArray;

//import com.mathworks.toolbox.javabuilder.*;

//import doClustering.*;
import weka.clusterers.HierarchicalClusterer;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;



public class ViewsManagement2 extends MaxObject {
	private ArrayList<View2> viewsList;
	//private ArrayList<String> viewNamesList;
	private HashMap <String, Sample> inBasketLookUp;
	private HashMap<String,Sample> polyAdressLookUp;
	
	private MaxPatcher parentPatcher;
	private MaxBox sonoAreaSend;
	
	private HierarchicalClusterer clusterer;
	
	private int curNumClusters;
	private boolean psychoAcousticClusterMode;
	private boolean distanceClusterMode;
	
	public ViewsManagement2(){
		viewsList = 	new ArrayList<View2>();
		//viewNamesList  = new ArrayList<String>();
		inBasketLookUp = new HashMap<String, Sample>();
		polyAdressLookUp = new HashMap<String,Sample>();
		
		parentPatcher = this.getParentPatcher();
		//sonoAreaSend = parentPatcher.getNamedBox("sonoAreaSend");
		sonoAreaSend = parentPatcher.getNamedBox("viewData_views_sonoArea");

		
		for (int i = 0; i<10; i++){
			String viewName = "view"+ i;
			//viewNamesList.add(viewName);
			View2 thisView =  new View2(i, parentPatcher, viewName);
			viewsList.add(thisView);
			
		}
		
		
		
		
		clusterer = new HierarchicalClusterer();
		
		 String[] options = new String[2];
		 options[0] = "-L";
		 options[1] = "WARD";
		
		
		try {
			clusterer.setOptions(options);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	
		curNumClusters = 0;
		psychoAcousticClusterMode = true;
		distanceClusterMode = false;
		
		
	}
	
	
	
	
	public void loadFolderFeatureData(String dirName){
		post("loadFolderFeatureData() method was called: "+ dirName);
		
		File dir = new File(dirName);
		String[] extensions = new String[] {"mat" };
		Collection<File> matFilePathColl =   FileUtils.listFiles(dir, extensions, true);
		System.out.println("There are "+matFilePathColl.size()+" mat files in this folder");
		
		
		for(File file : matFilePathColl){
			String filePath = null;
				try {
					filePath = 	file.getCanonicalPath();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				String viewNameMat = file.getName().replace(".mat", "");
//				post("viewName from mat file: "+viewNameMat);
				
//				check if view with name == viewName from mat file
				if(checkViewNameMatIsInViewsList(viewNameMat)){
//					post("viewName from mat file: "+viewNameMat+" is in views list");
					loadViewFeatureDataFromMatFile(filePath,viewNameMat);
				}	
					
				
				
			
				
				
		}
	}
	
	private void loadViewFeatureDataFromMatFile(String matFilePath, String viewNameMat){
		for(View2 view : viewsList){
				if(	view.getViewName().equals(viewNameMat)){
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
//					System.out.println(content);
					MLCell featureDataMLCell = (MLCell) content.get("featureData");
					int numRows = featureDataMLCell.getDimensions()[0];
					int numColumns = featureDataMLCell.getDimensions()[1];
					int numFeatures = numColumns -1;
					
					
					
					for(int i = 0; i<numRows;i++){
						String sampleFilePath = featureDataMLCell.get(i, 0).contentToString().substring(7).replaceAll("'", "");//filepath
						System.out.println("sampleFilePath: "+sampleFilePath);
						double[] featureValues = new double[numFeatures];
						for(int k = 1; k< numColumns;k++){
//							System.out.println("Without Substring: "+ new Double(featureDataMLCell.get(i, k).contentToString()));
							featureValues[k-1]= new Double (featureDataMLCell.get(i, k).contentToString());
							
//							System.out.println("featureValues:"+ featureDataMLCell.get(i, k).contentToString());
						}
						
						featureData.put(sampleFilePath, featureValues);
						
//						double [] storedValues = featureData.get(sampleFilePath);
//						for(int l = 0;l < storedValues.length; l++){
//							System.out.println("value: "+ storedValues[l]);
////							Double test = new Double(-0.0000000000000023456789);
////							System.out.println("test: "+test);
//						}
					}
					
					
					
					view.setFeatureData(featureData);
				break;
			}
		}
	}
	
	private boolean checkViewNameMatIsInViewsList(String viewNameMat){
		boolean isInList = false;
		for(View2 view : viewsList){
			post("name check view: "+view.getViewName());
			if(	view.getViewName().equals(viewNameMat)){
				isInList = true;
				break;
			}
		}
		post("check if viewNameMat is in viewsList: "+isInList);
		return isInList;
		
	}
	
	public void setClusterMode(int modeNumber){
		post("setClusterMode() method was called: "+ modeNumber);
		
		if(modeNumber == 0){
			psychoAcousticClusterMode = true;
			distanceClusterMode = false;
		}
		
		if(modeNumber == 1){
			psychoAcousticClusterMode = false;
			distanceClusterMode = true;
		}
		
		if(curNumClusters >= 2){
			if(psychoAcousticClusterMode){
				setNumberOfClusters(curNumClusters);
			}
			if(distanceClusterMode){
				setNumberOfClusters(curNumClusters);
			}
		}
		
	}
	
	public void setNumberOfClusters(int numClusters){
		post("setNumbersOfClusters() method was called: "+ numClusters);
		
		curNumClusters  = numClusters;
		
		
		if(psychoAcousticClusterMode){
		post("distanceClusterMode: "+distanceClusterMode);
//		get selected view
		for(View2 view : viewsList){
//			post("view "+view.getViewName()+" isSelected: "+view.isSelected());
			if(view.isSelected()){
//				post("found the selcted view: "+ view.getViewName());
				
//				load the feature data
				LinkedHashMap<String,double []> featureData = view.getFeatureData();
				if(featureData != null){
					LinkedHashMap<String, int[]> pathClusterNumber = calculateClusterNumbers(featureData, numClusters);
					
					
					
//					LinkedHashMap<String, int[]> pathClusterNumber = fakecalculateClusterNumbers(featureData, numClusters);
					
//					for (Entry<String, int[]> entry : pathClusterNumber.entrySet()) {
//						post(entry.getKey()+": "+entry.getValue()[0]);
//					}
										
					Atom [] clusterInfoAtomArray = getclusterInfoAtomArray(pathClusterNumber, numClusters);
					sonoAreaSend.send("list", clusterInfoAtomArray);
					break;
				}else{
					post(view.getViewName()+" feature data is NULL");
				}
			}
		
			}
		}
		
		
		if(distanceClusterMode){
			post("distanceClusterMode: "+distanceClusterMode);
			
			for(View2 view : viewsList){
//				post("view "+view.getViewName()+" isSelected: "+view.isSelected());
				if(view.isSelected()){
//					post("found the selcted view: "+ view.getViewName());
					
//					load the feature data
					LinkedHashMap<String,double []> positionData = view.getPositionData();
					if(positionData != null){
						
//						print positionData
//						for (Entry<String, double[]> entry : positionData.entrySet()) {
//						 	String filePath = entry.getKey();
//						 	double [] posArray = entry.getValue();
//						 	double xPos = posArray[0];
//						 	double yPos = posArray[1];
//						 	post(filePath+" xPos: "+xPos+" yPosition: "+yPos);
//						}
						
						
						
						LinkedHashMap<String, int[]> pathClusterNumber = calculateClusterNumbersDistance(positionData, numClusters);
						
						
						

											
						Atom [] clusterInfoAtomArray = getclusterInfoAtomArray(pathClusterNumber, numClusters);
						sonoAreaSend.send("list", clusterInfoAtomArray);
						break;
					}else{
						post(view.getViewName()+" feature data is NULL");
					}
				}
			
				}
		}
		
		
	}
	
	private LinkedHashMap<String, int[]> calculateClusterNumbers(LinkedHashMap<String,double []> featureData, int numClusters){
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
		
		
		

		
		clusterer.setNumClusters(numClusters);
		
		try {
			clusterer.buildClusterer(instances);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
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
//		for (Entry<String, int[]> entry : filePathsClusterNumbers.entrySet()) {
//			String filePath = entry.getKey();
//			int [] clustNumArray = entry.getValue();
//			int clustNum = clustNumArray[0];
//			System.out.println(filePath+": "+clustNum);
//		}
//		
		return filePathsClusterNumbers;
	}
	

	private LinkedHashMap<String, int[]> calculateClusterNumbersDistance(LinkedHashMap<String,double []> positionData, int numClusters){
		
		int numRows = positionData.size();
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for(int i = 0; i < 2; i++){
			attributes.add(new Attribute("feature"+i));
		}
		
//		prepare Instances
		Instances instances = new Instances("myInstances", attributes , numRows);
		for (Entry<String, double[]> entry : positionData.entrySet()) {
		 	double [] value = entry.getValue();
		 	Instance singleInstance = new DenseInstance(2); 
		    
		    for(int l = 0; l< value.length; l++){
		    	singleInstance.setValue(l, value[l]);
		    }
			instances.add(singleInstance);
		}
		
//		build clusterer
		clusterer.setNumClusters(numClusters);
		
		try {
			clusterer.buildClusterer(instances);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
//		Do the clustering
		
		LinkedHashMap<String, int[]> filePathsClusterNumbers = new LinkedHashMap<String, int[]>();
	
		
		for (Entry<String, double[]> entry : positionData.entrySet()) {
		    String key = entry.getKey();// = filePath
		    double [] value = entry.getValue();
//		    System.out.println(key);
		    
		   
		    
		    Instance singleInstance = new DenseInstance(2); 
		    
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
		
		
		
		return filePathsClusterNumbers;
		
	}
	
	private LinkedHashMap<String, int[]> fakecalculateClusterNumbers(LinkedHashMap<String,double []> featureData, int numClusters){
		LinkedHashMap<String, int[]> fileClusterNumber = new LinkedHashMap<String,int []> ();
		
		
		for (Entry<String, double[]> entry : featureData.entrySet()) {
			String filePath = entry.getKey();
			
			
			int randomNum = 1;
			if(numClusters != 0){
				randomNum= ThreadLocalRandom.current().nextInt(1, numClusters + 1);
			}
//			post("Random Number: "+randomNum);
			int [] randomNumArray = new int [1];
			randomNumArray[0] = randomNum;
			fileClusterNumber.put(filePath, randomNumArray);
	
		}
		
		return fileClusterNumber;
	}
	
	
//	private void calculateClusterNumbers(LinkedHashMap<String,double []> featureData, int numClusters){
//		
//		
//		int numRows = featureData.size();
//		Entry<String, double[]> entryLength = featureData.entrySet().iterator().next();
//		double [] testLength = entryLength.getValue();
//		int numFeatures = testLength.length;
//		
//		
//		post("numFeatures: " + numFeatures);
//		
//		double [][] nxd = new double [numRows][numFeatures];
//		int loopCounter = 0;
//		
//		for (Entry<String, double[]> entry : featureData.entrySet()) {
//		    String key = entry.getKey();
//		    double [] value = entry.getValue();
////		    System.out.println(key);
//		    
//		    for(int l = 0; l< value.length; l++){
//		    	nxd [loopCounter][l] = value[l];
////		    	 System.out.println(nxd [loopCounter][l]);
//		    }
//		   loopCounter++;
//		}
//		
//		
//		
//		
//		MWNumericArray nxdMWNummeric =  new MWNumericArray(nxd, MWClassID.DOUBLE);
//		
//		MWNumericArray numClustersMWNummericArray =  new MWNumericArray(numClusters, MWClassID.INT64);
//		
//		Object[] result = null;
//	    Class1 cluster = null;
//
//        try {
//			
//        	cluster = new Class1();
//        	
//        	if(cluster== null){
//        		post("cluster is NULL");
//        	}
//        	
//        	
//			result = cluster.doClustering(1, nxdMWNummeric, numClustersMWNummericArray);
////			result = cluster.doClustering(1, nxd, numClusters);
//			
//			
//			
//			MWClassID clsid = ((MWArray)result[0]).classID();
//			System.out.println(clsid);
//			
//			int [] dim = ((MWArray)result[0]).getDimensions();
//			System.out.println("Dimenions: "+dim[0]+" "+dim[1]);
//			
//			double [] values = (double[]) ((MWArray)result[0]).getData();
////			System.out.println("value1: "+values[0]);
//			
//			
//		
//			
//			
//			for (int m= 0; m< values.length;m++){
//				
//				System.out.println("singleClusterNumber "+m+": "+values[m]);
//			
//			}
//			
//			
//		}
//        catch (MWException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  
//        finally{
//         MWArray.disposeArray(nxd);
//         MWArray.disposeArray(result);
////         cluster.dispose();
//        }
//		
//		
//		
//		
//		
//		
//		
//	}
	
	
	
	
	
	private Atom[] getclusterInfoAtomArray(LinkedHashMap<String,int []> pathClusterNumber, int numClusters){
		Atom [] returnAtomArray = null;
		
		int numFiles = pathClusterNumber.size();
		
		returnAtomArray = new Atom [numFiles*2 +2];
		String messageString = "changeClusterColor";
		returnAtomArray[0]= Atom.newAtom(messageString);
		returnAtomArray[1]= Atom.newAtom(numClusters);
		
		
		int loopCounter = 2;
		for (Entry<String, int[]> entry : pathClusterNumber.entrySet()) {
			String filePath = entry.getKey();
			String trimedFilePath = filePath.trim();
			int clusterNum = entry.getValue()[0];
			
			returnAtomArray[loopCounter]= Atom.newAtom(trimedFilePath);
			returnAtomArray[loopCounter+1]= Atom.newAtom(clusterNum);
			loopCounter = loopCounter+2;
		}
		
		post("AtomArray:");
		
		for(int i = 0; i< returnAtomArray.length; i = i+2){
			post(returnAtomArray[i]+": "+returnAtomArray[i+1]);
		}
		

		return returnAtomArray;
	}
	
	
	
	
	
	private Atom [] getAtomArrayFromSamplesInBasket(){
		Atom [] returnArray  = new Atom[inBasketLookUp.size()*2+1];
		returnArray[0]= Atom.newAtom("setSelectedSamples");
		int loopCounter = 1;
		for(Entry<String, Sample> entry : inBasketLookUp.entrySet()){
		   Sample sample = entry.getValue();
		   String filePath = sample.getFilePath();
		   String shape = sample.getShape();
		   returnArray[loopCounter] = Atom.newAtom(filePath);
		   returnArray[loopCounter+1] = Atom.newAtom(shape);
		   loopCounter = loopCounter+2;
		   
		}


	

		return returnArray;
		
	}
	
	
	public void setViewData(Atom [] viewData){
		post("VIEWS MANAGEMENT setViewData() method was called");
		for(View2 view : viewsList){
			if(!view.isUsed()){
//				post("not used view found: "+view.getViewName());
				view.setUsed(true);
				view.setViewName(viewData[1].getString());
//				post("View Name was set to: "+viewData[1].getString());
				ArrayList<Sample> sampleList = createSamplesArrayList(viewData);
				view.setSampleList(sampleList);
				view.getTitleMessageBox().send("set",new Atom []{viewData[1]});//set the view title
				view.getJsui().send("list",viewData);
				
//				int viewID = view.getViewID();
//				Atom [] viewIDdata = new Atom []{Atom.newAtom("setViewID"), Atom.newAtom(viewID)};
//				view.getJsui().send("list", viewIDdata);
				
//				view.getJsui().send("list",getAtomArrayFromSamplesInBasket() );
				break;
			}
		}
	
		
	}
	
	
	
	public void resetAllViews(){
		System.out.println("resetAllViews() method was called");
		for (View2 view : viewsList){
			view.getJsui().send("clearView", null);
			view.setUsed(false);
			view.setSelected(false);
			view.setSampleList(new ArrayList<Sample>());
			view.getViewPanel().send("bgfillcolor", new Atom []{Atom.newAtom(1), Atom.newAtom (1),Atom.newAtom(1), Atom.newAtom (1)});
			String viewName = "view"+view.getViewID();
			view.getTitleMessageBox().send("set",new Atom []{Atom.newAtom(viewName)});
		}
	}
	
	public void resetAllSamplesToUntouchedSampleColor(){
		post("resetAllSamplesToUntouchedSampleColor() was called");
		for (int i = 0; i < this.viewsList.size(); i++) {
			viewsList.get(i).getJsui().send("resetAllSamplesToUntouchedSampleColor", null);
		}
		
		//MaxBox sonoAreaSend = parentPatcher.getNamedBox("sonoAreaSend");
		sonoAreaSend.send("resetAllSamplesToUntouchedSampleColor", null);
		
		inBasketLookUp = new HashMap<String, Sample>();
	}
	
	
	public void selectSamplesInAllViews(Atom  [] filePathAndShapeArray){
		post("selectSamplesInAllViews() method was called");
		
		//send selected Samples to all views
		for (int i = 0; i < this.viewsList.size(); i++) {
			viewsList.get(i).getJsui().send("list", filePathAndShapeArray);
			
		}
		//MaxBox sonoAreaSend = parentPatcher.getNamedBox("sonoAreaSend");
		sonoAreaSend.send("list", filePathAndShapeArray);
		
		//add selected Samples to inBasketLookUp
		for (int i =1; i < filePathAndShapeArray.length;i = i+2){
			Sample curSample  =  new Sample(filePathAndShapeArray[i].getString(),filePathAndShapeArray[i+1].getString() );
			inBasketLookUp.put(curSample.getFilePath(), curSample);
		}
		
	}
	
	public void unSelectSamplesInAllViews(Atom [] filePathArray){
		post("VM unSelectSamplesInAllViews() method was called");
		
		//send  unselected Samples to all Views
		for (int i = 0; i < this.viewsList.size(); i++) {
			viewsList.get(i).getJsui().send("list", filePathArray);
		}
		
		//MaxBox sonoAreaSend = parentPatcher.getNamedBox("sonoAreaSend");
		
//		send the unselect info to the sonoArea
		sonoAreaSend.send("list", filePathArray);
		
		
		//remove unselected Samples from inBasketLookUp
		for (int i =1; i < filePathArray.length;i++){
			Sample curSample  =  new Sample(filePathArray[i].getString());
			inBasketLookUp.remove(curSample.getFilePath());
		}
	}
	
	private ArrayList<Sample> createSamplesArrayList(Atom[] sampleAtomArray) {
		
		ArrayList <Sample> returnList = new ArrayList<Sample>();
		
		for (int i = 2; i < sampleAtomArray.length; i = i+4){// starting from 2because the sampleAtomAray has the methodName + vieName as  the first two argument 
			String filePath = sampleAtomArray[i].getString();
			String fileName = sampleAtomArray[i+1].getString();
			float xPosition = sampleAtomArray[i+2].toFloat();
			float yPosition = sampleAtomArray[i+3].toFloat();
			
			//post("addView x: "+xPosition);
			//post("addView y: "+yPosition);
			
			//look up the polyAdress
			Sample lookUpSample = polyAdressLookUp.get(filePath);
			if(lookUpSample == null){
				post("lookUpSample: "+lookUpSample);
				post("file not found in polyLookUp");
			}
			int polyAdress = lookUpSample.getPolyAdress();
			post("polyAdress: "+polyAdress+" filePath: "+filePath);
			Sample curSample = new Sample(filePath, fileName, xPosition, yPosition, polyAdress, false, "circle");
			

			returnList.add(curSample);
		}
		
		
		return returnList;
	
		
	}
	
	public void getSelectedView(){
		for (int i = 0; i < this.viewsList.size(); i++) {
		
			if(viewsList.get(i).isSelected()){
				post("Currently selected view: "+viewsList.get(i).getViewName());
			}
			
		}
	}
	
	
	public void setSelectedView(int viewID){
		
		System.out.println("VIEW MANAGER setSelctedView() method was called with ID: " +viewID);
		
		//int indexSelectedView  = viewNamesList.indexOf(viewName);
		//View2 selectedView  = viewsList.get(indexSelectedView);
		
		View2 selectedView  = getViewByID(viewID);
		selectedView.getViewPanel().send("bgfillcolor", new Atom []{Atom.newAtom(0.69), Atom.newAtom (0.69),Atom.newAtom(0.69), Atom.newAtom (1)});
		selectedView.setSelected(true);
		for(View2 view : viewsList){
			if(view.getViewID() != viewID){
				view.setSelected(false);
				view.getViewPanel().send("bgfillcolor", new Atom []{Atom.newAtom(1), Atom.newAtom (1),Atom.newAtom(1), Atom.newAtom (1)});
			}
		}
	
	}
	
	public void sendSelectedViewDataToSonoArea(int viewID){
		//int indexSelectedView  = viewNamesList.indexOf(viewName);
		//View2 selectedView  = viewsList.get(indexSelectedView);
		
		View2 selectedView  = getViewByID(viewID);
		ArrayList<Sample> sampleList = selectedView.getSampleList();
		Atom [] sonoAreaAtomArray = createSonoAreaAtomArray(sampleList);
		
	
//		MaxBox sonoAreaSend = parentPatcher.getNamedBox("viewData_views_sonoArea");
		sonoAreaSend.send("list", sonoAreaAtomArray);
		
		if(curNumClusters >= 2){
			setNumberOfClusters(curNumClusters);
		}
		
	}
	
	
	private View2 getViewByID(int viewID){
		View2 returnView = null;
		for(View2 view : viewsList){
			if(view.getViewID()== viewID){
				returnView = view;
				break;
			}
		}
		
		return returnView;
	}
	
	
	
	
	private Atom [] createSonoAreaAtomArray(ArrayList <Sample> sampleList){
		Atom [] returnAtomArray = null;
		
		if(sampleList == null){
			post("sampleList is null");
		}else{
		
		returnAtomArray = new Atom [sampleList.size()*7 +1];
		String messageString = "setSampleData";
		returnAtomArray[0]= Atom.newAtom(messageString);
		
		int loopCounter = 1;
		for(Sample sample : sampleList){
			String filePath = sample.getFilePath();
			
//			post("SAMPLE: "+ filePath);
			
			String fileName = sample.getFileName();
			int polyAdress = sample.getPolyAdress();
			double x = sample.getxPosition();
			double y = sample.getyPosition();
			boolean isInBasket = false;
			String shape = sample.getShape();
			if(inBasketLookUp.containsKey(sample.getFilePath())){
			 isInBasket = true;
			 Sample inBasketSample = inBasketLookUp.get(sample.getFilePath());
			 shape = inBasketSample.getShape();
			}
			returnAtomArray[loopCounter]= Atom.newAtom(filePath);
			returnAtomArray[loopCounter+1]= Atom.newAtom(fileName);
			returnAtomArray[loopCounter+2]= Atom.newAtom(polyAdress);
			returnAtomArray[loopCounter+3]= Atom.newAtom(x);
			returnAtomArray[loopCounter+4]= Atom.newAtom(y);
			returnAtomArray[loopCounter+5]= Atom.newAtom(isInBasket);
			returnAtomArray[loopCounter+6]= Atom.newAtom(shape);
			
			loopCounter = loopCounter+7;
		}
		
		}
		
		
		return returnAtomArray;
			
	}
	
	//called from Threader before the FE starts
	public void setPolyLookUp(Atom [] polyNoFilePath) {
		post("setPolyLookUp() method was called");
		
		HashMap <String, Sample> sampleMap =  new HashMap <String, Sample>();
		for(int i = 0; i< polyNoFilePath.length; i= i+2){
			post("VIEWS MANAGEMENT polyNumber: "+polyNoFilePath[i]+ " filePath: "+polyNoFilePath[i+1]);
			int polyNumber =  polyNoFilePath[i].getInt();
			String filePath =  polyNoFilePath[i+1].getString();
			Sample curSample =  new Sample (polyNumber, filePath);
			sampleMap.put(curSample.getFilePath(), curSample);
		}
	
		this.setPolyAdressLookUp(sampleMap);
	}


	public ArrayList<View2> getViewsList() {
		return viewsList;
	}


	public void setViewsList(ArrayList<View2> viewsList) {
		this.viewsList = viewsList;
	}


//	public ArrayList<String> getViewNamesList() {
//		return viewNamesList;
//	}
//
//
//	public void setViewNamesList(ArrayList<String> viewNamesList) {
//		this.viewNamesList = viewNamesList;
//	}


	public HashMap<String, Sample> getInBasketLookUp() {
		return inBasketLookUp;
	}


	public void setInBasketLookUp(HashMap<String, Sample> inBasketLookUp) {
		this.inBasketLookUp = inBasketLookUp;
	}


	public HashMap<String, Sample> getPolyAdressLookUp() {
		return polyAdressLookUp;
	}


	public void setPolyAdressLookUp(HashMap<String, Sample> polyAdressLookUp) {
		this.polyAdressLookUp = polyAdressLookUp;
	}
	
	
}
