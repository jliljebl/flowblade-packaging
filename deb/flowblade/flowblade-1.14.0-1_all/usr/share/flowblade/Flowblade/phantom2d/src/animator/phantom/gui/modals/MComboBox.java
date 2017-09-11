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

import java.awt.event.ActionListener;

import javax.swing.JComboBox;

public class MComboBox extends MInputField
{
	private JComboBox<String> comboBox;

	public MComboBox( String msg, String[] options )
	{
		setLeftAsLabel( msg );
		comboBox = new JComboBox<String>( options );
		this.rightComponent = comboBox;
		initPanels();
	}

	public MComboBox(String msg,  int leftSize, int rightSize, String[] options )
	{
		setLeftAsLabel( msg );
		comboBox = new JComboBox<String>( options );
		this.rightComponent = comboBox;
		initGUIWithSizes( leftSize, rightSize );
	}

	public void addActionListener( MActionListener l ){ comboBox.addActionListener( l ); }
	public void addActionListener( ActionListener l ){ comboBox.addActionListener( l ); }
	
	public void setOptions( String[] options ) 
	{ 
		comboBox.removeAllItems();
		for( int i = 0;i < options.length; i++ )
			comboBox.addItem( options[ i ] );
	}

	public void setSelectedIndex( int index ){ comboBox.setSelectedIndex( index ); }
	public int getSelectedIndex(){ return comboBox.getSelectedIndex(); }
	public Object getSelectedItem(){ return comboBox.getSelectedItem(); }	
	public Object getValue(){ return comboBox.getSelectedItem(); }

}//end class
