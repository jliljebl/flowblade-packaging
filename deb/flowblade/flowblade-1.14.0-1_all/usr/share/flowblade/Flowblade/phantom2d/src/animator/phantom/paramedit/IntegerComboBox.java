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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import animator.phantom.controller.UpdateController;
import animator.phantom.renderer.param.IntegerParam;

/**
* A GUI editor component for setting an <code>IntegerParam</code> value using combo box selection.
* User is presented with textual options and <code>IntegerParam</code> value will be set to 
* selected index.
*/
public class IntegerComboBox extends ParamComboBox implements ActionListener, UndoListener
{
	//--- Parameter to be edited.
	private IntegerParam editParam;
	private boolean discardEvent;//to not call undo when init or undo update
	/**
	* Constructor with parameter to be edited, label text and options.
	* @param editParam <code>IntegerParam</code> that is edited with this editor.
	* @param labelText Displayed name for editor and parameter.
	* @param options Text options displayed to the user.
	*/
	public IntegerComboBox( IntegerParam editParam, String labelText, String[] options )
	{

		this.editParam = editParam;
		initComponent( labelText, options, this, editParam );
		//--- Set initial value, this will fail if default not in options[] bounds
		discardEvent = true;
		getComboBox().setSelectedIndex( editParam.get() );
	}
	/**
	* Called after undo has been done to update display.
	*/
	public void undoDone()
	{
		discardEvent = true;
		comboBox.setSelectedIndex( editParam.get() );
		repaint();
	}
	/**
	* Called after user edit action.
	*/
	public void actionPerformed(ActionEvent e)
	{
		if( discardEvent )
		{
			discardEvent = false;
			return;
		}

		int index = getComboBox().getSelectedIndex();
		editParam.set( index );
		getComboBox().setSelectedIndex( index );
		UpdateController.valueChangeUpdate( UpdateController.PARAM_EDIT );
		editParam.registerUndo();
	}

}//end class
