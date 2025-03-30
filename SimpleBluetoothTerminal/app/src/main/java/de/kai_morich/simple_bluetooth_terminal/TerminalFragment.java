package de.kai_morich.simple_bluetooth_terminal;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayDeque;

public class TerminalFragment extends Fragment implements ServiceConnection, SerialListener {

    private enum Connected { False, Pending, True }

    private String deviceAddress;
    private SerialService service;

    private Button btnForward, btnBackward, btnLeft, btnRight;

    private Connected connected = Connected.False;
    private boolean initialStart = true;

    /*
     * Lifecycle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        deviceAddress = getArguments().getString("device");
    }

    @Override
    public void onDestroy() {
        if (connected != Connected.False)
            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    @Override
    public void onStop() {
        if(service != null && !getActivity().isChangingConfigurations())
            service.detach();
        super.onStop();
    }

    @SuppressWarnings("deprecation") // onAttach(context) was added with API 23. onAttach(activity) works for all API versions
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        try { getActivity().unbindService(this); } catch(Exception ignored) {}
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(initialStart && service != null) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
        if(initialStart && isResumed()) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    /*
     * UI
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal, container, false);
        
        btnForward = view.findViewById(R.id.btn_forward);
        btnBackward = view.findViewById(R.id.btn_backward);
        btnLeft = view.findViewById(R.id.btn_left);
        btnRight = view.findViewById(R.id.btn_right);

        btnForward.setOnClickListener(v -> sendCommand("F"));
        btnBackward.setOnClickListener(v -> sendCommand("B"));
        btnLeft.setOnClickListener(v -> sendCommand("L"));
        btnRight.setOnClickListener(v -> sendCommand("R"));

        setButtonsEnabled(false);
        
        return view;
    }

    /*
     * Serial + UI
     */
    private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            Toast.makeText(getActivity(), "Connecting...", Toast.LENGTH_SHORT).show();
            connected = Connected.Pending;
            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        setButtonsEnabled(false);
        service.disconnect();
        Toast.makeText(getActivity(), "Disconnected", Toast.LENGTH_SHORT).show();
    }

    private void sendCommand(String command) {
        if(connected != Connected.True) {
            Toast.makeText(getActivity(), "Not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        if(command == null || command.isEmpty()) {
            return;
        }
        try {
            byte[] data = command.getBytes(); 
            service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        if (btnForward != null) btnForward.setEnabled(enabled);
        if (btnBackward != null) btnBackward.setEnabled(enabled);
        if (btnLeft != null) btnLeft.setEnabled(enabled);
        if (btnRight != null) btnRight.setEnabled(enabled);
    }

    /*
     * SerialListener
     */
    @Override
    public void onSerialConnect() {
        Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
        connected = Connected.True;
        setButtonsEnabled(true);
    }

    @Override
    public void onSerialConnectError(Exception e) {
        Toast.makeText(getActivity(), "Connection Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
    }

    @Override
    public void onSerialRead(ArrayDeque<byte[]> datas) { 
    }

    @Override
    public void onSerialIoError(Exception e) {
        Toast.makeText(getActivity(), "Connection Lost: " + e.getMessage(), Toast.LENGTH_LONG).show();
        disconnect();
    }

}

