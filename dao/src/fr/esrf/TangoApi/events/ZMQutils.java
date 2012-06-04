//+======================================================================
// $Source$
//
// Project:   Tango
//
// Description:  java source code for the TANGO client/server API.
//
// $Author: pascal_verdier $
//
// Copyright (C) :      2004,2005,2006,2007,2008,2009,2010,2011,2012
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
// $Revision:  $
//
//-======================================================================


package fr.esrf.TangoApi.events;


/** 
 *	This class is a set of ZMQ low level utilities
 *
 * @author  verdier
 */

import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import fr.esrf.TangoDs.Except;
import fr.esrf.TangoDs.TangoConst;
import org.jacorb.orb.CDRInputStream;
import org.zeromq.ZMQ;

import java.util.ArrayList;


public class  ZMQutils {

    //  ZMQ commands
    public static final int   ZMQ_END                    = 0;
    public static final int   ZMQ_CONNECT_HEARTBEAT      = 1;
    public static final int   ZMQ_DISCONNECT_HEARTBEAT   = 2;
    public static final int   ZMQ_CONNECT_EVENT          = 3;
    public static final int   ZMQ_DISCONNECT_EVENT       = 4;
    //public static final int   ZMQ_CONNECT_MCAST_EVENT    = 5;
    private static final String[]   commandNames = {
            "ZMQ_END",
            "ZMQ_CONNECT_HEARTBEAT",
            "ZMQ_DISCONNECT_HEARTBEAT",
            "ZMQ_CONNECT_EVENT",
            "ZMQ_DISCONNECT_EVENT",
            "ZMQ_CONNECT_MCAST_EVENT",
    };

    private static ZMQ.Context     context = ZMQ.context(1);
	private static ZMQutils instance = null;
	//===============================================================
	//===============================================================
    static ZMQutils getInstance() {
        if (instance==null) {
            instance = new ZMQutils();
        }
        return instance;
    }
	//===============================================================
	//===============================================================
	private ZMQutils() {
    }
	//===============================================================
    /**
     *
     * @return the ZMQ context object.
     */
	//===============================================================
    static ZMQ.Context getContext() {
        return context;
    }
	//===============================================================
	//===============================================================


    //  Private methods
	//===============================================================
    /**
     * Build the buffer to be send
     *
     * @param command       command name
     * @param stringList    device, attribute, event....
     * @return the buffer built
     */
	//===============================================================
    private static byte[] buildTheBuffer(int command, ArrayList<String> stringList) {
        return buildTheBuffer(command, stringList, null);
    }
	//===============================================================
    /**
     * Build the buffer to be send
     *
     * @param command       command name
     * @param stringList    device, attribute, event....
     * @param intList       Sub HWM, Rate, IVL
     * @return the buffer built
     */
	//===============================================================
    private static byte[] buildTheBuffer(int command, ArrayList<String> stringList, ArrayList<Integer> intList) {

        //  Check size to allocate
        int size = 1; //    for Command
        if (stringList.size()>0) {
            for (String s : stringList) {
                size += s.length()+1;   //  +1 for '\0' separator
            }
            if (intList!=null && intList.size()>0) {
                size += intList.size()*4;
            }
        }
        byte[]  buffer = new byte[size];

        //  Then fill it with command first
        int idx = 0;
        buffer[idx++] = (byte)command;
        for (String s : stringList) {
            //  And with string bytes
            byte[]  bytes = s.getBytes();
            for (byte b : bytes) {
                buffer[idx++] = b;
            }
            //  Separated by '\0' as C string
            buffer[idx++] = 0;
        }

        //  Then add integers if any
        if (intList!=null && intList.size()>0) {
            for (int value : intList) {
                byte[] bytes = codeInteger(value);
                for (byte b : bytes)
                    buffer[idx++] = b;
            }
        }
        //dump(buffer);
        return buffer;
    }
	//===============================================================
    /**
     *
     * @param bytes  input byte buffer
     * @param start  index to start string construction
     * @return  a string built with byte buffer
     * @throws DevFailed in case of start index is equal or more than bytes.length
     */
	//===============================================================
    private static String getString(byte[] bytes, int start) throws DevFailed {
        //  Get '\0' size (coded as C string) if any
        int end = -1;
        for (int i=start ; i<bytes.length && end<0 ; i++)
            if (bytes[i]==0)
                end = i;
        if (end<0)  //  Not found
            end = bytes.length-1;
        int length = end - start;
        if (length<=0)
            Except.throw_wrong_syntax_exception("API_BadSyntax",
                    "Bad syntax in control buffer (String not found)",
                    "ZMQutils.getString()");
        byte[]  b = new byte[length];

        System.arraycopy(bytes, start, b, 0, length);
        return new String(b);
    }
	//===============================================================
    /**
     * build a byte buffer from an integer
     * @param value the specifid integer value
     * @return the byte buffer built.
     */
	//===============================================================
    private static byte[] codeInteger(int value) {
        byte[] b = new byte[4];
        for (int i=0 ; i<4 ;i++) {
             b[i] = (byte) (value >> i*8);
        }
        return b;
     }
	//===============================================================
    /**
     * Code an integer from byte buffer
     * @param bytes input buffer
     * @return the coded integer.
     */
	//===============================================================
    private static int decodeInteger(byte[] bytes) {
        int value = 0;
        for (int i=0 ; i<4 ;i++) {
            int   x  = (bytes[i] << i*8) & (0xFF << i*8);
            value += x;
        }
        return value;
    }
	//===============================================================
    /**
     * Code an integer from byte buffer
     * @param buffer    input buffer
     * @param start     index to start integer construction
     * @return the coded integer.
     */
	//===============================================================
    private static int getInteger(byte[] buffer, int start) {
        //  Build a temporary byte array containing only the integer
        byte[]  bytes = new byte[4];
        System.arraycopy(buffer, start, bytes, 0, 4);
        return decodeInteger(bytes);
    }
	//===============================================================
	//===============================================================




