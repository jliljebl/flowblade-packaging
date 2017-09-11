package animator.phantom.gui.keyframe;

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

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JPanel;

import animator.phantom.controller.EditorsController;
import animator.phantom.gui.AnimFrameGUIParams;
import animator.phantom.gui.GUIColors;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.KeyFrameParam;
import animator.phantom.renderer.param.Param;

//--- Panel used select a parameter to edit in the keyframe editor.
public class KFColumnPanel extends JPanel implements MouseListener
{
	private Vector<KFParamBox> params = new Vector<KFParamBox>();
	//--- IOP being edited
	private ImageOperation iop;
	private KFParamBox selectedParam;

	//---------------------------------------------- CONSTRUCTOR	
	public KFColumnPanel()
	{	
		addMouseListener( this );
	}

	public ImageOperation getIOP(){ return iop; }

	public void initGUI( ImageOperation iop )
	{
		params = new Vector<KFParamBox>();
		this.iop = iop;
		if( iop == null )
		{
			params.clear();
			repaint();
			return;
		}

		Vector<Param> pVec = iop.getkeyFrameParamsAsParams();

		for( Param p : pVec )
		{
			KeyFrameParam kfp = (KeyFrameParam) p;
			if( kfp.hideFromKFEditor() != true )
				params.add( 0, new KFParamBox( p ));
		}

		if( params.size() > 0 ) 
			selectedParam = params.elementAt( 0 );

		repaint();
	}

	public void setSelected( int index )
	{
		if( selectedParam != null ) selectedParam.setSelected( false );
		selectedParam = params.elementAt( index );
		selectedParam.setSelected( true );
		repaint();
	}

	public Param getCurrentParam()
	{
		if( selectedParam == null ) return null;
		return selectedParam.getParamAsParam();
	}

	//-------------------------------------------- MOUSE EVENTS
	public void mouseClicked(MouseEvent e)
	{
		requestFocusInWindow();

		//--- Get click coordinates.
 		int y = e.getY();
		//--- If click is not on any param, leave
		if( y > ( params.size() * AnimFrameGUIParams.TE_ROW_HEIGHT ) - 1 )
			return;
		//--- Get clicked param
		int selectedIndex = y / AnimFrameGUIParams.TE_ROW_HEIGHT;
		if( selectedParam != null ) selectedParam.setSelected( false );
		selectedParam = params.elementAt( selectedIndex );
		selectedParam.setSelected( true );
		//--- Display it in editor panel.
		EditorsController.setKFEditParam( selectedParam.getParam(), iop );
	}
	
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	//public void mouseMoved(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}

	//---------------------------------------------- GRAPHICS 
	public void paintComponent( Graphics g )
	{
		//--- Draw bg
		g.setColor( GUIColors.timeLineColumnColor );
		g.fillRect( 0, 0, getWidth(), getHeight() );

		//--- Draw boxes
		for( int i = 0; i < params.size(); i++ )
		{
			KFParamBox drawBox = params.elementAt( i );
			drawBox.paint( g, 0, i * AnimFrameGUIParams.TE_ROW_HEIGHT );
		}
		//--- Draw closing horizontal line.
		g.setColor( GUIColors.lineBorderColor );
		if( params.size() > 0 )
			g.drawLine( 0, ( params.size() * AnimFrameGUIParams.TE_ROW_HEIGHT ) + 1,
					0 + AnimFrameGUIParams.TE_LEFT_COLUMN_WIDTH,
					( params.size() * AnimFrameGUIParams.TE_ROW_HEIGHT ) + 1 );
	}

}//end class