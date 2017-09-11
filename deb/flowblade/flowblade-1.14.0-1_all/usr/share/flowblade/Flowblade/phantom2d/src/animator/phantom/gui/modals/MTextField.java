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

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class MTextField extends MInputField
{
	public JTextField textField;

	public MTextField( String msg, Object defaultVal )
	{
		initComponents( msg, defaultVal );
		initPanels();
	}
	
	public MTextField( String msg, int cutColumnPix, Object defaultVal )
	{
		initComponents( msg, defaultVal );
		
		initPanels();
	}

	public MTextField( String msg, int leftSize, int rightSize, Object defaultVal )
	{
		initComponents( msg, defaultVal );
		
		initPanels();
	}

	private void initComponents(  String msg, Object defaultVal )
	{
		this.value = defaultVal;
			
		JLabel textLabel  = new JLabel( msg );
		textField = new JTextField();

		if( value instanceof String )
		{
			textField.setText( (String) value );
		}
		else if( value instanceof Integer )
		{
			textField.setText( ((Integer) value).toString() );
		}
		else if( value instanceof Float )
		{
			textField.setText( ((Float) value).toString() );
		}
		this.leftComponent = textLabel;
		this.rightComponent = textField;
	}

	public void setTextFieldSize( int w )
	{
		Dimension size = new Dimension( w, HEIGHT - 1 );
		textField.setPreferredSize( size );
		textField.setMaximumSize( size );
	}
	
	public void setValue( String value )
	{
		textField.setText( value );
	}

	public void setValue( float value )
	{
		textField.setText( new Float(value).toString() );
	}
	
	public int getIntValue()
	{
		return Integer.parseInt( textField.getText() );
	}
	
	public float getFloatValue()
	{
		return Float.parseFloat( textField.getText() );
	}
	
	public String getStringValue(){ return textField.getText(); }

}//end class
