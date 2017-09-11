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

import javax.swing.JLabel;

import animator.phantom.gui.GUIResources;

public class MTextInfo extends MInputField
{
	private JLabel valueLabel = null;

	public MTextInfo( String key, int value )
	{
		this(key, Integer.toString(value) );
	}

	public MTextInfo( String key, long value )
	{
		this(key, Long.toString(value) );
	}

	public MTextInfo( String key, String value )
	{
		initComponents( key, value );
		initPanels();
		this.rightComponent.setFont( GUIResources.BOLD_FONT_11 );
	}

	private void initComponents(  String key, String value )
	{
		JLabel keyLabel  = new JLabel( key );
		valueLabel  = new JLabel( value );

		this.leftComponent = keyLabel;
		this.rightComponent = valueLabel;
	}

	public void setText(String text)
	{
		valueLabel.setText(text);
	}
	
}//end class
