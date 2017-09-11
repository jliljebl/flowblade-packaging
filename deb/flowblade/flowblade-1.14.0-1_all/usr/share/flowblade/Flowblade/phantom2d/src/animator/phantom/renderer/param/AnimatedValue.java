package animator.phantom.renderer.param;

/*
    Copyright Janne Liljeblad.

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

import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;

import animator.phantom.bezier.BezierSegment;
import animator.phantom.gui.modals.DialogUtils;
import animator.phantom.renderer.ImageOperation;
//Setting values in non-clip area is refused only here.

/**
* A parameter that captures the change of a float value in time. 
* <p>
* Change is defined by setting keyframes with values, frame positions  
* and leading and trailing interpolations. Interpolations between keyframes are  either linear or bezier curves.
* <p>
* Keyframes and value changes are relative to the position in the timeline of the plugin the parameter is registered to, not relative to absolute
* movie frames.
* All the movie frame to clip frame conversions are done internally in this class.
* <p>
* When writing plugins only constructors and methods <code>getValue( int movieFrame )</code> 
* and <code>getValue( float movieFrame )</code> should be used <b>unless</b> 
* you wish to create a custom GUI editor component for this paramater. Other public methods are used internally by Phantom2D.
* @see <code>AnimValueSliderEditor</code>, <code>AnimValueNumberEditor</code>
*/
public class AnimatedValue extends Param implements KeyFrameParam 
{
	//--- All keyframes. Members in vector are kept in frame order at all times.
	private Vector <AnimationKeyFrame> keyFrames = new Vector<AnimationKeyFrame>();
	//--- Value can be locked from editing
	private boolean isLocked = false;
 	//--- Range
	private boolean restrictedValueRange = false;
	private float minValue = 0;
	private float maxValue = 0;
	//--- Flag for set restrictions.
	private boolean freeSet = false;
	//--- Flag stepped value output.
	private boolean stepped = false;
	/**
	* Used to hide parameter from keyframe editor in some rare cases. Default value false.
	*/
	private boolean hideFromKFEditor = false;
	/**
	* No params constructor.
	*/
	public AnimatedValue(){}

	/**
	* Constructor with default value.
	*/
	public AnimatedValue( float defaultValue )
	{
		this( null, defaultValue );
	}


	/**
	* Constructor with default value and range of acceptable values.
	*/
	public AnimatedValue( float defaultValue, float minValue, float maxValue  )
	{
		this( null, defaultValue, minValue, maxValue  );
	}

	/**
	* Constructor with default value of zero for non-plugin use.
	*/
	public AnimatedValue( ImageOperation iop )
	{
		this.iop = iop;
		//--- One keyframe in frame 0 must exist.
		keyFrames.add( AnimationKeyFrame.createNewKeyframe( 0, 0 ) );
	}

	/**
	* Constructor with default value for non-plugin use.
	*/
	public AnimatedValue( ImageOperation iop, float defaultValue )
	{
		this.iop = iop;
		//--- One keyframe in frame 0 must exist.
		keyFrames.add( AnimationKeyFrame.createNewKeyframe( 0, defaultValue ) );
	}

	/**
	* Constructor with default value  and range of acceptable values for non-plugin use.
	*/
	public AnimatedValue( ImageOperation iop, float defaultValue, float minValue, float maxValue  )
	{
		this.iop = iop;
		//--- Set range and default value.
		this.minValue = minValue;
		this.maxValue = maxValue;
		if( defaultValue > maxValue ) defaultValue = maxValue;
		if( defaultValue < minValue ) defaultValue = minValue;
		restrictedValueRange = true;
		//--- One keyframe in frame 0 must exist.
		keyFrames.add( AnimationKeyFrame.createNewKeyframe( 0, defaultValue ) );
	}

