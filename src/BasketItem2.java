import com.cycling74.max.Atom;
import com.cycling74.max.MaxBox;
import com.cycling74.max.MaxObject;
import com.cycling74.max.MaxPatcher;

public class BasketItem2 extends MaxObject{
	private String filePath;
	private MaxBox playlistObject;
	private SymbolButton symButton;
	private MaxBox dict;
	private MaxBox dictListenerButton;
	private MaxBox dictListener;
	private MaxBox audioPatcherReceiverLeft;
	private MaxBox audioPatcherReceiverRight;
	private int xPosition;
	private int yPosition;
	private int xPositionInBasket;
	private int yPositionInBasket;
	private boolean isInBasket;
	
	public BasketItem2(String filePath, MaxPatcher parentPatcher, int itemNumber){
		isInBasket = false;
		this.filePath  =filePath;
		audioPatcherReceiverLeft  = parentPatcher.getNamedBox("audioPatcherReceiverLeft");
		audioPatcherReceiverRight =  parentPatcher.getNamedBox("audioPatcherReceiverRight");
			
		 xPosition = 450;
		 yPosition = itemNumber*50;
		 xPositionInBasket  =0;
		 yPositionInBasket = 0;
		 
		symButton = new SymbolButton(parentPatcher,xPosition, yPosition ,filePath);
		
		playlistObject = parentPatcher.newDefault( xPosition+50, yPosition, "playlist~",null);
		playlistObject.send("size", new Atom [] { Atom.newAtom(400), Atom.newAtom (50)});
		//playlistObject.send("append", new Atom [] { Atom.newAtom(filePath)});
		playlistObject.send("waveformdisplay", new Atom [] { Atom.newAtom(0)});
		playlistObject.send("clipheight", new Atom [] { Atom.newAtom(50)});
		playlistObject.send("allowreorder", new Atom [] { Atom.newAtom(0)});
		playlistObject.send("presentation", new Atom[] {Atom.newAtom(true)});
		//connect the audio outputs
		parentPatcher.connect(playlistObject, 0,audioPatcherReceiverLeft , 0);
		parentPatcher.connect(playlistObject, 1,audioPatcherReceiverRight , 0);
		
		dict  = parentPatcher.newDefault(xPosition +600, yPosition,"dict", null);
		parentPatcher.connect(playlistObject, 4, dict, 0);
		//dict.send("name", new Atom[]{Atom.newAtom(filePath)});
		
		dictListenerButton = parentPatcher.newDefault(xPosition +700, yPosition, "button", null);
		parentPatcher.connect(dict, 0, dictListenerButton, 0);
		
		dictListener = parentPatcher.newDefault(xPosition +800, yPosition, "js",  new Atom []{Atom.newAtom("dictListener")});
		parentPatcher.connect( dictListenerButton, 0, dictListener, 0);
		
		
		
		//dictListener.send("setFilePathAndDict", new Atom []{Atom.newAtom(filePath)});
		
		
	}
	
	public void showBasketItem(String filePath, int positionInBasket){
		this.filePath = filePath;
		isInBasket = true;
		dict.send("name", new Atom[]{Atom.newAtom(filePath)});
		dictListener.send("setFilePathAndDict", new Atom []{Atom.newAtom(filePath)});
		playlistObject.send("append", new Atom [] { Atom.newAtom(filePath)});
		
		 xPositionInBasket  =0;
		 yPositionInBasket = 50*positionInBasket;
		
		Atom[] sendAtomArray =  new Atom[]{Atom.newAtom( xPositionInBasket +50),Atom.newAtom( yPositionInBasket),Atom.newAtom(400), Atom.newAtom(50)};
		playlistObject.send("presentation_rect", sendAtomArray);
		symButton.showSymButton(filePath, positionInBasket);
	}
	

	public void hideBasketItem(String filePath){
		post("hideBasketItem() method was called");
		this.filePath = "";
		isInBasket = false;
		
		
		Atom[] sendAtomArray =  new Atom[]{Atom.newAtom(xPosition+50),Atom.newAtom(yPosition),Atom.newAtom(400), Atom.newAtom(50)};
		playlistObject.send("presentation_rect", sendAtomArray);
		//playlistObject.send("clear", null);
		
		symButton.hideSymButton(filePath);
		
	}
	
	public void moveUpOneSlot(){
		 xPositionInBasket  =0;
		 yPositionInBasket = yPositionInBasket -50;
		 
		Atom[] sendAtomArray =  new Atom[]{Atom.newAtom(xPositionInBasket+50),Atom.newAtom(yPositionInBasket),Atom.newAtom(400), Atom.newAtom(50)};
		playlistObject.send("presentation_rect", sendAtomArray);
		
		symButton.moveUpOneSlot();
		 
	}
	
	public void clearPlaylistObject() {
		//this.playlistObject.send("clear", null);
		this.playlistObject.send("clear", new Atom []{});
		
	}

	public boolean isInBasket() {
		return isInBasket;
	}

	public void setInBasket(boolean isInBasket) {
		this.isInBasket = isInBasket;
	}

	public MaxBox getPlaylistObject() {
		return playlistObject;
	}


	public void setPlaylistObject(MaxBox playlistObject) {
		this.playlistObject = playlistObject;
	}


	public SymbolButton getSymButton() {
		return symButton;
	}


	public void setSymButton(SymbolButton symButton) {
		this.symButton = symButton;
	}


	public MaxBox getDict() {
		return dict;
	}


	public void setDict(MaxBox dict) {
		this.dict = dict;
	}


	public MaxBox getDictListenerButton() {
		return dictListenerButton;
	}


	public void setDictListenerButton(MaxBox dictListenerButton) {
		this.dictListenerButton = dictListenerButton;
	}


	public MaxBox getDictListener() {
		return dictListener;
	}


	public void setDictListener(MaxBox dictListener) {
		this.dictListener = dictListener;
	}


	public String getFilePath() {
		return filePath;
	}


	public void setFilePath(String filePath) {
		this.filePath = filePath;
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
