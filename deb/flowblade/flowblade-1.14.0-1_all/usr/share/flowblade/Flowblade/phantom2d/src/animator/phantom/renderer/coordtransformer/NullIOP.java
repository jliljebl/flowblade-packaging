package animator.phantom.renderer.coordtransformer;

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

import java.awt.image.BufferedImage;
import java.util.Vector;

import animator.phantom.gui.view.editlayer.NullEditLayer;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.paramedit.coordtransformer.NullIOPEditPanel;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.AnimatedImageCoordinates;

//--- Animatable null object that has the basic 2d animation properties.
//--- Is to be used as a movement parent.
public class NullIOP extends ImageOperation
{

	public NullIOP()
	{
		name = "Animation Null";
		registerCoords(  new AnimatedImageCoordinates( this ) );
		setAsSource();
	}

	public ParamEditPanel getEditPanelInstance()
	{	
		return new NullIOPEditPanel( this );
	}

	public ViewEditorLayer getEditorlayer()
	{
		return new NullEditLayer( this, name );
	}

	public void doImageRendering( int frame, Vector<BufferedImage> sourceImages )
	{
		//--- noop
		//--- anim params are used as parent
	}

}//end class