	//-------------------------------------------------------- INTERFACE
	/** 
	* Returns parent ImageOperation
	*/
	public ImageOperation getIOP(){ return iop; }
	/**
	* Returns value in movie frame. Frame is converted to clip frame internally. This is the only method that should be used when writing plugins.
	* @param movieFrame Frame in timeline.
	* @return Value at frame.
	*/
	public float get( int movieFrame )
	{
		return getValue( movieFrame );
	}
	/**
	* Returns value in movie frame. Frame is converted to clip frame internally. This is the only method that should be used when writing plugins.
	* @param movieFrame Frame in timeline.
	* @return Value at frame.
	*/
	public float getValue( int movieFrame )
	{
		//--- Translate frame from movie frame to clip frame.
		int animationFrame = iop.getClipFrame( movieFrame );
		//--- Try to get keyframes before and after given frame.
		AnimationKeyFrame leadingFrame = null;
		AnimationKeyFrame trailingFrame = null;
		//--- Iterate all key frames. They are in growing frame order.
		for( int i = 0; i < keyFrames.size(); i++ )
		{
			//--- Get next keyframe.
			AnimationKeyFrame kf = keyFrames.elementAt( i );
			//--- There is a key frame in requested frame, return it's value.
			if( kf.getFrame() == animationFrame ) return kf.getValue();
			//--- Leading frame is changed until keyframe with bigger frame found.
			if( kf.getFrame() < animationFrame ) leadingFrame = kf;
			//--- Trailing frame is the first with bigger frame
			if( kf.getFrame() > animationFrame )
			{
				trailingFrame = kf;
				break;
			}
		}

		//--- Stepped Animatedvalue objects returns leading frame value for full range between two keyframes.
		if( stepped == true ) return leadingFrame.getValue();
		//--- If no trailingFrame found, animationframe was after last keyframe,
		//--- use leadingFrame's value.
		if( trailingFrame == null ) return leadingFrame.getValue();
		//--- Values before clipFrame 0  == value at clipFrame 0
		if( leadingFrame == null ) return keyFrames.elementAt( 0 ).getValue();
		//--- because frame 0 always has keyframe.
		return getInterPolatedvalue( animationFrame, leadingFrame, trailingFrame );
	}
	/**
	* Returns value in movie frame fraction. Used when rendering motion blur.
	* @param movieFrame Frame in timeline.
	* @return Value at frame.
	*/
	public float get( float movieFrame )
	{
		return getValue( movieFrame );
	}
	/**
	* Returns value in movie frame fraction. Used when rendering motion blur.
	* @param movieFrame Frame in timeline.
	* @return Value at frame.
	*/
	public float getValue( float movieFrame )
	{
		//--- Translate frame from movie frame to clip frame.
		int floorFrame = (new Double( Math.floor( (double) movieFrame ) )).intValue();
		int animationFrame = iop.getClipFrame( floorFrame );
		int clipDelta = floorFrame - animationFrame;
		float clipFrame = movieFrame - clipDelta;
		//--- Try to get keyframes before and after given frame.
		AnimationKeyFrame leadingFrame = null;
		AnimationKeyFrame trailingFrame = null;
		//--- Iterate all key frames. They are in growing frame order.
		for( int i = 0; i < keyFrames.size(); i++ )
		{
			//--- Get next keyframe.
			AnimationKeyFrame kf = keyFrames.elementAt( i );
			float testFrame = (float) kf.getFrame();
			//--- There is a key frame in requested frame, return it's value.
			if( clipFrame == testFrame ) return kf.getValue();
			//--- Leading frame is changed until keyframe with bigger frame found.
			if( testFrame < clipFrame ) leadingFrame = kf;
			//--- Trailing frame is the first with bigger frame
			if( testFrame > clipFrame )
			{
				trailingFrame = kf;
				break;
			}
		}
		//--- Stepped Animatedvalue objects returns leading frame value for full range between two keyframes.
		if( stepped == true ) return leadingFrame.getValue();
		//--- If no trailingFrame found, animationframe was after last keyframe,
		//--- use its value.
		if( trailingFrame == null ) return keyFrames.lastElement().getValue();
		//--- Values before clipFrame 0  == value at clipFrame 0
		if( leadingFrame == null ) return keyFrames.elementAt( 0 ).getValue();
	
		return getInterPolatedvalue( (movieFrame - clipDelta), leadingFrame, trailingFrame );
	}
	/**
	* Set value in given frame by adding keyframe if none exists. If keyframe exists set its value, else create keyframe.
	* Used generally when creating editor components or edit layers for ViewEditor.
	*/
	public void setValue( int movieFrame, float value )
	{
		//--- Only in clip area you can set values.
		if( !iop.frameInClipArea( movieFrame ) && !freeSet )
		{
			Exception e = new Exception();
			System.out.println( "Tried to set value outside clip are, stack trace:" );
			e.printStackTrace();
			DialogUtils.showTwoStyleInfo( "Value discarded", "You can only set keyframe values in clip area.", DialogUtils.WARNING_MESSAGE );
			return;
		}
		//--- Translate frame from movie frame to clip frame.
		int animationFrame = iop.getClipFrame( movieFrame );
		//--- Force value in range if restrictedValueRange.
		if( restrictedValueRange ) value = getValueInRange( value );
		//--- Set value or create new keyframe
		AnimationKeyFrame kf = getKeyFrameInClipSpace( animationFrame );
		if( kf == null ) addKeyFrameInClipSpace( animationFrame, value );
		else kf.setValue( value );
		//--- Update draw Vector.
		iop.createKeyFramesDrawVector();
	}
	/**
	* Creates a new keyframe.
	*/
	public void addKeyFrame( int movieFrame, float value )
	{
		//--- Only in clip area you can set values.
		if( !iop.frameInClipArea( movieFrame ) && !freeSet )
		{
			Exception e = new Exception();
			System.out.println( "Tried to add key frame outside clip are, stack trace:" );
			e.printStackTrace();
			DialogUtils.showTwoStyleInfo( "Value discarded", "You can only set keyframe values in clip area.", DialogUtils.WARNING_MESSAGE );
			return;
		}
		//--- Translate frame from movie frame to clip frame.
		int animationFrame = iop.getClipFrame( movieFrame );
		//--- Add key frame.
		addKeyFrameInClipSpace( animationFrame, value );
	}

