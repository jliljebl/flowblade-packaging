package animator.phantom.renderer.plugin;

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

import giotto2D.filters.merge.ValueShift;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.RowSeparator;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;

public class FileImagePatternMergePlugin extends PhantomPlugin
{
	private AnimatedValue patternOpacity;
	private AnimatedValue patternSoftness;
	
	public FileImagePatternMergePlugin()
	{
		initPlugin( MERGE, MERGE_INPUTS );
	}

	public void buildDataModel()
	{
 		setName( "FileImageMerge" );
		patternOpacity = new AnimatedValue( 100, 0, 100 );
		patternSoftness = new AnimatedValue( 0, 0, 255 );

		registerParameter( patternOpacity );
		registerParameter( patternSoftness );
	}

	public void buildEditPanel()
	{
 		AnimValueNumberEditor opacityE = new AnimValueNumberEditor( "Pattern Wipe", patternOpacity );
 		AnimValueNumberEditor softnessE = new AnimValueNumberEditor( "Pattern Softness", patternSoftness );

		addEditor( opacityE );
		addEditor( new RowSeparator() );
		addEditor( softnessE );
	}

	public void renderMask( float frame, Graphics2D maskGraphics, int canvasWidth, int canvasHeight )
	{
		BufferedImage pattern = getFileSourceImage( (int) Math.round((double) frame) );
		BufferedImage wipePattern = ValueShift.createShiftedImage( pattern, patternOpacity.get( frame ) / 100.0f, patternSoftness.get( frame ) );
		AffineTransform scaleTrans = new AffineTransform();
		scaleTrans.scale( (double)canvasWidth / (double)wipePattern.getWidth(), (double)canvasHeight / (double)wipePattern.getHeight() );
		maskGraphics.setTransform( scaleTrans );
		maskGraphics.drawImage( wipePattern, 0, 0, null );
		maskGraphics.setTransform( new AffineTransform() );
	}

}//end class
