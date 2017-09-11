package animator.phantom.gui.view.editlayer;

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
import java.awt.Rectangle;
import java.util.Vector;

import animator.phantom.gui.view.component.ViewControlButtons;
import animator.phantom.plugin.PhantomPlugin;

//--- Real editor layer for PluginAnchorRectEditLayer
public class PluginAnchorRectViewEditorLayer extends AnchorRectEditLayer
{
	//--- Buttons data.
	private Vector<Integer> buttonsVec = new Vector<Integer>();
	private int selectedButton = 0;

	public PluginAnchorRectViewEditorLayer( PhantomPlugin plugin, Dimension untransformedRectSize )
	{
		super( plugin.getIOP(), new Rectangle( 0,0, untransformedRectSize.width, untransformedRectSize.height ) );
		setName( plugin.getIOP().getName() );
	}

	//--- Save data for callback
	public void setButtonsData( Vector<Integer> buttonsVec, int selIndex )
	{
		this.buttonsVec = buttonsVec;
		this.selectedButton = selIndex;
	}

	//--- give saved data on callback
	public void setLayerButtons( ViewControlButtons buttons )
	{
		buttons.setModeButtons( buttonsVec );
		buttons.setSelected( selectedButton );
	}

}//end class