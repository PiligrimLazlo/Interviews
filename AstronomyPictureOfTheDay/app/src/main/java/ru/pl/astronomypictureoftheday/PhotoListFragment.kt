package ru.pl.astronomypictureoftheday

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import ru.pl.astronomypictureoftheday.api.NasaApi
import ru.pl.astronomypictureoftheday.api.NasaPhotoRepository
import ru.pl.astronomypictureoftheday.databinding.FragmentPhotoListBinding
import ru.pl.astronomypictureoftheday.utils.SERVER_DATE_FORMAT
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*

private const val TAG = "PhotoListFragment";

class PhotoListFragment : Fragment() {

    private var _binding: FragmentPhotoListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoListBinding.inflate(inflater, container, false)
        binding.photoGrid.layoutManager = GridLayoutManager(context, 3)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {

            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("GMT+3")
            val endDate = sdf.format(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, -3)
            val startDate = sdf.format((calendar.time))

            val responseOnePhoto = NasaPhotoRepository().fetchTopPhoto()
            Log.d(TAG, "${responseOnePhoto.title} : ${responseOnePhoto.imageUrl}")


            val responseSeveralPhotos = NasaPhotoRepository().fetchTopPhoto(startDate, endDate)
            Log.d(TAG, "-------------------------------------------------------")

            responseSeveralPhotos.forEach {
                Log.d(TAG, "${it.title} : ${it.imageUrl}")
            }
        }
    }
}