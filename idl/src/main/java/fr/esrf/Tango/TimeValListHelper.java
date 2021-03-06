package fr.esrf.Tango;

/**
 * Generated from IDL alias "TimeValList".
 *
 * @author JacORB IDL compiler V 3.5
 * @version generated at Sep 5, 2014 10:37:19 AM
 */

public abstract class TimeValListHelper
{
	private volatile static org.omg.CORBA.TypeCode _type;

	public static void insert (org.omg.CORBA.Any any, fr.esrf.Tango.TimeVal[] s)
	{
		any.type (type ());
		write (any.create_output_stream (), s);
	}

	public static fr.esrf.Tango.TimeVal[] extract (final org.omg.CORBA.Any any)
	{
		if ( any.type().kind() == org.omg.CORBA.TCKind.tk_null)
		{
			throw new org.omg.CORBA.BAD_OPERATION ("Can't extract from Any with null type.");
		}
		return read (any.create_input_stream ());
	}

	public static org.omg.CORBA.TypeCode type ()
	{
		if (_type == null)
		{
			synchronized(TimeValListHelper.class)
			{
				if (_type == null)
				{
					_type = org.omg.CORBA.ORB.init().create_alias_tc(fr.esrf.Tango.TimeValListHelper.id(), "TimeValList",org.omg.CORBA.ORB.init().create_sequence_tc(0, org.omg.CORBA.ORB.init().create_struct_tc(fr.esrf.Tango.TimeValHelper.id(),"TimeVal",new org.omg.CORBA.StructMember[]{new org.omg.CORBA.StructMember("tv_sec", org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.from_int(3)), null),new org.omg.CORBA.StructMember("tv_usec", org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.from_int(3)), null),new org.omg.CORBA.StructMember("tv_nsec", org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.from_int(3)), null)})));
				}
			}
		}
		return _type;
	}

	public static String id()
	{
		return "IDL:Tango/TimeValList:1.0";
	}
	public static fr.esrf.Tango.TimeVal[] read (final org.omg.CORBA.portable.InputStream _in)
	{
		fr.esrf.Tango.TimeVal[] _result;
		int _l_result29 = _in.read_long();
		try
		{
			 int x = _in.available();
			 if ( x > 0 && _l_result29 > x )
				{
					throw new org.omg.CORBA.MARSHAL("Sequence length too large. Only " + x + " available and trying to assign " + _l_result29);
				}
		}
		catch (java.io.IOException e)
		{
		}
		_result = new fr.esrf.Tango.TimeVal[_l_result29];
		for (int i=0;i<_result.length;i++)
		{
			_result[i]=fr.esrf.Tango.TimeValHelper.read(_in);
		}

		return _result;
	}

	public static void write (final org.omg.CORBA.portable.OutputStream _out, fr.esrf.Tango.TimeVal[] _s)
	{
		
		_out.write_long(_s.length);
		for (int i=0; i<_s.length;i++)
		{
			fr.esrf.Tango.TimeValHelper.write(_out,_s[i]);
		}

	}
}
