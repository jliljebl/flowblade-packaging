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
import animator.phantom.renderer.imagesource.ImageSequenceIOP;
import animator.phantom.renderer.imagesource.VideoClipIOP;

public class MovieSourceEditLayer extends AnchorRectEditLayer
{

	public MovieSourceEditLayer( ImageSequenceIOP miop )
	{
		super( miop, miop.getImageSize() );
		name = miop.getName();
		registerFileSource( miop.getFileSource() );
	}

	public MovieSourceEditLayer( VideoClipIOP miop )
	{
		super( miop, miop.getImageSize() );
		name = miop.getName();
		registerFileSource( miop.getFileSource() );
	}
	
	public void setLayerButtons( ViewControlButtons buttons )
	{
		Vector<Integer> btns = new Vector<Integer>();
		btns.add( new Integer( ViewControlButtons.MOVE_B ));
		btns.add( new Integer( ViewControlButtons.ROTATE_B ));
		buttons.setModeButtons( btns );
	}

}//end class