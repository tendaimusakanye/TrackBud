package com.tendai.common.data

import com.tendai.common.FakeAlbumDataSource
import com.tendai.common.source.AlbumsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AlbumsRepositoryTest {

    private val testDispatcher = TestCoroutineDispatcher()
    private var albumsRepository: AlbumsRepository? = null

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        albumsRepository =
            AlbumsRepository(FakeAlbumDataSource())

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun getAlbums() {
        // runBlocking is needed when you need to call suspending functions I guess ?
        //with these tests neh... I am not entirely sure kkkkkkk
        val allAlbums = albumsRepository?.getAlbums(2)

        Assert.assertEquals("Beer bottles and Bongs", allAlbums?.get(1)?.albumTitle)
        Assert.assertEquals(2, allAlbums?.get(1)?.artistId)
    }

}