	//  Public methods
	//===============================================================
    /**
     * Send a data buffer on control socket
     * @param buffer    the specified buffer
     * @throws DevFailed in case of internal communication problem.
     */
	//===============================================================
    static void sendToZmqControlSocket(byte[] buffer) throws DevFailed {
        ZMQ.Socket  controlSocket = context.socket(ZMQ.REQ);
        controlSocket.connect("inproc://control");
        controlSocket.send(buffer, 0);
        byte[]  resp = controlSocket.recv(0);
        controlSocket.close();
        if (resp.length>0) {
            Except.throw_exception("API_InternalCommunicationError",
                    new String(resp),
                    "sendToZmqControlSocket()");
        }
        ApiUtil.printTrace("---> Message sent");
    }
	//===============================================================
    /**
     *
     * @param deviceName    specified device name
     * @param attributeName specified attribute name
     * @param eventName     specified event name
     * @return the full attribute name with tango host and event name
     * @throws DevFailed in case of TANGO_HOST not defined
     */
	//===============================================================
    static String getFullAttributeName(String deviceName, String attributeName, String eventName) throws DevFailed {
        return ("tango://" + ApiUtil.get_db_obj().getFullTangoHost() +
                "/" + deviceName + "/" + attributeName + "."+ eventName).toLowerCase();
    }
	//===============================================================
    /**
     *
     * @param deviceName    specified device name
     * @return the full heartbeat name with tango host
     * @throws DevFailed in case of TANGO_HOST not defined
     */
	//===============================================================
    static String getFullHeartBeatName(String deviceName) throws DevFailed {
        return ("tango://" + ApiUtil.get_db_obj().getFullTangoHost() +
                "/" + deviceName + ".heartbeat");
    }
	//===============================================================
    /**
     * Request to control to disconnect event
     * @param deviceName    specified device
     * @param attributeName specified attribute
     * @param eventName     specified event
     * @throws DevFailed    in case of internal communication problem.
     */
	//===============================================================
    static void  disConnectEvent(String deviceName, String attributeName, String eventName) throws DevFailed{
        byte[]  buffer = getBufferToDisConnectEvent(deviceName, attributeName, eventName);
        sendToZmqControlSocket(buffer);
    }
	//===============================================================
    /**
     * Build the buffer to request to control to disconnect event
     * @param deviceName    specified device
     * @param attributeName specified attribute
     * @param eventName     specified event
     * @return  the buffer buffer to disconnect event
     * @throws DevFailed    in case of internal communication problem.
     */
	//===============================================================
    private static byte[] getBufferToDisConnectEvent(String deviceName, String attributeName, String eventName) throws DevFailed{
        byte[]  buffer = new byte[0];
        try {
            ArrayList<String>   stringList = new ArrayList<String>();
            stringList.add(getFullAttributeName(deviceName, attributeName, eventName));    //  Event name
            buffer = buildTheBuffer((byte)ZMQ_DISCONNECT_EVENT, stringList);
        }
        catch (Exception e) {
            e.printStackTrace();
            Except.throw_exception("API_ConversionFailed",
                    e.toString(), "ZMQtest.codeBufferToConnectHeartbeat()");
        }
        return buffer;
    }
	//===============================================================
    /**
     * Request to control to connect event
     * @param deviceName    specified device
     * @param attributeName specified attribute
     * @param lsa   the subscription parameters
     * @param eventName     specified event
     * @throws DevFailed    in case of internal communication problem.
     */
	//===============================================================
    static void connectEvent(String deviceName, String attributeName, DevVarLongStringArray lsa, String eventName) throws DevFailed{
        byte[] buffer = ZMQutils.getBufferToConnectEvent(
                deviceName, attributeName, lsa, eventName);
        sendToZmqControlSocket(buffer);
    }
	//===============================================================
    /**
     * Build a buffer to request to control to connect event
     * @param deviceName    specified device
     * @param attributeName specified attribute
     * @param lsa   the subscription parameters
     * @param eventName     specified event
     * @return the beffer built to connect event
     * @throws DevFailed    in case of internal communication problem.
     */
	//===============================================================
    private static byte[] getBufferToConnectEvent(String deviceName, String attributeName, DevVarLongStringArray lsa, String eventName) throws DevFailed{
        byte[]  buffer = new byte[0];
        try {
            ArrayList<String>   stringList = new ArrayList<String>();
            stringList.add(lsa.svalue[1]);                          //  EndPoint
            stringList.add(getFullAttributeName(deviceName, attributeName, eventName));    //  Event name
            ArrayList<Integer>  intList = new ArrayList<Integer>();
            intList.add(lsa.lvalue[2]);     //  Sub HWM
            intList.add(lsa.lvalue[3]);     //  Rate
            intList.add(lsa.lvalue[4]);     //  IVL
            buffer = buildTheBuffer((byte)ZMQ_CONNECT_EVENT, stringList, intList);
        }
        catch (DevFailed e) {
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            Except.throw_exception("API_ConversionFailed",
                    e.toString(), "ZMQtest.codeBufferToConnectHeartbeat()");
        }
        return buffer;
    }
	//===============================================================
    /**
     * Request to control to connect heartbeat
     * @param adminDeviceName    specified admin device
     * @param lsa   the subscription parameters
     * @throws DevFailed    in case of internal communication problem.
     */
	//===============================================================
    static void connectHeartbeat(String adminDeviceName, DevVarLongStringArray lsa) throws DevFailed{
        //  Build the buffer to connect heartbeat and send it
        byte[]  buffer = getBufferToConnectHeartbeat(adminDeviceName, lsa);
        sendToZmqControlSocket(buffer);
    }
	//===============================================================
    /**
     * RBuild buffer to request to control to connect heartbeat
     * @param adminDeviceName    specified admin device
     * @param lsa   the subscription parameters
     * @return the buffer built to connect heartbeat
     * @throws DevFailed    in case of internal communication problem.
     */
	//===============================================================
    private static byte[] getBufferToConnectHeartbeat(String adminDeviceName, DevVarLongStringArray lsa) throws DevFailed{
        byte[]  buffer = new byte[0];
        try {
            ArrayList<String>   stringList = new ArrayList<String>();
            stringList.add(lsa.svalue[0]);                          //  EndPoint
            stringList.add(getFullHeartBeatName(adminDeviceName));  //  Heartbeat name
            buffer = buildTheBuffer((byte)ZMQ_CONNECT_HEARTBEAT, stringList);
        }
        catch (DevFailed e) {
            throw e;
        }
        catch (Exception e) {
            Except.throw_exception("API_ConversionFailed",
                    e.toString(), "ZMQtest.codeBufferToConnectHeartbeat()");
        }
        return buffer;
    }
	//===============================================================
    /**
     * RBuild buffer to request to control to disconnect heartbeat
     * @param deviceName    specified admin device
     * @return the buffer built to disconnect heartbeat
     * @throws DevFailed    in case of internal communication problem.
     */
	//===============================================================
    @SuppressWarnings({"UnusedDeclaration"})
    private static byte[] getBufferToDisconnectHeartbeat(String deviceName) throws DevFailed{
        byte[]  buffer = new byte[0];
        try {
            ArrayList<String>   stringList = new ArrayList<String>();
            stringList.add(getFullHeartBeatName(deviceName));   //  Heartbeat name
            buffer = buildTheBuffer((byte)ZMQ_DISCONNECT_HEARTBEAT, stringList);
        }
        catch (DevFailed e) {
            throw e;
        }
        catch (Exception e) {
            Except.throw_exception("API_ConversionFailed",
                    e.toString(), "ZMQtest.codeBufferToDisconnectHeartbeat()");
        }
        return buffer;
    }
	//===============================================================
    /**
     * Get the event subscription info from admin device
     * @param adminDevice   specified admin device
     * @param deviceName    specified device
     * @param attributeName specified attribute
     * @param eventName     specified event
     * @return the event subscription info
     * @throws DevFailed in case of admin device connection failed
     */
	//===============================================================
    static DevVarLongStringArray getEventSubscriptionInfoFromAdmDevice(DeviceProxy adminDevice,
            String deviceName, String attributeName, String eventName) throws DevFailed {

        DeviceData argin    = new DeviceData();
        String[]	strArray = { deviceName, attributeName, "subscribe", eventName};
        argin.insert(strArray);
        DeviceData argout = adminDevice.command_inout("ZmqEventSubscriptionChange", argin);
        return argout.extractLongStringArray();
    }
	//===============================================================
    /**
     * De Marshall data from a receve byte buffer
     * @param recData   receive data
     * @param littleIndian endianness to de marshall
     * @return the data after de marshaling
     * @throws DevFailed in case of de marshaling failed
     */
	//===============================================================
    static AttDataReady deMarshallAttDataReady(byte[] recData, boolean littleIndian) throws DevFailed {
        try {
            //  Remove the 4 first bytes (added for c++ alignment)
            byte[]  buffer = new byte[recData.length-4];
            System.arraycopy(recData, 4, buffer, 0, recData.length - 4);
            CDRInputStream is = new CDRInputStream(null, buffer, littleIndian);
            return AttDataReadyHelper.read(is);
        }
        catch (Exception e) {
            Except.throw_exception("Api_ConvertionFailed",
                    "An exception " + e + " has been catch",
                    "ZMQutils.deMarshallAttDataReady()");
            return null;    //  Cannot occur
        }
    }
	//===============================================================
    /**
     * De Marshall data from a receve byte buffer
     * @param recData   receive data
     * @param littleIndian endianness to de marshall
     * @return the data after de marshaling
     * @throws DevFailed in case of de marshaling failed
     */
	//===============================================================
    static AttributeInfoEx deMarshallAttributeConfig(byte[] recData, boolean littleIndian) throws DevFailed{
        try {
            //  Remove the 4 first bytes (added for c++ alignment)
            byte[]  buffer = new byte[recData.length-4];
            System.arraycopy(recData, 4, buffer, 0, recData.length - 4);
            CDRInputStream is = new CDRInputStream(null, buffer, littleIndian);
            AttributeConfig_3 attributeConfig_3 = AttributeConfig_3Helper.read(is);
            return new AttributeInfoEx(attributeConfig_3);
        }
        catch (Exception e) {
            Except.throw_exception("Api_ConvertionFailed",
                    "An exception " + e + " has been catch",
                    "ZMQutils.deMarshallAttributeConfig()");
            return null;    //  Cannot occur
        }
    }
	//===============================================================
    /**
     * De Marshall data from a receve byte buffer
     * @param recData   receive data
     * @param littleIndian endianness to de marshall
     * @return the data after de marshaling
     * @throws DevFailed in case of de marshaling failed
     */
	//===============================================================
    static DeviceAttribute deMarshallAttribute(byte[] recData, boolean littleIndian) throws DevFailed {
        try {
            //  Remove the 4 first bytes (added for c++ alignment)
            byte[]  buffer = new byte[recData.length-4];
            System.arraycopy(recData, 4, buffer, 0, recData.length - 4);
            CDRInputStream is = new CDRInputStream(null, buffer, littleIndian);

            AttributeValue_4    attributeValue_4 = AttributeValue_4Helper.read(is);
            return new DeviceAttribute(attributeValue_4);
        }
        catch (Exception e) {
            Except.throw_exception("Api_ConvertionFailed",
                    "An exception " + e + " has been catch",
                    "ZMQutils.deMarshallAttribute()");
            return null;    //  Cannot occur
        }
    }
 	//===============================================================
    /**
     * return event type from full event name
     * @param eventName full event name
     * @return event type from full event name
     * @throws DevFailed if no event name found
     */
	//===============================================================
    static int getEventType(String eventName) throws DevFailed{
        int type = -1;
        int pos = eventName.lastIndexOf('.');
        if (pos>0) {
            String  strType = eventName.substring(pos+1);
            for (int i=0 ; type<0 && i<TangoConst.eventNames.length ; i++)
                if (strType.equals(TangoConst.eventNames[i]))
                    type = i;
        }
        if (type<0)
            Except.throw_exception("Api_BadParameterException",
                    "Cannot find event type for "+eventName,
                    "ZMQutils.getEventType()");
        return type;
    }
	//===============================================================
	//===============================================================


