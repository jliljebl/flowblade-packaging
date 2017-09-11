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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Vector;

import animator.phantom.controller.UpdateController;
import animator.phantom.gui.view.ColorPickListener;
import animator.phantom.gui.view.PressCaptureShape;
import animator.phantom.plugin.PluginEditLayer;
import animator.phantom.renderer.plugin.ColorDifferenceKeyPlugin;
import animator.phantom.renderer.plugin.ColorSampleKeyPlugin;

public class ColorDifferenceEditLayer extends PluginEditLayer
{
	private ColorPickListener listener;

	public ColorDifferenceEditLayer( ColorSampleKeyPlugin diffPlugin, ColorPickListener listener )
	{
		super( diffPlugin );
		this.listener = listener;
		init();
	}


	public ColorDifferenceEditLayer( ColorDifferenceKeyPlugin diffPlugin, ColorPickListener listener )
	{
		super( diffPlugin );
		this.listener = listener;
		init();
	}

	private void init()
	{
		PressCaptureShape  editShape = new  PressCaptureShape( this );
		registerShape( editShape );

		Vector<Integer> buttons = new Vector<Integer>();
		buttons.add( new Integer( COLOR_PICK_MODE ));
		setButtonsData( buttons, 0 );
	}

	public void frameChanged( int frame ){}
	public void modeChanged( int newMode ){}//--- noop, only one edit mode
	

	public void mousePressed( int frame, Point2D.Float startPoint ){}

	public void mouseDragged( int frame, Point2D.Float startPoint, Point2D.Float dragPoint ){}

	public void mouseReleased( int frame, Point2D.Float startPoint, Point2D.Float relesePoint )
	{
		Color c = getPanelColor( relesePoint );
		if( c == null )
			return;
		listener.colorPicked( c );
		UpdateController.valueChangeUpdate( UpdateController.VIEW_EDIT );
	}

	public void paintLayer( Graphics2D g ){}

}//end class

