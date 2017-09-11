package animator.phantom.xml;

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

import java.awt.Color;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import animator.phantom.bezier.CurvePoint;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.AnimatedValueVectorParam;
import animator.phantom.renderer.param.AnimationKeyFrame;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.CRCurveParam;
import animator.phantom.renderer.param.ColorParam;
import animator.phantom.renderer.param.FloatParam;
import animator.phantom.renderer.param.IOPParam;
import animator.phantom.renderer.param.IOPVectorParam;
import animator.phantom.renderer.param.IntegerParam;
import animator.phantom.renderer.param.IntegerVectorParam;
import animator.phantom.renderer.param.Param;

//--- This class reads and writes values of Params to XML Elements.
public class ValueXML extends AbstractXML
{
	public static final String KF_ELEMENT_NAME = "kf";
	public static final String VALUE_ATTR = "value";
	public static final String NODE_ID_ELEMENT_NAME = "iopvecnodeid";
	public static final String CRPOINT_ELEMENT_NAME = "crpoint";

	//------------------------------------------------------------ READ
	public static void readBooleanParam( Element e, Param p )
	{
		((BooleanParam) p).set( getBoolean( e, VALUE_ATTR ));
	}
	public static void readIntegerValue( Element e, Param p )
	{
		((IntegerParam) p).set( getInt( e, VALUE_ATTR ));
	}
	public static void readFloatParam( Element e, Param p )
	{
		((FloatParam) p).set( getFloat( e, VALUE_ATTR ));
	}
	public static void readColorParam( Element e, Param p )
	{
		ColorParam cv = (ColorParam)p;
		int r = getInt( e, "red" );
		int g = getInt( e, "green" );
		int b = getInt( e, "blue" );
		Color col = new Color( r, g, b );
		cv.set( col );
	}
	public static void readAnimatedValue( Element e, Param p )
	{
		AnimatedValue av = (AnimatedValue)p;
		av.setLocked( getBoolean( e, "l" ) );
		av.setRestrictedValueRange( getBoolean( e, "r" ) );
		av.setStepped( getBoolean( e, "stepped" ) );
		av.setMinValue( getFloat( e, "min" ) );
		av.setMaxValue( getFloat( e, "max" ) );
		//--- keyframes
		NodeList kflist = e.getElementsByTagName( KF_ELEMENT_NAME );
		Vector <AnimationKeyFrame> kframes = new Vector <AnimationKeyFrame>();
		for( int i = 0; i < kflist.getLength(); i++ )
		{
			Element kfE = (Element) kflist.item( i );
			AnimationKeyFrame kf = getKF( kfE );
			kframes.add( kf );
		}
		av.setKeyFrames( kframes );
	}
	public static void readIOPParamValue( Element e, Param p )
	{
		IOPParam iopP = (IOPParam) p;
 		iopP.setNodeID( getInt( e, "nodeid" ) );
	}
	public static void readIOPVectorValue( Element e, Param p )
	{
		IOPVectorParam iopVP = (IOPVectorParam) p;
 		Vector<Integer>  iopNodeIDs = new Vector<Integer>();
		NodeList idlist = e.getElementsByTagName( NODE_ID_ELEMENT_NAME );
		for( int i = 0; i < idlist.getLength(); i++ )
		{
			Element idE = (Element) idlist.item( i );
			iopNodeIDs.add( new Integer( getInt( idE, "id" ) ));
		}
		iopVP.setNodeIDs( iopNodeIDs );
	}

	public static void readIntVectorValue( Element e, Param p )
	{
		IntegerVectorParam iVec = (IntegerVectorParam) p;
		Vector<Integer> valVec = new Vector<Integer>(); 
		String val = e.getAttribute("arraydata");
		StringTokenizer tok = new StringTokenizer(val, ";" );
		String token;
		while (tok.hasMoreTokens()) 
		{
			token = tok.nextToken();
			valVec.add( new Integer( token ));
		}
		System.out.println( "valVec.size()" +  valVec.size() );
		iVec.set( valVec );
	}

	public static void readCRCurveValue( Element e, Param p )
	{
		CRCurveParam cr = (CRCurveParam) p;
		cr.curve.clear();//for undo, we only add points here, in undo might want to remove.
		NodeList pointlist = e.getElementsByTagName( CRPOINT_ELEMENT_NAME );

		for( int i = 0; i < pointlist.getLength(); i++ )
		{
			Element pointE = (Element) pointlist.item( i );
			CurvePoint cp = getCurvePoint( pointE );
			cr.curve.addCurvePointOnLoad( cp );
		}
	}
	public static void readAnimValVecValue( Element e, Param p )
	{
		AnimatedValueVectorParam avvp = (AnimatedValueVectorParam) p;
		NodeList avValsNodes = e.getElementsByTagName( ParamXML.ELEMENT_NAME );
		Vector<AnimatedValue> avs = new Vector<AnimatedValue>();
		for( int i = 0; i < avValsNodes.getLength(); i++ )
		{
			Element avElem = (Element) avValsNodes.item( i );
			AnimatedValue av = new AnimatedValue( ImageOperationXML.currentIop );
			ParamXML.readParamValue( avElem, av );
			avs.add( av );
		}
		avvp.set( avs );
	}
	
