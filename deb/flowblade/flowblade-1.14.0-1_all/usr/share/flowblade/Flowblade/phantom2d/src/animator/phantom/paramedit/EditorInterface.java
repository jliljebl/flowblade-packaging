package animator.phantom.paramedit;

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

import animator.phantom.controller.FlowController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UpdateController;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.KeyFrameParam;

//--- Interface to editor data and gui-actions for paramedit classes. 
//--- Created to increase modularity when refactoring code, not necessary anymore
/**
* Interface into editor data, use in plugins is deprecated. This is only needed when writing new param editors.
*/
public class EditorInterface
{
	//--- Gets Current frame.
	public static int getCurrentFrame()
	{
		return TimeLineController.getCurrentFrame();
	}

	//--- Moved to next keyframe as defined by provided param.
	public static void moveCurrentFrameToNextKeyFrame( KeyFrameParam param )
	{
		TimeLineController.moveCurrentFrameToNextKeyFrame( param );
		UpdateController.updateCurrentFrameDisplayers( false );
	}
	//--- Moved to previous keyframe as defined by provided param.
	public static void moveCurrentFrameToPreviousKeyFrame( KeyFrameParam param )
	{
		TimeLineController.moveCurrentFrameToPreviousKeyFrame( param );
		UpdateController.updateCurrentFrameDisplayers( false );
	}
	/*
	public static void outputsNumberChanged( ImageOperation iop, int outputsNumber )
	{
		FlowController.outputsNumberChanged( iop, outputsNumber );
	}
	*/
}//end class
