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

import animator.phantom.blender.Blender;
import animator.phantom.controller.RenderModeController;

//--- An abstract base class for iops that have their rendering affected by render blend / motion blur / interpolation  mode settings.
public abstract class RenderModeIOP extends ImageOperation
{
	//--- Overrides iop motion blur with global if needed
	protected boolean getCurrentMotionBlur()
	{
		boolean motionBlur = getMotionBlur();
		if( !RenderModeController.getGlobalMotionBlur() ) motionBlur = false;
		return motionBlur;
	}

	//--- Overrides iop interpolation with global if needed
	protected int getCurrentInterpolation()
	{
		int interpolation = getInterpolation();

		if( EditorRendererInterface.getRenderMode() == RenderModeController.DRAFT )
			interpolation = SwitchData.NEAREST_NEIGHBOR;

		return interpolation;
	}

	//--- Cuts smooth edges if needed and sets flags
	protected void handleSmoothEdges( BufferedImage srcImg )
	{
		if( this.switches != null && this.switches.fineEdges == true )
			Blender.cutEdges( srcImg );
	}

}//end class
