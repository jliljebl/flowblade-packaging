package giotto2D.blending;

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
import giotto2D.blending.mbmodes.MoveBlurAddBlend;
import giotto2D.blending.modes.AbstractBlender;
import giotto2D.blending.modes.AddBlend;
import giotto2D.blending.modes.ColorBurn;
import giotto2D.blending.modes.ColorDodgeBlend;
import giotto2D.blending.modes.DarkenBlend;
import giotto2D.blending.modes.HardlightBlend;
import giotto2D.blending.modes.LightenBlend;
import giotto2D.blending.modes.MultiplyBlend;
import giotto2D.blending.modes.NormalBlend;
import giotto2D.blending.modes.OverlayBlend;
import giotto2D.blending.modes.ScreenBlend;
import giotto2D.blending.modes.SubtractBlend;

public abstract class AbstractImageBlender
{
	//--- BLENDING MODES
	public static final int NORMAL = 0;
	public static final int ADD = 1;
	public static final int LIGHTEN = 2;
	public static final int SCREEN = 3;
	public static final int SUBTRACT = 4;
	public static final int DARKEN = 5;
	public static final int MULTIPLY = 6;
	public static final int OVERLAY = 7;
	public static final int HARDLIGHT = 8;
	public static final int COLORBURN = 9;
	public static final int COLORDODGE = 10;
	public static final int MOVE_BLUR_ADD = 11;

	
	//--- Return blender object for given mode
	protected AbstractBlender getBlender( int mode )
	{
		switch( mode )
		{
			case NORMAL:
				return new NormalBlend();

			case LIGHTEN:
				return new LightenBlend();
			
			case DARKEN:
				return new DarkenBlend();

			case ADD:
				return new AddBlend();

			case SCREEN:
				return new ScreenBlend();

			case MULTIPLY:
				return new MultiplyBlend();

			case SUBTRACT:
				return new SubtractBlend();

			case OVERLAY:
				return new OverlayBlend();

			case HARDLIGHT:
				return new HardlightBlend();

			case COLORBURN:
				return new ColorBurn();

			case COLORDODGE:
				return new ColorDodgeBlend();

			case MOVE_BLUR_ADD:
				return new MoveBlurAddBlend();

			default:
				System.out.println("ImageBlender.getBlender() default hit");
				return new NormalBlend();
		}
	}
	
}//end class