package fr.esrf.Tango;

/**
 * Generated from IDL struct "ArchiveEventProp".
 *
 * @author JacORB IDL compiler V 3.5
 * @version generated at Sep 5, 2014 10:37:19 AM
 */

public final class ArchiveEventPropHolder
	implements org.omg.CORBA.portable.Streamable
{
	public fr.esrf.Tango.ArchiveEventProp value;

	public ArchiveEventPropHolder ()
	{
	}
	public ArchiveEventPropHolder(final fr.esrf.Tango.ArchiveEventProp initial)
	{
		value = initial;
	}
	public org.omg.CORBA.TypeCode _type ()
	{
		return fr.esrf.Tango.ArchiveEventPropHelper.type ();
	}
	public void _read(final org.omg.CORBA.portable.InputStream _in)
	{
		value = fr.esrf.Tango.ArchiveEventPropHelper.read(_in);
	}
	public void _write(final org.omg.CORBA.portable.OutputStream _out)
	{
		fr.esrf.Tango.ArchiveEventPropHelper.write(_out, value);
	}
}
