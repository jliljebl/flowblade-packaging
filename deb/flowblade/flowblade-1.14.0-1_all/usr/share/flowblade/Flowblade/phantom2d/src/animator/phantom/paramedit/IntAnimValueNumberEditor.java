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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.GUIResources;
import animator.phantom.renderer.param.IntegerAnimatedValue;
import animator.phantom.renderer.param.ParamKeyFrameInfo;

/**
* A GUI editor component for setting values of <code>IntegerAnimatedValue</code> parameter.
*<p>
* Primary editor is a text area that sets value or creates new keyframe 
* when user presses enter. Has a secondary editor that has 
* two triangles and a diamond shape to move in timeline and to set or remove keyframes.
*/
public class IntAnimValueNumberEditor extends JPanel implements PropertyChangeListener, FrameChangeListener
{
	//--- AnimatedValue edited with this component
	private IntegerAnimatedValue editValue;
	//--- GUI component used to edit value.
	private JFormattedTextField numberField;
	//--- Keyframe editor component
	private ParamKeyFramesEditor kfEdit;
	//--- This is involved in a hack to get around the fact that setting value in numberField
	//--- after frame has changed causes propertyChange to be fired.
	private boolean FRAME_CHANGE_CAUSED_VALUE_CHANGE = false;

	/**
	* Constructor with parameter to be edited, label text and width.
	* @param text Displayed name for editor and parameter.
	* @param editValue <code>IntegerAnimatedValue</code> that is edited with this editor.
	* @param columns Width in columns of edit field.
	*/
	public IntAnimValueNumberEditor( String text, IntegerAnimatedValue editValue, int columns )
	{
		this.editValue = editValue;
		editValue.setParamName( text );

		//--- current frame
		int currentFrame = EditorInterface.getCurrentFrame();

		//--- Text field
		JLabel textLabel  = new JLabel( text );
		textLabel.setFont( GUIResources.PARAM_EDIT_LABEL_FONT );
		//--- Keyframe editor
		ParamKeyFrameInfo kfInfo = 
			editValue.getKeyFrameInfo( EditorInterface.getCurrentFrame() );
		kfEdit = new ParamKeyFramesEditor( editValue, editValue, kfInfo );
		//--- Set up edit field.
		numberField = new JFormattedTextField( NumberFormat.getNumberInstance() );
		numberField.setColumns( columns );
		numberField.setValue( editValue.getValue( currentFrame ) );
		numberField.addPropertyChangeListener( "value", this );
	
		//--- Create layout
		//--- Left side
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.X_AXIS) );
		leftPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );
		leftPanel.add( textLabel );
		leftPanel.add( Box.createHorizontalGlue() );
		leftPanel.add( kfEdit );
		//--- Right side
		JPanel rightPanel = new JPanel();	
		rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.X_AXIS) );
		rightPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );
		rightPanel.add( numberField );
		rightPanel.add( Box.createHorizontalGlue() );
		//--- Put GUI together.
		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) );
		add( leftPanel );
		add( rightPanel );
 		setPreferredSize( ParamEditResources.EDIT_ROW_SIZE );
		setMaximumSize( ParamEditResources.EDIT_ROW_SIZE );
	}
	/**
	* Called after current frame has been changed to display correct value.
	*/
	public void frameChanged()
	{
		float oldValue = ( (Number) numberField.getValue()).floatValue();
		float newValue = editValue.getValue( EditorInterface.getCurrentFrame() );
		if( oldValue != newValue )
		{
			FRAME_CHANGE_CAUSED_VALUE_CHANGE = true;
			int dval = (int) Math.round( (double) newValue );
			numberField.setValue( dval );
		}

		ParamKeyFrameInfo kfInfo = 
			editValue.getKeyFrameInfo( EditorInterface.getCurrentFrame() );

		kfEdit.setStateAndDisplay( kfInfo );
	}
	/**
	* Called after user edits number value.
	*/
	public void propertyChange( PropertyChangeEvent e )
	{
		//--- if were here because frame change, reset flag and leave.
		if( FRAME_CHANGE_CAUSED_VALUE_CHANGE )
		{
			FRAME_CHANGE_CAUSED_VALUE_CHANGE = false;
			return;
		}

		//--- Set new value.
		float newValue = ( (Number) numberField.getValue()).floatValue();
		int intval = (int) Math.round( (double) newValue );
		editValue.setValue( EditorInterface.getCurrentFrame(), intval );
		//--- Recreate keyFrame draw vector.
		editValue.getIOP().createKeyFramesDrawVector();
		//--- Redraw editors that might have their view changed
		//--- because of value change.
		UpdateController.valueChangeUpdate( UpdateController.PARAM_EDIT );
		TimeLineController.initClipsGUI();//for kf
		//--- Update own gui.
		ParamKeyFrameInfo kfInfo = 
			editValue.getKeyFrameInfo( EditorInterface.getCurrentFrame() );
		kfEdit.setStateAndDisplay( kfInfo );
		//--- undo
		editValue.registerUndo();
	}

}//end class
