package ro.pub.cs.systems.eim.practicaltest02.general;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by laura on 13.05.2016.
 */
public interface Constants {

    public final static String  SERVER_HOST  = "localhost";
    public final static String  WEATHER_URL  = "http://www.wunderground.com/cgi-bin/findweather/getForecast?query=";
    public final static int     SERVER_PORT  = 2016;

    public final static String  SERVER_START = "Start";
    public final static String  SERVER_STOP  = "Stop";

    public final static boolean DEBUG        = true;

    public final static String  TAG          = "Client/ServerComm";



}