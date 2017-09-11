/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.jhlabs.image;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Simplified from MotionBlurOp to get rid of some surprising behaviour when used to create light rays.
 */
public class RaysBlurOp extends AbstractBufferedImageOp {

    private float centreX = 0.5f;
    private float centreY = 0.5f;
    private float zoom = 0.0f;

    /**
     * Construct a MotionBlurOp.
     */
    public RaysBlurOp() {
	}

	
	/**
     * Set the blur zoom.
     * @param zoom the zoom factor.
     * @see #getZoom
     */
	public void setZoom( float zoom ) {
		this.zoom = zoom;
	}

	/**
     * Get the blur zoom.
     * @return the zoom factor.
     * @see #setZoom
     */
	public float getZoom() {
		return zoom;
	}
	
	/**
	 * Set the centre of the effect in the X direction as a proportion of the image size.
	 * @param centreX the center
     * @see #getCentreX
	 */
	public void setCentreX( float centreX ) {
		this.centreX = centreX;
	}

	/**
	 * Get the centre of the effect in the X direction as a proportion of the image size.
	 * @return the center
     * @see #setCentreX
	 */
	public float getCentreX() {
		return centreX;
	}
	
	/**
	 * Set the centre of the effect in the Y direction as a proportion of the image size.
	 * @param centreY the center
     * @see #getCentreY
	 */
	public void setCentreY( float centreY ) {
		this.centreY = centreY;
	}

	/**
	 * Get the centre of the effect in the Y direction as a proportion of the image size.
	 * @return the center
     * @see #setCentreY
	 */
	public float getCentreY() {
		return centreY;
	}

	
    private int log2( int n ) {
        int m = 1;
        int log2n = 0;

        while (m < n) {
            m *= 2;
            log2n++;
        }
        return log2n;
    }

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        if ( dst == null )
            dst = createCompatibleDestImage( src, null );
        BufferedImage tsrc = src;
        float cx = (float)src.getWidth() * centreX;
        float cy = (float)src.getHeight() * centreY;
	float imageRadius = (float)Math.sqrt((float)src.getWidth() * (float)src.getWidth() + (float)src.getHeight() * (float)src.getHeight());
        float scale = zoom;
        float maxDistance = zoom * imageRadius;
        int steps = log2((int)maxDistance);
        scale /= maxDistance;

        if ( steps == 0 ) {
            Graphics2D g = dst.createGraphics();
            g.drawRenderedImage( src, null );
            g.dispose();
            return dst;
        }
        
        BufferedImage tmp = createCompatibleDestImage( src, null );
        for ( int i = 0; i < steps; i++ ) {
            Graphics2D g = tmp.createGraphics();
            g.drawImage( tsrc, null, null );

           g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
           g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
           g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.5f ) );

            g.translate( cx, cy );
            g.scale( 1.0001+scale, 1.0001+scale );  // The .0001 works round a bug on Windows where drawImage throws an ArrayIndexOutofBoundException
            g.translate( -cx, -cy );

            g.drawImage( dst, null, null );
            g.dispose();
            BufferedImage ti = dst;
            dst = tmp;
            tmp = ti;
            tsrc = dst;

            scale *= 2;
        }
        return dst;
    }
    
	public String toString() {
		return "Blur/Faster Motion Blur...";
	}
}
