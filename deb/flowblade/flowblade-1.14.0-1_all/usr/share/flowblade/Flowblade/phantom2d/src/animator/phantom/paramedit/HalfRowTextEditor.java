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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import animator.phantom.gui.GUIResources;

/** 
* Half row wide GUI text area editor component for inputting numeric values.
* Passes edit events to listener and obivously does NOT handle undo registering or undo gui updates because does
* not hold a reference to a parameters.
*/
public class HalfRowTextEditor extends JPanel implements PropertyChangeListener
{
	//--- GUI component used to edit value.
	private JFormattedTextField numberField;
	//--- This is a hack to get around the fact that setting value in numberField
	//--- causes propertyChange to be fired.
	private boolean VALUE_SET_CAUSED_VALUE_CHANGE = false;

	private float oldValue;

	private static final String valueChangePropName = new String("valuechangeprop");
	/**
	* Constructor with parameter to be listener, label text and gui parameter.
	* @param text Displayed name for editor and parameter.
	* @param midGap Gap between text and input area.
	* @param listener Text edit events are passed on to this <code>PropertyChangeListener</code>.
	*/
	public HalfRowTextEditor( String text, int midGap, PropertyChangeListener listener )
	{
		addPropertyChangeListener( valueChangePropName, listener );
		//--- Text field
		JLabel textLabel  = new JLabel( text );
		textLabel.setFont( GUIResources.PARAM_EDIT_LABEL_FONT );

		//--- Set up edit field.
		numberField = new JFormattedTextField( NumberFormat.getNumberInstance() );
		//numberField.setColumns( columns );
		numberField.setValue( 0 );
		numberField.addPropertyChangeListener( "value", this );

		//--- Put GUI together.
		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) );
		add( Box.createRigidArea( new Dimension( 5, 0 ) ) );
		add( textLabel );
		add( Box.createRigidArea( new Dimension( midGap, 0 ) ) );
		add( numberField );

 		setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );
		setMaximumSize( ParamEditResources.EDIT_ROW_HALF_SIZE );
	}
	/**
	* Sets value in number field.
	* @param value New value
	*/
	public void setValue( float value )
	{
		float oldValue = ( (Number) numberField.getValue()).floatValue();
		if( oldValue != value )
		{
			VALUE_SET_CAUSED_VALUE_CHANGE = true;
			numberField.setValue( value );
		}
	}
	/**
	* Returns value from number field.
	* @return Value of text field as float.
	*/
	public float getValue(){ return ((Number) numberField.getValue()).floatValue(); }
	/**
	* Called after user edit event.
	*/
	public void propertyChange( PropertyChangeEvent e )
	{
		if( VALUE_SET_CAUSED_VALUE_CHANGE )//hack
		{
			VALUE_SET_CAUSED_VALUE_CHANGE = false;
			return;
		}
		
		float newValue = ( (Number) numberField.getValue()).floatValue();
	
		firePropertyChange( valueChangePropName, oldValue, newValue);

		oldValue = newValue;
	}

}//end class
