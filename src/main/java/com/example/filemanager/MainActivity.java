package com.example.filemanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylayout);
    }

    class Adapter extends BaseAdapter{
        private List<String> list1= new ArrayList<>();
        public void setlist1(List<String> list1){
            if(list1!=null){
                this.list1.clear();
                if(list1.size()>0){
                    this.list1.addAll(list1);
                }
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return list1.size();
        }

        @Override
        public String getItem(int position) {
            return list1.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //return convertView;
            if(convertView==null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent,false);
                convertView.setTag(new ViewCreate((TextView) convertView.findViewById(R.id.item1)));
            }
            ViewCreate holder = (ViewCreate)convertView.getTag();
            final String item = getItem(position);
            holder.data.setText(item.substring(item.lastIndexOf('/')));
            return convertView;

        }
        class ViewCreate{
            TextView data;
            ViewCreate(TextView data){
                this.data=data;
            }
        }
    }
    private static final int get_permissions= 9999;
    private static final String[] PERMISSIONS= {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int PERMISSIONS_COUNT=2;
    private boolean PermissionsStatus(){
        // If android version is greater than Android 6, previous method of getting permissions doesn't work, so this is the new one.
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int p=0;
            while(p<PERMISSIONS_COUNT){
                if (checkSelfPermission(PERMISSIONS[p])!= PackageManager.PERMISSION_GRANTED){
                    return true;
                }
                p++;

            }
            return false;
        }
        return true;
    }
    public String dirName;
    private boolean Alreadyset= false;
    private File dir;
    private File[] files;
    private int filesCount;
    private String Filename;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume(){
        super.onResume();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                if ( PermissionsStatus()) {
                    requestPermissions(PERMISSIONS, get_permissions);
                    return;
                }

            if(!Alreadyset) {
                Alreadyset=true;
                dirName = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                dir = new File(dirName);
                files = dir.listFiles();
                final TextView dirpath = findViewById(R.id.dirpath);
                dirpath.setText("Path: "+dirName);
                filesCount= files.length;
                final ListView listView = findViewById(R.id.listview1);
                final TextView infopane= findViewById(R.id.infopane);
                final Adapter Adapter1= new Adapter();
                Calendar cal= Calendar.getInstance(TimeZone.getTimeZone("EST"));
                Date currentLocalTime= cal.getTime();
                DateFormat date = new SimpleDateFormat("E, MM-dd-yyyy, HH:mm a");
                String localTime = date.format(currentLocalTime);
                listView.setAdapter(Adapter1);


                List<String> filesList = new ArrayList<>();
                for(int i=0;i<filesCount ;i++){
                    filesList.add(String.valueOf(files[i].getAbsolutePath()));
                }
                Adapter1.setlist1(filesList);
                infopane.setText("Total items: "+files.length+System.lineSeparator()+"Current Local Date and Time: "+localTime);


                // Refreshing the list of files
                Button refresh = findViewById(R.id.refresh);
                refresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        files = dir.listFiles();
                        filesCount= files.length;
                        filesList.clear();
                        for(int i=0;i<filesCount ;i++){
                                    filesList.add(String.valueOf(files[i].getAbsolutePath()));
                                }
                        Adapter1.setlist1(filesList);
                        // (Ignore) Backup Code if the above doesn't work.

                     //   final String dirName = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                    //    File dir = new File(dirName);
                    //    final File[] files = dir.listFiles();
                     //   final int filesCount= files.length;
                      //  List<String> filesList = new ArrayList<>();
                     //   for(int i=0;i<filesCount ;i++){
                    //        filesList.add(String.valueOf(files[i].getAbsolutePath()));
                    //    }
                    //    Adapter1.setlist1(filesList);
                        Calendar cal= Calendar.getInstance(TimeZone.getTimeZone("EST"));
                        Date currentLocalTime= cal.getTime();
                        DateFormat date = new SimpleDateFormat("E, MM-dd-yyyy, HH:mm a");
                        String localTime = date.format(currentLocalTime);
                        infopane.setText("Total items: "+files.length+System.lineSeparator()+"Current Local Date and Time: "+localTime);

                    }
                });

                Button back = findViewById(R.id.back);
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String PreviousDirectory = dirName.substring(0,dirName.lastIndexOf('/'));
                        dir=new File(PreviousDirectory);
                        refresh.callOnClick();
                        dirpath.setText("Path: "+dir.getAbsolutePath());

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dirName= files[position].getAbsolutePath();
                        Filename= dirName.substring(dirName.lastIndexOf('/'),dirName.length()-1);
                        infopane.setText(Filename);
                        if(Filename.contains("jpg")){
                            return;
                        }
                        dir= new File(dirName);
                        refresh.callOnClick();
                        dirpath.setText("Path: "+dir.getAbsolutePath());
                    }
                });


            }
    }
    //@Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void userPermissionStatus(final int requestCode, final String[] permissions, final int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode==9999&&grantResults.length>0){
            if(PermissionsStatus()){
                final TextView tview1= findViewById(R.id.perror);
                tview1.setText("Storage Permission is required to use this app!");
            }
            else{
                onResume();
            }
        }
    }
}