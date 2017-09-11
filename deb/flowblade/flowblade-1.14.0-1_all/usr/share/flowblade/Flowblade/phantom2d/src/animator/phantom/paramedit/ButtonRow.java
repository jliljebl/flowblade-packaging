package animator.phantom.paramedit;

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
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import animator.phantom.gui.GUIResources;

/**
* A GUI component for holding a row of buttons in the panel used to edit parameters.
*/
public class ButtonRow extends JPanel
{
	//--- GUI param
	private int height = 30;
	private Insets buttonInsets = new Insets( 3,3,3,3 );

	/**
	* Constructor with listener and Vector of buttons.
	* @param listener Listener that is added to all buttons.
	* @param buttons Displayed buttons.
	*/
	public ButtonRow(ActionListener listener, Vector <JButton> buttons )
	{
		//--- Set layout and add components.
		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) );
		 
		for( JButton nextButton : buttons )
		{
			nextButton.addActionListener( listener );
			nextButton.setMargin(buttonInsets);
			nextButton.setFont( GUIResources.BASIC_FONT_12 );
			add( nextButton );
		}
		add( Box.createHorizontalGlue() );

		//--- Set size.
		Dimension componentSize = new Dimension(  ParamEditResources.EDIT_ROW_SIZE.width, height );
		setPreferredSize( componentSize );
		setMaximumSize( componentSize );
	}

}//end class