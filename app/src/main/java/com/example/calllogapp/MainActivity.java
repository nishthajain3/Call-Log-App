package com.example.calllogapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CallLogAdapter adapter;
    private List<CallLogEntry> callLogEntries = new ArrayList<>();
    private List<CallLogEntry> filteredCallLogEntries = new ArrayList<>();

    private Button btnAll, btnOutgoing, btnIncoming, btnMissed, btnConnected, btnNotConnected;
    private LinearLayout llOutgoingSubtypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CallLogAdapter(filteredCallLogEntries);
        recyclerView.setAdapter(adapter);

        btnAll = findViewById(R.id.btnAll);
        btnOutgoing = findViewById(R.id.btnOutgoing);
        btnIncoming = findViewById(R.id.btnIncoming);
        btnMissed = findViewById(R.id.btnMissed);
        btnConnected = findViewById(R.id.btnConnected);
        btnNotConnected = findViewById(R.id.btnNotConnected);
        llOutgoingSubtypes = findViewById(R.id.llOutgoingSubtypes);

        btnAll.setOnClickListener(v -> filterCallLogs("ALL"));
        btnOutgoing.setOnClickListener(v -> {
            llOutgoingSubtypes.setVisibility(View.VISIBLE);
            filterCallLogs("OUTGOING");
        });
        btnIncoming.setOnClickListener(v -> {
            llOutgoingSubtypes.setVisibility(View.GONE);
            filterCallLogs("INCOMING");
        });
        btnMissed.setOnClickListener(v -> {
            llOutgoingSubtypes.setVisibility(View.GONE);
            filterCallLogs("MISSED");
        });
        btnConnected.setOnClickListener(v -> filterCallLogs("CONNECTED"));
        btnNotConnected.setOnClickListener(v -> filterCallLogs("NOT_CONNECTED"));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
        } else {
            fetchCallLogs();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchCallLogs();
        }
    }

    private void fetchCallLogs() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String phoneNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                int callType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                long callDate = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
                String callDuration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
                boolean isConnected = Integer.parseInt(callDuration) > 0;

                String callTypeStr = "";
                switch (callType) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        callTypeStr = "Outgoing";
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        callTypeStr = "Incoming";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        callTypeStr = "Missed";
                        break;
                }

                CallLogEntry entry = new CallLogEntry();
                entry.setPhoneNumber(phoneNumber);
                entry.setCallType(callTypeStr);
                entry.setCallDate(DateFormat.format("dd-MM-yyyy", new Date(callDate)).toString());
                entry.setCallTime(DateFormat.format("hh:mm:ss a", new Date(callDate)).toString());
                entry.setCallDuration(callDuration);
                entry.setConnected(isConnected);

                callLogEntries.add(entry);
            }
            cursor.close();
            filterCallLogs("ALL");
        }
    }

    private void filterCallLogs(String filter) {
        filteredCallLogEntries.clear();

        switch (filter) {
            case "ALL":
                filteredCallLogEntries.addAll(callLogEntries);
                break;
            case "OUTGOING":
                filteredCallLogEntries.addAll(callLogEntries.stream()
                        .filter(entry -> "Outgoing".equals(entry.getCallType()))
                        .collect(Collectors.toList()));
                break;
            case "INCOMING":
                filteredCallLogEntries.addAll(callLogEntries.stream()
                        .filter(entry -> "Incoming".equals(entry.getCallType()))
                        .collect(Collectors.toList()));
                break;
            case "MISSED":
                filteredCallLogEntries.addAll(callLogEntries.stream()
                        .filter(entry -> "Missed".equals(entry.getCallType()))
                        .collect(Collectors.toList()));
                break;
            case "CONNECTED":
                filteredCallLogEntries.addAll(callLogEntries.stream()
                        .filter(entry -> "Outgoing".equals(entry.getCallType()) && entry.isConnected())
                        .collect(Collectors.toList()));
                break;
            case "NOT_CONNECTED":
                filteredCallLogEntries.addAll(callLogEntries.stream()
                        .filter(entry -> "Outgoing".equals(entry.getCallType()) && !entry.isConnected())
                        .collect(Collectors.toList()));
                break;
        }

        adapter.notifyDataSetChanged();
    }
}