    //  Non static methods.
	//===============================================================
    /**
     * Decode data receive from control socket
     * @param bytes input buffer
     * @return decoded data from buffer
     * @throws DevFailed in case of control command unknown
     */
	//===============================================================
    ControlStructure decodeControlBuffer(byte[] bytes) throws DevFailed {

        ControlStructure    controlStructure = new ControlStructure();

        int idx = 0;
        controlStructure.commandCode  = bytes[idx++];

        switch (controlStructure.commandCode) {
            case ZMQ_END:
                break;  //  Nothing to decode

            case ZMQ_CONNECT_HEARTBEAT:
                //  Get endPoint size (coded as C string)
                controlStructure.endPoint = getString(bytes, idx++);
                idx += controlStructure.endPoint.length();
                controlStructure.eventName = getString(bytes, idx);
                break;

            case ZMQ_DISCONNECT_HEARTBEAT:
                controlStructure.eventName = getString(bytes, idx);
                break;

            case ZMQ_CONNECT_EVENT:
                controlStructure.endPoint = getString(bytes, idx++);
                idx += controlStructure.endPoint.length();
                controlStructure.eventName = getString(bytes, idx++);
                idx += controlStructure.eventName.length();
                controlStructure.hwm  = getInteger(bytes, idx);
                controlStructure.rate = getInteger(bytes, idx + 4);
                controlStructure.ivl  = getInteger(bytes, idx+8);
                //System.out.println(controlStructure);
                break;

            case ZMQ_DISCONNECT_EVENT:
                controlStructure.eventName = getString(bytes, idx);
                break;

            default:
                Except.throw_exception("API_NotImplemented",
                        "Command " + controlStructure.commandCode + "  NOT yet implemented",
                        "ZMQutils.decodeControlBuffer()");
        }

        return controlStructure;
    }
	//===============================================================
	//===============================================================












