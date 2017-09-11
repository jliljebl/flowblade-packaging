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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import animator.phantom.controller.UpdateController;
import animator.phantom.gui.GUIResources;
import animator.phantom.renderer.param.IntegerParam;

/**
* A GUI editor component for an <code>IntegerParam</code> with a set value range.
* <p>
* Program will abort at runtime if this class is instantiated with an I<code>IntegerParam</code> that does not have a value range defined.
*/
public class IntegerValueSliderEditor extends JPanel implements ChangeListener, UndoListener
{
	//--- AnimatedValue edited with this component.
	private IntegerParam editValue;
	//--- GUI component used to edit value.
	private JSlider slider;
	//--- Value display.
	private JLabel valueDisplay;
	//----
	private SliderListener listener = null;
	//--- 
	private boolean discardEvent = false; 

	/**
	* Constructor with parameter to be edited and label text.
	* @param editValue <code>IntegerParam</code> that is edited with this editor.
	* @param text Displayed name for editor and parameter.
	*/
	public IntegerValueSliderEditor( String text, IntegerParam editValue )
	{
		this( text, editValue, null );
	}
	/**
	* Constructor with parameter to be edited and label text and listener.
	* Listener must take care of setting parameter value and registering undos.
	* @param editValue <code>IntegerParam</code> that is edited with this editor.
	* @param text Displayed name for editor and parameter.
	* @param listener Called after mouse release has been done.
	*/
	public IntegerValueSliderEditor( String text, IntegerParam editValue, SliderListener listener )
	{
		this.editValue = editValue;
		this.listener = listener;
		editValue.setParamName( text );

		if( !editValue.hasRange() )
		{
			System.out.println( text + " AnimValueSliderEditor was created without range. Exiting..." ); 
			System.exit( 1 );
		}

		int value = editValue.get();

		JLabel textLabel  = new JLabel( text +": " );
		textLabel.setFont( GUIResources.PARAM_EDIT_LABEL_FONT );
		valueDisplay = new JLabel( Integer.toString( value ) );

		slider = new JSlider( editValue.getMinValue(),
					editValue.getMaxValue(), value );
		slider.addChangeListener( this );
		slider.setPaintTicks(false);
		slider.setPaintLabels(false);
		slider.setPreferredSize( 
			new Dimension( ParamEditResources.EDIT_ROW_HALF_SIZE.width * 2,
					ParamEditResources.EDIT_ROW_HALF_SIZE.height - 18 ) );

		JPanel topPanel = new JPanel();
		topPanel.setLayout( new BoxLayout( topPanel, BoxLayout.X_AXIS) );
		topPanel.setPreferredSize( 
			new Dimension( ParamEditResources.EDIT_ROW_HALF_SIZE.width * 2,
					ParamEditResources.EDIT_ROW_HALF_SIZE.height  ) );
		topPanel.add( textLabel );
		topPanel.add( Box.createHorizontalGlue() );
		topPanel.add( valueDisplay );
		topPanel.add( Box.createRigidArea( new Dimension( 10, 0) ) );

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout( new BoxLayout( bottomPanel, BoxLayout.X_AXIS) );
		bottomPanel.setPreferredSize( 
			new Dimension( ParamEditResources.EDIT_ROW_HALF_SIZE.width * 2,
						ParamEditResources.EDIT_ROW_HALF_SIZE.height - 18  ) );
		bottomPanel.add( slider );

		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS) );
		add( topPanel );
		add( bottomPanel );
		
		setPreferredSize( ParamEditResources.EDIT_SLIDER_ROW_SIZE );
		setMaximumSize( ParamEditResources.EDIT_SLIDER_ROW_SIZE );
	}

	private void displayValue()
	{
		valueDisplay.setText( Integer.toString( editValue.get() ) );
	}
	/**
	* Specifying true makes the knob (and the data value it represents) resolve 
	* to the closest tick mark next to where the user positioned the knob. By default, this property is false.
	* @param b true to snap the knob to the nearest tick mark
	*/
	public void setSnapToTicks(boolean b)
	{
		slider.setSnapToTicks( b);
	}

	/**
	* Called after undo has been done to set slider value to current state.
	*/
	public void undoDone()
	{
		discardEvent = true;
		slider.setValue( editValue.get() );
		displayValue();
	}

	/**
	* Called after user edit action.
	*/
	public void stateChanged(ChangeEvent e) 
	{
		if( discardEvent )
		{
			discardEvent = false;
			return;
		}

		if( slider.getValueIsAdjusting() == true )
		{
			int value = (int) slider.getValue();
			valueDisplay.setText( Integer.toString( value ) );
			return;
		}

		if( listener == null )
		{
			editValue.set( slider.getValue() );
			editValue.registerUndo();
			UpdateController.valueChangeUpdate( UpdateController.PARAM_EDIT );
		}
		else
		{
			listener.valueChanged( this, (float) slider.getValue() );
		}

		displayValue();
	}

}//end class
