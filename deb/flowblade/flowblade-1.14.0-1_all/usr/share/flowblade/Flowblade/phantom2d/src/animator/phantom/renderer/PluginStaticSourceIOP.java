package animator.phantom.renderer;

/*
    Copyright Janne Liljeblad 

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

import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.imagesource.StaticSource;

public class PluginStaticSourceIOP extends StaticSource
{
	public PluginStaticSourceIOP( PhantomPlugin plugin )
	{
		this.plugin = plugin;
		initBlendParams();
	}

	public ViewEditorLayer getEditorlayer()
	{
		return plugin.getEditorLayer();
	}

	public void doImageRendering( int frame, Vector<BufferedImage> sourceImages )
	{
		plugin.doImageRendering( frame );
	}

	public ParamEditPanel getEditPanelInstance()
	{
		return plugin.getEditPanel(); 
	}

}//end class
