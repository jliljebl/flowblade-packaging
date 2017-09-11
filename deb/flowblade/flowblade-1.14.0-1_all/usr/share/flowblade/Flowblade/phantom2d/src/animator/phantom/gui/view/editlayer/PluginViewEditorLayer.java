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
import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.view.component.ViewControlButtons;
import animator.phantom.plugin.PluginEditLayer;
import animator.phantom.renderer.ImageOperation;

public class PluginViewEditorLayer extends ViewEditorLayer
{
	private PluginEditLayer pluginLayer;
	private ImageOperation iop;

	private Vector<Integer> buttonsVec = new Vector<Integer>();
	private int selectedButton = 0;

	public PluginViewEditorLayer( PluginEditLayer pluginLayer, ImageOperation iop )
	{
		super( iop  );
		this.pluginLayer = pluginLayer;
		this.iop = iop;
	}

	public void setButtonsData( Vector<Integer> buttonsVec, int selIndex )
	{
		this.buttonsVec = buttonsVec;
		this.selectedButton = selIndex;
	}

	public void setLayerButtons( ViewControlButtons buttons )
	{
		buttons.setModeButtons( buttonsVec );
		buttons.setSelected( selectedButton );//index of existing buttons array
	}

	public void frameChanged()
	{
		pluginLayer.frameChanged( getCurrentFrame() );
	}

	public void modeChanged()
	{
		pluginLayer.modeChanged( getMode() );
	}

	public void mousePressed()
	{
		pluginLayer.mousePressed( getCurrentFrame(), mouseStartPoint );
	}
	public void mouseDragged()
	{
		pluginLayer.mouseDragged( getCurrentFrame(), mouseStartPoint, mouseCurrentPoint );

		EditorsController.displayCurrentInViewEditor( true );
	}
	public void mouseReleased()
	{
		pluginLayer.mouseReleased( getCurrentFrame(), mouseStartPoint, mouseCurrentPoint );

		clearMouseMoveData();
		//--- kf diamonds update
		iop.createKeyFramesDrawVector();
		TimeLineController.initClipsGUI();
		//--- value displayers update
		UpdateController.valueChangeUpdate( UpdateController.VIEW_EDIT );
	}

	public float getHitAreaSize(){ return 0; } 

	public void paintLayer( Graphics2D g )
	{
		pluginLayer.paintLayer( g );
	}

}//emd class