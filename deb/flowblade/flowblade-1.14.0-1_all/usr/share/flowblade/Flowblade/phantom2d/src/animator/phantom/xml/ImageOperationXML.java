package animator.phantom.xml;

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

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.project.Project;
import animator.phantom.renderer.IOPLibrary;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.SwitchData;
import animator.phantom.renderer.param.Param;

public class ImageOperationXML extends AbstractXML
{
	public static final String ELEMENT_NAME = "iop";
	public static final String FILTER_STACK_ELEMENT = "filterstack";
	public static final String FILTER_STACK_IOP_ELEMENT = "filteriop";
	public static ImageOperation currentIop = null;//used by AnimatedValueVectorParam, they need reference to iop.

	public static ImageOperation getObject( Element e, Project projectObj, boolean isFilterStackIop  )
	{
		String iopClass = e.getAttribute( "class" );

		try
		{
			// NOTE: iopClass class here is usually PhantomPlugin class,
			// called method then creates plugin object and returns its 
			// ImageOperation object which holds all persistent data.
			ImageOperation iop = IOPLibrary.getNewInstance( iopClass );
			
			currentIop = iop;
			//--- iop attributes
			String name = e.getAttribute("name" );
			if( !name.equals("") ) 
				iop.setName( name );
			iop.blendMode.set( getInt( e, "blendmode" ));
			iop.setOnOffState( getBoolean( e, "on" ) );

			try //added later might fails, with earlier project files
			{
				int bgtype = getInt( e, "bgtype" );
				iop.backgroundType.set( bgtype );
			}
			catch( Exception ex ){}

			int maxLength = getInt( e, "maxlength" );
			int beginFrame = getInt( e, "beginframe" );
			int clipStartFrame = getInt( e, "clipstartframe" );
			int clipEndFrame = getInt( e, "clipendframe" );
			iop.loadClipValues( maxLength, beginFrame, clipStartFrame, clipEndFrame );

			iop.setLooping(  getInt( e, "looping" ) );
			if( e.getAttribute( "parenttype" ).length() != 0 )
				iop.setParentMover( getInt( e, "parenttype" ), getInt( e, "parentnodeid" ), null );//parent iop set later
			String locked = e.getAttribute( "locked" );
			if( locked != null )
				iop.setLocked( getBoolean( e, "locked" )); 
		
			//--- switches
			boolean hasSwitches = getBoolean( e, "hasswitches" );
			if( hasSwitches )
			{
				SwitchData switches = iop.getSwitches();
				switches.motionBlur = getBoolean( e, "motionblur");
				switches.fineEdges = getBoolean( e, "fineedges" );
				switches.interpolation = getInt( e, "interpolation" );
			}
			//--- file sources
			int fid = getInt( e, "filesource" );
			if( fid != -1 ) 
				iop.registerFileSource( projectObj.getFileSource( fid ) );

			//--- params
			NodeList plist = e.getElementsByTagName( ParamXML.ELEMENT_NAME );
			for( int i = 0; i < plist.getLength(); i++ )
			{
				Element p = (Element) plist.item( i );

				//--- Params that are children of params are not handled here
				//--- as they are part of state of params.
				//--- So parent node has to be iop element
				//--- see ValueXML.readAnimValVecValue
				Node parent =  p.getParentNode();
				if( !parent.getNodeName().equals( ELEMENT_NAME ) && !parent.getNodeName().equals( FILTER_STACK_IOP_ELEMENT ))
					continue;

				//--- Don't load filter stack params for top level IOPs
				if( isFilterStackIop == false  && !parent.getNodeName().equals( ELEMENT_NAME ))
					continue;

				//--- Create param
				Param pObj = iop.getParam( p.getAttribute( ParamXML.ID_ATTR ) );
				ParamXML.readParamValue( p, pObj );
				System.out.println("down");
			}
			//--- filter stack
			NodeList felist = e.getElementsByTagName( FILTER_STACK_ELEMENT );
			//--- NOTE: Filter stack filters have their own, empty filter stacks
			if( felist.getLength() > 0 )
			{
				Element fsE = (Element) felist.item( 0 );
				NodeList filters = fsE.getElementsByTagName( FILTER_STACK_IOP_ELEMENT );
				Vector<ImageOperation> filterStack = new Vector<ImageOperation>();
				for( int i = 0; i < filters.getLength(); i++ )
				{
					Element iopE = (Element) filters.item( i );
					ImageOperation filter = ImageOperationXML.getObject( iopE, projectObj, true );
					filter.setFilterStackIOP( true );
					filterStack.add( filter );
				}
				iop.setFilterStack( filterStack );
			}

			//--- GUI editor components are created before the values they display are loaded so
			//--- they need to have their display state updated.
			//--- Only UndoListeners need to be updated, FrameChangeListeners
			//--- are updated on display because they can be displayed at any frame.
			ParamEditPanel panel = iop.getEditPanelInstance();
			panel.undoDone();

			return iop;
		}
		catch( Exception x )// return object with default values for load fail so only single object lost
		{
			System.out.println("load fail for iop of class " + iopClass );
			return IOPLibrary.getNewInstance( iopClass );
		}
	}

