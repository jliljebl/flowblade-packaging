package animator.phantom.paramedit;

/*
    Copyright Janne Liljeblad

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
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.ParamKeyFrameInfo;

/**
* A GUI editor component to edit values of <code>AnimatedValue</code> parameters. 
* Primary editor is a text area that sets value or creates new keyframe 
* when user presses enter. Has a secondary editor that has 
* two triangles and a diamond shape to move in timeline and to set or remove keyframes.
* Displayed value updates automatically when current frame is changed.
*/
public class AnimValueNumberEditor extends JPanel implements PropertyChangeListener, FrameChangeListener
{
	//--- AnimatedValue edited with this component
	private AnimatedValue editValue;
	//--- GUI component used to edit value.
	private JFormattedTextField numberField;
	//--- Keyframe editor component
	private ParamKeyFramesEditor kfEdit;
	//--- This is involved in a hack to get around the fact that setting value in numberField
	//--- after frame has changed causes propertyChange to be fired.
	private boolean FRAME_CHANGE_CAUSED_VALUE_CHANGE = false;

	/**
	* Constructor with parameter to be edited and label text.
	* @param editValue <code>AnimatedValue</code> that is edited with this editor.
	* @param text Displayed name for editor and parameter.
	*/
	public AnimValueNumberEditor( String text, AnimatedValue editValue )
	{
		this( text, editValue, 7 );
	}

	/**
	* Constructor with parameter to be edited, label text and width.
	* @param text Displayed name for editor and parameter.
	* @param editValue <code>AnimatedValue</code> that is edited with this editor.
	* @param columns Width in columns of edit field.
	*/
	public AnimValueNumberEditor( String text, AnimatedValue editValue, int columns )
	{
		this.editValue = editValue;
		editValue.setParamName( text );
		int currentFrame = EditorInterface.getCurrentFrame();

		//--- Text field
		JLabel textLabel  = new JLabel( text );
		textLabel.setFont( GUIResources.PARAM_EDIT_LABEL_FONT );
		//textLabel.setPreferredSize( new Dimension( ParamEditResources.EDIT_ROW_HALF_SIZE.width - 17, ParamEditResources.EDIT_ROW_HALF_SIZE.height ) );
		//--- Keyframe editor
		ParamKeyFrameInfo kfInfo = 
			editValue.getKeyFrameInfo( EditorInterface.getCurrentFrame() );
		kfEdit = new ParamKeyFramesEditor( editValue, editValue, kfInfo );
		//--- Set up edit field.
		numberField = new JFormattedTextField( NumberFormat.getNumberInstance() );
		numberField.setColumns( columns );
		numberField.setValue( editValue.getValue( currentFrame ) );
		numberField.addPropertyChangeListener( "value", this );
		numberField.setMargin( new Insets(0, 2, 0, 0));
		//--- Create layout
		//--- Left side
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.X_AXIS) );
		leftPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );
		leftPanel.add( Box.createHorizontalGlue() );
		leftPanel.add( textLabel );
		leftPanel.add( Box.createRigidArea( new Dimension( ParamEditResources.PARAM_MID_GAP, 6 ) ) );
		//--- Right side
		JPanel rightPanel = new JPanel();	
		rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.X_AXIS) );
		rightPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );
		rightPanel.add( kfEdit );
		rightPanel.add( numberField );
		rightPanel.add( Box.createHorizontalGlue() );

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
			numberField.setValue( newValue );
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
		editValue.setValue( EditorInterface.getCurrentFrame(), newValue );
		//--- Recreate keyFrame draw vector.
		editValue.getIOP().createKeyFramesDrawVector();
		//--- Redraw editors that might have their view changed
		//--- because of value change.		
		UpdateController.valueChangeUpdate( UpdateController.PARAM_EDIT );
		//--- Redraw timeline to update keyframe diamonds.
		TimeLineController.initClipsGUI();
		//--- Update own gui.
		ParamKeyFrameInfo kfInfo = 
			editValue.getKeyFrameInfo( EditorInterface.getCurrentFrame() );
		kfEdit.setStateAndDisplay( kfInfo );
		//--- undo
		editValue.registerUndo();
	}

}//end class
