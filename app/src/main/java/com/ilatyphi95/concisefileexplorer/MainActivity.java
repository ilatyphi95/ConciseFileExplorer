package com.ilatyphi95.concisefileexplorer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private String path;
    private final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1;
    private String m_root= Environment.getExternalStorageDirectory().getPath();
    List<String> mItem, mPath, mFiles, mFilespath;
    String mCurDir;

    private View.OnClickListener mOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int adapterPostion = viewHolder.getAdapterPosition();
            File m_isFile = new File(mPath.get(adapterPostion));
            if (m_isFile.isDirectory()) {
                getDirFromRoot(m_isFile.toString());
            } else {
                Toast.makeText(MainActivity.this, "This is File", Toast.LENGTH_SHORT).show();
            }
        }
    };;
    private FileAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        } else {
            getDirFromRoot(m_root);
        }

    }

    //get directories and files from selected path
    public void getDirFromRoot(String p_rootPath)
    {
        mItem = new ArrayList<String>();
        Boolean m_isRoot=true;
        mPath = new ArrayList<String>();
        mFiles =new ArrayList<String>();
        mFilespath =new ArrayList<String>();
        File m_file = new File(p_rootPath);
        File[] m_filesArray = m_file.listFiles();
        if(!p_rootPath.equals(m_root))
        {
            mItem.add("../");
            mPath.add(m_file.getParent());
            m_isRoot=false;
        }
        mCurDir =p_rootPath;
        //sorting file list in alphabetical order
        Arrays.sort(m_filesArray);
        for(int i=0; i < m_filesArray.length; i++)
        {
            File file = m_filesArray[i];
            if(file.isDirectory())
            {
                mItem.add(file.getName());
                mPath.add(file.getPath());
            }
            else
            {
                mFiles.add(file.getName());
                mFilespath.add(file.getPath());
            }
        }
        for(String m_AddFile: mFiles)
        {
            mItem.add(m_AddFile);
        }
        for(String m_AddPath: mFilespath)
        {
            mPath.add(m_AddPath);
        }

        final RecyclerView recyclerView = findViewById(R.id.recycler);
        mAdapter = new FileAdapter(this, mItem, mPath, m_isRoot);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(mOnItemClickListener);

    }

    void createNewFolder( final int p_opt)
    {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

        // Set up the input
        final EditText m_edtinput = new EditText(this);
        // Specify the type of input expected;
        m_edtinput.setInputType(InputType.TYPE_CLASS_TEXT);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String m_text = m_edtinput.getText().toString();
                if(p_opt == 1)
                {
                    File m_newPath=new File(mCurDir,m_text);
                    Log.d("cur dir", mCurDir);
                    if(!m_newPath.exists()) {
                        m_newPath.mkdirs();
                    }
                }
                else
                {
                    try {
                        FileOutputStream m_Output = new FileOutputStream((mCurDir + File.separator + m_text), false);
                        m_Output.close();
                        //  <!--<intent-filter>
                        //  <action android:name="android.intent.action.SEARCH" />
                        //  </intent-filter>
                        //  <meta-data android:name="android.app.searchable"
                        //  android:resource="@xml/searchable"/>-->

                    } catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                getDirFromRoot(mCurDir);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setView(m_edtinput);
        builder.show();
    }

    void deleteFile()
    {
        for(int m_delItem : mAdapter.m_selectedItem)
        {
            File m_delFile = new File(mPath.get(m_delItem));
            Log.d("file", mPath.get(m_delItem));
            boolean m_isDelete=m_delFile.delete();
            Toast.makeText(MainActivity.this, "File(s) Deleted", Toast.LENGTH_SHORT).show();
            getDirFromRoot(mCurDir);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.file_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_add_folder:
                createNewFolder(1);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getDirFromRoot(m_root);
                }
        }
    }
}
