package com.codepath.qzineat.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.models.Event;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static java.lang.Integer.parseInt;

/**
 * Created by glondhe on 3/1/16.
 */
public class HostFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private static final int RESULT_OK = -1 ;
    private static final int CAMERA_REQUEST = 1888;
    private static int RESULT_LOAD_IMAGE = 1;
    public static StringBuilder date;
    private int _year;
    private int _month;
    private int _day;
    private String imgDecodableString;
    private Date dateObject;
    Bitmap bitmap;

    ArrayAdapter arrayAdapter;
    @Bind(R.id.ivEventImage)
    ImageView ivEventImage;
    @Bind(R.id.ivEventImage2)
    ImageView ivEventImage2;
    @Bind(R.id.tvTitle)
    TextView tvTitile;
    @Bind(R.id.etTitle)
    EditText etTitile;
    @Bind(R.id.btUpload)
    Button btUpload;
    @Bind(R.id.btCamera)
    Button btCamera;
    @Bind(R.id.tvDate)
    TextView tvDate;
    @Bind(R.id.tvDatePicker)
    TextView tvDatePicker;
    @Bind(R.id.tvGuest)
    TextView tvGuest;
    @Bind(R.id.spGuest)
    Spinner spGuest;
    @Bind(R.id.tvCharge)
    TextView tvCharge;
    @Bind(R.id.etCharge)
    EditText etCharge;
    @Bind(R.id.tvVenue)
    TextView tvVenue;
    @Bind(R.id.etVenue)
    EditText etVenue;
    @Bind(R.id.tvDesc)
    TextView tvDesc;
    @Bind(R.id.etDesc)
    EditText etDesc;
    @Bind(R.id.tvAlcohol)
    TextView tvAlcohol;
    @Bind(R.id.spAlcohol)
    Spinner spAlcohol;
    @Bind(R.id.btSave)
    Button btSave;
    @Bind(R.id.btCancel)
    Button btCancel;
    private Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.host_layout, container, false);
        ButterKnife.bind(this, view);

        spGuest.setAdapter(arrayAdapter);
        tvDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });
        btUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        btCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveEvent(getContext());

            }
        });

        return view;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContext().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                // Set the Image in ImageView after decoding the String
                ivEventImage.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    ivEventImage.setImageBitmap(photo);
                }
            } else {
                Toast.makeText(getContext(), "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void saveEvent(final Context context) {
        // TODO: Lets add Login SignUp
        ParseUser currentUser = ParseUser.getCurrentUser();
        // Parse Save
        Event event = new Event();
        event.setTitle(etTitile.getText().toString());
        event.setGuestLimit(parseInt(String.valueOf(spGuest.getSelectedItem())));
        event.setDescription(etDesc.getText().toString());
        dateObject = getDateObject();
        event.setDate(dateObject);
        event.setAddress(etVenue.getText().toString());
        event.setPrice(parseInt(String.valueOf(etCharge.getText())));
        event.setGuestLimit(parseInt(String.valueOf(spGuest.getSelectedItem())));
        event.setAlcohol(spAlcohol.getSelectedItem().toString());
        bitmap = ((BitmapDrawable) ivEventImage.getDrawable()).getBitmap();
        byte[] text = BitMapToString(bitmap);
        ParseFile File = new ParseFile("EventImage.txt", text);
        event.setImageFile(File);
        event.setHost(currentUser);
        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                Toast.makeText(context, "Successfully created event on Parse", Toast.LENGTH_SHORT).show();
            }
        });
