package giotto2D.core;

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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

//--- Abstract object that capsulates basic functionality of this package.
//--- Classes main responsibility is to handle giotto object transformations by
//--- transforming graphics context so that object renders as specified.
public abstract class GTTObject
{
	//--- Transformation data x, y, scales, anchor and rotation.
	protected TransformData trans;
	//--- Opacity of image, fill and stroke, and composite rule
	protected float opacity = 1.0f;
	protected int compositeRule = AlphaComposite.SRC_OVER;
	//--- if false no composining values are set
	protected boolean setComposite = true;
	//--- Graphics context of target image.
	protected Graphics2D g;
	//--- Fill visiblity
	protected boolean fillVisible = true;
	//--- Stroke visibility.
	protected boolean strokeVisible = true;
	//--- Main color of obejct
	protected Paint fillPaint = Color.white;
	//--- Color of object outline.
	protected Paint strokePaint = Color.darkGray;
	//--- flag for drawing stroke
	protected int strokeWidth = 0;
	//--- User settable stroke 
	protected Stroke stroke = null;

	//----------------------------------------------- INITIALIZATION
	//--- all extending MUST call this.
	protected void init()
	{
		trans = new TransformData();
	}

	//----------------------------------------------- TRANSFORMATION INTERFACE
	//--- translation
	public void setPos( float x, float y )
	{
		trans.x = x;
		trans.y = y;
	}
	public void setPos( Point2D.Float pos )
	{		
		trans.x = pos.x;
		trans.y = pos.y;
	}
	public Point2D.Float getPos(){ return new Point2D.Float( trans.x, trans.y ); }
	
	public void move(  float x, float y )
	{
		trans.x = trans.x + x;
		trans.y = trans.y + y;
	}
	//--- scale
	public void setScale( float scale )
	{
		trans.scaleX = scale;
		trans.scaleY = scale;
	}
	public void setScale( float xScale, float yScale )
	{
		trans.scaleX = xScale;
		trans.scaleY = yScale;
	}
	public void setXScale( float xScale ){ trans.scaleX = xScale; }
	public void setYScale( float yScale ){ trans.scaleY = yScale; }	
	public float getXScale(){ return trans.scaleX; }
	public float getYScale(){ return trans.scaleY; }
	//--- rotation
	public void setRotation( float rotation ){ trans.rotation = rotation; }
	public void rotate( float rDelta ){ trans.rotation = trans.rotation + rDelta; }
	public float getRotation(){ return trans.rotation; }
	//--- anchor
	public void setAnchorPoint( Point2D.Float aPos )
	{		
		trans.anchorX = aPos.x;
		trans.anchorY = aPos.y;
	}
	public void setAnchorPoint( float ax, float ay )
	{		
		trans.anchorX = ax;
		trans.anchorY = ay;
	}
	public Point2D.Float getAnchorPoint()
	{ 
		return new Point2D.Float( trans.anchorX, trans.anchorY );
	}
	public float getAnchorX(){ return trans.anchorX; }
	public float getAnchorY(){ return trans.anchorY; }

	//--------------------------------------------- ATRRIBUTE INTERFACE
	//--- paint
	public void setPaint( Paint p )
	{ 
		fillPaint = p;
		strokePaint = p;
	}
	//--- fill
	public void setFillPaint( Paint p ){ fillPaint = p; }
	public void setFillVisible( boolean b ){ fillVisible = b; }
	public boolean fillIsVisible(){ return fillVisible; }
	//--- stroke
	public void setStrokePaint( Paint p ){ strokePaint = p; }
	public void setStrokeVisible( boolean b ){ strokeVisible = b; }
	public void setStroke( Stroke s ){ stroke = s; }
	public void setStrokeWidth( int sWidth ){ strokeWidth = sWidth; }
	public boolean strokeIsVisible()
	{
		if( strokeVisible == false ) return false;
		if( stroke != null ) return true;
		if( strokeWidth == 0 ) return false;
		else return true;
	}
	//--- opacity
	public void setOpacity( float o )
	{ 
		opacity = o;
	}

	public void setCompositeRule( int rule ){ compositeRule = rule; }
	public void setApplyCompositeToContext( boolean val ){ setComposite = val; }

	//--------------------------------------------- RENDERING
	public void draw( Graphics2D g2, Rectangle canvasSize )
	{
		g = g2;
 		AffineTransform saveAT = g2.getTransform();
		resetAttributes();
		g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON );
		g.setRenderingHint( RenderingHints.KEY_RENDERING, 
					RenderingHints.VALUE_RENDER_QUALITY );

		//--- Render image
		render();
		//--- Return transform to original state
		g.setTransform( saveAT );
	}

		/*
	public void draw( TransformableImage tImg )
	{
		//--- Capture image and create graphics object.
		//img = tImg;
		g = tImg.createGraphics();
		//--- Get size data.
		CANVAS_WIDTH = tImg.getWidth();
		CANVAS_HEIGHT = tImg.getHeight();
		//--- Render image
		render();
		//--- close graphics object
		g.dispose();
	}
	*/
	//--- Render is called when graphics object creted and data
	private void render()
	{
		//--- Transform graphics context before drawing
		applyTransformation();
		//--- Draw object image. This method IS overloded by extending classes.
		drawUnTransformedObject();
		//--- Reset transformation
		
	}
	//--- Transforms graphics so that object renders as specified.
	private void applyTransformation()
	{
		//--- Get scaled anchor offsets.
		float anchorX = trans.scaleX * trans.anchorX;
		float anchorY = trans.scaleY * trans.anchorY;

		//--- Get offset to topleft corner of scaled graphics context from anchor point.
		Point2D.Float topLeftOffset = new Point2D.Float( -anchorX, -anchorY );
		topLeftOffset =
			GeometricFunctions.rotatePointAroundOrigo( trans.rotation, topLeftOffset );

		//--- Set translation to topleft point, so that after translation,
		//--- scale and rotation anchor point in transformed context
		//--- will hit same place as x and y in untransformed context.
		float xTranslation = trans.x + topLeftOffset.x;
		float yTranslation = trans.y + topLeftOffset.y;
	
		//--- Translate.
		g.translate( xTranslation, yTranslation );

		//--- Rotate
		g.rotate( Math.toRadians( (double) trans.rotation ) );

		//--- Scale 
		g.scale( (double) trans.scaleX, (double) trans.scaleY );

		//--- Set opacity and composite type.
		if( setComposite )
			g.setComposite( AlphaComposite.getInstance( compositeRule, opacity ));

		//--- Now, drawing to g will produce effect of correctly moved object.
	}

	//----------------------------------------------- ATTRIBUTES SETTING FOR EXTENDING CLASSES.
	//--- Sets attributesa like color, paint etc
	protected void setFillAttributes()
	{
		if( setComposite )
			g.setComposite( AlphaComposite.getInstance( compositeRule, opacity ));

		g.setPaint( fillPaint );
	}
	protected void setStrokeAtrributes()
	{
		if( setComposite )
			g.setComposite( AlphaComposite.getInstance( compositeRule, opacity ));

		if( stroke != null ) 
		g.setStroke( stroke );
		else
		{
			g.setPaint( strokePaint );
			g.setStroke( new BasicStroke( (float) strokeWidth ) );
		}
	}
	protected void resetAttributes()
	{
		if( setComposite )
			g.setComposite( AlphaComposite.getInstance( compositeRule, opacity ));

		g.setPaint( null );
	}

	//----------------------------------------------- DRAW FUNCTION
	//--- ->Everything<- is drawn here.
	protected abstract void drawUnTransformedObject();

}//end class 