 	//===============================================================
    /**
     *   A little class to define the data read from control socket
     */
	//===============================================================
    class ControlStructure {
        int commandCode = -1;
        String  endPoint;
        String  eventName;
        int     hwm  = 0;
        int     rate = 0;
        int     ivl  = 0;
        //===========================================================
        public String toString() {
            StringBuffer    sb = new StringBuffer();
            sb.append("Command: ").append(commandNames[commandCode]).append("\n");
            if (endPoint!=null)
                sb.append("endPoint: ").append(endPoint).append("\n");
            if (eventName!=null)
                sb.append("eventName: ").append(eventName).append("\n");
            sb.append("int: ").append(hwm).append("  ").append(rate).append("  ")
                    .append(ivl).append("\n");

            return sb.toString();
        }
        //===========================================================
    }
	//===============================================================
	//===============================================================







    //  Trace and dump methods
	//===============================================================
	//===============================================================
    @SuppressWarnings({"UnusedDeclaration"})
    public static void trace(DevVarLongStringArray lsa){
        System.out.println("Svalue");
        for (String s : lsa.svalue)
            System.out.println("	" + s);
        System.out.println("Lvalue");
        for (int i : lsa.lvalue)
            System.out.println("	" + i);
    }
	//===========================================================================
	//===========================================================================
    @SuppressWarnings({"UnusedDeclaration"})
	public static void dump(byte[] rec) {
		for (int i=0 ; i<rec.length ; i++) {

			String	s = String.format("%02x",(0xFF & rec[i]));
			System.out.print("0x" + s + " ");
			if ( ((i+1)%16)==0 )
				System.out.println();
		}
		System.out.println();
	}
	//===============================================================
	//===============================================================
}