package ru.pl.astronomypictureoftheday.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.*
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class LanguageTranslator() {

    private val enRuTranslator: Translator

    init {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.RUSSIAN)
            .build()
        enRuTranslator = Translation.getClient(options)
    }

    private suspend fun downloadModelIfNeeded() {
        return suspendCoroutine {
            enRuTranslator.downloadModelIfNeeded()
                .addOnSuccessListener { _ ->
                    it.resumeWith(Result.success(Unit))
                }
                .addOnFailureListener { exception ->
                    it.resumeWith(Result.failure(exception))
                }
        }
    }

    suspend fun translate(text: String): String {
        downloadModelIfNeeded()
        return suspendCoroutine {
            enRuTranslator.translate(text)
                .addOnSuccessListener { translatedText ->
                    it.resumeWith(Result.success(translatedText))
                }
                .addOnFailureListener { exception ->
                    it.resumeWith(Result.failure(exception))
                }
        }
    }

    suspend fun isRuModelDownloaded(): Boolean {
        return suspendCoroutine {
            val modelManager = RemoteModelManager.getInstance()
            val ruModel = TranslateRemoteModel.Builder(TranslateLanguage.RUSSIAN).build()
            modelManager.isModelDownloaded(ruModel).addOnSuccessListener { success ->
                it.resumeWith(Result.success(success))
            }
        }
    }

    fun close() {
        enRuTranslator.close()
    }


}