	public static Element getElement( ImageOperation iop )
	{
		 return getElement( iop, ELEMENT_NAME );
	}

	public static Element getElement( ImageOperation iop, String elemName )
	{
		Element e = doc.createElement( elemName );
		e.setAttribute( "name", iop.getName() );

		//--- if not plugin write iop class, else plugin class
		if( iop.getPlugin() == null)
			e.setAttribute( "class", iop.getClass().getName() );
		else
			e.setAttribute( "class", iop.getPlugin().getClass().getName() );
		//--- Instance variables
		e.setAttribute( "blendmode", intStr( iop.getBlendMode() ) );
		e.setAttribute( "bgtype", intStr( iop.backgroundType.get() ) );
		e.setAttribute( "on", booleanStr( iop.isOn() ) );
		e.setAttribute( "beginframe", intStr( iop.getBeginFrame()) );
		e.setAttribute( "maxlength", intStr( iop.getMaxLength() ) );
		e.setAttribute( "clipstartframe", intStr( iop.getClipStartFrame()) );
		e.setAttribute( "clipendframe", intStr( iop.getClipEndFrame() ) );
		e.setAttribute( "parenttype", intStr( iop.parentMoverType ) );
		e.setAttribute( "parentnodeid", intStr( iop.parentNodeID ) );
		e.setAttribute( "looping", intStr( iop.getLooping() ) );
		e.setAttribute( "locked", booleanStr( iop.getLocked() ));
		//--- switches
		boolean hasSwitches = ( iop.switches != null );
		e.setAttribute( "hasswitches",  booleanStr( hasSwitches ) );
		if( hasSwitches )
		{
			e.setAttribute( "motionblur", booleanStr( iop.getMotionBlur() ));
			e.setAttribute( "fineedges", booleanStr( iop.getFineEdges() ));
			e.setAttribute( "interpolation", intStr( iop.getInterpolation() ));
		}

		//--- file source
		int fid = -1;
		if( iop.getFileSource() != null ) 
			fid = iop.getFileSource().getID();
		e.setAttribute( "filesource", intStr( fid ) );
		//--- params
		Vector<Param> params = iop.getParameters();
		for( Object o : params )
		{
			Element pe = ParamXML.getElement( (Param) o );
			e.appendChild( pe );
		}
		//--- filter stack
		Element fse = doc.createElement( FILTER_STACK_ELEMENT );
		for( int i = 0; i < iop.getFilterStack().size(); i++ )
		{
			Element iopE = ImageOperationXML.getElement( iop.getFilterStack().elementAt( i ), FILTER_STACK_IOP_ELEMENT );
			fse.appendChild( iopE );
		}
		e.appendChild( fse );

		return e;
	}

}//end class
