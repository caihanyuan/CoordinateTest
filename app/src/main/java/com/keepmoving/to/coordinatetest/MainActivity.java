package com.keepmoving.to.coordinatetest;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity_TAG";
    private static final int PICK_CONTACT = 0x01;

    private static final int CONTACT_PERMISION_REQUEST = 0x01;

    private EditText mNameText;
    private EditText mPhoneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNameText = (EditText) findViewById(R.id.name_text);
        mPhoneText = (EditText) findViewById(R.id.phone_text);

        findViewById(R.id.location_btn).setOnClickListener(this);
        findViewById(R.id.contact_btn).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_CONTACT:
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        return;
                    }
                    //处理返回的data,获取选择的联系人信息
                    Uri uri = data.getData();
                    String[] contacts = getPhoneContacts(uri);
                    if (!TextUtils.isEmpty(contacts[0])) {
                        mNameText.setText(contacts[0]);
                    }
                    if (!TextUtils.isEmpty(contacts[1])) {
                        mPhoneText.setText(contacts[1]);
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CONTACT_PERMISION_REQUEST) {
            requestPickContact();
        }
    }

    private String[] getPhoneContacts(Uri uri) {
        String[] contact = new String[2];
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            //取得联系人姓名
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0] = cursor.getString(nameFieldColumnIndex);
            //取得电话号码
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
            if (phone != null && phone.moveToFirst()) {
                int numberColumn = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                contact[1] = phone.getString(numberColumn);
            }
            phone.close();
            cursor.close();
        } else {
            return null;
        }
        return contact;
    }

    private void requestPickContact() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, CONTACT_PERMISION_REQUEST);
            Log.d(TAG, "ACCESS_FINE_LOCATION not granted");
            return;
        }

        Uri uri = Uri.parse("content://contacts/people");
        Intent pickInent = new Intent(Intent.ACTION_PICK, uri);
        startActivityForResult(pickInent, PICK_CONTACT);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.location_btn:
                Intent intent = new Intent(this, LocationActivity.class);
                startActivity(intent);
                break;
            case R.id.contact_btn:
                requestPickContact();
                break;
        }
    }
}
