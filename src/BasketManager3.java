

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.cycling74.max.Atom;
import com.cycling74.max.MaxBox;
import com.cycling74.max.MaxObject;
import com.cycling74.max.MaxPatcher;

public class BasketManager3 extends MaxObject {
	HashSet<Sample> currentlyInBasketSet;
	HashSet<Sample> newSet;
	ArrayList<Sample> currentlyInBasketList;
	ArrayList<Sample> newList;
	MaxPatcher parentPatcher;
	ArrayList <SymbolButton3> symButtonList;

	
	MaxBox viewManagerSend;
	
	public BasketManager3(){
		currentlyInBasketSet = new HashSet<Sample>();
		newSet=  new HashSet<Sample>();
		currentlyInBasketList = new ArrayList<Sample>();
		newList = new ArrayList<Sample>();
		parentPatcher = this.getParentPatcher();
		symButtonList  = new ArrayList<SymbolButton3>();
		viewManagerSend = parentPatcher.getNamedBox("symbuttonInfo_basket_views");
		for(int i = 0; i<200; i++){
			SymbolButton3 symButton = new SymbolButton3(parentPatcher);
			symButtonList.add(symButton);
		}
		
	}

	
	
	public void checkBasketChange(String [] filePathArray){
		post("checkBasketChange() method was called");
		
//		post("State of currentlyInBasketSet:");
//		for(Sample sample : currentlyInBasketSet){
//			post(sample.getFilePath());
//		}
//		post("State of currentlyInBasketList:");
//		for(Sample sample : currentlyInBasketList){
//			post(sample.getFilePath());
//		}
		
		//check for playlist clear
		boolean allSamplesRemoved =  false;
		if(filePathArray.length == 0){
			currentlyInBasketSet = new HashSet<Sample>();
			currentlyInBasketList = new ArrayList<Sample>();
			resetAllSymButtonsToUnusedState();
			allSamplesRemoved = true;
		}
		post("allSamplesRemoved: "+allSamplesRemoved);
		
		if(!allSamplesRemoved){
			//check if user moved the mouse over the drag handle and triggered the dict output
			
			for(int k  =0; k< filePathArray.length; k++){
				newSet.add(new Sample(filePathArray[k]));
				newList.add(new Sample(filePathArray[k]));
			}
			
			boolean triggerdButNoChange  = false;
			if(newSet.equals(currentlyInBasketSet )&& newList.equals(currentlyInBasketList)){
				triggerdButNoChange  = true;
			}
		
			post("triggerdButNoChange: "  +triggerdButNoChange);
		
			if(!triggerdButNoChange){
				
				boolean singleRemove = false;
				if(currentlyInBasketSet.size()-newSet.size()==1 && currentlyInBasketList.size()-newList.size()==1){
					singleRemove = true;
					
					//which file was removed?
					currentlyInBasketSet.removeAll(newSet);
					Object [] removedArray =  currentlyInBasketSet.toArray();
					String filePath =  "";
					if(removedArray.length == 1){
						Sample removedSample  = (Sample) removedArray[0];
						filePath = removedSample.getFilePath();
						hideSingleSymButton(filePath);
						
					}
				
					currentlyInBasketSet  = new HashSet<Sample>(newSet);
					currentlyInBasketList  = new ArrayList<Sample>(newList);
					
					adaptBasketSizeAfterSingleRemove();
				}
				
				post("singleRemove:"+singleRemove);
				
				
				if(!singleRemove){
					boolean reorderingHappend  = false;
					if(newSet.equals(currentlyInBasketSet )&& !newList.equals(currentlyInBasketList)){
						reorderingHappend  = true;
						
						reorderSymButtons();
						
						currentlyInBasketSet  = new HashSet<Sample>(newSet);
						currentlyInBasketList  = new ArrayList<Sample>(newList);
					}
					post("reorderingHappend:"+reorderingHappend);
					
					if(!reorderingHappend){
						boolean illegalDuplicate  = false;
						Set<Sample> set = new HashSet<Sample>(newList);
						if(set.size() < newList.size()){
							illegalDuplicate  =true;
							//reset Playlist to previous state
							MaxBox gate = parentPatcher.getNamedBox("basketGate");
							MaxBox playList  = parentPatcher.getNamedBox("playlistObject");
							gate.send(0);//close the gate
							playList.send("clear", null);
							for(Sample sample : currentlyInBasketList){
								playList.send("append", new Atom []{Atom.newAtom(sample.getFilePath())});
							}
							gate.send(1);
						}
				
						post("illegalDuplicate: "+illegalDuplicate);
				
			
				
						if(!reorderingHappend && !illegalDuplicate && !singleRemove){
							ArrayList<Sample> newSamples  = new ArrayList<Sample>();
							for(Sample sample : newList){
								if(!currentlyInBasketSet.contains(sample)){
									newSamples.add(sample);
									currentlyInBasketSet.add(sample);
									currentlyInBasketList.add(0,sample);
								}
							}
		
							for(Sample sample : newSamples){
								post("New Sample: "+sample.getFilePath());
							}
						}
					}
				}
			}
		}
		
		newList  = new ArrayList<Sample>();
		newSet = new HashSet<Sample>();
		
	}

