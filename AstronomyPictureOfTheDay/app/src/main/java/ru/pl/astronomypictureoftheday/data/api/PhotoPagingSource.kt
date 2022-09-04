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

    private var validateNotLastPage = true

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoDto> {
        return try {
            val pageIndex = params.key ?: 1

            //два варианта: либо весь список с сегодняшнего дня и назад
            //либо диапазон дат пришел, тогда берем его
            //test

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
                if (favouritePhotoList.size == params.loadSize && validateNotLastPage) pageIndex + 1
                else null

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

    private fun getPeriod(loadSize: Int, pageIndex: Int): Pair<String, String> {
        val nowDateTime = LocalDateTime.now()

        val start = nowDateTime.minusDays((loadSize * pageIndex).toLong())
        val end = start.plusDays(loadSize.toLong())

        val formatter = DateTimeFormatter.ofPattern(SERVER_DATE_FORMAT)
        val startDate = (start.plusDays(1)).format(formatter)
        val endDate = end.format(formatter)

        return Pair(startDate, endDate)
    }

    private fun getPeriod(
        loadSize: Int, pageIndex: Int, from: Long, to: Long
    ): Pair<String, String> {

        val fromDate = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(from),
            TimeZone.getDefault().toZoneId()
        )
        val toDate = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(to),
            TimeZone.getDefault().toZoneId()
        )
        var start = toDate.minusDays((loadSize * pageIndex).toLong())
        val end = start.plusDays(loadSize.toLong())
        start = start.plusDays(1)

        if (start < fromDate) {
            start = fromDate
        }

        Log.d(TAG, "start: $start, end: $end")
        Log.d(TAG, "pageSize from getPeriod(range): ${ChronoUnit.DAYS.between(start, end)}")

        calculateItemNumbersOdd(fromDate, toDate, loadSize, pageIndex)

        val formatter = DateTimeFormatter.ofPattern(SERVER_DATE_FORMAT)
        val startDate = start.format(formatter)
        val endDate = end.format(formatter)

        return Pair(startDate, endDate)
    }

    // расчет дополнительного условия nextPage. При числе страниц == loadSize || %loadSize == 0
    // То есть если loadSize == 10, то при числе страницы 10, 20, 30 и так далее
    // для nextPage нужно доп условие, чтобы выдавал null
    private fun calculateItemNumbersOdd(
        fromDate: LocalDateTime?,
        toDate: LocalDateTime?,
        loadSize: Int,
        pageIndex: Int
    ) {
        val daysBetween = (ChronoUnit.DAYS.between(fromDate, toDate) + 1).toInt()
        val pageCount = daysBetween / loadSize
        if (pageCount == pageIndex)
            validateNotLastPage = (daysBetween % loadSize) != 0
    }

}