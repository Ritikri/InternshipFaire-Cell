package com.ecell.internshipfairendeavour.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ecell.internshipfairendeavour.Employe.Application_vh;
import com.ecell.internshipfairendeavour.Employe.Student_detail_emp;
import com.ecell.internshipfairendeavour.Employe.applied_intern_md;
import com.ecell.internshipfairendeavour.Notifications.Customised.BucketRecyclerView;
import com.ecell.internshipfairendeavour.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class Hired_FragmentAdmin extends Fragment {
    BucketRecyclerView rv_internall;
    DatabaseReference drinternall;
    private Button send_email_btn,shortlist_btn,hire_btn,reject_btn,send_notif;
    FirebaseRecyclerOptions<applied_intern_md> optionsinternall;
    FirebaseRecyclerAdapter<applied_intern_md, Application_vh> adapterinternall;
String id;
    ArrayList<String> selectedStudents;
    private View no_app;
    ArrayList<String> statusStudents,key_status;
    String key,cmpid,eid;
    CheckBox all;
    ArrayList<String> all_noti,all_names,all_keys;
    String email;
    private FirebaseAuth mFirebaseAuth;
    public Hired_FragmentAdmin() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=  inflater.inflate( R.layout.fragment_hired_, container, false );
        key = getArguments().getString("id");
        cmpid=getArguments().getString( "cmpid"  );
        send_email_btn=view.findViewById( R.id.send_email_btn );
        shortlist_btn=view.findViewById( R.id.shortlist_btn );
        hire_btn=view.findViewById( R.id.hired_btn );
        reject_btn=view.findViewById( R.id.reject_btn );
        send_notif=view.findViewById( R.id.notification );
        all_names = new ArrayList<>();
        all=view.findViewById( R.id.all );
        all_noti = new ArrayList<>();
        all_keys = new ArrayList<>();

        selectedStudents = new ArrayList<>();
        statusStudents = new ArrayList<>();
        key_status = new ArrayList<>();

        rv_internall = view.findViewById(R.id.hired_rv);
        no_app=view.findViewById( R.id.no_new_notifications );
        rv_internall.showIfEmpty( no_app );
        mFirebaseAuth = FirebaseAuth.getInstance();
        rv_internall.setHasFixedSize(true);
        rv_internall.setLayoutManager(new LinearLayoutManager(container.getContext()));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Forms").child( cmpid );
        databaseReference.keepSynced(true);
        databaseReference.orderByChild("internid_status").equalTo( key+"Hired").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    applied_intern_md user = dataSnapshot1.getValue(applied_intern_md.class);


                    all_noti.add(user.getUserid());
                    all_names.add(user.getUsername());
                    all_keys.add(user.getId());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
                    drinternall = FirebaseDatabase.getInstance().getReference().child("Forms").child( cmpid);
                    drinternall.keepSynced(true);
                    Query query = drinternall.orderByChild("internid_status").equalTo(key+"Hired");

                    optionsinternall = new FirebaseRecyclerOptions.Builder<applied_intern_md>().setQuery(query,applied_intern_md.class).build();

                    adapterinternall = new FirebaseRecyclerAdapter<applied_intern_md, Application_vh>(optionsinternall) {
                        @Override
                        protected void onBindViewHolder(@NonNull final Application_vh holder, int position, @NonNull final applied_intern_md model) {
                            holder.cmpname.setText(model.getUsername());
                            holder.cmpsubname.setText(model.getUsernumber());
                        //    Picasso.get().load(model.getUserimg()).into(holder.cmpimg);
                            holder.checkBox.setChecked(model.isSelected());
id=model.getInternid();
                            if (model.getUserimg()!=null){

                                Picasso.get().load(model.getUserimg()).resize(400,400).into(holder.cmpimg);

                            }
                            else {
                                holder.cmpimg.setImageResource(R.drawable.user);

                            }
                            holder.checkBox.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(holder.checkBox.isChecked()) {
                                        model.setSelected(true);
                                        selectedStudents.add(model.getUseremail());
                                        statusStudents.add(model.getUserid());
                                        key_status.add(model.getId());
                                    }
                                    else if (!holder.checkBox.isChecked()) {
                                        model.setSelected(false);
                                        selectedStudents.remove(model.getUseremail());
                                        statusStudents.remove( model.getUserid() );
                                        key_status.remove(model.getId());
                                    }
                                }
                            } );
                            send_notif.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if(statusStudents.isEmpty()){

                                        Toast.makeText(getActivity(), "You have not selected any student", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                           //             Intent intent = new Intent( getActivity(), MainActivity.class );
                           //             intent.putStringArrayListExtra( "uid_noti", statusStudents );
                            //            startActivity( intent );
                                    }
                                }
                            } );

                            holder.profile.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), Student_detail_emp.class);
                                    intent.putExtra("id",model.getInternid());
                                    intent.putExtra( "userid",model.getUserid() );
                                    intent.putExtra("cmpid",model.getCompanyid());
                                    Log.d("has","thi0"+model.getUserid() );
                                    startActivity( intent );
                                }
                            } );
                        }

                        @NonNull
                        @Override
                        public Application_vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            return new Application_vh(LayoutInflater.from(view.getContext()).inflate(R.layout.application_vh, parent,false));
                        }
                    };
                    rv_internall.setAdapter(adapterinternall);
                    adapterinternall.startListening();

        all.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!all.isChecked()){
                    Query query = drinternall.orderByChild("internid_status").equalTo(key+"Hired");
                    optionsinternall = new FirebaseRecyclerOptions.Builder<applied_intern_md>().setQuery(query,applied_intern_md.class).build();

                    adapterinternall = new FirebaseRecyclerAdapter<applied_intern_md, Application_vh>(optionsinternall) {
                        @Override
                        protected void onBindViewHolder(@NonNull final Application_vh holder, int position, @NonNull final applied_intern_md model) {
                            holder.cmpname.setText(model.getUsername());
                            holder.cmpsubname.setText(model.getUsernumber());
                            //Picasso.get().load(model.getUserimg()).into(holder.cmpimg);
                            holder.checkBox.setChecked(model.isSelected());
                            id=model.getInternid();

                            if (model.getUserimg()!=null){

                                Picasso.get().load(model.getUserimg()).resize(400,400).into(holder.cmpimg);

                            }
                            else {
                                holder.cmpimg.setImageResource(R.drawable.user);

                            }
                            selectedStudents.clear();
                            statusStudents.clear();
                            key_status.clear();
                            holder.checkBox.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(holder.checkBox.isChecked()) {
                                        model.setSelected(true);
                                        selectedStudents.add(model.getUseremail());
                                        Log.d("has","CHI"+selectedStudents.toString() );
                                        statusStudents.add(model.getUserid());
                                        key_status.add(model.getId());
                                    }
                                    else if (!holder.checkBox.isChecked()) {
                                        model.setSelected(false);
                                        selectedStudents.remove(model.getUseremail());
                                        statusStudents.remove( model.getUserid() );
                                        key_status.remove(model.getId());
                                    }
                                }
                            } );
                            holder.profile.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(getActivity(), Student_detail_emp.class);
                                    intent.putExtra("id",key);
                                    intent.putExtra( "userid",model.getUserid() );
                                    intent.putExtra("cmpid",model.getCompanyid());
                                    Log.d("has","thi0"+model.getUserid() );
                                    Log.d("has","CHECKK "+key );
                                    startActivity( intent );
                                }
                            } );



                            send_notif.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if(statusStudents.isEmpty()){

                                        Toast.makeText(getActivity(), "You have not selected any student", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                               //         Intent intent = new Intent( getActivity(), MainActivity.class );
                              //          intent.putStringArrayListExtra( "uid_noti", statusStudents );
                             //           startActivity( intent );
                                    }
                                }
                            } );
                            send_email_btn.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(selectedStudents.isEmpty()){

                                        Toast.makeText(getActivity(), "You have not selected any student", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Intent intent = new Intent( getActivity(), Email_sendpopupAdmin.class );

                                        intent.putStringArrayListExtra( "emails", selectedStudents );

                                        startActivity( intent );

                                    }
                                }
                            } );


                        }

                        @NonNull
                        @Override
                        public Application_vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            return new Application_vh(LayoutInflater.from(view.getContext()).inflate(R.layout.application_vh, parent,false));
                        }
                    };
                    rv_internall.setAdapter(adapterinternall);
                    adapterinternall.startListening();
                }
                else{
                    selectedStudents.clear();
                    selectedStudents.addAll( all_names );
                    statusStudents.clear();
                    statusStudents.addAll( all_noti );
                    key_status.clear();
                    key_status.addAll( all_keys );

                    Query query = drinternall.orderByChild("internid_status").equalTo(key+"Hired");
                    optionsinternall = new FirebaseRecyclerOptions.Builder<applied_intern_md>().setQuery(query,applied_intern_md.class).build();

                    adapterinternall = new FirebaseRecyclerAdapter<applied_intern_md, Application_vh>(optionsinternall) {
                        @Override
                        protected void onBindViewHolder(@NonNull final Application_vh holder, int position, @NonNull final applied_intern_md model) {
                            holder.cmpname.setText(model.getUsername());
                            holder.cmpsubname.setText(model.getUsernumber());
                            //Picasso.get().load(model.getUserimg()).into(holder.cmpimg);
                            //  holder.checkBox.setChecked(model.isSelected());
                            holder.checkBox.setChecked(true);
                            id=model.getInternid();

                            if (model.getUserimg()!=null){

                                Picasso.get().load(model.getUserimg()).resize(400,400).into(holder.cmpimg);

                            }
                            else {
                                holder.cmpimg.setImageResource(R.drawable.user);

                            }

                            holder.checkBox.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(holder.checkBox.isChecked()) {
                                        model.setSelected(true);
                                        selectedStudents.add(model.getUseremail());
                                        Log.d("has","CHI"+selectedStudents.toString() );
                                        statusStudents.add(model.getUserid());
                                        key_status.add(model.getId());
                                    }
                                    else if (!holder.checkBox.isChecked()) {
                                        model.setSelected(false);
                                        selectedStudents.remove(model.getUseremail());
                                        statusStudents.remove( model.getUserid() );
                                        key_status.remove(model.getId());
                                    }
                                }
                            } );
                            holder.profile.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(getActivity(), Student_detail_emp.class);
                                    intent.putExtra("id",key);
                                    intent.putExtra( "userid",model.getUserid() );
                                    intent.putExtra("cmpid",model.getCompanyid());
                                    Log.d("has","thi0"+model.getUserid() );
                                    Log.d("has","CHECKK "+key );
                                    startActivity( intent );
                                }
                            } );



                            send_notif.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if(statusStudents.isEmpty()){

                                        Toast.makeText(getActivity(), "You have not selected any student", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                 //       Intent intent = new Intent( getActivity(), MainActivity.class );
                                 //       intent.putStringArrayListExtra( "uid_noti", statusStudents );
                                 //       startActivity( intent );
                                    }
                                }
                            } );
                            send_email_btn.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(selectedStudents.isEmpty()){
                                        Log.d("has","EMAILS"+selectedStudents.toString() );
                                        Toast.makeText(getActivity(), "You have not selected any student", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Intent intent = new Intent( getActivity(), Email_sendpopupAdmin.class );

                                        intent.putStringArrayListExtra( "emails", selectedStudents );

                                        startActivity( intent );

                                    }
                                }
                            } );


                        }

                        @NonNull
                        @Override
                        public Application_vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            return new Application_vh(LayoutInflater.from(view.getContext()).inflate(R.layout.application_vh, parent,false));
                        }
                    };
                    rv_internall.setAdapter(adapterinternall);
                    adapterinternall.startListening();


                }
            }
        } );

        shortlist_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (statusStudents.isEmpty()) {

                    Toast.makeText( container.getContext(), "You have not selected any student", Toast.LENGTH_SHORT ).show();
                } else {
               //     Toast.makeText( container.getContext(), statusStudents.toString(), Toast.LENGTH_SHORT ).show();
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    for (String userid : statusStudents) {
                        rootRef.child( "Formsself" ).child( userid ).child( id ).child( "status" ).setValue( "Shortlisted" );


                    }
                    for (String keys : key_status) {

                        rootRef.child( "Forms" ).child( cmpid ).child( keys ).child( "status" ).setValue( "Shortlisted" );
                        rootRef.child( "Forms" ).child( cmpid ).child( keys ).child( "internid_status" ).setValue( key + "Shortlisted" );

                    }
                    //-------------------------------------------------Here is code for Deleting---------------------------------------------------------------------
                    statusStudents.clear();
                    key_status.clear();
                    all.setChecked(false);
                    getActivity().recreate();
                    //------
                }

            }
        } );
        hire_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusStudents.isEmpty()) {

                    Toast.makeText( container.getContext(), "You have not selected any student", Toast.LENGTH_SHORT ).show();
                } else {
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    for (String userid : statusStudents) {
                        rootRef.child( "Formsself" ).child( userid ).child( id ).child( "status" ).setValue( "Hired" );

                    }
                    for (String keys : key_status) {

                        rootRef.child( "Forms" ).child( cmpid ).child( keys ).child( "status" ).setValue( "Hired" );
                        rootRef.child( "Forms" ).child( cmpid ).child( keys ).child( "internid_status" ).setValue( key + "Hired" );
                    }
                    //-------------------------------------------------Here is code for Deleting---------------------------------------------------------------------
                    statusStudents.clear();
                    key_status.clear();
                    all.setChecked(false);
                    getActivity().recreate();
                    //------
                }
            }
        } );
        reject_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusStudents.isEmpty()) {

                    Toast.makeText( container.getContext(), "You have not selected any student", Toast.LENGTH_SHORT ).show();
                } else {
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    for (String userid : statusStudents) {
                        rootRef.child( "Formsself" ).child( userid ).child( id ).child( "status" ).setValue( "Rejected" );

                    }
                    for (String keys : key_status) {

                        rootRef.child( "Forms" ).child( cmpid ).child( keys ).child( "status" ).setValue( "Rejected" );
                        rootRef.child( "Forms" ).child( cmpid ).child( keys ).child( "internid_status" ).setValue( key + "Rejected" );
                    }
                    //-------------------------------------------------Here is code for Deleting---------------------------------------------------------------------
                    statusStudents.clear();
                    key_status.clear();
                    all.setChecked(false);
                    getActivity().recreate();
                    //------
                }
            }
        } );
        send_email_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedStudents.isEmpty()) {

                    Toast.makeText( container.getContext(), "You have not selected any student", Toast.LENGTH_SHORT ).show();
                } else {
                    Intent intent = new Intent( getActivity(), Email_sendpopupAdmin.class );

                    intent.putStringArrayListExtra( "emails", selectedStudents );

                    startActivity( intent );
                }
            }
        } );

        return view;
    }

}
