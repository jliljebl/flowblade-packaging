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

import org.w3c.dom.Element;

import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.AnimatedValueVectorParam;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.CRCurveParam;
import animator.phantom.renderer.param.ColorParam;
import animator.phantom.renderer.param.FloatParam;
import animator.phantom.renderer.param.IOPParam;
import animator.phantom.renderer.param.IOPVectorParam;
import animator.phantom.renderer.param.IntegerAnimatedValue;
import animator.phantom.renderer.param.IntegerParam;
import animator.phantom.renderer.param.IntegerVectorParam;
import animator.phantom.renderer.param.Param;

public class ParamXML extends AbstractXML
{
	public static String ELEMENT_NAME = "p";
	public static final String TYPE_ATTR = "t";
	public static final String NAME_ATTR = "name";
	public static final String ID_ATTR = "id";

	public static final String ANIMATED_VALUE;
	public static final String BOOLEAN_VALUE;
	public static final String INTEGER_VALUE;
	public static final String COLOR_VALUE;
	public static final String IOP_PARAM;
	public static final String IOP_VECTOR_PARAM;
	public static final String INTEGER_VECTOR_PARAM;
	public static final String CRCURVE_PARAM;
	public static final String ANIM_VAL_VEC_PARAM;
	public static final String ANIMATED_INTEGER_VALUE;
	public static final String FLOAT_VALUE;

	static
	{
		ANIMATED_VALUE = paramType( (new AnimatedValue()).getClass().getName() );
		BOOLEAN_VALUE = paramType( (new BooleanParam()).getClass().getName() );
		INTEGER_VALUE = paramType( (new IntegerParam()).getClass().getName() );
		COLOR_VALUE = paramType( (new ColorParam()).getClass().getName() );
		IOP_PARAM = paramType( (new IOPParam()).getClass().getName() );
		IOP_VECTOR_PARAM = paramType( (new IOPVectorParam()).getClass().getName() );
		INTEGER_VECTOR_PARAM = paramType( (new IntegerVectorParam()).getClass().getName() );
		CRCURVE_PARAM = paramType( new CRCurveParam().getClass().getName() );
 		ANIM_VAL_VEC_PARAM = paramType( new AnimatedValueVectorParam().getClass().getName() );
		ANIMATED_INTEGER_VALUE = paramType( new IntegerAnimatedValue().getClass().getName() );
		FLOAT_VALUE = paramType( new FloatParam().getClass().getName() );
	}

	public static void readParamValue( Element e, Param p )
	{
		String type = e.getAttribute( TYPE_ATTR );
		String name = e.getAttribute( NAME_ATTR );
		p.setParamName( name );
		if( type.equals( BOOLEAN_VALUE ) ) ValueXML.readBooleanParam( e, p );
		else if( type.equals( INTEGER_VALUE ) ) ValueXML.readIntegerValue( e, p );
		else if( type.equals( COLOR_VALUE ) ) ValueXML.readColorParam( e, p );
		else if( type.equals( ANIMATED_VALUE ) ) ValueXML.readAnimatedValue( e, p );
		else if( type.equals( IOP_PARAM ) ) ValueXML.readIOPParamValue( e, p );
		else if( type.equals( IOP_VECTOR_PARAM ) ) ValueXML.readIOPVectorValue( e, p );
		else if( type.equals( INTEGER_VECTOR_PARAM ) ) ValueXML.readIntVectorValue( e, p );
		else if( type.equals( CRCURVE_PARAM ) ) ValueXML.readCRCurveValue( e, p );
		else if( type.equals( ANIM_VAL_VEC_PARAM ) ) ValueXML.readAnimValVecValue( e, p );
		else if( type.equals( ANIMATED_INTEGER_VALUE ) ) ValueXML.readAnimatedValue( e, p );
		else if( type.equals( FLOAT_VALUE ) ) ValueXML.readFloatParam( e, p );
	}

	public static Element getElement( Param p )
	{
		Element e = doc.createElement( ELEMENT_NAME );
 		e.setAttribute( ID_ATTR, p.getID() );
		e.setAttribute( NAME_ATTR, p.getParamName() );
		String type = paramType( p.getClass().getName() );
		e.setAttribute( TYPE_ATTR, type );
		//--- Get value atrrs/elems
		if( type.equals( BOOLEAN_VALUE ) ) ValueXML.writeBooleanParam( e, p );
		else if( type.equals( INTEGER_VALUE ) ) ValueXML.writeIntegerValue( e, p );
		else if( type.equals( COLOR_VALUE ) ) ValueXML.writeColorParam( e, p );
		else if( type.equals( ANIMATED_VALUE ) ) ValueXML.writeAnimatedValue( e, p );
		else if( type.equals( IOP_PARAM ) ) ValueXML.writeIOPParamValue( e, p );
		else if( type.equals( IOP_VECTOR_PARAM ) ) ValueXML.writeIOPVectorValue( e, p );
		else if( type.equals( INTEGER_VECTOR_PARAM ) ) ValueXML.writeIntVectorValue( e, p );
		else if( type.equals( CRCURVE_PARAM ) ) ValueXML.writeCRCurveValue( e, p );
		else if( type.equals( ANIM_VAL_VEC_PARAM ) ) ValueXML.writeAnimValVecValue( e, p );
		else if( type.equals( ANIMATED_INTEGER_VALUE ) ) ValueXML.writeAnimatedValue( e, p );
		else if( type.equals( FLOAT_VALUE ) ) ValueXML.writeFloatParam( e, p );
		return e;
	}

	private static String paramType( String className )
	{
		int lastDot = className.lastIndexOf( "." );
		return className.substring(lastDot + 1);
	}

}//end class