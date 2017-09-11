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
*  Special GUI component for editing flip transformations.
* Selection value is put into given <code>IntegerParam</code>.
*/
public class FlipSelect extends ParamComboBox implements ActionListener, UndoListener
{
	private IntegerParam flipParam;
	
	private static String[] flipOptions = {	"none",
						"horizontal",
						"vertical" };

	/**
	* Value indicates no flip transform.
	*/
	public static final int NONE = 0;
	/**
	* Value indicates horizontal flip transform.
	*/
	public static final int HORIZONTAL = 1;
	/**
	* Value indicates vertical flip transform.
	*/
	public static final int VERTICAL = 2;

	private boolean discardEvent = false;

	/**
	* Constructor with parameter holding information of flip transformation.
	* @param flipParam <code>IntegerParam</code> with possible values NONE, HORIZONTAL, VERTICAL.
	*/
	public FlipSelect( IntegerParam flipParam )
	{
		this.flipParam = flipParam;
		initComponent( "Flip on axis", flipOptions, this, flipParam );

		discardEvent = true;
		getComboBox().setSelectedIndex( flipParam.get() );
	}
	/**
	* Called after undo has been done to update display.
	*/
	public void undoDone()
	{
		discardEvent = true;
		comboBox.setSelectedIndex( flipParam.get() );
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

		flipParam.set( getComboBox().getSelectedIndex() );
		UpdateController.valueChangeUpdate( UpdateController.PARAM_EDIT );
		flipParam.registerUndo();
	}

}//end class