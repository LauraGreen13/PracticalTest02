package ro.pub.cs.systems.eim.practicaltest02;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ro.pub.cs.systems.eim.practicaltest02.R;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientFragment extends Fragment {

    private EditText serverAddressEditText, serverPortEditText;
    private TextView serverMessageTextView;
    private Button displayMessageButton;
    private EditText cityEditText;

    private class ClientAsyncTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Socket socket = null;
            try {
                String serverAddress = params[0];
                int serverPort = Integer.parseInt(params[1]);
                socket = new Socket(serverAddress, serverPort);
                if (socket == null) {
                    return null;
                }

                PrintWriter printWriter = Utilities.getWriter(socket);
                printWriter.println(cityEditText.getText());

                Log.v(Constants.TAG, "Connection opened with " + socket.getInetAddress() + ":" + socket.getLocalPort());
                BufferedReader bufferedReader = Utilities.getReader(socket);
                String currentLine;
                while ((currentLine = bufferedReader.readLine()) != null) {
                    publishProgress(currentLine);
                }
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                    Log.v(Constants.TAG, "Connection closed");
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            serverMessageTextView.setText("");
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            serverMessageTextView.append(progress[0] + "\n");
        }

        @Override
        protected void onPostExecute(Void result) { }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        return inflater.inflate(R.layout.client_fragment, parent, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        serverAddressEditText = (EditText)getActivity().findViewById(R.id.server_address_edit_text);
        serverPortEditText = (EditText)getActivity().findViewById(R.id.server_port_edit_text);
        serverMessageTextView = (TextView)getActivity().findViewById(R.id.server_message_text_view);
        cityEditText = (EditText) getActivity().findViewById(R.id.city_edit_text);
        displayMessageButton = (Button)getActivity().findViewById(R.id.display_message_button);
        displayMessageButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClientAsyncTask clientAsyncTask = new ClientAsyncTask();
                clientAsyncTask.execute(serverAddressEditText.getText().toString(), serverPortEditText.getText().toString());
            }
        });

    }




}