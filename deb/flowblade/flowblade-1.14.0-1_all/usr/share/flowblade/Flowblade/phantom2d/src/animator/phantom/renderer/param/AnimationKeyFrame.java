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

import animator.phantom.controller.EditorPersistance;

/**
* Animation keyframe object. Used internally by application. Don't create or touch objects of this class.
*/
public class AnimationKeyFrame implements Comparable<Object>
{
	//--- The value of the keyframe.
	private float value = 0;
	
	//--- The frame of this keyframe.
	//--- This is clip frame. Movie frame = clip beginFrame + frame
	private int frame = 0;
	
	//--- Interpolation types.
	public static final int LINEAR = 1;
	public static final int BEZIER = 2;

	//--- Values for leading and trailing interpolations.
	private int leadingInterpolation = LINEAR;
	private int trailingInterpolation = LINEAR;

	//--- Tensions for leading and trailing curves
	//--- These are overridden in static creator method.
	private static float DEFAULT_TENSION = 0.1f;
	private float leadingTension = DEFAULT_TENSION;
	private float trailingTension = DEFAULT_TENSION;

	/**
	* Used internally by application.
	*/
	public static AnimationKeyFrame createNewKeyframe( int frame, float value )
	{
		AnimationKeyFrame kf = new AnimationKeyFrame( frame, value );
		int interpolation = EditorPersistance.getIntPref( EditorPersistance.KF_DEF_INTERP );
		float tension = EditorPersistance.getFloatPref( EditorPersistance.KF_DEF_TENS );

		kf.setLeadingInterpolation( interpolation );
		kf.setTrailingInterpolation( interpolation );
		kf.setLeadingTension( tension );
		kf.setTrailingTension( tension );

		return kf;
	}
	
	//---------------------------------------- CONSTRUCTORS
	/**
	* Used internally by application.
	*/
	public AnimationKeyFrame( int frame, float value  )
	{
		this.value = value;
		this.frame = frame;
	}

	/**
	* Used internally by application.
	*/
	public AnimationKeyFrame(){}

	public void setFrame( int frame ){ this.frame = frame; }
	public void setValue( float value ){ this.value = value; }
	public float getValue(){ return value; }
	public int getFrame(){ return frame; }
	//--- Get and set for interpolation.
	public int getLeadingInterpolation(){ return leadingInterpolation; }
	public int getTrailingInterpolation(){ return trailingInterpolation; }
	public void setLeadingInterpolation( int interP ){ leadingInterpolation = interP; }
	public void setTrailingInterpolation( int interP ){ trailingInterpolation = interP; }
	//--- Get and set for tensions
	public float getLeadingTension(){ return leadingTension; }
	public float getTrailingTension(){ return trailingTension; }
	public void setLeadingTension( float leadingTension_ ){ leadingTension = leadingTension_; }
	public void setTrailingTension( float trailingTension_ ){ trailingTension = trailingTension_; }	
	//--- Loads all params from provided key frame.
	public void loadParams( AnimationKeyFrame paramSource )
	{
		leadingInterpolation = paramSource.getLeadingInterpolation();
		trailingInterpolation = paramSource.getTrailingInterpolation();
		leadingTension = paramSource.getLeadingTension();
		trailingTension = paramSource.getTrailingTension();
	}
	//--- Used for sorting keyframes
	public int compareTo( Object o )
	{
		AnimationKeyFrame cKF = (AnimationKeyFrame) o;
		if( frame < cKF.getFrame() ) return -1;
		if( frame > cKF.getFrame() ) return 1;

		return 0;
	}
	//--- Print debug info.
	public void printDebugInfo()
	{
		System.out.println("kf, v:" + value + ", f:" + frame 
					+ ", lip:" + leadingInterpolation 
					+ ", tip:" + trailingInterpolation );
	}

}//end class
