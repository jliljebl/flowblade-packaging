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
* A GUI editor component for selecting alpha merge operation when combining two alpha channels. Output values correspond to field values here. 
*/
public class AlphaActionSelect extends ParamComboBox implements ActionListener, UndoListener
{
	private IntegerParam selection;
	private boolean discardEvent;//to not call undo when init or undo update
	
	private static String[] actionsTypes = { "destination",
						"source",
						"opaque",
						"union",
						"intersection",
						"exclusion",
						"difference" };

	/**
	* Output is destination alpha.
	*/
	public static final int ALPHA_DESTINATION = 0;
	/**
	* Output is source alpha.
	*/
	public static final int ALPHA_SOURCE = 1;
	/**
	* Output is fully opaque.
	*/
	public static final int ALPHA_OPAQUE = 2;
	/**
	* Output is union of alpha channels.
	*/
	public static final int ALPHA_UNION = 3;
	/**
	* Output is intersection of alpha channels.
	*/
	public static final int ALPHA_INTERSECTION = 4;
	/**
	* Output is exclusion of alpha channels.
	*/
	public static final int ALPHA_EXCLUSION = 5;
	/**
	* Output is difference of alpha channels.
	*/
	public static final int ALPHA_DIFFERENCE = 6;

	/**
	* Creates componentent with <code>IntegerParam</code> holding selection data.
	* @param selection Parameter holding selection. 
	*/
	public AlphaActionSelect( IntegerParam selection )
	{
		this.selection = selection;
		selection.setParamName( "Alpha Action" );
		initComponent( "Alpha channel", actionsTypes, this );
		discardEvent = true;
		comboBox.setSelectedIndex( selection.get() );
	}
	/**
	* Called after undo has been done.
	*/
	public void undoDone()
	{
		discardEvent = true;
		comboBox.setSelectedIndex( selection.get() );
		repaint();
	}
	/**
	* Handles user edit actions.
	*/
	public void actionPerformed(ActionEvent e)
	{
		if( discardEvent )
		{
			discardEvent = false;
			return;
		}

		selection.set( getComboBox().getSelectedIndex() );
		UpdateController.valueChangeUpdate( UpdateController.PARAM_EDIT );
		selection.registerUndo();
	}

}//end class