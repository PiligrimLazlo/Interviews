package ru.pl.astronomypictureoftheday.data.api

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.pl.astronomypictureoftheday.utils.SERVER_DATE_FORMAT
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

private const val TAG = "TopPhotoPagingSource"

class TopPhotoPagingSource(
    private val photoApi: PhotoApi,
    private val dateRange: Pair<Long, Long>? = null
) : PagingSource<Int, PhotoDto>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoDto> {
        return try {
            val pageIndex = params.key ?: 1

            //два варианта: либо весь список с сегодняшнего дня и назад
            //либо диапазон дат пришел, тогда берем его

            val period = if (dateRange == null) {
                getPeriod(params.loadSize, pageIndex)
            } else {
                getPeriod(params.loadSize, pageIndex, dateRange.first, dateRange.second)
            }

            val favouritePhotoList: List<PhotoDto> = photoApi
                .fetchTopPhotos(period.first, period.second)
                .reversed()

            Log.d(TAG, "${period.first} : ${period.second} -> pageIndex: $pageIndex")
            Log.d(TAG, "photosListSize: ${favouritePhotoList.size}")

            val prev = if (pageIndex == 1) null else pageIndex - 1
            val next =
                //todo fix когда размер dateRange==LoadSize, то он выдает не null,
                // а "следующую" несуществующую страницу и показывает кнопку try again
                if (favouritePhotoList.size == params.loadSize) pageIndex + 1 else null

            LoadResult.Page(favouritePhotoList, prev, next)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PhotoDto>): Int? {
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

        val formatter = DateTimeFormatter.ofPattern(SERVER_DATE_FORMAT)
        val startDate = (start.plusDays(1)).format(formatter)
        val endDate = end.format(formatter)

        return Pair(startDate, endDate)
    }

    private fun getPeriod(
        size: Int, pageIndex: Int, from: Long, to: Long
    ): Pair<String, String> {

        val fromDate = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(from),
            TimeZone.getDefault().toZoneId()
        )
        val toDate = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(to),
            TimeZone.getDefault().toZoneId()
        )
        var start = toDate.minusDays((size * pageIndex).toLong())
        var end = start.plusDays(size.toLong())
        start = start.plusDays(1)

        if (start < fromDate) {
            start = fromDate
        }

        Log.d(TAG, "start: $start, end: $end")
        Log.d(TAG, "pageSize from getPeriod(range): ${ChronoUnit.DAYS.between(start, end)}")


        val formatter = DateTimeFormatter.ofPattern(SERVER_DATE_FORMAT)
        val startDate = start.format(formatter)
        val endDate = end.format(formatter)

        return Pair(startDate, endDate)
    }

    companion object {
        private const val MILLIS_IN_DAY = 86400000L
    }
}