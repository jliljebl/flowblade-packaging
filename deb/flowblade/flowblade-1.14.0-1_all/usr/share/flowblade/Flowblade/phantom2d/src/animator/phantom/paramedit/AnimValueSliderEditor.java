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

import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.GUIResources;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.ParamKeyFrameInfo;

/**
* A GUI editor component to edit values of <code>AnimatedValue</code> parameters with a set value range.
* <p> 
* Program will abort at runtime if this class is instantiated with an <code>AnimatedValue</code> 
* that does not have a value range defined. 
*/
public class AnimValueSliderEditor extends JPanel implements ChangeListener, FrameChangeListener
{
	//--- AnimatedValue edited with this component.
	private AnimatedValue editValue;
	//--- GUI component used to edit value.
	private JSlider slider;
	//--- Value display.
	private JLabel valueDisplay;

	private SliderListener listener = null;
	//--- Keyframe editor component
	private ParamKeyFramesEditor kfEdit;

	/**
	* Constructor with parameter to be edited and label text.
	* @param editValue <code>AnimatedValue</code> that is edited with this editor.
	* @param text Displayed name for editor and parameter.
	*/
	public AnimValueSliderEditor( String text, AnimatedValue editValue )
	{
		this( text, editValue, null );
	}
	/**
	* Constructor with parameter to be edited and label text and listener.
	* Listener must take care of setting parameter value and registering undos.
	* @param editValue <code>AnimatedValue</code> that is edited with this editor.
	* @param text Displayed name for editor and parameter.
	* @param listener Called after mouse release has been done.
	*/
	public AnimValueSliderEditor( String text, AnimatedValue editValue, SliderListener listener )
	{
		//---  Capture value.
		this.editValue = editValue;
		this.listener = listener;
	
		editValue.setParamName( text );
		if( !editValue.hasRestrictedValueRange() )
		{
			System.out.println( text + ": AnimValueSliderEditor must be created with parameter with range." );
			System.exit( 1 );
		}

		//--- current frame and value
		int currentFrame = EditorInterface.getCurrentFrame();
		int value = (int) editValue.getValue( currentFrame );

		//--- Keyframe editor
		ParamKeyFrameInfo kfInfo = 
			editValue.getKeyFrameInfo( EditorInterface.getCurrentFrame() );
		kfEdit = new ParamKeyFramesEditor( editValue, editValue, kfInfo );

		//--- Text field
		JLabel textLabel  = new JLabel( text +": " );
		textLabel.setFont( GUIResources.PARAM_EDIT_LABEL_FONT );

		//--- Display value.
		valueDisplay = new JLabel( Integer.toString( value ) );

		//--- Value slider.
		slider = new JSlider( (int) editValue.getMinValue(),
					(int) editValue.getMaxValue(), value );
		slider.addChangeListener( this );
		slider.setPaintTicks(false);
		slider.setPaintLabels(false);
		slider.setPreferredSize( 
			new Dimension( ParamEditResources.EDIT_ROW_HALF_SIZE.width * 2,
					ParamEditResources.EDIT_ROW_HALF_SIZE.height - 20 ) );
	
		//--- Create layout
		//--- Top row
		JPanel topPanel = new JPanel();
		topPanel.setLayout( new BoxLayout( topPanel, BoxLayout.X_AXIS) );
		topPanel.setPreferredSize( 
			new Dimension( ParamEditResources.EDIT_ROW_HALF_SIZE.width * 2,
					ParamEditResources.EDIT_ROW_HALF_SIZE.height + 3  ) );
		topPanel.add( textLabel );
		topPanel.add( kfEdit );
		topPanel.add( Box.createHorizontalGlue() );
		topPanel.add( valueDisplay );
		topPanel.add( Box.createRigidArea( new Dimension( 10, 0) ) );


		//--- Bottom row.
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout( new BoxLayout( bottomPanel, BoxLayout.X_AXIS) );
		bottomPanel.setPreferredSize( 
			new Dimension( ParamEditResources.EDIT_ROW_HALF_SIZE.width * 2,
						ParamEditResources.EDIT_ROW_HALF_SIZE.height - 20 ) );
		bottomPanel.add( slider );

		//--- Put GUI together.
		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS) );
		add( topPanel );
		add( bottomPanel );
		
		setPreferredSize( ParamEditResources.EDIT_SLIDER_ROW_SIZE );
		setMaximumSize( ParamEditResources.EDIT_SLIDER_ROW_SIZE );
	}
	/**
	* Called after current frame has been changed to display correct value.
	*/
	public void frameChanged()
	{
		slider.removeChangeListener( this );
		slider.setValue( (int) editValue.getValue( EditorInterface.getCurrentFrame() ) );
		slider.addChangeListener( this );
		displayValue();

		ParamKeyFrameInfo kfInfo = 
			editValue.getKeyFrameInfo( EditorInterface.getCurrentFrame() );
		kfEdit.setStateAndDisplay( kfInfo );
	}

	private void displayValue()
	{
		int value = (int) editValue.getValue( EditorInterface.getCurrentFrame() );
		valueDisplay.setText( Integer.toString( value ) );
	}

	/**
	* Called after slider moved.
	*/
	public void stateChanged(ChangeEvent e) 
	{
		if( slider.getValueIsAdjusting() == true )
		{
			int value = (int) slider.getValue();
			valueDisplay.setText( Integer.toString( value ) );
			return;
		}

		if( listener == null )
		{
			editValue.setValue( EditorInterface.getCurrentFrame(), (float) slider.getValue() );
			UpdateController.valueChangeUpdate( UpdateController.PARAM_EDIT );

			//--- Redraw timeline to update keyframe diamonds.
			TimeLineController.initClipsGUI();
			editValue.registerUndo();

			ParamKeyFrameInfo kfInfo = 
				editValue.getKeyFrameInfo( EditorInterface.getCurrentFrame() );
			kfEdit.setStateAndDisplay( kfInfo );
		}
		else
		{
			listener.valueChanged( this, (float) slider.getValue() );

			ParamKeyFrameInfo kfInfo = 
				editValue.getKeyFrameInfo( EditorInterface.getCurrentFrame() );
			kfEdit.setStateAndDisplay( kfInfo );
		}

		displayValue();
	}

}//end class
