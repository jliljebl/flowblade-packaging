package animator.phantom.controller;

import javax.swing.plaf.metal.MetalToggleButtonUI;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;

//This is shown by some tools as not used but it is really needed.
public class DarkToggleUI extends MetalToggleButtonUI 
{
	private static final DarkToggleUI darkToggleButtonUI = new DarkToggleUI();

	public static ComponentUI createUI( JComponent b ) 
	{
		return darkToggleButtonUI;
	}

	public void paint(Graphics g, JComponent c) 
	{
		super.paint( g, c );
	}

}//end class