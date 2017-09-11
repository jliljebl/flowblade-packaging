package giotto2D.filters.transform;

import giotto2D.filters.AbstractFilter;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

//--- Copies specified channel ( or desaturates image first ) of source to destination alpha.
public class FlipFilter extends AbstractFilter
{

	public static BufferedImage filter( BufferedImage dest, boolean flipVertical, boolean filpHorizontal )
	{
		//--- Flip nothing
		if( flipVertical == false && filpHorizontal == false ) return dest;
	
		System.out.println("in FlipFilter");	
	
		AffineTransform tx;
		AffineTransformOp op;

		//--- Flip the image vertically and horizontally.
		//--- equivalent to rotating the image 180 degrees
		if( flipVertical == true && filpHorizontal == true )
		{
			tx  = AffineTransform.getScaleInstance(-1.0, -1.0);
			tx.translate(-dest.getWidth(), -dest.getHeight() );
			op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			return op.filter( dest, null );
		}

		//--- Flip vertically
		if( flipVertical == true )
		{
			tx = AffineTransform.getScaleInstance(1, -1);
			tx.translate(0, -dest.getHeight() );
			op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			return op.filter( dest, null );
		}

		//--- Flip horizontally
		tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-dest.getWidth(), 0);
		op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return op.filter( dest, null);
	}

}//end class