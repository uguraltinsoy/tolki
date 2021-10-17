package com.deeplabstudio.tolki;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deeplabstudio.tolki.Adapter.Contacts.Account;
import com.deeplabstudio.tolki.Adapter.Contacts.ContactsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ContacsFragment extends DialogFragment {

    private ArrayList<Account> accounts;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mContacts;
    private ContactsAdapter adapter;
    private SearchView mSearchView;

    public ContacsFragment() {}

    @Override
    public int getTheme() {
        return R.style.FullScreenDialog;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contacs_fragment, container, false);
        rootView.findViewById(R.id.mBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        mContacts = rootView.findViewById(R.id.mContacts);
        mContacts.setHasFixedSize(true);
        mContacts.setLayoutManager(new LinearLayoutManager(getActivity()));

        createUsersList();

        mSearchView = rootView.findViewById(R.id.mSearchView);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!s.equals("")){
                    filter(s);
                }else {
                    adapter = new ContactsAdapter(accounts);
                    mContacts.setAdapter(adapter);
                }
                return false;
            }
        });
        return rootView;
    }

    private void createUsersList(){
        try{
            String UID = FirebaseAuth.getInstance().getUid();
            accounts = new ArrayList<>();
            db.collection("Accounts")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String uid = document.getString("uid");
                                    String name = document.getString("name");
                                    String image = document.getString("image");
                                    System.out.println("BB " + name);
                                    if (!UID.equals(uid)) accounts.add(new Account(uid,name,image));
                                }
                                adapter = new ContactsAdapter(accounts);
                                mContacts.setAdapter(adapter);
                            } else {
                                System.out.println("FB ERROR");
                            }
                        }
                    });
        }catch (Exception e){System.out.println(e.getMessage());}
    }

    private void filter(String text) {
        ArrayList<Account> filteredList = new ArrayList<>();
        for (Account item : accounts) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    @Override
    public void onStart(){
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
