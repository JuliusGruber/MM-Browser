
import com.cycling74.max.Atom;
import com.cycling74.max.MaxBox;
import com.cycling74.max.MaxObject;
import com.cycling74.max.MaxPatcher;

public class SymbolButton3 extends MaxObject {
	private MaxBox jsui;
	private MaxBox panel;
	private String filePath;
	private MaxPatcher parentPatcher;
	private int positionInBasket;
	private boolean isInBasket;
	private int yPositionInBasket;

	
	public SymbolButton3(MaxPatcher parentPatcher, int positionInBasket,  String filePath){
		this.positionInBasket = positionInBasket;
		this.parentPatcher =  parentPatcher;
		this.filePath = filePath;
		
		int xPosition  = 0;
		int yPosition = 50*positionInBasket;
	
		
		panel  = parentPatcher.newDefault( xPosition, yPosition, "panel",null);
		panel.send("size", new Atom [] { Atom.newAtom(50), Atom.newAtom (50)});
		panel.send("presentation", new Atom[] {Atom.newAtom(true)});
		panel.send("background", new Atom []{Atom.newAtom(true)});
		panel.send("rounded", new Atom []{Atom.newAtom(0)});
		
		
		jsui = parentPatcher.newDefault( xPosition +5, yPosition +5, "jsui",null);
		jsui.send("filename", new Atom []{ Atom.newAtom("C:/Users/Julius/Dropbox/Master/maxMSPpatches/symbolButtonScript.js")});
		jsui.send("size", new Atom [] { Atom.newAtom(40), Atom.newAtom (40)});
		jsui.send("presentation", new Atom[] {Atom.newAtom(true)});
		jsui.send("setFilePath", new Atom []{Atom.newAtom(filePath)});
		jsui.send("bang", null);
		
//		Atom [] arg2 = new Atom[] {Atom.newAtom(name)};
//		jsui.send("setName", arg2);
	}
	
	public SymbolButton3(MaxPatcher parentPatcher){
	
		this.parentPatcher =  parentPatcher;
		isInBasket = false;
		
		int xPosition  = 700;
		int yPosition = 0;
	
		
		panel  = parentPatcher.newDefault( xPosition, yPosition, "panel",null);
		panel.send("size", new Atom [] { Atom.newAtom(50), Atom.newAtom (50)});
		panel.send("presentation", new Atom[] {Atom.newAtom(true)});
		panel.send("background", new Atom []{Atom.newAtom(true)});
		panel.send("rounded", new Atom []{Atom.newAtom(0)});
		
		
		
		jsui = parentPatcher.newDefault( xPosition +5, yPosition +5, "jsui",null);
		jsui.send("filename", new Atom []{ Atom.newAtom("C:/Users/Julius/Dropbox/Master/maxMSPpatches/symbolButtonScript.js")});
		jsui.send("size", new Atom [] { Atom.newAtom(40), Atom.newAtom (40)});
		jsui.send("presentation", new Atom[] {Atom.newAtom(true)});
		jsui.send("bang", null);
		

	}
	
	


	public SymbolButton3(String filePath) {
		this.filePath = filePath;
	

	}
	

	
	public void showSymButton(String filePath, int positionInBasket){
		this.filePath = filePath;
		setInBasket(true);
		yPositionInBasket = positionInBasket*50;
		
		Atom[] sendAtomArray =  new Atom[]{Atom.newAtom(0),Atom.newAtom(yPositionInBasket),Atom.newAtom(50), Atom.newAtom(50)};
		panel.send("presentation_rect", sendAtomArray);
		
		sendAtomArray =  new Atom[]{Atom.newAtom(5),Atom.newAtom(yPositionInBasket+5),Atom.newAtom(40), Atom.newAtom(40)};
		jsui.send("setFilePath", new Atom []{Atom.newAtom(filePath)});
		jsui.send("setShape",new Atom []{ Atom.newAtom("quad")});
		jsui.send("presentation_rect", sendAtomArray);
	}
	
	
	public void hideSymButton(){
		filePath = "";
		setInBasket(false);
		yPositionInBasket = 0;
		
		Atom[] sendAtomArray =  new Atom[]{Atom.newAtom(700),Atom.newAtom(0),Atom.newAtom(50), Atom.newAtom(50)};
		panel.send("presentation_rect", sendAtomArray);
		
		sendAtomArray =  new Atom[]{Atom.newAtom(705),Atom.newAtom(5),Atom.newAtom(40), Atom.newAtom(40)};
		jsui.send("setFilePath", new Atom []{Atom.newAtom("")});
		jsui.send("setShape",new Atom []{ Atom.newAtom("quad")});
		jsui.send("presentation_rect", sendAtomArray);
	}


	
	public void moveButtonOneSlotDown(){
		yPositionInBasket  = yPositionInBasket + 50;
		panel.send("presentation_rect", new Atom[] {Atom.newAtom(0), Atom.newAtom(yPositionInBasket), Atom.newAtom(50), Atom.newAtom(50)});
		jsui.send("presentation_rect", new Atom[] {Atom.newAtom(5), Atom.newAtom(yPositionInBasket+5), Atom.newAtom(40), Atom.newAtom(40)});
	}
	
	public void moveButtonOneSlotUp(){
		
		yPositionInBasket = yPositionInBasket -50;
		
		panel.send("presentation_rect", new Atom[] {Atom.newAtom(0), Atom.newAtom(yPositionInBasket), Atom.newAtom(50), Atom.newAtom(50)});
		jsui.send("presentation_rect", new Atom[] {Atom.newAtom(5), Atom.newAtom(yPositionInBasket+5), Atom.newAtom(40), Atom.newAtom(40)});
	}
	
	public void moveToBasketPosition(int position){
		positionInBasket = position;
		int xPositionInBasket = 0;
		yPositionInBasket = position*50;
		
		panel.send("presentation_rect", new Atom[] {Atom.newAtom(xPositionInBasket), Atom.newAtom(yPositionInBasket), Atom.newAtom(50), Atom.newAtom(50)});
		jsui.send("presentation_rect", new Atom[] {Atom.newAtom(xPositionInBasket+5), Atom.newAtom(yPositionInBasket+5), Atom.newAtom(40), Atom.newAtom(40)});
	}
	
	




	public boolean isInBasket() {
		return isInBasket;
	}

	public void setInBasket(boolean isInBasket) {
		this.isInBasket = isInBasket;
	}
	

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

	public int getyPositionInBasket() {
		return yPositionInBasket;
	}

	public void setyPositionInBasket(int yPositionInBasket) {
		this.yPositionInBasket = yPositionInBasket;
	}


	
	
}