	//------------------------------------------------------------- WRITE
	public static void writeBooleanParam(Element e, Param p )
	{
		e.setAttribute( VALUE_ATTR, booleanStr( ((BooleanParam)p).get() ) );
	}
	public static void writeIntegerValue(Element e, Param p )
	{
		e.setAttribute( VALUE_ATTR, intStr( ((IntegerParam)p).get() ) );
	}
	public static void writeFloatParam(Element e, Param p )
	{
		e.setAttribute( VALUE_ATTR, floatStr( ((FloatParam)p).get() ) );
	}
	public static void writeColorParam(Element e, Param p )
	{
		ColorParam cv = (ColorParam)p;
		Color c = cv.get();
		e.setAttribute( "red", intStr( c.getRed() ) );
		e.setAttribute( "green", intStr( c.getGreen() ) );
		e.setAttribute( "blue", intStr( c.getBlue() ) );
	}
	public static void writeAnimatedValue(Element e, Param p )
	{
		AnimatedValue av = (AnimatedValue)p;
		e.setAttribute( "l", booleanStr( av.isLocked() ) );
		e.setAttribute( "r", booleanStr( av.hasRestrictedValueRange() ) );
		e.setAttribute( "stepped",  booleanStr( av.getStepped() ) );
		e.setAttribute( "min", floatStr( av.getMinValue() ) );
		e.setAttribute( "max", floatStr( av.getMaxValue() ) );
		//--- keyframes
		Vector <AnimationKeyFrame> keyFrames = av.getKeyFrames();
		for( AnimationKeyFrame kf : keyFrames )
			e.appendChild( getKFElement( kf ) );
	}
	public static void writeIOPParamValue( Element e, Param p )
	{
		IOPParam iopP = (IOPParam) p;
		e.setAttribute( "nodeid", intStr( iopP.getNodeID() ) );
	}
	public static void writeIOPVectorValue( Element e, Param p )
	{
		IOPVectorParam iopVP = (IOPVectorParam) p;
 		Vector<Integer>  iopNodeIDs = iopVP.getNodeIDs();
		for( Integer id : iopNodeIDs )
			e.appendChild( getNodeIdElem( id.intValue() ));
	}
	public static void writeIntVectorValue( Element e, Param p )
	{
		IntegerVectorParam intVec = (IntegerVectorParam) p;
		StringBuffer buf = new StringBuffer();
		for( int i = 0; i < intVec.get().size(); i++ )
		{
			buf.append( Integer.toString( intVec.get().elementAt( i ) ));
			if( i != intVec.get().size() - 1 ) buf.append( ";" );
		}
		e.setAttribute( "arraydata", buf.toString() );
	}
	
	public static void writeCRCurveValue( Element e, Param p )
	{
		CRCurveParam cr = (CRCurveParam) p;
		Vector<CurvePoint> points = cr.curve.getCurvePoints();
		for( CurvePoint cp : points )
			e.appendChild( getCRPointElem( cp ) );
	}
	public static void writeAnimValVecValue( Element e, Param p )
	{
		AnimatedValueVectorParam avvp = (AnimatedValueVectorParam) p;
		Vector<AnimatedValue> animVals = avvp.get();
		for( int i = 0; i < animVals.size(); i++ )
		{
			AnimatedValue av = animVals.elementAt( i );
			av.setParamName("child"+ intStr(i) );//set paramname to something
			Element avElement = ParamXML.getElement( av );
			e.appendChild( avElement );
		}
	}

	//------------------------------------------------------------ HELP METHODS
	//--- keyframe
	private static Element getKFElement( AnimationKeyFrame kf )
	{
		Element e = doc.createElement( KF_ELEMENT_NAME );
		e.setAttribute( "v", floatStr( kf.getValue() ) );
		e.setAttribute( "f", intStr( kf.getFrame() ) );
		e.setAttribute( "l", intStr( kf.getLeadingInterpolation() ) );
		e.setAttribute( "t", intStr( kf.getTrailingInterpolation() ) );
		e.setAttribute( "lt", floatStr( kf.getLeadingTension() ) );
		e.setAttribute( "tt", floatStr( kf.getTrailingTension() ) );
		return e;
	}
	private static AnimationKeyFrame getKF( Element e )
	{
		AnimationKeyFrame kf = new AnimationKeyFrame();
		kf.setValue( getFloat( e, "v" ) );
		kf.setFrame( getInt( e, "f" ) );
		kf.setLeadingInterpolation( getInt( e, "l" ) );
		kf.setTrailingInterpolation( getInt( e, "t" ));
		kf.setLeadingTension( getFloat( e, "lt" ) );
		kf.setTrailingTension( getFloat( e, "tt" ) );
		return kf;
	}
	//--- node ids
	private static Element getNodeIdElem( int id )
	{
		Element e = doc.createElement( NODE_ID_ELEMENT_NAME );
		e.setAttribute( "id", intStr( id ) );
		return e;
	}
	//--- Curve point
	private static Element getCRPointElem( CurvePoint cp )
	{
		Element e = doc.createElement( CRPOINT_ELEMENT_NAME );
		e.setAttribute( "x", intStr( cp.x ) );
		e.setAttribute( "y", intStr( cp.y ) );
		return e;
	}
	private static CurvePoint getCurvePoint( Element e )
	{
		CurvePoint cp = new CurvePoint();
		cp.x = getInt( e, "x" );
		cp.y = getInt( e, "y" );
		return cp;
	}
}//end class