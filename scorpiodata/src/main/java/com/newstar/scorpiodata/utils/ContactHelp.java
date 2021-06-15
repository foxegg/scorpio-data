package com.newstar.scorpiodata.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.Nullable;

import com.newstar.scorpiodata.entity.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactHelp {
    public String contactName;
    public String phoneNumber;
    public String sendToVoicemail;
    public String starred;
    public String pinned;
    public String photoFileId;
    public String inDefaultDirectory;
    public String inVisibleGroup;
    public String isUserProfile;
    public String contactLastUpdatedTimestamp;
    public String groups;
    public String timesContacted;
    public String lastTimeContacted;

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setSendToVoicemail(String sendToVoicemail) {
        this.sendToVoicemail = sendToVoicemail;
    }

    public void setStarred(String starred) {
        this.starred = starred;
    }

    public void setPinned(String pinned) {
        this.pinned = pinned;
    }

    public void setPhotoFileId(String photoFileId) {
        this.photoFileId = photoFileId;
    }

    public void setInDefaultDirectory(String inDefaultDirectory) {
        this.inDefaultDirectory = inDefaultDirectory;
    }

    public void setInVisibleGroup(String inVisibleGroup) {
        this.inVisibleGroup = inVisibleGroup;
    }

    public void setIsUserProfile(String isUserProfile) {
        this.isUserProfile = isUserProfile;
    }

    public void setContactLastUpdatedTimestamp(String contactLastUpdatedTimestamp) {
        this.contactLastUpdatedTimestamp = contactLastUpdatedTimestamp;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public void setTimesContacted(String timesContacted) {
        this.timesContacted = timesContacted;
    }

    public void setLastTimeContacted(String lastTimeContacted) {
        this.lastTimeContacted = lastTimeContacted;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj)||phoneNumber.equals(((ContactHelp)obj).phoneNumber);
    }

    /**
     * 加载数据
     *
     * @return
     */
    public static List<ContactHelp> getDataList(ContentResolver resolver) {
        Log.e("luolaigang","getDataList");
        List<ContactHelp> contactsList = new ArrayList<>();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, ContactsContract.Contacts.IN_VISIBLE_GROUP+"=1 and "+ ContactsContract.Contacts.HAS_PHONE_NUMBER+" =1",
                null, ContactsContract.Contacts._ID + " LIMIT 1000");
        Log.e("luolaigang","getDataList getCount:"+cursor.getCount());
        if (cursor != null) {
            try{
                while(cursor.moveToNext()) {
                    try{
                        String id=cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts._ID));
                        ContactHelp tmpObj = new ContactHelp();
                        tmpObj.setContactName(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                        tmpObj.setSendToVoicemail(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.SEND_TO_VOICEMAIL)));
                        tmpObj.setStarred(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.STARRED)));
                        tmpObj.setPinned(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PINNED)));
                        tmpObj.setPhotoFileId(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_FILE_ID)));
                        tmpObj.setInDefaultDirectory( cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.IN_DEFAULT_DIRECTORY)));
                        tmpObj.setInVisibleGroup(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.IN_VISIBLE_GROUP)));
                        tmpObj.setIsUserProfile(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.IS_USER_PROFILE)));
                        tmpObj.setContactLastUpdatedTimestamp(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP)));
                        tmpObj.setGroups(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                        tmpObj.setTimesContacted(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.TIMES_CONTACTED)));
                        tmpObj.setLastTimeContacted(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED)));
                        Cursor phoneCursor = resolver.query(
                                android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+id, null, null);
                        while(phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(
                                    phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            tmpObj.setPhoneNumber(phoneNumber);
                        }
                        phoneCursor.close();
                        contactsList.add(tmpObj);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(!cursor.isClosed()){
                    cursor.close();
                }
            }
        }
        return contactsList;
    }

    /**
     * 获取所有的 联系人分组信息
     *
     * @return
     */
    private static Map<Integer, String> getAllGroupInfo(ContentResolver resolver) {

        Map<Integer, String> groupList = new HashMap<Integer, String>();

        Cursor cursor = null;

        try {
            cursor = resolver.query(ContactsContract.Groups.CONTENT_URI,
                    null, null, null, null);

            while (cursor.moveToNext()) {
                int groupId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Groups._ID)); // 组id
                String groupName = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Groups.TITLE)); // 组名

                groupList.put(groupId, groupName);
            }

            return groupList;

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public static Contact getContactPhone(Cursor cursor, Context context) {
        int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum = cursor.getInt(phoneColumn);
        Contact contact = new Contact();
        if (phoneNum > 0) {
            // 获得联系人的ID号
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idColumn);
            // 获得联系人电话的cursor
            Cursor phone = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                            + contactId, null, null);
            if (phone.moveToFirst()) {
                for (; !phone.isAfterLast(); phone.moveToNext()) {
                    int phoneColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int displayNameColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    String phoneNumber = phone.getString(phoneColumnIndex);
                    String displayName = phone.getString(displayNameColumnIndex);
                    if (phoneNumber != null && phoneNumber.length() > 0) {
                        contact.setName(displayName);
                        contact.setPhone(phoneNumber);
                        break;
                    }
                }
                if (!phone.isClosed()) {
                    phone.close();
                }
            }
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return contact;
    }
}
