package com.codepath.qzineat.activities;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.codepath.android.qzineat.R;

import org.eclipse.egit.github.core.Blob;
import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.CommitUser;
import org.eclipse.egit.github.core.Reference;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.Tree;
import org.eclipse.egit.github.core.TreeEntry;
import org.eclipse.egit.github.core.TypedResource;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.DataService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by glondhe on 3/5/16.
 */
public class AsyncTaskActivity extends Activity {
    Button btn;
    private Bitmap bitmap;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_layout);
        Intent intent = getIntent();
        ImageView ivEventImage = (ImageView) findViewById(R.id.ivEventImage);
        bitmap = ((BitmapDrawable)ivEventImage.getDrawable()).getBitmap();
//        ivEventImage2.setImageBitmap(bitmap);
        new GitHub().execute("");
    }



    public class GitHub extends AsyncTask<String, Void, String> {

        @Override
        public String doInBackground(String... params) {
//            new Thread(new Runnable() {
//                public void run() {
                    try {


                        // based on http://swanson.github.com/blog/2011/07/23/digging-around-the-github-api-take-2.html
                        // initialize github client
                        GitHubClient client = new GitHubClient();
                        client.setCredentials("londhegaurav", "rujuta1982");
                        Log.d("DEBUGGIT", client.getUser());

                        // create needed services
                        RepositoryService repositoryService = new RepositoryService();
                        CommitService commitService = new CommitService(client);
                        DataService dataService = new DataService(client);
//                    List<Repository> repo = repositoryService.getRepositories("londhegaurav");
//
//                    for (int i = 0; i < repo.size(); i++) {
//                        Log.d("DEBUGGIT", repo.get(i).getGitUrl().toString());
//                        Log.d("DEBUGGIT", repo.get(i).getName().toString());
//                        Log.d("DEBUGGIT", String.valueOf(repo.get(i).getId()));
//                    }

                        // get some sha's from current state in git
                        Repository repository = repositoryService.getRepository("londhegaurav", "qzinphotos");
                        String baseCommitSha = repositoryService.getBranches(repository).get(0).getCommit().getSha();
                        RepositoryCommit baseCommit = commitService.getCommit(repository, baseCommitSha);
                        String treeSha = baseCommit.getSha();
                        //        Log.d("DEBUGGIT", repositoryService.getRepositories().toString());

                        // create new blob with data
                        Blob blob = new Blob();
                        //blob.setContent("[\"" + System.currentTimeMillis() + "\"]").setEncoding(Blob.ENCODING_UTF8);
                        blob.setContent(String.valueOf(bitmap)).setEncoding(Blob.ENCODING_UTF8);
                        String blob_sha = dataService.createBlob(repository, blob);
                        Tree baseTree = dataService.getTree(repository, treeSha);

                        // create new tree entry
                        TreeEntry treeEntry = new TreeEntry();
                        treeEntry.setPath("photos/"+System.currentTimeMillis()+".png");
                        treeEntry.setMode(TreeEntry.MODE_BLOB);
                        treeEntry.setType(TreeEntry.TYPE_BLOB);
                        treeEntry.setSha(blob_sha);
                        treeEntry.setSize(blob.getContent().length());
                        Collection<TreeEntry> entries = new ArrayList<TreeEntry>();
                        entries.add(treeEntry);
                        Tree newTree = dataService.createTree(repository, entries, baseTree.getSha());

                        // create commit
                        CommitUser author = new CommitUser();
                        author.setName(client.getUser());
                        author.setEmail("londhegaurav@gmail.com");
                        author.setDate(new GregorianCalendar().getTime());

                        Commit commit = new Commit();
                        commit.setMessage("Android commit at " + new Date(System.currentTimeMillis()).toLocaleString());
                        commit.setTree(newTree);
                        commit.setAuthor(author);
                        commit.setCommitter(author);
                        List<Commit> listOfCommits = new ArrayList<Commit>();
                        listOfCommits.add(new Commit().setSha(baseCommitSha));
                        // listOfCommits.containsAll(base_commit.getParents());
                        commit.setParents(listOfCommits);

                        // commit.setSha(base_commit.getSha());
                        Commit newCommit = dataService.createCommit(repository, commit);

                        // create resource
                        TypedResource commitResource = new TypedResource();
                        commitResource.setSha(newCommit.getSha());
                        commitResource.setType(TypedResource.TYPE_COMMIT);
                        commitResource.setUrl(newCommit.getUrl());

                        // get master reference and update it
                        Reference reference = dataService.getReference(repository, "heads/master");
                        reference.setObject(commitResource);
                        dataService.editReference(repository, reference, true);

                        // success
                    } catch (Exception e) {
                        // error
                        e.printStackTrace();
                    }
//                }
//            }).start();
            return null;
        }

    }
}
