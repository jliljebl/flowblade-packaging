package animator.phantom.gui.modals;

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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class MDialog extends JDialog implements ActionListener,  WindowListener
{
	private static final int BUTTON_MIN_WIDTH = 70;
	private static final int MULTI_INPUT_BUTTON_END_PAD = 0;
	private static final int BORDER_GAP = 10;
	
	private PHButtonRow buttonRow;
	private int response = -99;

	public MDialog( Frame owner, String title, Component msg, int width, int height, boolean displayCancel )
	{
	
		super( owner, title, true );

		String[] buttons;
		String[] cancelButtons = { "Cancel", "OK" };
		String[] okButtons = { "OK" };

		if( displayCancel )
			buttons = cancelButtons;
		else
			buttons = okButtons;
	
		buttonRow = new PHButtonRow( buttons, getPreferredSize().width, true, MULTI_INPUT_BUTTON_END_PAD, BUTTON_MIN_WIDTH );
		buttonRow.setActionListener( this );
		
		JPanel pane = new JPanel();
		pane.setLayout( new BoxLayout( pane, BoxLayout.Y_AXIS ));
		pane.add( msg );
		pane.add( Box.createRigidArea( new Dimension( 0, 12 ) ) );
		pane.add( Box.createVerticalGlue());
		pane.add( buttonRow );
		pane.setBorder( BorderFactory.createEmptyBorder( BORDER_GAP, BORDER_GAP, BORDER_GAP, BORDER_GAP ) );
		pane.setPreferredSize( new Dimension( width, height ));

		add( pane );

		addWindowListener( this );

		pack();
		setResizable( false );

		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
	}

	public int getResponseValue(){ return response; }

	public void actionPerformed( ActionEvent e )
	{
		response = buttonRow.buttonIndex( (JButton) e.getSource() );
		dispose();
	}

	public void windowActivated(WindowEvent e){}
	public void windowClosed(WindowEvent e){}
	public void windowClosing(WindowEvent e)
	{
		response = -1;
	}
	public void windowDeactivated(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowOpened(WindowEvent e) {}

}//end class
