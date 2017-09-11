package animator.phantom.renderer.param;

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

/**
* Object informs <code>ParamKeyFramesEditor</code> of keyframe positions relative to current frame.
* All displayed  <code>ParamKeyFramesEditor</code> must provided with one of these
* every time current frame is changed.
*/
public class ParamKeyFrameInfo
{
	private boolean keyFramesAfter;
	private boolean keyFramesBefore;
	private boolean onKeyFrame;

	public ParamKeyFrameInfo( 	boolean keyFramesAfter,
					boolean keyFramesBefore,
					boolean onKeyFrame )
	{
		this.keyFramesAfter = keyFramesAfter;
		this.keyFramesBefore = keyFramesBefore;
		this.onKeyFrame = onKeyFrame;
	}
	
	//--- Interface
	public boolean keyFramesAfter(){ return keyFramesAfter; }
	public boolean keyFramesBefore(){ return keyFramesBefore; }
	public boolean onKeyFrame(){ return onKeyFrame; }

}//end class
