package com.example.alphaver;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FBref {

    public static final FirebaseStorage storage = FirebaseStorage.getInstance();
    public static final StorageReference storageRef = storage.getReference();

    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    //public static final DatabaseReference fileRef = database.getReference("files");
}
