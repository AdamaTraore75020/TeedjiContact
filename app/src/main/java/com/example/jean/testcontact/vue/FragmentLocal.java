package com.example.jean.testcontact.vue;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jean.testcontact.R;
import com.example.jean.testcontact.adapter.ContactAdapter;
import com.example.jean.testcontact.modele.Contact;

import java.util.ArrayList;

/**
 * Created by JEAN on 17/05/2017.
 */

public class FragmentLocal extends Fragment {
    private ArrayList<Contact> mesContacts;
    private RecyclerView rv;
    private ContactAdapter contactAdapter;
    private Cursor cursor;
    private static final int PERMISSIONS_REQUEST = 100;

    public FragmentLocal() {
        //
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local, container, false);

        mesContacts = new ArrayList<>();
        //Appel de la méthode qui gère les findViewByID
        retrieveView(view);

        //Méthode gère l'affichage des contacts du téléphone
        affichContact();

        return view;
    }

    /**
     * Methode qui gère les findViewByID
     */
    private void retrieveView(View view) {
        rv = (RecyclerView) view.findViewById(R.id.idRecyclerLocal);
    }


    /**
     * Méthoe qui gère la création et l'affichage de la liste déroulante
     */
    private void affichContact() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST);
        }else {
            mesContacts = recupAllPhoneContacts();
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            rv.setLayoutManager(layoutManager);
            contactAdapter = new ContactAdapter(mesContacts, getActivity());
            rv.setAdapter(contactAdapter);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée
                affichContact();
            } else {
                Toast.makeText(getActivity(), "Sans votre permission la liste des contacts ne peut être affichée", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Recupere tous les contacts du telephone
     * @return
     */
    private ArrayList<Contact> recupAllPhoneContacts(){
        ArrayList<Contact> allContacts = new ArrayList<>();

        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, null);
        while(cursor.moveToNext())
        {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String tel = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            allContacts.add(new Contact(null, name, name, tel));
        }
        return allContacts;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(cursor != null){
            cursor.close();
        }
    }
}
