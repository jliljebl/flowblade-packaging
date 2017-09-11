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

import java.awt.Graphics2D;
import java.util.Vector;

import animator.phantom.controller.EditorsController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.view.component.ViewControlButtons;
import animator.phantom.renderer.plugin.GradientMaskPlugin;
import animator.phantom.renderer.plugin.GradientPlugin;

public class GradientEditLayerP extends ViewEditorLayer
{
	private	TwoPointShape editShape;

	public GradientEditLayerP( GradientPlugin gp )
	{
		super( gp.getIOP()  );
		editShape = new TwoPointShape(	gp.x1,
						gp.y1,
						gp.x2,
						gp.y2 );
		registerShape( editShape );
		setName( gp.getName() );
	}

	public GradientEditLayerP( GradientMaskPlugin gp )
	{
		super(  gp.getIOP()  );
		editShape = new TwoPointShape(	gp.x1,
						gp.y1,
						gp.x2,
						gp.y2 );
		registerShape( editShape );
		setName( gp.getName()  );
	}

	public void setLayerButtons( ViewControlButtons buttons )
	{
		Vector<Integer> btns = new Vector<Integer>();
		btns.add( new Integer( ViewControlButtons.MOVE_B ));
		buttons.setModeButtons( btns );
	}

	//--- Udpate for frame change
	public void frameChanged()
	{
		editShape.movePoints( getCurrentFrame() );
	}
	//--- Udadate for mode change.
	public void modeChanged(){}

	public void mousePressed()
	{
		//--- Get possibly pressed edit point.
		lastPressedPoint = getEditPoint( mouseStartPoint );
	}

	public void mouseDragged()
	{
		int frame = getCurrentFrame();
		if( lastPressedPoint != null )
		{
			lastPressedPoint.setPos( mouseCurrentPoint.x, mouseCurrentPoint.y );
				
		}
		editShape.updateValues( frame );
		EditorsController.displayCurrentInViewEditor( true );
	}

	public void mouseReleased()
	{
		int frame = getCurrentFrame();
		if( lastPressedPoint != null )
		{
			lastPressedPoint.setPos( mouseCurrentPoint.x, mouseCurrentPoint.y );
				
		}
		editShape.updateValues( frame );
		UpdateController.valueChangeUpdate( UpdateController.VIEW_EDIT );
		lastPressedPoint = null;
	}

	public float getHitAreaSize(){ return 0; }//only points can be hit

	public void paintLayer( Graphics2D g )
	{
		//--- Draws handles
		drawEditPoints( g, editShape.getEditPoints(), getDrawColor() );
		//--- Draws line between them 
		drawPolygon( g, editShape.getEditPoints(), getDrawColor() );
	}

}//emd class