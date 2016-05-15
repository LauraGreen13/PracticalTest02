package ro.pub.cs.systems.eim.practicaltest02;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02.model.WeatherInfo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFragment extends Fragment {
    private EditText serverTextEditText;
    private EditText cityEditText;

    private ServerTextContentWatcher serverTextContentWatcher = new ServerTextContentWatcher();

    private class ServerTextContentWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            Log.v(Constants.TAG, "Text changed in edit text: " + charSequence.toString());
            if (Constants.SERVER_START.toString().equals(charSequence.toString())) {
                serverThread = new ServerThread();
                serverThread.startServer();
                Log.v(Constants.TAG, "Starting server...");
            }
            if (Constants.SERVER_STOP.equals(charSequence.toString())) {
                serverThread.stopServer();
                Log.v(Constants.TAG, "Stopping server...");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }

    }

    private class CommunicationThread extends Thread {

        private Socket socket;
        WeatherInfo weatherInfo = new WeatherInfo();

        public CommunicationThread(Socket socket) {
            if (socket != null) {
                this.socket = socket;
                Log.d(Constants.TAG, "[SERVER] Created communication thread with: " + socket.getInetAddress() + ":" + socket.getLocalPort());
            }
        }

        @Override
        public void run() {
            try {

                /////////////////////
//                if (socket == null) {
//                    return;
//                }
//                boolean isRunning = true;
//                InputStream requestStream = null;
//                OutputStream responseStream = null;
//                try {
//                    requestStream = socket.getInputStream();
//                } catch (IOException ioException) {
//                    Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
//                    if (Constants.DEBUG) {
//                        ioException.printStackTrace();
//                    }
//                }
                String result = "";

                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpWebPageGet = new HttpGet(Constants.WEATHER_URL + cityEditText.getText());

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                try {
                    result = httpClient
                            .execute(httpWebPageGet, responseHandler);
                    Log.i("RESPONSE", result);
                } catch (ClientProtocolException e) {
                    Log.e(Constants.TAG, e.getMessage());
                } catch (IOException e) {
                    Log.e(Constants.TAG, e.getMessage());
                }

                Document document = Jsoup.parse(result);
                Element htmlTag = document.child(0);

                Element temperatureDiv = htmlTag.getElementById("curTemp").getElementsByClass("wx-value").first();
                weatherInfo.setTemperature(Float.valueOf(temperatureDiv.ownText()));


                Element windDiv = htmlTag.getElementById("windDir").getElementsByClass("wx-data").last().getElementsByClass("wx-value").first();
                weatherInfo.setWindSpeed(Float.valueOf(windDiv.ownText()));

                Element pressureDiv = htmlTag.getElementsByClass("ccDetails").first().getElementsByTag("table").first().getElementsByClass("wx-value").first();
                weatherInfo.setPressure(Float.valueOf(pressureDiv.ownText()));

//                serverTextEditText.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        serverTextEditText.setText("Temperature: " + weatherInfo.getTemperature() + "\n" );
//                    }
//                });

                //////////////////////
//                Log.v(Constants.TAG, "Connection opened with " + socket.getInetAddress() + ":" + socket.getLocalPort());
                PrintWriter printWriter = Utilities.getWriter(socket);
                printWriter.println("Temperature: " + weatherInfo.getTemperature() + "\n" + "Wind Speed: " + weatherInfo.getWindSpeed() + "\n"
                                    + "Pressure: " + weatherInfo.getPressure() + "\n");
                socket.close();
                Log.v(Constants.TAG, "Connection closed");
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    private ServerThread serverThread;

    private class ServerThread extends Thread {

        private boolean isRunning;

        private ServerSocket serverSocket;

        public void startServer() {
            isRunning = true;
            start();
            Log.v(Constants.TAG, "startServer() method invoked");
        }

        public void stopServer() {
            isRunning = false;
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
            Log.v(Constants.TAG, "stopServer() method invoked");
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(Constants.SERVER_PORT);
                while (isRunning) {
                    Socket socket = serverSocket.accept();
                    if (socket != null) {
                        CommunicationThread communicationThread = new CommunicationThread(socket);
                        communicationThread.start();
                    }
                }
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        return inflater.inflate(R.layout.server_fragment, parent, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        serverTextEditText = (EditText) getActivity().findViewById(R.id.server_text_edit_text);
        cityEditText = (EditText) getActivity().findViewById(R.id.city_edit_text);
        serverTextEditText.addTextChangedListener(serverTextContentWatcher);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serverThread != null) {
            serverThread.stopServer();
        }
    }

}