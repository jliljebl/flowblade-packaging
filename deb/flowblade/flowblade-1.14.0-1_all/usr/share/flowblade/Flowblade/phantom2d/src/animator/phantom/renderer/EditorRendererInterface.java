package animator.phantom.renderer;

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

import java.awt.Dimension;

import animator.phantom.controller.Application;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.RenderModeController;


//--- This was used for name space refactoring and is no longer really needeed or a meaningful part of design, but has not been removed.
public class EditorRendererInterface
{
	public static int getMovieLength()
	{
		return ProjectController.getLength();
	}
	public static Dimension getScreenDimensions()
	{
		return ProjectController.getScreenSize();
	}
	public static RenderNode getNode( int nodeID )
	{
		return ProjectController.getFlow().getNode( nodeID );
	}
	//--- Get methods for global render settings.
	//public static boolean getGlobalMotionBlur(){ return RenderModeController.getGlobalMotionBlur(); }
	//public static boolean isWriteRender(){ return RenderModeController.isWriteRender(); }
	public static int getRenderMode(){ return RenderModeController.getRenderMode(); }

	//--- Frame renderer aborted
	public static void framerendererAbort()
	{
		Application.renderAbort();
	}


}//end class
