package fr.esrf.Tango;

/**
 * Generated from IDL alias "DevAttrHistoryList_3".
 *
 * @author JacORB IDL compiler V 3.5
 * @version generated at Sep 5, 2014 10:37:19 AM
 */

public final class DevAttrHistoryList_3Holder
	implements org.omg.CORBA.portable.Streamable
{
	public fr.esrf.Tango.DevAttrHistory_3[] value;

	public DevAttrHistoryList_3Holder ()
	{
	}
	public DevAttrHistoryList_3Holder (final fr.esrf.Tango.DevAttrHistory_3[] initial)
	{
		value = initial;
	}
	public org.omg.CORBA.TypeCode _type ()
	{
		return DevAttrHistoryList_3Helper.type ();
	}
	public void _read (final org.omg.CORBA.portable.InputStream in)
	{
		value = DevAttrHistoryList_3Helper.read (in);
	}
	public void _write (final org.omg.CORBA.portable.OutputStream out)
	{
		DevAttrHistoryList_3Helper.write (out,value);
	}
}