	/**
	* Removes keyframe from frame if exists.
	*/
	public void removeKeyFrame( int movieFrame )
	{
		//--- Translate frame from movie frame to clip frame.
		int animationFrame = iop.getClipFrame( movieFrame );
		removeKeyFrameInClipSpace( animationFrame );
	}

	/**
	* Copies params from provided keyframe to keyframe in given frame if there is a keyframe.
	*/
	public void copyParams( int movieFrame, AnimationKeyFrame paramSource )
	{
		int animationFrame = iop.getClipFrame( movieFrame );
		//--- Set value or create new keyframe
		AnimationKeyFrame kf = getKeyFrameInClipSpace( animationFrame );
		if( kf != null ) kf.loadParams( paramSource );
	}
	/**
	* Returns keyframe in movie frame or null.
	*/
	public AnimationKeyFrame getKeyFrame( int movieFrame )
	{ 
		int animationFrame = iop.getClipFrame( movieFrame );
		return getKeyFrameInClipSpace( animationFrame );
	}
	/**
	* Gets all keyframes.
	*/
	public Vector <AnimationKeyFrame> getKeyFrames(){ return keyFrames; }
	/**
	* Sets keyframes Vector.
	*/
	public void setKeyFrames( Vector <AnimationKeyFrame> keyFrames_ ){ keyFrames = keyFrames_; }
	/**
	* Retuns keyframe info for movieframe. Used too display triangles and diamond in AnimValueNumberEditor
	*/
	public ParamKeyFrameInfo getKeyFrameInfo( int movieFrame )
	{
		//--- lastKeyFrameFrame in clip space.
		int lastKeyFrameFrame = keyFrames.elementAt( keyFrames.size() - 1 ).getFrame();
		//--- Translate frame from movie frame to clip frame.
		int animationFrame = iop.getClipFrame( movieFrame );
		//--- See if has keyframes after
		boolean keyFramesAfter = false;//right triangle
		if( animationFrame < lastKeyFrameFrame ) keyFramesAfter = true;
		//--- See if has keyframes before
		boolean keyFramesBefore = false;//left triagle
		if( animationFrame > 0 ) keyFramesBefore = true;
		//--- See if has keyframe in frame.
		boolean onKeyFrame = hasKeyFrameInFrame( movieFrame );
		//--- Create and return answer
		return new ParamKeyFrameInfo( keyFramesAfter, keyFramesBefore, onKeyFrame );
	}
	/**
	* Retuns true if there is keyframe in this movie frame.
	*/
	public boolean hasKeyFrameInFrame( int movieFrame )
	{
		//--- Translate frame from movie frame to clip frame.
		int animationFrame = iop.getClipFrame( movieFrame );
		for( AnimationKeyFrame keyFrame: keyFrames ) 
			if( keyFrame.getFrame() == animationFrame ) return true;
		return false;
	}
	/**
	* Currently does nothing.
	*/
	public void setLocked( boolean value ){ isLocked = value; }
	/**
	* Currently locking does nothing.
	*/
	public boolean isLocked(){ return isLocked; }
	/**
	* Gets min range value
	*/
	public float getMinValue(){ return minValue; }
	/**
	* Gets max range value.
	*/
	public float getMaxValue(){ return maxValue; }
	/**
	* Sets min range value.
	*/
	public void setMinValue( float min ){ minValue = min; }
	/**
	* Sets max range value.
	*/
	public void setMaxValue( float max ) { maxValue = max; }
	/**
	* Returns true if value range is restricted.
	*/
	public boolean hasRestrictedValueRange(){ return restrictedValueRange; }
	/**
	* Sets value range to be restricted.
	*/
	public void setRestrictedValueRange( boolean b ){ restrictedValueRange = b; }
	/**
	* Retuns max value of keyframes.
	*/
	public float getMaxKeyValue()
	{
		float max = keyFrames.elementAt( 0 ).getValue();
		for( AnimationKeyFrame keyFrame: keyFrames ) 
			if( keyFrame.getValue() > max ) max = keyFrame.getValue();
		return max;
	}
	/**
	* Retuns min value of keyframes.
	*/
	public float getMinKeyValue()
	{
		float min = keyFrames.elementAt( 0 ).getValue();
		for( AnimationKeyFrame keyFrame: keyFrames ) 
			if( keyFrame.getValue() < min ) min = keyFrame.getValue();
		return min;
	}
	/**
	* Removes all but clip frame 0 keyframe.
	*/
	public void clearKeyframes()
	{
		for( int i = 0; i < keyFrames.size() - 1; i++ )
			keyFrames.remove( 1 );
	}

