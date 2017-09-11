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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import animator.phantom.gui.GUIResources;

public class PHDialog extends JDialog implements ActionListener,  WindowListener
{
	public static final int PLAIN_MESSAGE = 0;
	public static final int WARNING_MESSAGE = 1;	

	private static final int MID_GAP = 25;
	//private static final int MULTI_INPUT_MID_GAP = 8;
	//private static final int MULTI_INPUT_BUTTON_END_PAD = 0;
	//private static final int MULTI_INPUT_TOP_PAD = 2;
	//private static final int MULTI_INPUT_TITLE_PAD = 2;
	private static final int BORDER_GAP = 10;
	private static final int BUTTON_MIN_WIDTH = 70;

	private PHButtonRow buttonRow;
	private int response = -99;

	private static ImageIcon gtkWarning = GUIResources.getIcon( GUIResources.gtk_dialog_warning );

	//--- When in doubt just use the last one.
	/*
	public PHDialog( Frame owner, String title, String[] buttons, Component msg )
	{
		this( owner, title, buttons, msg, MID_GAP, WARNING_MESSAGE );
	}
	*/
	public PHDialog( Frame owner, String title, String[] buttons, Component msg, int msgType )
	{
		this( owner, title, buttons, msg, MID_GAP, msgType );
	}

	public PHDialog( Frame owner, String title, String[] buttons, Component msg, int midGap, int msgType )
	{
		super( owner, title, true );

		JPanel rpane = new JPanel();
		rpane.setLayout( new BoxLayout( rpane, BoxLayout.Y_AXIS ));
		rpane.add( msg );
		rpane.add( Box.createVerticalGlue() );

		JPanel lpane = new JPanel();
		lpane.setLayout( new BoxLayout( lpane, BoxLayout.Y_AXIS ));
		lpane.add( new JLabel( gtkWarning ) );
		lpane.add( Box.createVerticalGlue() );
		
		JPanel topPane = new JPanel();
		topPane.setLayout( new BoxLayout( topPane, BoxLayout.X_AXIS ));
		//--- PLAIN_MESSAGE does not get warning icon.
		if( msgType != PLAIN_MESSAGE )
		{
			topPane.add( lpane );
			topPane.add( Box.createRigidArea( new Dimension( BORDER_GAP, 0 ) ));
		}
		topPane.add( rpane );

		int width = topPane.getPreferredSize().width;
		buttonRow = new PHButtonRow( buttons, width, BUTTON_MIN_WIDTH, true );
		buttonRow.setActionListener( this );
		//--- were getting varying sizes after pack() when buttonRow is the widest element
		//--- and need to add a little more width 
		int hackFix = 0;
		if( width < buttonRow.getPreferredSize().width )
			hackFix = 4;
	
		JPanel pane = new JPanel();
		pane.setLayout( new BoxLayout( pane, BoxLayout.Y_AXIS ));
		pane.add( topPane );

		finishPanel( pane, owner, buttonRow, midGap, hackFix, 0 );
	}

	/*
	public PHDialog( Frame owner, String title, MInputPanel iPanel )
	{
		super( owner, title, true );



		JPanel topPanel = new JPanel(); 
		topPanel.setLayout( new BoxLayout( topPanel, BoxLayout.Y_AXIS ));
		topPanel.add( Box.createRigidArea( new Dimension( 0, MULTI_INPUT_TOP_PAD ) ) ); 

		for( int i = 0; i < iPanel.areas.size(); i++ )
			fillPanel( topPanel, iPanel.areas.elementAt( i ) );

		JPanel hackContainer = new JPanel(); 
		hackContainer.setLayout( new BoxLayout( hackContainer, BoxLayout.X_AXIS ));
		hackContainer.add( topPanel ); 

		String[] buttons = { "Cancel", "OK" };
		int width = hackContainer.getPreferredSize().width;
		buttonRow = new PHButtonRow( buttons, width, true, MULTI_INPUT_BUTTON_END_PAD, BUTTON_MIN_WIDTH );
		buttonRow.setActionListener( this );
		
		JPanel pane = new JPanel();
		pane.setLayout( new BoxLayout( pane, BoxLayout.Y_AXIS ));
		pane.add( topPanel );
		
		finishPanel( pane, owner, buttonRow, MULTI_INPUT_MID_GAP, 0, -6 );
	}
	*/
	//--- Put input fields into panel.
	/*
	private void fillPanel( JPanel p, MInputArea area )
	{
		JPanel titlePane = new JPanel();
		titlePane.setLayout( new BoxLayout( titlePane, BoxLayout.X_AXIS) );
		titlePane.setPreferredSize( new Dimension(  MInputField.WIDTH, MInputField.HEIGHT ) );
		titlePane.add( new JLabel( area.getTitle() ) );
		titlePane.add( Box.createHorizontalGlue() );
		p.add( titlePane );
		p.add( Box.createRigidArea( new Dimension( 0, MULTI_INPUT_TITLE_PAD )));

		for( int i = 0; i < area.fields.size(); i++ )
		{
			if( area.fields.elementAt( i ).topBuf != 0 )
				p.add( Box.createRigidArea( new Dimension( 0, area.fields.elementAt( i ).topBuf ) ) );
			p.add( area.fields.elementAt( i ) );
			if( area.fields.elementAt( i ).bottomBuf != 0 )
				p.add( Box.createRigidArea( new Dimension( 0, area.fields.elementAt( i ).bottomBuf ) ) );
		}
		//p.add( Box.createRigidArea( new Dimension( 0, MULTI_INPUT_AREA_PAD ) ) ); 
	}
	*/
	private void finishPanel( JPanel pane, Frame owner, PHButtonRow buttonRow, int midGap, int hackFix, int yHackFix )
	{
		pane.add( Box.createRigidArea( new Dimension( 0, midGap ) ) );
		pane.add( buttonRow );
		pane.setBorder( BorderFactory.createEmptyBorder( BORDER_GAP, BORDER_GAP, BORDER_GAP, BORDER_GAP ) );

		add( pane );

		addWindowListener( this );

		pack();
		setResizable( false );

		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
		
		//Dimension size = getSize();
		//Rectangle r = owner.getBounds();
		
		//int ownerMidX = r.x + r.width / 2;
		//int ownerMidY = r.y + r.height / 2;

		//Rectangle bounds = new Rectangle( ownerMidX - size.width / 2, ownerMidY -  size.height / 2, size.width + hackFix, size.height + yHackFix );
		//setBounds( bounds );
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