//        new GitHub().execute("");
    }

    private Date getDateObject() {

        DateFormat formatter = new SimpleDateFormat("yyyy/mm/dd"); // Make sure user insert date into edittext in this format.
        Date dateObject = null;
        String dob_var=(tvDatePicker.getText().toString());
        try {
            dateObject = formatter.parse(dob_var);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return dateObject;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter();

    }

    private void setListAdapter() {
        List list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (i == 0) list.add("Choose");
            else list.add(i);
        }
        arrayAdapter = new ArrayAdapter<>(this.getActivity(),
        android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        _year = year;
        _month = monthOfYear;
        _day = dayOfMonth;

        this.date = new StringBuilder()
                .append(_year).append("/");

        if (_month < 10) this.date.append(0).append(_month + 1).append("/");
        else this.date.append(_month + 1).append("/");

        if (_day < 10) this.date.append(0).append(_day);
        else this.date.append(_day + 1);

        tvDatePicker.setText(this.date);

    }


//    public class GitHub extends AsyncTask<String, Void, String> {
//
//        @Override
//        public String doInBackground(String... params) {
////            new Thread(new Runnable() {
////                public void run() {
//            try {
//
//
//                // based on http://swanson.github.com/blog/2011/07/23/digging-around-the-github-api-take-2.html
//                // initialize github client
//                GitHubClient client = new GitHubClient();
//                client.setCredentials("londhegaurav", "rujuta1982");
//                Log.d("DEBUGGIT", client.getUser());
//
//                // create needed services
//                RepositoryService repositoryService = new RepositoryService();
//                CommitService commitService = new CommitService(client);
//                DataService dataService = new DataService(client);
////                    List<Repository> repo = repositoryService.getRepositories("londhegaurav");
////
////                    for (int i = 0; i < repo.size(); i++) {
////                        Log.d("DEBUGGIT", repo.get(i).getGitUrl().toString());
////                        Log.d("DEBUGGIT", repo.get(i).getName().toString());
////                        Log.d("DEBUGGIT", String.valueOf(repo.get(i).getId()));
////                    }
//
//                // get some sha's from current state in git
//                Repository repository = repositoryService.getRepository("londhegaurav", "qzinphotos");
//                String baseCommitSha = repositoryService.getBranches(repository).get(0).getCommit().getSha();
//                RepositoryCommit baseCommit = commitService.getCommit(repository, baseCommitSha);
//                String treeSha = baseCommit.getSha();
//                //        Log.d("DEBUGGIT", repositoryService.getRepositories().toString());
//
//                // create new blob with data
//                Blob blob = new Blob();
//                //blob.setContent("[\"" + System.currentTimeMillis() + "\"]").setEncoding(Blob.ENCODING_UTF8);
//                String bitmpString = BitMapToString(bitmap);
//                blob.setContent(bitmpString);
//                String blob_sha = dataService.createBlob(repository, blob);
//                Tree baseTree = dataService.getTree(repository, treeSha);
//
//                // create new tree entry
//                TreeEntry treeEntry = new TreeEntry();
//                treeEntry.setPath("photos/" + System.currentTimeMillis()+".txt");
//                treeEntry.setMode(TreeEntry.MODE_BLOB);
//                treeEntry.setType(TreeEntry.TYPE_BLOB);
//                treeEntry.setSha(blob_sha);
//                treeEntry.setSize(blob.getContent().length());
//                Collection<TreeEntry> entries = new ArrayList<TreeEntry>();
//                entries.add(treeEntry);
//                Tree newTree = dataService.createTree(repository, entries, baseTree.getSha());
//
//                // create commit
//                CommitUser author = new CommitUser();
//                author.setName(client.getUser());
//                author.setEmail("londhegaurav@gmail.com");
//                author.setDate(new GregorianCalendar().getTime());
//
//                Commit commit = new Commit();
//                commit.setMessage("Android commit at " + new Date(System.currentTimeMillis()).toLocaleString());
//                commit.setTree(newTree);
//                commit.setAuthor(author);
//                commit.setCommitter(author);
//                List<Commit> listOfCommits = new ArrayList<Commit>();
//                listOfCommits.add(new Commit().setSha(baseCommitSha));
//                // listOfCommits.containsAll(base_commit.getParents());
//                commit.setParents(listOfCommits);
//
//                // commit.setSha(base_commit.getSha());
//                Commit newCommit = dataService.createCommit(repository, commit);
//
//                // create resource
//                TypedResource commitResource = new TypedResource();
//                commitResource.setSha(newCommit.getSha());
//                commitResource.setType(TypedResource.TYPE_COMMIT);
//                commitResource.setUrl(newCommit.getUrl());
//
//                // get master reference and update it
//                Reference reference = dataService.getReference(repository, "heads/master");
//                reference.setObject(commitResource);
//                dataService.editReference(repository, reference, true);
//
//                // success
//            } catch (Exception e) {
//                // error
//                e.printStackTrace();
//            }
////                }
////            }).start();
//            return null;
//        }
//
//    }
    public byte [] BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return b;
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}