	/**
	* Returns this as Param when handled as KeyFrameParam
	*/
	public Param getAsParam(){ return this; }
	
	/**
	* Sets parameter accept values outside of clip range. Used by <code>TransformClones.</code>
	*/
	public void setFreeSet(){ freeSet = true; }

	public void setStepped( boolean stepped ){ this.stepped = stepped; }
	public boolean getStepped(){ return this.stepped; }
	
	/**
	* Sorts keyframes by frame value.
	*/
	public void sortKeyframes()
	{
		//--- Sort
		Collections.sort( keyFrames );

		//--- Remove duplicates
		HashSet<AnimationKeyFrame> removeFrames = new HashSet<AnimationKeyFrame>();
		for( int i = 1; i < keyFrames.size(); i++)
		{
			int next = i + 1;
			if( next < keyFrames.size() )
			{
				AnimationKeyFrame kf1 = keyFrames.elementAt( i );
				AnimationKeyFrame kf2 = keyFrames.elementAt( next );
				if( kf1.getFrame() == kf2.getFrame() )
					removeFrames.add( kf2 );
			}
		}
		keyFrames.remove( removeFrames );
	}

	/**
	* Returns true if param should be hidden from KeyFrameEditor.
	*/
	public boolean hideFromKFEditor(){ return hideFromKFEditor; }
	/**
	* Hides param from KeyFrameEditor.
	*/
	//public void setHiddenFromKFEditor(){ hideFromKFEditor = true; }

