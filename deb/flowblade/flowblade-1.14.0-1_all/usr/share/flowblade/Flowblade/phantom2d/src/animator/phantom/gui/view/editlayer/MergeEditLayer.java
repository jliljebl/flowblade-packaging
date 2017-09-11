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

import java.util.Vector;

import animator.phantom.gui.view.component.ViewControlButtons;
import animator.phantom.renderer.imagemerge.BasicTwoMergeIOP;

//--- Edit layer for editing FileImageSource objects' place, rotation and scale.
public class MergeEditLayer extends AnchorRectEditLayer
{
	public MergeEditLayer( BasicTwoMergeIOP mergeIOP )
	{
		super( mergeIOP, mergeIOP.getImageSize() );
		name = mergeIOP.getName();
	}

	public void setLayerButtons( ViewControlButtons buttons )
	{
		Vector<Integer> btns = new Vector<Integer>();
		btns.add( new Integer( ViewControlButtons.MOVE_B ));
		btns.add( new Integer( ViewControlButtons.ROTATE_B ));
		buttons.setModeButtons( btns );
	}

}//end class
