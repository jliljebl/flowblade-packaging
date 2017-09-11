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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import animator.phantom.controller.UpdateController;
import animator.phantom.gui.GUIResources;
import animator.phantom.renderer.param.BooleanParam;

/**
* A GUI editor component for setting the value of a <code>BooleanParam</code> using a checkbox.
*/
public class CheckBoxEditor extends JPanel implements ItemListener, UndoListener
{
	//--- Param to be edited
	private BooleanParam editParam;
	//--- True if option corresponding to boolean value true is a checked box;
	private boolean checkedIsTrue;

	private JCheckBox checkBox;
	private JPanel rightPanel;
	private JPanel leftPanel;
	private ItemListener secondListener = null;

	private boolean discardEvent = false;//to not call undo when init or undo update
	/**
	* Constructor with parameter to be edited, label text and option.
	* @param editValue <code>BooleanParam</code> that is edited with this editor.
	* @param paramName Displayed name for editor and parameter.
	* @param checkedIsTrue Value <code>true</code> sets checked option to set param value <code>true</code>.
	*/
	public CheckBoxEditor( BooleanParam editParam, String paramName, boolean checkedIsTrue )
	{
		this.editParam = editParam;
		this.checkedIsTrue = checkedIsTrue;
		editParam.setParamName( paramName );

		checkBox = new JCheckBox();
		checkBox.addItemListener( this );

		setCheckBoxToCurrent();

		leftPanel = new JPanel();
		rightPanel = new JPanel();
		JLabel textLabel = new JLabel( paramName );
		textLabel.setFont( GUIResources.PARAM_EDIT_LABEL_FONT );

		leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.X_AXIS) );
		leftPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );
		leftPanel.add( Box.createHorizontalGlue() );
		leftPanel.add( textLabel );
		leftPanel.add( Box.createRigidArea( new Dimension( ParamEditResources.PARAM_MID_GAP, 6 ) ) );

		rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.X_AXIS) );
		rightPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );
		rightPanel.add( checkBox );
		rightPanel.add( Box.createHorizontalGlue() );

		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) );
		add( leftPanel );
		add( rightPanel );

		setPreferredSize( ParamEditResources.EDIT_ROW_SIZE );
		setMaximumSize( ParamEditResources.EDIT_ROW_SIZE );
	}

	private void setCheckBoxToCurrent()
	{
		boolean initialSelection = editParam.get();
		boolean desiredSelection = false;
		if( initialSelection && checkedIsTrue )
			desiredSelection = true;
		else if( !initialSelection && !checkedIsTrue )
			desiredSelection = true;
		else
			desiredSelection = false;

		if( desiredSelection != checkBox.getModel().isSelected() )
		{
			discardEvent = true;
			checkBox.setSelected( desiredSelection );
		}
	}
	/**
	* Called after undo has been done to set checkbox display to current state.
	*/
	public void undoDone()
	{
		discardEvent = true;
		setCheckBoxToCurrent();
		repaint();
	}
	/**
	* Adds an additonal ItemListener that can be used for GUI update. Listener does NOT need to handle parameter
	* value setting or undo registering, they are handled by this component before calling 
	* additional listener.
	*/
	public void addItemListener( ItemListener l )
	{
		secondListener = l;
	}
	/**
	* Called after user edit action.
	*/ 
	public void itemStateChanged(ItemEvent e)
	{
		if( discardEvent )
		{
			discardEvent = false;
			return;
		}
		e.setSource( this );
		boolean buttonState = checkBox.isSelected();
		boolean paramValue = ( buttonState == checkedIsTrue );
		editParam.set( paramValue );
		if( secondListener != null ) secondListener.itemStateChanged(e);
		UpdateController.valueChangeUpdate( UpdateController.PARAM_EDIT );
		editParam.registerUndo();
	}

}//end class
