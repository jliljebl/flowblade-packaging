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

import java.util.Vector;

import animator.phantom.renderer.ImageOperation;


//--- This interfacce has methods for handling keyframes and values that are common
//--- with all such parameter types.
/**
*	 Parameter classes implementing this interface use keyframes. Used internally.
*/
public interface KeyFrameParam
{
	//--- Returns all keyframes.
	public Vector <AnimationKeyFrame> getKeyFrames();

	//--- Sets keyframe Vector
	public void setKeyFrames( Vector <AnimationKeyFrame> keyFrames );

	//--- Returns keyframe info for given frame.
	public ParamKeyFrameInfo getKeyFrameInfo( int frame );

	//--- Return parent ImageOperation.
	//--- Needed to navigate based on keyframes, so that absolute(movie) 
	//--- positions can be determined.
	public ImageOperation getIOP();

	//--- Adds keyframe with given value to given frame.
	public void addKeyFrame( int movieFrame, float value );

	//--- Removes keyframe from frame if exists.
	public void removeKeyFrame( int movieFrame );

	//--- Value in given frame.
	public float getValue( int movieFrame );

	//--- Set value in given frame. if keyframe exists set value, else create keyframe.
	public void setValue( int movieFrame, float value );

	//--- returns max key vqalue for KeyFrameParam
	public float getMaxKeyValue();

	//--- returns min key vqalue for KeyFrameParam
	public float getMinKeyValue();

	//--- return keyframe in frame, used to get interpolation and tension params.
	//--- DO NOT USE TO SET VALUE!
	public AnimationKeyFrame getKeyFrame( int movieFrame );

	//--- Copies params from provided keyframe to keyframe in given frame if there is a one
	public void copyParams( int movieFrame, AnimationKeyFrame paramSource );

	//--- Returns as Param
	public Param getAsParam();

	/**
	* Returns true if param should be hidden from KeyFrameEditor.
	*/
	public boolean hideFromKFEditor();
	/**
	* Hides param from KeyFrameEditor.
	*/
	//public void setHiddenFromKFEditor();
	public void setStepped( boolean stepped );
	public boolean getStepped();

}//end interface