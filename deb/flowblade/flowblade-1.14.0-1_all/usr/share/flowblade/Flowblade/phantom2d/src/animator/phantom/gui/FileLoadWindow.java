package animator.phantom.gui;

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

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

//--- Window that is displayed when loading projects
public class FileLoadWindow extends JDialog
{
	public static JLabel infoLabel;

	public FileLoadWindow( JFrame parent, String projectName )
	{
		super( parent, "Loading" );

		JPanel panel = new JPanel();
		panel.setLayout( new BoxLayout( panel, BoxLayout.X_AXIS ));
		panel.add( Box.createRigidArea( new Dimension(0,20 ) ) );
		infoLabel =  new JLabel( "Loading " +  projectName +"..." );
		infoLabel.setFont( GUIResources.BASIC_FONT_12 );
		panel.add( Box.createHorizontalGlue() );
		panel.add( infoLabel );
		panel.add( Box.createHorizontalGlue() );

		panel.setPreferredSize( new Dimension( 280, 60 ) );

		setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		add( panel );
		setVisible( true );
		pack();
		setLocationRelativeTo( parent );
	}

}//end class 