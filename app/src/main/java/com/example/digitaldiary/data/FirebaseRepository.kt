package com.example.digitaldiary.data

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.util.UUID

class FirebaseRepository {

    val db = Firebase.firestore

    val diaries = db.collection("diaries")

    val sampleEntry = DiaryEntry(
        id = UUID.randomUUID().toString(),
        title = "Spacer nad jeziorem",
        content = "Dzisiaj poszedłem na długi spacer nad jeziorem. Było spokojnie i relaksująco.",
        locationName = "Männedorf",
        latitude = 47.2567,
        longitude = 8.6931,
        imageUrl = null,
        audioUrl = null,
        timestamp = System.currentTimeMillis()
    )

    fun addEntry(diary: DiaryEntry){
            diaries.document(sampleEntry.id).set(diary)
                .addOnSuccessListener {
                        Log.d(TAG, "Adding Diary succesful")

                }.addOnFailureListener{
                        Log.d(TAG, "Error while creatind Diary")
                }
    }

    fun getEntry(id: String){
        diaries.document(id)
            .get()
            .addOnSuccessListener { doc ->
                if(doc != null){
                    val diary = doc.toObject<DiaryEntry>()
                    Log.d(TAG, "Fetched data: ${doc.data}")
                }else{
                    Log.d(TAG, "Fetching data unsuccessful")
                }
            }
    }

    fun getAllEntries(){
        diaries.get()
            .addOnSuccessListener {  }

        return
    }
}