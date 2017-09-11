package animator.phantom.renderer.plugin;

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

import giotto2D.core.GTTObject;
import giotto2D.core.GTTOval;
import giotto2D.core.GTTRectangle;
import giotto2D.core.GeometricFunctions;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.AnimValueSliderEditor;
import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.IntegerParam;

public abstract class GridPlugin extends PhantomPlugin
{
	protected IntegerParam shapeType = new IntegerParam( 0 );
	protected AnimatedValue shapeSize = new AnimatedValue( 50.0f, 0.0f, 100.0f );
	protected AnimatedValue x = new AnimatedValue( 0 );
	protected AnimatedValue y = new AnimatedValue( 0 );
	protected AnimatedValue width = new AnimatedValue( 50 );
	protected AnimatedValue height = new AnimatedValue( 50 );
	protected AnimatedValue rotation = new AnimatedValue( 0 );

	//--- Shape types
	protected static final int BIT_MAP = 0;
	protected static final int RECT = 1;
	protected static final int OVAL = 2;

	protected void registerGridParams()
	{
		registerParameter( x );
		registerParameter( y );
		registerParameter( rotation );
		registerParameter( shapeType );
		registerParameter( shapeSize );
		registerParameter( width );
		registerParameter( height );
	}

	protected void addGridEditors( boolean bitmapOption )
	{
		String[] optionsWithBitmap = { "Bitmap", "Rectangle","Oval" };
		String[] options= { "Rectangle","Oval" };

		IntegerComboBox shapeSelect;
		if( bitmapOption )
			shapeSelect = new IntegerComboBox( 	shapeType,
								"Shape type",
								 optionsWithBitmap );
		else
			shapeSelect = new IntegerComboBox( 	shapeType,
								"Shape type",
								options );
			
		AnimValueSliderEditor sizeEdit = new AnimValueSliderEditor( "Shape size", shapeSize );
		AnimValueNumberEditor wEdit = new AnimValueNumberEditor( "Grid width", width );
		AnimValueNumberEditor hEdit = new AnimValueNumberEditor( "Grid height", height );
		AnimValueNumberEditor xEdit = new AnimValueNumberEditor( "X", x );
		AnimValueNumberEditor yEdit = new AnimValueNumberEditor( "Y", y );
		AnimValueNumberEditor rotationEdit = new AnimValueNumberEditor( "Rotation", rotation );

		addEditor( shapeSelect );
		addRowSeparator();
		addEditor( sizeEdit );
		addRowSeparator();
		addEditor( xEdit );
		addRowSeparator();
		addEditor( yEdit );
		addRowSeparator();
		addEditor( rotationEdit );
		addRowSeparator();
		addEditor( wEdit );
		addRowSeparator();
		addEditor( hEdit );
	}

	protected void drawGrid(Graphics2D graphics, 
				int canvasWidth, 
				int canvasHeight, 
				BufferedImage source, 
				Color color, 
				float frameTime,
				boolean hasBitmapOption )
	{
		//--- Get size multiplier
		float sizeVal = shapeSize.getValue( frameTime );
		float mult = 1.0f;
		if( shapeType.get() == OVAL ) 
			mult = 1.5f;// so size 100 about fills for oval 

		//--- 
		float gridWidth = width.getValue( frameTime );
		float gridHeight = height.getValue( frameTime );

		//--- 
		float shapeWidth = gridWidth * mult * (sizeVal / 100.f);
		float shapeHeight =  gridHeight * mult * (sizeVal / 100.f);

		//--- x, y, rotation
		float xd = x.getValue( frameTime );
		float yd = y.getValue( frameTime );
		float rVal = rotation.getValue( frameTime );

		//--- Get graphics and transform it 
		graphics.rotate( Math.toRadians( (double) rVal ) );
		graphics.translate( xd, yd );

		//--- Get rect of output img in untranslated space
		Point2D.Float topLeft = new Point2D.Float( 0, 0 );
		Point2D.Float topRight = new Point2D.Float( canvasWidth, 0);
		Point2D.Float bottomRight = new Point2D.Float( 0, canvasHeight );
		Point2D.Float bottomLeft = new Point2D.Float( canvasWidth, canvasHeight );

		//--- rotate output rect
		topRight = GeometricFunctions.rotatePointAroundOrigo( -rVal, topRight );
		bottomRight = GeometricFunctions.rotatePointAroundOrigo( -rVal, bottomRight );
		bottomLeft = GeometricFunctions.rotatePointAroundOrigo( -rVal, bottomLeft );

		//--- move output rect
		move( topLeft, -xd, -yd );
		move( topRight, -xd, -yd );
		move( bottomRight, -xd, -yd );
		move( bottomLeft, -xd, -yd );

		//--- Get min and max x and y
		Point2D.Float[] points = { topLeft, topRight, bottomRight, bottomLeft };
		float minY = GeometricFunctions.getMinY( points );
		float maxY = GeometricFunctions.getMaxY( points );
		float maxX = GeometricFunctions.getMaxX( points );
		float minX = GeometricFunctions.getMinX( points );

		//--- Create object for drawing
		GTTObject shape;
		int type = shapeType.get();
		if( !hasBitmapOption )
			type = type + 1;

		if( type == RECT ) 
			shape = new GTTRectangle( shapeWidth, shapeHeight );
		else 
			shape = new GTTOval( shapeWidth, shapeHeight );
		shape.setFillPaint( color );
		shape.setAnchorPoint( gridWidth / 2, gridHeight / 2 );//into center of shape

		Image tile = null;
		if( type == 0 && source != null )
		{
			tile = source.getScaledInstance( (int) gridWidth, (int) gridHeight, Image.SCALE_SMOOTH );
		}

		//--- Draw grid
		int startX = (int)(minX / gridWidth) - 2;
		int startY = (int)(minY / gridHeight) - 2;
		int endX = (int)( maxX /  gridWidth) + 2;
		int endY = (int)( maxY / gridHeight) + 4;

		for( int i = startX; i < endX; i++)
		{
			for( int j = startY; j < endY; j++)
			{
				//--- center pos for shape in grid box
				float x = i * gridWidth;
				float y = j * gridHeight;

				if( type > 0 )
				{
					//--- draw
					shape.setPos( x, y );
					shape.draw( graphics, new Rectangle( 0, 0, canvasWidth, canvasHeight ));
				}
				else
				{
					AffineTransform setPosTrans = new AffineTransform();
					setPosTrans.translate( (double) x, (double) y );
					graphics.drawImage( tile, setPosTrans, null );
				}
			}
		}
	}
	
	private static void move( Point2D.Float p, float dx, float dy )
	{
		float sx = (float) p.getX();
		float sy = (float) p.getY();
		p.setLocation( sx + dx, sy + dy ); 
	}

}//end class