	public void appendSamplesToBasket(String [] filePathArray){
		post("appendSamplesToBasket() method was called");
		post(" Trying to add : "+filePathArray.length+" samples");
		MaxBox playList  = parentPatcher.getNamedBox("playlistObject");
		
	
		
		
		ArrayList <String> samplesToUpdate  = new ArrayList <String>();
		for(int i = 0; i< filePathArray.length; i++){
			//post(filePathArray[i]);
			Sample testSample = new Sample(filePathArray[i]);
		
			if(!currentlyInBasketSet.contains(testSample)){
				samplesToUpdate.add( filePathArray[i]);
			

			}
		}
		
		sendAppendInfoToViews(samplesToUpdate);
		
		for(String filePath: samplesToUpdate){
			addOneSlotToPlaylist();
			//add one button at the beginning of the basket
			for(SymbolButton3 symButton : symButtonList){
				if(!symButton.isInBasket()){
					
					symButton.showSymButton(filePath, 0);
					break;
				}
			}
			//move the symButton that are in the basket one slot down
			for(SymbolButton3 symButton : symButtonList){
				if(symButton.isInBasket()&& !symButton.getFilePath().equals(filePath)){
					symButton.moveButtonOneSlotDown();
				}
			}
			
			playList.send("append", new Atom []{Atom.newAtom(filePath), Atom.newAtom(1)});
		}
	}
	
	private void setBasketToMinimumSize(){
		MaxBox playlist = parentPatcher.getNamedBox("playlistObject");
		MaxBox buttonBackground  = parentPatcher.getNamedBox("buttonBackground");
		
		MaxBox basketGate  = parentPatcher.getNamedBox("basketGate");
		basketGate.send(0);
		
		int [] rectPlaylist = playlist.getRect();
		int [] rectBackground  = buttonBackground.getRect();
		
		int xUpperLeft = rectPlaylist[0];
		int yUpperLeft  = rectPlaylist[1];
	
		
		int xUpperLeftBackground = rectBackground[0];
		int yUpperLeftBackground  = rectBackground[1];
		
		
	
		
		Atom [] sendArray  =  new Atom[]{Atom.newAtom(xUpperLeft),Atom.newAtom(yUpperLeft),Atom.newAtom(400), Atom.newAtom(600)};
		playlist.send("patching_rect", sendArray);
		playlist.send("presentation_rect", sendArray);
		
		Atom [] sendArrayBackground  =  new Atom[]{Atom.newAtom(xUpperLeftBackground),Atom.newAtom(yUpperLeftBackground),Atom.newAtom(50), Atom.newAtom(600)};
		buttonBackground.send("patching_rect", sendArrayBackground);
		buttonBackground.send("presentation_rect", sendArrayBackground);
		
		basketGate.send(1);
	}
	
