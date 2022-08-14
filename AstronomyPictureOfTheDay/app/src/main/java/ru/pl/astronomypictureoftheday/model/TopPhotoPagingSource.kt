package ru.pl.astronomypictureoftheday.model

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.pl.astronomypictureoftheday.model.api.TopPhotoApi
import ru.pl.astronomypictureoftheday.model.room.FavouritePhotoRepository
import ru.pl.astronomypictureoftheday.utils.SERVER_DATE_FORMAT
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private const val TAG = "TopPhotoPagingSource"

class TopPhotoPagingSource(
    private val topPhotoApi: TopPhotoApi
) : PagingSource<Int, TopPhotoResponse>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TopPhotoResponse> {
        return try {
            val pageIndex = params.key ?: 1

            val period = getPeriod(params.loadSize, pageIndex)
            //переводит TopPhotoResponse в FavouritePhoto,
            // далее если есть запись в БД, заменяем в списке
            val favouritePhotoList: List<TopPhotoResponse> = topPhotoApi
                .fetchTopPhotos(period.first, period.second)
                .reversed()
            Log.d(TAG, "${period.first} : ${period.second} -> pageIndex: $pageIndex")
            Log.d(TAG, "photosListSize: ${favouritePhotoList.size}")

            val prev = if (pageIndex == 1) null else pageIndex - 1
            val next = if (favouritePhotoList.size == params.loadSize) pageIndex + 1 else null

            LoadResult.Page(favouritePhotoList, prev, next)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TopPhotoResponse>): Int? {
        // get the most recently accessed index in the users list:
        val anchorPosition = state.anchorPosition ?: return null
        // convert item index to page index:
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        // page doesn't have 'currentKey' property, so need to calculate it manually:
        return page.nextKey?.minus(1) ?: page.prevKey?.plus(1)
    }

    private fun getPeriod(size: Int, pageIndex: Int): Pair<String, String> {
        val nowDateTime = LocalDateTime.now()

        val start = nowDateTime.minusDays((size * pageIndex).toLong())
        val end = start.plusDays(size.toLong())

        Log.d(TAG, "pageSize from getPeriod(): ${ChronoUnit.DAYS.between(start, end)}")

        /*val sdf = SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("GMT+3")*/

        val formatter = DateTimeFormatter.ofPattern(SERVER_DATE_FORMAT)
        val startDate = (start.plusDays(1)).format(formatter)
        val endDate = end.format(formatter)

        return Pair(startDate, endDate)
    }
}