	//--------------------------------------------------- PRIVATE METHODS
	private void addKeyFrameInClipSpace( int animationFrame, float value )
	{
		//--- Keyframe at frame 0 is handled differently, because it can't be removed.
		if( restrictedValueRange ) value = getValueInRange( value );
		if( animationFrame == 0 )
		{
			setValue( animationFrame, value );
			return;
		}

		//--- Remove possible keyframe in this frame.
		removeKeyFrameInClipSpace( animationFrame );
	
		//--- Place keyframe in correct place in vector, rising frame order.
		int indexForNewFrame = 0;
		for( int i = 0; i < keyFrames.size(); i++ )
		{
			AnimationKeyFrame kf = keyFrames.elementAt( i );
			if( kf.getFrame() < animationFrame ) indexForNewFrame = i;
		}
		//--- Create new keyframe.
		AnimationKeyFrame addFrame = AnimationKeyFrame.createNewKeyframe( animationFrame, value );
		
		//--- Add keyframe into end of vector if it has largest frame number
		if( indexForNewFrame == ( keyFrames.size() - 1 ) ) 
		{
			keyFrames.add( addFrame );
		}
		//--- Add key frame into correct position in the middle of the Vector.
		else
		{
			keyFrames.add( indexForNewFrame + 1, addFrame );
		}
		//--- Update draw Vector.
		iop.createKeyFramesDrawVector();
	}
	
	//--- Returns keyframe if exists, null if not.
	private AnimationKeyFrame getKeyFrameInClipSpace( int animationFrame )
	{
		AnimationKeyFrame returnFrame = null;
		for( AnimationKeyFrame keyFrame: keyFrames ) 
			if( keyFrame.getFrame() == animationFrame ) returnFrame = keyFrame;
		return returnFrame;
	}

	//--- Removes keyframe from frame if exists.
	private void removeKeyFrameInClipSpace( int animationFrame )
	{
		//--- First key frame from frame 0 can't be removed.
		if( animationFrame == 0 ) return;
		//--- Remove keyframe from given frame, if found.
		for( int i = 0; i < keyFrames.size(); i++ )
		{
			if( keyFrames.elementAt( i ).getFrame() == animationFrame )
				keyFrames.remove( i );
		}
		//--- Update draw Vector.
		iop.createKeyFramesDrawVector();
	}

	//--- Returns value from linear range between two frames.
	private float getInterPolatedvalue( 	float frame,
						AnimationKeyFrame leadingFrame,
						AnimationKeyFrame trailingFrame )
	{

		if( leadingFrame.getTrailingInterpolation() == AnimationKeyFrame.BEZIER ||
			trailingFrame.getLeadingInterpolation() == AnimationKeyFrame.BEZIER )
			return getBezierInterpolatedValue( frame, leadingFrame, trailingFrame );
		else return getLinearInterpolatedValue( frame, leadingFrame, trailingFrame );
	}

	private float getLinearInterpolatedValue( 	float frame,
							AnimationKeyFrame leadingFrame,
							AnimationKeyFrame trailingFrame )
	{
		float valuechange = trailingFrame.getValue() - leadingFrame.getValue();
		float frameDifference = (float)trailingFrame.getFrame() - (float)leadingFrame.getFrame();
		float valueChangePerFrame = valuechange / frameDifference;
		float framesFromLeading = frame - (float)leadingFrame.getFrame();

		return leadingFrame.getValue() + ( framesFromLeading * valueChangePerFrame );
	}
	
	private float getBezierInterpolatedValue(	float frame,
							AnimationKeyFrame leadingFrame,
							AnimationKeyFrame trailingFrame )
	{
		BezierSegment bezSeq = new BezierSegment( leadingFrame, trailingFrame );
		return bezSeq.getSegmentValue( frame );
	}

	//--- Returns value that is forced into into current set value range.
	private float getValueInRange( float value )
	{
		if( value > maxValue ) value = maxValue;
		if( value < minValue ) value = minValue;
		return value;
	}

}//end class