	private void adaptBasketSizeAfterSingleRemove(){
				post("adaptBasketSizeAfterSingleRemove() method was called");
				
				MaxBox playlist = parentPatcher.getNamedBox("playlistObject");
				MaxBox buttonBackground  = parentPatcher.getNamedBox("buttonBackground");
				MaxBox basketGate = parentPatcher.getNamedBox("basketGate");
				basketGate.send(0);
				
				int [] rectPlaylist = playlist.getRect();
				int [] rectBackground  = buttonBackground.getRect();
				
				int xUpperLeft = rectPlaylist[0];
				int yUpperLeft  = rectPlaylist[1];
				int xLowerRight = rectPlaylist[2];
				
				
				int xUpperLeftBackground = rectBackground[0];
				int yUpperLeftBackground  = rectBackground[1];
				int xLowerRightBackground = rectBackground[2];
			
				
			
				int currentWidthPlaylist  = xLowerRight-xUpperLeft;
				
			
				int currentWidthBackground  = xLowerRightBackground-xUpperLeftBackground;
				
				int newHeight= 0;
				if(currentlyInBasketList.size()*50 <= 600 ){
					newHeight  = 600;
				}else{
					newHeight  = currentlyInBasketList.size()*50;
				}
				
			
				Atom [] sendArray  =  new Atom[]{Atom.newAtom(xUpperLeft),Atom.newAtom(yUpperLeft),Atom.newAtom(currentWidthPlaylist), Atom.newAtom(newHeight)};
				playlist.send("patching_rect", sendArray);
				playlist.send("presentation_rect", sendArray);
				
				Atom [] sendArrayBackground  =  new Atom[]{Atom.newAtom(xUpperLeftBackground),Atom.newAtom(yUpperLeftBackground),Atom.newAtom(currentWidthBackground), Atom.newAtom(newHeight)};
				buttonBackground.send("patching_rect", sendArrayBackground);
				buttonBackground.send("presentation_rect", sendArrayBackground);
				
				basketGate.send(1);
			
	}
	
	
	private void addOneSlotToPlaylist(){
		//is a reseize needed?
		boolean reseizeNeeded  = false;
		
		MaxBox playlist = parentPatcher.getNamedBox("playlistObject");
		MaxBox buttonBackground  = parentPatcher.getNamedBox("buttonBackground");
		int [] rectPlaylist = playlist.getRect();
		int [] rectBackground  = buttonBackground.getRect();
		
		int xUpperLeft = rectPlaylist[0];
		int yUpperLeft  = rectPlaylist[1];
		int xLowerRight = rectPlaylist[2];
		int yLowerRight = rectPlaylist[3];
		
		int xUpperLeftBackground = rectBackground[0];
		int yUpperLeftBackground  = rectBackground[1];
		int xLowerRightBackground = rectBackground[2];
		int yLowerRightBackground = rectBackground[3];
		
		int currentHeightPlaylist  =  yLowerRight-yUpperLeft;
		int currentWidthPlaylist  = xLowerRight-xUpperLeft;
		
		int currentHeightBackground  =  yLowerRightBackground-yUpperLeftBackground;
		int currentWidthBackground  = xLowerRightBackground-xUpperLeftBackground;
		
		if(currentHeightPlaylist <= currentlyInBasketSet.size()*50 ){
			reseizeNeeded  = true;
		}
		
		if(reseizeNeeded  ){
		Atom [] sendArray  =  new Atom[]{Atom.newAtom(xUpperLeft),Atom.newAtom(yUpperLeft),Atom.newAtom(currentWidthPlaylist), Atom.newAtom(currentHeightPlaylist+50)};
		playlist.send("patching_rect", sendArray);
		playlist.send("presentation_rect", sendArray);
		
		Atom [] sendArrayBackground  =  new Atom[]{Atom.newAtom(xUpperLeftBackground),Atom.newAtom(yUpperLeftBackground),Atom.newAtom(currentWidthBackground), Atom.newAtom(currentHeightBackground+50)};
		buttonBackground.send("patching_rect", sendArrayBackground);
		buttonBackground.send("presentation_rect", sendArrayBackground);
		}
	}
	
