
import java.util.ArrayList;

import com.cycling74.max.MaxObject;
import com.cycling74.max.MaxPatcher;

public class SymbolButtonManager extends MaxObject{
	ArrayList <SymbolButton> symButtonList ;
	MaxPatcher parentPatcher;
	
	public SymbolButtonManager(){
		symButtonList = new ArrayList <SymbolButton>();
		parentPatcher  =  this.getParentPatcher();
	}
	
	
	
	public void addSymbolButton(String filePath){
		post("addSymbolButton() was called with: "+filePath);
		int yPosition;
		if(symButtonList.size()== 0){
			yPosition = 30;
		}else{
			yPosition = 30 + symButtonList.size()*50;
		}
		
		SymbolButton symButton = new SymbolButton(parentPatcher, 50, yPosition, filePath);
		symButtonList.add(symButton);
	}
	
	
	
	public void removeSingleButton(String filePath){
		post("removeSingleButton() method was called");
		
		int removeIndex  = 0;
		for( int i = 0; i<symButtonList.size();i++){
			if(symButtonList.get(i).getFilePath().equals(filePath)){
				removeIndex = i;
				symButtonList.get(i).getPanel().remove();
				symButtonList.get(i).getJsui().remove();
				symButtonList.remove(i);
				break;
			}
		}
		
		post("removeIndex: "+ removeIndex);
		post("Size symButtonList: "+symButtonList.size());
		
		for(int i = removeIndex; i < symButtonList.size(); i++){
			//symButtonList.get(i).moveButtonUp();
		}
	}
	
	
	
	
	public void removeAllButtons(){
		for( int i = 0; i<symButtonList.size();i++){
			symButtonList.get(i).getPanel().remove();
			symButtonList.get(i).getJsui().remove();
			
		}
		
		symButtonList =  new ArrayList<SymbolButton>();
	}
	
	
}
