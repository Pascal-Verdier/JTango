package fr.esrf.Tango;

/**
 * Generated from IDL struct "AttributeValue".
 *
 * @author JacORB IDL compiler V 3.5
 * @version generated at Sep 5, 2014 10:37:19 AM
 */

public final class AttributeValueHolder
	implements org.omg.CORBA.portable.Streamable
{
	public fr.esrf.Tango.AttributeValue value;

	public AttributeValueHolder ()
	{
	}
	public AttributeValueHolder(final fr.esrf.Tango.AttributeValue initial)
	{
		value = initial;
	}
	public org.omg.CORBA.TypeCode _type ()
	{
		return fr.esrf.Tango.AttributeValueHelper.type ();
	}
	public void _read(final org.omg.CORBA.portable.InputStream _in)
	{
		value = fr.esrf.Tango.AttributeValueHelper.read(_in);
	}
	public void _write(final org.omg.CORBA.portable.OutputStream _out)
	{
		fr.esrf.Tango.AttributeValueHelper.write(_out, value);
	}
}
