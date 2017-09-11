package animator.phantom.renderer.plugin.editlayer;

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
import java.awt.geom.Point2D;
import java.util.Vector;

import animator.phantom.gui.view.EditPoint;
import animator.phantom.gui.view.editlayer.TwoPointShape;
import animator.phantom.plugin.PluginEditLayer;
import animator.phantom.renderer.plugin.GradientPlugin;
import animator.phantom.renderer.plugin.PatternGradientPlugin;

public class GradientPluginEditLayer extends PluginEditLayer
{
	private	TwoPointShape editShape;
	private EditPoint pressedPoint;

	public GradientPluginEditLayer( GradientPlugin gPlugin )
	{
		super( gPlugin );
		editShape = new TwoPointShape(	gPlugin.x1,
						gPlugin.y1,
						gPlugin.x2,
						gPlugin.y2 );
		registerShape( editShape );
		initButtons();
	}

	public GradientPluginEditLayer( PatternGradientPlugin gPlugin )
	{
		super( gPlugin );
		editShape = new TwoPointShape(	gPlugin.x1,
						gPlugin.y1,
						gPlugin.x2,
						gPlugin.y2 );
		registerShape( editShape );
		initButtons();
	}

	private void initButtons()
	{
		Vector<Integer> buttons = new Vector<Integer>();
		buttons.add( new Integer( MOVE_MODE ));
		setButtonsData( buttons, 0 );
	}

	public void frameChanged( int frame )
	{
		editShape.movePoints( frame );
	}

	public void modeChanged( int newMode ){}//--- noop, only one edit mode
	

	public void mousePressed( int frame, Point2D.Float startPoint )
	{
		pressedPoint = getEditPoint( startPoint );
	}

	public void mouseDragged( int frame, Point2D.Float startPoint, Point2D.Float dragPoint )
	{
		if( pressedPoint != null )
		{
			pressedPoint.setPos( dragPoint.x, dragPoint.y );
		}
		editShape.updateValues( frame );
	}

	public void mouseReleased( int frame, Point2D.Float startPoint, Point2D.Float relesePoint )
	{
		if( pressedPoint != null )
			pressedPoint.setPos( relesePoint.x, relesePoint.y );
		editShape.updateValues( frame );
		if( pressedPoint != null )
			editShape.registerUndos();

		pressedPoint = null;
	}

	public void paintLayer( Graphics2D g )
	{
		//--- Draws handles
		drawEditPoints( g, editShape.getEditPoints(), getDrawColor() );
		//--- Draws line between them 
		drawPolygon( g, editShape.getEditPoints(), getDrawColor() );
	}

}//end class

