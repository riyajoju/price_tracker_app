package com.riya.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.riya.data.mapper.toDomain
import com.riya.domain.model.Stock
import com.riya.domain.result_handling.Result
import com.riyajoju.network.remote.stocks.StocksRemoteDataSource

class StockPagingSource(
    private val remoteDataSource: StocksRemoteDataSource
) : PagingSource<Int, Stock>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Stock> {
        val page = params.key ?: 1
        return try {
            val response = remoteDataSource.getStocks(page, params.loadSize)
            
            when (val result = response) {
                is Result.Success -> {
                    val stocks = result.data.stocks.map { it.toDomain() }.sortedByDescending { it.price }
                    LoadResult.Page(
                        data = stocks,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (result.data.hasNext) page + 1 else null
                    )
                }
                is Result.Error -> {
                    LoadResult.Error(Exception(result.exception.message))
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Stock>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
