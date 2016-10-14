import com.cycling74.max.Atom;
import com.cycling74.max.MaxBox;
import com.cycling74.max.MaxObject;
import com.cycling74.max.MaxPatcher;

public class SymbolButton extends MaxObject {
	private MaxBox jsui;
	private MaxBox panel;
	private String filePath;
	private MaxPatcher parentPatcher;
	private int xPosition;
	private int yPosition;
	private int xPositionInBasket;
	private int yPositionInBasket;
	
	public SymbolButton(MaxPatcher parentPatcher, int xPosition, int yPosition, String filePath){
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		xPositionInBasket = 0;
		yPositionInBasket = 0;
		this.parentPatcher =  parentPatcher;
		this.filePath = filePath;
		
	
		
		panel  = parentPatcher.newDefault( xPosition, yPosition, "panel",null);
		panel.send("size", new Atom [] { Atom.newAtom(50), Atom.newAtom (50)});
		panel.send("presentation", new Atom[] {Atom.newAtom(true)});
		panel.send("background", new Atom []{Atom.newAtom(true)});
		panel.send("rounded", new Atom []{Atom.newAtom(0)});
		
		
		jsui = parentPatcher.newDefault( xPosition +5, yPosition +5, "jsui",null);
		jsui.send("filename", new Atom []{ Atom.newAtom("C:/Users/Julius/Dropbox/Master/maxMSPpatches/symbolButtonScript.js")});
		jsui.send("size", new Atom [] { Atom.newAtom(40), Atom.newAtom (40)});
		jsui.send("presentation", new Atom[] {Atom.newAtom(true)});
		//jsui.send("setFilePath", new Atom []{Atom.newAtom(filePath)});
		jsui.send("bang", null);
		
//		Atom [] arg2 = new Atom[] {Atom.newAtom(name)};
//		jsui.send("setName", arg2);
	}
	
	
	public SymbolButton(String filePath) {
		this.filePath = filePath;
	

	}
	
	
	public void showSymButton(String filePath, int positionInBasket){
		this.filePath = filePath;
		xPositionInBasket = 0;
		yPositionInBasket = positionInBasket*50;
		Atom[] sendAtomArray =  new Atom[]{Atom.newAtom(xPositionInBasket),Atom.newAtom(yPositionInBasket),Atom.newAtom(50), Atom.newAtom(50)};
		panel.send("presentation_rect", sendAtomArray);
		
		sendAtomArray =  new Atom[]{Atom.newAtom(xPositionInBasket+5),Atom.newAtom(yPositionInBasket+5),Atom.newAtom(40), Atom.newAtom(40)};
		jsui.send("setFilePath", new Atom []{Atom.newAtom(filePath)});
		jsui.send("setShape",new Atom []{ Atom.newAtom("quad")});
		jsui.send("presentation_rect", sendAtomArray);
	}
	
	
	public void hideSymButton(String filePath){
		this.filePath = "";
		Atom[] sendAtomArray =  new Atom[]{Atom.newAtom(xPosition),Atom.newAtom(yPosition),Atom.newAtom(50), Atom.newAtom(50)};
		panel.send("presentation_rect", sendAtomArray);
		
		sendAtomArray =  new Atom[]{Atom.newAtom(xPosition+5),Atom.newAtom(yPosition+5),Atom.newAtom(40), Atom.newAtom(40)};
		jsui.send("setFilePath", new Atom []{Atom.newAtom("")});
		jsui.send("setShape",new Atom []{ Atom.newAtom("quad")});
		jsui.send("presentation_rect", sendAtomArray);
	}


	public void moveUpOneSlot(){
		xPositionInBasket = 0;
		yPositionInBasket = yPositionInBasket -50;
		
		panel.send("presentation_rect", new Atom[] {Atom.newAtom(xPositionInBasket), Atom.newAtom(yPositionInBasket), Atom.newAtom(50), Atom.newAtom(50)});
		jsui.send("presentation_rect", new Atom[] {Atom.newAtom(xPositionInBasket+5), Atom.newAtom(yPositionInBasket+5), Atom.newAtom(40), Atom.newAtom(40)});
	}

//	public void moveButtonUp(){
//		
//		post("Moving symButton for file: "+filePath);
//		
//		int [] panelRect = panel.getRect();
//		int [] jsuiRect  = jsui.getRect();
//		
//		post("panelRect: "+panelRect[0]+" "+panelRect[1]+" "+panelRect[2]+" "+panelRect[3]);
//		post("jsuiRect: "+jsuiRect[0]+" "+jsuiRect[1]+" "+jsuiRect[2]+" "+jsuiRect[3]);
//	
//		panel.send("presentation_rect", new Atom[] {Atom.newAtom(panelRect[0]), Atom.newAtom(panelRect[1]-50), Atom.newAtom(50), Atom.newAtom(50)});
//		jsui.send("presentation_rect", new Atom[] {Atom.newAtom(jsuiRect[0]), Atom.newAtom(jsuiRect[1]-50), Atom.newAtom(40), Atom.newAtom(40)});
//
//	}




	public MaxBox getJsui() {
		return jsui;
	}


	public void setJsui(MaxBox jsui) {
		this.jsui = jsui;
	}


	public MaxBox getPanel() {
		return panel;
	}


	public void setPanel(MaxBox panel) {
		this.panel = panel;
	}


	public String getFilePath() {
		return filePath;
	}


	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


	public MaxPatcher getParentPatcher() {
		return parentPatcher;
	}


	public void setParentPatcher(MaxPatcher parentPatcher) {
		this.parentPatcher = parentPatcher;
	}


	public int getXPosition() {
		return xPosition;
	}


	public void setXPosition(int xPosition) {
		this.xPosition = xPosition;
	}


	public int getYPosition() {
		return yPosition;
	}


	public void setYPosition(int yPosition) {
		this.yPosition = yPosition;
		
	}


	public int getxPositionInBasket() {
		return xPositionInBasket;
	}


	public void setxPositionInBasket(int xPositionInBasket) {
		this.xPositionInBasket = xPositionInBasket;
	}


	public int getyPositionInBasket() {
		return yPositionInBasket;
	}


	public void setyPositionInBasket(int yPositionInBasket) {
		this.yPositionInBasket = yPositionInBasket;
	}
	
	
	
}
