package animator.phantom.renderer.parent;

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

import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.AnimatedImageCoordinates;

//--- This is a version of AnimatedImageCoordinates tbat has only key frames at 0 frame 
//--- and values to it MUST be set in frame 0.
//--- Used to create temporary values when rendering iops with parents.
public class TransformClone extends AnimatedImageCoordinates
{
	//--- AnimatedImageCoordinates with given values.
	public TransformClone( 	ImageOperation iop,
				float Fx,
				float Fy,
				float FxScale,
				float FyScale,
				float FxAnchor,
				float FyAnchor,
				float Frotation )
	{
		super( 	iop,
			Fx,
			Fy,
			FxScale,
			FyScale,
			FxAnchor,
			FyAnchor,
			Frotation );

		IS_TRANSFORM_CLONE = true;
		setAllValuesTypeFreeSet();
	}

}//end class