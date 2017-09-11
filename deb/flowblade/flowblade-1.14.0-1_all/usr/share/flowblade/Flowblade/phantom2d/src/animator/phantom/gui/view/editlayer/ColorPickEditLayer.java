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

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Vector;

import animator.phantom.controller.UpdateController;
import animator.phantom.gui.view.ColorPickListener;
import animator.phantom.gui.view.PressCaptureShape;
import animator.phantom.gui.view.component.ViewControlButtons;
import animator.phantom.renderer.ImageOperation;

public class ColorPickEditLayer extends ViewEditorLayer
{
	//protected ImageOperation iop;
	protected ColorPickListener listener;

	public ColorPickEditLayer( 	ImageOperation iop,
					ColorPickListener listener )
	{
		super( iop  );
		this.iop = iop;
		this.listener = listener;
		name = iop.getName();
		PressCaptureShape  editShape = new  PressCaptureShape( this );
		registerShape( editShape );
	}

	public void setLayerButtons( ViewControlButtons buttons )
	{
		Vector<Integer> inxs = new Vector<Integer>();
		inxs.add( new Integer( ViewControlButtons.PICK_COLOR_B ));
		buttons.setModeButtons( inxs );
	}

	public void frameChanged(){}

	public void modeChanged(){}

	public void mousePressed(){}
	public void mouseDragged(){}
	public void mouseReleased()
	{
		Color c = getPanelColor( mouseStartPoint );
		listener.colorPicked( c );
		UpdateController.valueChangeUpdate( UpdateController.VIEW_EDIT );
	}

	public float getHitAreaSize(){ return 0; } 

	public void paintLayer( Graphics2D g ){}

}//emd class