	public void hideSingleSymButton(String filePath){
		post("hideSingleSymButton() metuhod :"+filePath);
		int yPositionInBasketOfButtonToHide= 0;
		for(int i = 0; i< symButtonList.size(); i++){
			if(filePath.equals(symButtonList.get(i).getFilePath())){
				
				yPositionInBasketOfButtonToHide  = symButtonList.get(i).getyPositionInBasket();
				symButtonList.get(i).hideSymButton();
			
				break;
			}
			
		}
		
		post("yPositionInBasketOfButtonToHide: "+yPositionInBasketOfButtonToHide);
		
		//move the following buttons upwards
		for(int i = 0; i< symButtonList.size(); i++){
			if(symButtonList.get(i).isInBasket()&& !symButtonList.get(i).getFilePath().equals(filePath) && symButtonList.get(i).getyPositionInBasket() > yPositionInBasketOfButtonToHide  ){
				symButtonList.get(i).moveButtonOneSlotUp();
			}
		}
		
	
		
		Atom [] atomSendArray  = new Atom []{Atom.newAtom("unSelectSamples"), Atom.newAtom(filePath), Atom.newAtom("circle")};
		viewManagerSend.send("unSelectSamplesInAllViews", atomSendArray);
		
	
	}
	
	public void reorderSymButtons(){
		
		for(int i = 0; i <newList.size();i++){
			String filePath = newList.get(i).getFilePath();
			for(SymbolButton3 symButton : symButtonList){
				if(symButton.isInBasket() && symButton.getFilePath().equals(filePath)){
					symButton.moveToBasketPosition(i);
				}
			}
		}
	}
	
	public void resetAllSymButtonsToUnusedState(){
		post("resetAllSymButtonsToUnusedState() method was called");

		
		for(SymbolButton3 symButton : symButtonList){
			symButton.hideSymButton();
		}
	

		
		viewManagerSend.send("resetAllSamplesToUntouchedSampleColor", null);
		setBasketToMinimumSize();
	}
	
	public void removeAllSamples(){
		post("removeAllSampels() method was called");
		MaxBox playList  = parentPatcher.getNamedBox("playlistObject");
		playList.send("clear", null);
			
	}
	
	private void sendAppendInfoToViews(ArrayList <String> filePathList){
		post("sendAppendInfoToViews() method was called");
//		for(String filePath : filePathList){
//			post(filePath);
//		}
		
		Atom [] atomSendArray  = new Atom [filePathList.size()*2+1];
		atomSendArray[0]= Atom.newAtom("setSelectedSamples");
		
		int loopCounter = 1;
		for(int i = 0; i< filePathList.size(); i++){
			atomSendArray[loopCounter]= Atom.newAtom(filePathList.get(i));
			atomSendArray[loopCounter+1]= Atom.newAtom("quad");
			loopCounter = loopCounter +2;
			
		}
		
	
		
		viewManagerSend.send("selectSamplesInAllViews", atomSendArray);
	}
	
	public void sendButtonInfoToAllViews(String [] buttonInfo){
		post("sendButtonInfoToAllViews() method was called");
		post("filePath: "+buttonInfo[0]);
		post("shape: "+ buttonInfo[1]);
		Atom [] sendArray  = new Atom []{Atom.newAtom("setSelectedSamples"),Atom.newAtom(buttonInfo[0]), Atom.newAtom(buttonInfo[1])};
		viewManagerSend.send("selectSamplesInAllViews", sendArray);
	}

	
	public void load200() throws IOException{
		MaxBox playList  = parentPatcher.getNamedBox("playlistObject");
		MaxBox gate = parentPatcher.getNamedBox("basketGate");
		gate.send(0);//close the gate
		String dirName = "C:/Users/Julius Gruber/Desktop/Sample_Datenbanken/200";
		File dir = new File(dirName);
		String[] extensions = new String[] { "aif", "aiff" , "flac", "mp3", "snd", "wav" };
		Collection<File> files =   FileUtils.listFiles(dir, extensions, true);

		for(File file : files){
		post("Appending file: "+file.getCanonicalPath());
		playList.send("append", new Atom []{Atom.newAtom(file.getCanonicalPath()), Atom.newAtom(1)});
		}
	}

}//class end
