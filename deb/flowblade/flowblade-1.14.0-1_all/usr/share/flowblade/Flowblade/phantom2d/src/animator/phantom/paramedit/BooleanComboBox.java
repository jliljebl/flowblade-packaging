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
import animator.phantom.renderer.param.BooleanParam;

/**
* A GUI editor component for setting the value of a <code>BooleanParam</code> using a two option combo box.
*/
public class BooleanComboBox extends ParamComboBox implements ActionListener, UndoListener
{
	//--- Param to be edited
	private BooleanParam editParam;
	//--- True if option corresponding to boolean value true is diplayed first.
	private boolean trueOptionFirst;
	//--- Labels for options.
	private static String[] options = new String[ 2 ];
	//--- hack for undo
	private boolean discardEvent = false;
	/**
	* Constructor with parameter to be edited, label text and options.
	* @param editValue <code>BooleanParam</code> that is edited with this editor.
	* @param labelText Displayed name for editor and parameter.
	* @param trueOption Text for option that sets param value <code>true</code>.
	* @param falseOption Text for option that sets param value <code>false</code>.
	* @param trueOptionFirst Value <code>true</code> sets option for <code>true</code> option to be displayed above <code>false</code> option.
	*/
	public BooleanComboBox( BooleanParam editParam, String labelText, String trueOption, String falseOption, boolean trueOptionFirst )
	{
		this.editParam = editParam;
		this.trueOptionFirst = trueOptionFirst;
;
		if( trueOptionFirst )
		{
			options[ 0 ] = trueOption;
			options[ 1 ] = falseOption;
		}
		else
		{
			options[ 1 ] = trueOption;
			options[ 0 ] = falseOption;
		}

		initComponent( labelText, options, this, editParam );
		discardEvent = true;
		setComboBoxSelection();
	}

	private void setComboBoxSelection()
	{
		if( (trueOptionFirst &&  editParam.get() == true) 
		|| ( !trueOptionFirst &&  editParam.get() == false) ) getComboBox().setSelectedIndex( 0 );
		else getComboBox().setSelectedIndex( 1 );
	}

	/**
	* Called after user edit selection.
	*/
	public void actionPerformed(ActionEvent e)
	{
		if( discardEvent )
		{
			discardEvent = false;
			return;
		}

		int index = getComboBox().getSelectedIndex();
		
		if(( index == 0 && trueOptionFirst )||( index == 1 && !trueOptionFirst ))
			editParam.set( true );
		else
			editParam.set( false );

		UpdateController.valueChangeUpdate( UpdateController.PARAM_EDIT );
		editParam.registerUndo();
	}
	/**
	* Called after undo has been done to set combo box display to current state.
	*/
	public void undoDone()
	{
		discardEvent = true;
		setComboBoxSelection();
		repaint();
	}

}//end class
