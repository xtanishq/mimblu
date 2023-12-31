package com.example.mimblu;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> adapter;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview);
        submit = findViewById(R.id.btn);

        // Fetch symptoms data from the API and populate the ListView
        fetchSymptomsData();

        // ListView item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateButtonState();
            }
        });

        // Initial state of the button (disabled)
        submit.setEnabled(false);
    }

    private void fetchSymptomsData() {
        String url = "http://dev.mimblu.com/mimblu-yii2-1552/api/user/symptoms";

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: " + response);
                        try {
                            // Parse the JSON response into a JSONArray
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("list");

                            List<String> symptomsList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String symptomTitle = jsonArray.getString(i);
                                symptomsList.add(symptomTitle);

                                Log.i(TAG, "Symptom Title: " + symptomTitle);
                            }

                            // Update the ListView adapter with the fetched data
                            adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_multiple_choice, symptomsList);
                            listView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                        Log.e("API Error", "Error fetching data: " + error.toString());
                    }
                }
        );

        // Add the request to the RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void updateButtonState() {
        // Check if any items are selected in the ListView
        SparseBooleanArray selectedItems = listView.getCheckedItemPositions();
        boolean anyItemSelected = false;
        for (int i = 0; i < selectedItems.size(); i++) {
            if (selectedItems.valueAt(i)) {
                anyItemSelected = true;
                break;
            }
        }

        // Enable or disable the button based on selection
        submit.setEnabled(anyItemSelected);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.done) {
            String itemSelected = "Selected items:\n";
            SparseBooleanArray selectedItems = listView.getCheckedItemPositions();
            for (int i = 0; i < selectedItems.size(); i++) {
                int position = selectedItems.keyAt(i);
                if (selectedItems.valueAt(i)) {
                    itemSelected += adapter.getItem(position) + "\n";
                }
            }

            Toast.makeText(this, itemSelected, Toast.LENGTH_SHORT).show();

            // Reset selected items
            listView.clearChoices();
            updateButtonState();
        }

        return super.onOptionsItemSelected(item);
    }
}
