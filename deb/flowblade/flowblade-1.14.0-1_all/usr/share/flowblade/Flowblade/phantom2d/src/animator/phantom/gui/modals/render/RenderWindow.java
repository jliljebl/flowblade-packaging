package animator.phantom.gui.modals.render;

/*
    Copyright Janne Liljeblad 2006,2007,2008

    This file is part of Phantom2D.

    Phantom2D is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Phantom2D is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Phantom2D.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import animator.phantom.controller.RenderModeController;
import animator.phantom.gui.GUIUtils;

public class RenderWindow extends JFrame implements WindowListener
{
	public RenderWindowPanel panel;

	public RenderWindow()
	{
		super( "Render" );
		panel = new RenderWindowPanel( this );
		getContentPane().add( panel );
		pack();
		GUIUtils.centralizeWindow( this );
		setVisible( true );
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener( this );
	}
	
	public RenderWindowPanel getPanel(){ return panel; }

	//---------------------------------------------- WINDOW EVENTS
	public void windowActivated(WindowEvent e){}
	public void windowClosed(WindowEvent e){}
	public void windowClosing(WindowEvent e)
	{
		if (panel.getRendering() == true) return;
		RenderModeController.disposeRenderWindow();
	}
	public void windowDeactivated(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowOpened(WindowEvent e) {}
}//end class