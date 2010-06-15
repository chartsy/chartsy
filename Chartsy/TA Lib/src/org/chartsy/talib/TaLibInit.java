/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.talib;

import com.tictactec.ta.lib.Core;

/**
 *
 * @author jtaylor
 */
public class TaLibInit {
    //needs to be initialized as early as possible to ensure serialization
    //concerns for objects in other modules that use this library and not
    //follow the pattern of delayed singleton initialization
    //that would normally be part of the "getCore()" method.
    //See "Effective Java 2nd edition," pgs. 17,18.
    private static final Core CORE = new Core();

    private TaLibInit(){}

    public static Core getCore(){return CORE;}
    private Object readResolve(){return CORE;}
}
