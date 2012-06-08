//+============================================================================
//
// file :               MultiClasAttribute.java
//
// description :        Java source code for the MultiClassAttribute
//			class. This class handles all the Attribute
//			with properties defined at the class level
//
// project :            TANGO
//
// $Author: :          E.Taurel
//
// Copyright (C) :      2004,2005,2006,2007,2008,2009
//						European Synchrotron Radiation Facility
//                      BP 220, Grenoble 38043
//                      FRANCE
//
// This file is part of Tango.
//
// Tango is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Tango is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License
// along with Tango.  If not, see <http://www.gnu.org/licenses/>.
//
// $Revision$
//
// $Log$
// Revision 1.5  2009/01/16 12:37:13  pascal_verdier
// IntelliJIdea warnings removed.
//
// Revision 1.4  2008/10/10 11:30:46  pascal_verdier
// Headers changed for LGPL conformity.
//
// Revision 1.3  2008/09/19 09:02:35  pascal_verdier
// Remove trace
//
// Revision 1.2  2008/09/19 07:51:45  pascal_verdier
// Database.get_device_attribute_property() replace command_inout("DbGetDeviceAttributeProperty") for new db command.
//
// Revision 1.1  2007/08/23 08:33:25  ounsy
// updated change from api/java
//
// Revision 3.6  2005/12/02 09:55:46  pascal_verdier
// java import have been optimized.
//
// Revision 3.5  2004/03/12 14:07:57  pascal_verdier
// Use JacORB-2.1
//
// Revision 2.0  2003/01/09 16:02:58  taurel
// - Update release number before using SourceForge
//
// Revision 1.1.1.1  2003/01/09 15:54:39  taurel
// Imported sources into CVS before using SourceForge
//
// Revision 1.6  2001/10/10 08:11:25  taurel
// See Tango WEB pages for list of changes
//
// Revision 1.5  2001/07/04 15:06:38  taurel
// Many changes due to new release
//
// Revision 1.2  2001/05/04 12:03:22  taurel
// Fix bug in the Util.get_device_by_name() method
//
// Revision 1.1.1.1  2001/04/04 08:23:54  taurel
// Imported sources
//
// Revision 1.3  2000/04/13 08:23:01  taurel
// Added attribute support
//
//
// copyleft :           European Synchrotron Radiation Facility
//                      BP 220, Grenoble 38043
//                      FRANCE
//
//-============================================================================

package fr.esrf.TangoDs;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.ApiUtil;
import fr.esrf.TangoApi.DbAttribute;

import java.util.Vector;
 
class MultiClassAttribute implements TangoConst
{

	protected Vector		attr_list;
	
//+-------------------------------------------------------------------------
//
// method : 		MultiClassAttribute 
// 
// description : 	constructor for the MultiClassAttribute class
//
//--------------------------------------------------------------------------
 
	public MultiClassAttribute()
	{
		attr_list = new Vector();
	}

//+-------------------------------------------------------------------------
//
// method : 		MultiClassAttribute.init_class_attribute
// 
// description : 	Ask the database for properties defined at class
//			level and build the ClassAttribute object for
//			each attribute with defined properties
//
// argument : in : 	- class_name : The device class name
//
//--------------------------------------------------------------------------

	void init_class_attribute(String class_name,int base) throws DevFailed
	{
		Util.out4.println("Entering MultiClassAttribute.init_class_attribute");
		
		int nb_attr;
		if (base == 0)
			nb_attr = attr_list.size();
		else
			nb_attr = 1;
		
		if ((nb_attr != 0) && (Util._UseDb == true))
		{
			// Get class attribute properties
			String[]	attnames = new String[nb_attr];
			for (int i=0 ; i<nb_attr ; i++)
				attnames[i] = ((Attr)(attr_list.elementAt(i + base))).get_name();
			// Get class attribute(s) properties from database
			DbAttribute[]	db_attr = new DbAttribute[0];
			if (Util._UseDb)
				db_attr = ApiUtil.get_db_obj().get_class_attribute_property(class_name, attnames);

			// Sort property for each attribute and create a ClassAttribute object for each
			for (int i=0; i<attnames.length ; i++)
			{
				Attr	attr = (Attr)attr_list.get(i + base);
				Vector prop_list = new Vector();
				if (Util._UseDb)
				{
					String[]	propnames = db_attr[i].get_property_list();
					for (int p=0 ; p<propnames.length ; p++)
					{
						AttrProperty	property =
							new AttrProperty(propnames[p], db_attr[i].get_value(p));
						prop_list.add(property);
						//System.out.println(property);
					}
				}
				attr.set_class_properties(prop_list);
			}

		}
		
		for (int i=0 ; i<nb_attr ; i++)
			Util.out4.println(attr_list.elementAt(i + base));
			
		Util.out4.println("Leaving MultiClassAttribute.init_class_attribute");
	}

//+-------------------------------------------------------------------------
//
// method : 		MultiClassAttribute.get_attr
// 
// description : 	Get the ClassAttribute object for the attribute with
//			name passed as parameter
//
// in :			attr_name : The attribute name
//
// This method returns a reference to the ClassAttribute object or throw
// an exceptionif the attribute is not found
//
//--------------------------------------------------------------------------


	Attr get_attr(String attr_name) throws DevFailed
	{

//
// Search for the wanted attribute in the attr_list vector from its name
//

		int i;
		int nb_attr = attr_list.size();
		
		for (i = 0;i < nb_attr;i++)
		{
			if (((Attr)(attr_list.elementAt(i))).get_name().equals(attr_name) == true)
				break;
		}

		if (i == nb_attr)
		{
			StringBuffer o = new StringBuffer("Attribute ");
			o.append(attr_name);
			o.append(" not found in class attribute(s)");
			
			Except.throw_exception("API_AttrWrongDefined",
					       o.toString(),
					       "ClassAttribute.get_attr()");
		}
		
		return ((Attr)(attr_list.elementAt(i)));
	}
		
//+-------------------------------------------------------------------------
//
// Methods to retrieve/set some data members from outside the class and all
// its inherited classes
//
//--------------------------------------------------------------------------

	Vector get_attr_list()
	{
		return attr_list;
	}
}
