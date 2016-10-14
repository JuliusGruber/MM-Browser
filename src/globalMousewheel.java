
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

import com.cycling74.max.Atom;
import com.cycling74.max.MaxObject;

public class globalMousewheel extends MaxObject implements NativeMouseWheelListener{
	
	public globalMousewheel(Atom[] args) {
		//declareAttribute("autostart", null, "autoStart");
		declareIO(1, 1);
	}

	@Override
	public void nativeMouseWheelMoved(NativeMouseWheelEvent e) {
		outlet(0, e.getWheelRotation());
		
	}
	
	public void start(){
		// Get the logger for "org.jnativehook" and set the level to warning.
//        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
//        logger.setLevel(Level.WARNING);
        
		//no log message please
		final Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);	
        
		//start NativeHook
		try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
			post("There was a problem registering the native hook.");
			post(ex.getMessage());
		}
		//start the listener
		GlobalScreen.addNativeMouseWheelListener(this);
	}


	public void stop(){
		//stop the listener
		GlobalScreen.removeNativeMouseWheelListener(this);
		//Stop the nativeHook
		try {
			GlobalScreen.unregisterNativeHook();
		}
		catch (NativeHookException ex) {
			post("There was a problem unregistering the native hook.");
			post(ex.getMessage());
		}
	}


	
}
