package com.example.digitaldiary.data

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class FirebaseRepository {

    private val db = Firebase.firestore

    private val diaries = db.collection("diaries")


    fun addSampleEntries(){
        diaries.get()
            .addOnSuccessListener { snapshot ->
                if(snapshot.isEmpty){
                    val sampleEntries = listOf(
                        DiaryEntry(
                            id = UUID.randomUUID().toString(),
                            title = "Spacer w Zurichu",
                            content = "Byłem dziś w Zurichu, piękne widoki!",
                            locationName = "Zurich",
                            latitude = 47.3769,
                            longitude = 8.5417,
                            imageUrl = "https://picsum.photos/id/1015/600/400",
                            audioUrl = null,
                            timestamp = System.currentTimeMillis()
                        ),
                        DiaryEntry(
                            id = UUID.randomUUID().toString(),
                            title = "Kawa nad jeziorem",
                            content = "Relaks z widokiem na wodę",
                            locationName = "Männedorf",
                            latitude = 47.2567,
                            longitude = 8.6931,
                            imageUrl = "https://picsum.photos/id/1016/600/400",
                            audioUrl = null,
                            timestamp = System.currentTimeMillis()
                        ),
                        DiaryEntry(
                            id = UUID.randomUUID().toString(),
                            title = "Spacer po lesie",
                            content = "Wyciszenie i spokój wśród drzew.",
                            locationName = "Pfannenstiel",
                            latitude = 47.3172,
                            longitude = 8.6847,
                            imageUrl = "https://picsum.photos/id/1020/600/400",
                            audioUrl = null,
                            timestamp = System.currentTimeMillis()
                        ),
                        DiaryEntry(
                            id = UUID.randomUUID().toString(),
                            title = "Wieczorny zachód słońca",
                            content = "Piękne kolory nad Männedorf. Udało mi się zrobić świetne zdjęcie!",
                            locationName = "Männedorf",
                            latitude = 47.2567,
                            longitude = 8.6931,
                            imageUrl = "https://picsum.photos/id/1024/600/400",
                            audioUrl = null,
                            timestamp = System.currentTimeMillis()
                        ),
                        DiaryEntry(
                            id = UUID.randomUUID().toString(),
                            title = "Weekend w górach",
                            content = "Wycieczka do Alp, trochę wspinaczki i dużo zdjęć!",
                            locationName = "Zermatt",
                            latitude = 46.0207,
                            longitude = 7.7491,
                            imageUrl = "https://picsum.photos/id/1035/600/400",
                            audioUrl = null,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    sampleEntries.forEach {
                        diaries.document(it.id).set(it)
                    }
                    Log.d(TAG, "Example data added")
                }else {
                    Log.d(TAG, "Database is not empty")
                }
            }
    }



    fun addEntry(diary: DiaryEntry){
            diaries.document(diary.id).set(diary)
                .addOnSuccessListener {
                        Log.d(TAG, "Adding Diary succesful")

                }.addOnFailureListener{
                        Log.d(TAG, "Error while creatind Diary")
                }
    }

    fun getEntry(id: String, onResult:(DiaryEntry?) -> Unit ){
        diaries.document(id)
            .get()
            .addOnSuccessListener { doc ->
                if(doc != null){
                    val diary = doc.toObject<DiaryEntry>()
                    Log.d(TAG, "Fetching data successful")
                    onResult(diary)
                }else{
                    Log.d(TAG, "Fetching data unsuccessful")
                    onResult(null)
                }
            }
    }

    fun getEntryById(id: String, onResult: (DiaryEntry?) -> Unit) {
        diaries.document(id).get()
            .addOnSuccessListener { doc ->
                val diary = doc.toObject<DiaryEntry>()
                onResult(diary)
            }
            .addOnFailureListener {
                Log.e(TAG, "Error fetching diary", it)
                onResult(null)
            }
    }

    fun getAllEntries(): Flow<List<DiaryEntry>> = callbackFlow {
        val listener = diaries.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Listen failed.", error)
                return@addSnapshotListener
            }

            val entries = snapshot?.documents?.mapNotNull { it.toObject(DiaryEntry::class.java) }
            trySend(entries ?: emptyList())
        }

        awaitClose { listener.remove() }
    }

    fun updateEntry(diaryEntry: DiaryEntry){
        diaries.document(diaryEntry.id).set(diaryEntry)
            .addOnSuccessListener{
                Log.e(TAG, "Diary update successful")
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed updating diary")
            }
    }

}