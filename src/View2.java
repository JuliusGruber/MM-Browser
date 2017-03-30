


import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.cycling74.max.*;




public class View2 extends MaxObject {
	
	private MaxBox jsui;
	private MaxPatcher parentPatcher;
	private boolean isSelected;
	private boolean isUsed;
	private int viewID;
	private String viewName;
	private MaxBox viewPanel;
	private MaxBox titleMessageBox;
	private ArrayList<Sample> sampleList;
	private LinkedHashMap<String,double []> featureData;


	
	
	public View2(int viewID, MaxPatcher viewParentPatcher, String viewName) {
		parentPatcher =viewParentPatcher;
		this.viewID = viewID;
		this.viewName = viewName;
		
		isSelected = false;
		isUsed  = false;
		
		int xPosition = 190*viewID;
		int yPosition = 0;
		
		this.sampleList = null;
		 
		viewPanel = parentPatcher.newDefault( xPosition, yPosition, "panel", null);
		viewPanel.send("size", new Atom [] { Atom.newAtom(180), Atom.newAtom(210)});
		viewPanel.send("bgfillcolor", new Atom []{Atom.newAtom(1), Atom.newAtom (1),Atom.newAtom(1), Atom.newAtom (1)});
		viewPanel.send("ignoreclick", new Atom []{Atom.newAtom(1)});
		viewPanel.send("presentation",new Atom []{Atom.newAtom(true)});
		viewPanel.send("background",new Atom []{Atom.newAtom(1)});
		
		titleMessageBox  = parentPatcher.newDefault( xPosition + 10, yPosition +10, "message", null);
		titleMessageBox.send("presentation",new Atom []{Atom.newAtom(true)});
		titleMessageBox.send("set",new Atom []{Atom.newAtom(viewName)});
		titleMessageBox.send("textjustification",new Atom []{Atom.newAtom(1)});
		titleMessageBox.send("size", new Atom [] { Atom.newAtom(160), Atom.newAtom (30)});
		
		jsui = parentPatcher.newDefault( xPosition+10, yPosition+40, "jsui",null);
		jsui.send("filename", new Atom []{ Atom.newAtom("C:/Users/Julius/Dropbox/Master/maxMSPpatches/viewScript.js")});
		jsui.send("size", new Atom [] { Atom.newAtom(160), Atom.newAtom (160)});
		jsui.send("presentation", new Atom[] {Atom.newAtom(true)});
		jsui.send("setName", new Atom[] {Atom.newAtom(viewName)});
		jsui.send("setViewID" , new Atom[]{Atom.newAtom(viewID)});
		
	}
	
	
	public LinkedHashMap<String, double[]> getPositionData(){
		LinkedHashMap<String, double[]> returnPositionData =  new LinkedHashMap<String, double[]>();
		for(Sample sample : sampleList){
			String filePath = sample.getFilePath();
			double xPosition = sample.getxPosition();
			double yPosition = sample.getyPosition();
			double[] positionArray = new double[2];
			positionArray[0] = xPosition;
			positionArray[1] = yPosition;
			returnPositionData.put(filePath, positionArray);
		}
		
		
		return returnPositionData;
	}

	public LinkedHashMap<String, double[]> getFeatureData() {
		return featureData;
	}




	public void setFeatureData(LinkedHashMap<String, double[]> featureData) {
		this.featureData = featureData;
	}




	public MaxBox getJsui() {
		return jsui;
	}

	public void setJsui(MaxBox jsui) {
		this.jsui = jsui;
	}

	public MaxPatcher getParentPatcher() {
		return parentPatcher;
	}

	public void setParentPatcher(MaxPatcher parentPatcher) {
		this.parentPatcher = parentPatcher;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public int getViewID() {
		return viewID;
	}

	public void setViewID(int viewNumber) {
		this.viewID = viewNumber;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public MaxBox getViewPanel() {
		return viewPanel;
	}

	public void setViewPanel(MaxBox viewPanel) {
		this.viewPanel = viewPanel;
	}

	public ArrayList<Sample> getSampleList() {
		return sampleList;
	}

	public void setSampleList(ArrayList<Sample> sampleList) {
		this.sampleList = sampleList;
	}


	public MaxBox getTitleMessageBox() {
		return this.titleMessageBox;
	}


	public void setTitleMessageBox(MaxBox titleMessageBox) {
		this.titleMessageBox = titleMessageBox;
	}


	public boolean isUsed() {
		return isUsed;
	}


	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}


	

	
	
}
