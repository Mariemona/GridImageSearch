package gridimagesearch.codepath.com.gridimagesearch.activities;

import android.app.Activity;
//import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
//import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
//import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

//import org.apache.http.Header;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gridimagesearch.codepath.com.gridimagesearch.R;
//import gridimagesearch.codepath.com.gridimagesearch.activities.ImageResultsAdapter;
//
import gridimagesearch.codepath.com.gridimagesearch.adapters.ImageResultsAdapter;
import gridimagesearch.codepath.com.gridimagesearch.models.ImageResult;


public class SearchActivity extends Activity {
    EditText ETQuery;
    GridView gvResults;
    ArrayList<ImageResult> ImageResults;
    ImageResultsAdapter aImageResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
        // Create the data source
        ImageResults= new ArrayList<ImageResult>();
        // Attaches the data source to an adapter
        aImageResults= new ImageResultsAdapter(this,ImageResults);
        // Link the adapter o the adapterview (gridview)
        gvResults.setAdapter(aImageResults);

    }
    private void setupViews(){
        ETQuery = (EditText) findViewById(R.id.ETQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Launch the image display activity
                // Creating on intent
                Intent i= new Intent(SearchActivity.this,ImageDisplayActivity.class);
                // Get the image result to display
                ImageResult result= ImageResults.get(position);
                // Pass image result into the intent
                i.putExtra("result", result); // need to eider be serializable or parcelable
                // Launch the new activity
                startActivity(i);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }
    // fired whenever the is pressed (android:onclik property)
    public void onImageSearch(View v) {
        String query = ETQuery.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        //https://ajax.googleapis.com/ajax/services/search/images
        //https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=Android
        String searchUrl ="https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + query +"&rsz=8";
        client.get(searchUrl,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray ImageResultsJson = null;
                try {
                    ImageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                    ImageResults.clear();// clear the existing images from the array (in cases where its a new search)
                    // When you make to the adapter, it does notify the underlying data
                    aImageResults.addAll(ImageResult.fromJSONArray(ImageResultsJson));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("INFO", ImageResults.toString());

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
