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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
* A GUI component to be displayed when there are no editable parameters for a plugin.
*/
public class NoParamsPanel extends JPanel
{
	/**
	* Constructor with plugin name.
	* @param pluginName Name of the plugin that has no editable parameters.
	*/
	public NoParamsPanel( String pluginName )
	{
		//--- Info text label.
		JLabel textLabel = new JLabel( pluginName + " has no editable parameters." );
	
		//--- Create layout
		JPanel panel = new JPanel();
		panel.setLayout( new BoxLayout( panel, BoxLayout.X_AXIS) );
		panel.setPreferredSize( ParamEditResources.EDIT_ROW_SIZE );
		panel.add( textLabel );
		panel.add( Box.createHorizontalGlue() );
		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) );
		add( panel );

		//--- Set component size
		setPreferredSize( ParamEditResources.EDIT_ROW_SIZE );
		setMaximumSize( ParamEditResources.EDIT_ROW_SIZE );
	}

}//end class