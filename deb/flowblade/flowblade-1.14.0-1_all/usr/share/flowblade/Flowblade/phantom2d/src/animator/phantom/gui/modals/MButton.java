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

import javax.swing.JButton;
import javax.swing.JLabel;

import animator.phantom.gui.PHButtonFactory;

public class MButton extends MInputField
{
	private JLabel textLabel;
	private JButton button;

	public MButton( String msg, MActionListener listener )
	{
		this(  msg, PHButtonFactory.getPreferredWidth( msg ), listener );
	}

	public MButton( String msg, int width, MActionListener listener )
	{
		this( msg, width, listener, true );
	}

	public MButton( String msg, int width, MActionListener listener, boolean buttonOnLeft )
	{

		if( width > ( ROW_LEFT_SIZE - 5 ) )
			width = ROW_LEFT_SIZE - 5;

		button = PHButtonFactory.getButton( msg, width );
		button.addActionListener( listener );

		textLabel = new JLabel("");

		if( buttonOnLeft )
		{
			this.leftComponent = button;
			this.rightComponent = textLabel;
		}
		else
		{
			this.leftComponent = textLabel;
			this.rightComponent = button;
			this.rightJustifyRightComponent = true;
		}

		initPanels();
	}

	public void setText( String text )
	{
		textLabel.setText( text );
	}

	public void addActionListener(MActionListener listener)
	{
		button.addActionListener( listener );
	}
}//end class
