package com.codepath.qzineat;

import android.content.Context;
import android.widget.Toast;

import com.codepath.qzineat.models.Event;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by Shyam Rokde on 3/3/16.
 */
// TODO: Remove this class later
public class Example {

    /**
     * How to save in Parse?
     * TODO: We have to associate user to events...
     */
    public static void saveEvent(final Context context){
        // Current User - currently this is Anonymous until we implement login
        ParseUser currentUser = ParseUser.getCurrentUser();
        // Parse Save
        Event event = new Event();
        event.setTitle("Sunset Sushi Party!!");
        event.setDescription("Throwing a sushi party might sound a little daunting, but I promise, you won't regret the effort. Lets have fun together!!!.");
        event.setImageUrl("http://cdn1.tnwcdn.com/wp-content/blogs.dir/1/files/2012/10/Food.jpg");
        event.setHost(currentUser);
        event.setGuestLimit(25);

        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null)
                    Toast.makeText(context, "Successfully created event on Parse", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
