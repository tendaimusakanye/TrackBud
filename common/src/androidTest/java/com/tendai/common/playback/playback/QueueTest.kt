package com.tendai.common.playback.playback

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tendai.common.TRACKS_ROOT
import com.tendai.common.playback.MainCoroutineRule
import com.tendai.common.playback.Queue
import com.tendai.common.source.Repository
import com.tendai.common.source.TracksRepository
import com.tendai.common.source.local.LocalDataSource
import com.tendai.common.source.local.LocalTracksDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class QueueTest {

    private lateinit var coroutineScope: CoroutineScope
    private lateinit var tracksRepository: Repository.Tracks
    private lateinit var dispatcher: CoroutineDispatcher
    private lateinit var tracksDataSource: LocalDataSource.Tracks

    //Using the concrete Queue class for tests.
    private lateinit var queue: Queue

    private var trackId: Long = -1

    private val bundle = Bundle().apply {
        putBoolean(TRACKS_ROOT, true)
    }

    @get: Rule
    var coroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        dispatcher = TestCoroutineDispatcher()
        tracksDataSource = LocalTracksDataSource(appContext)
        tracksRepository = TracksRepository(tracksDataSource, dispatcher)
        coroutineScope = coroutineRule
        queue = Queue(coroutineScope, tracksRepository)
    }

    @Test
    fun givenValidTrackIdAndBundle_BuildProperQueue() = coroutineRule.runBlockingTest {
        //QueueManager#onQueueChanged is initialized when the service is created.Therefore for in tests since there is no service creating,
        // it is important to initialize the property before calling any methods that invoke the property.
        // However further assertions have to be done here.
        queue.onQueueChangedListener { title, queueItems ->
            assertEquals(title, "Tracks")
            assertEquals(queueItems.size, 16)
        }

        //given
        trackId = 18

        //when the queue is created for the first time
        queue.buildQueue(trackId, bundle)

        //then
        assertEquals(queue.currentIndex, 0)
        assertEquals(queue.playingQueue.size, 289)
    }

    @Test
    fun givenTrackIdInQueue_checkIdAndDoNothing() = coroutineRule.runBlockingTest {
        //create queue for the first time
        givenValidTrackIdAndBundle_BuildProperQueue()

        //given trackId which already exists in the playingQueue
        trackId = 75983

        //when
        queue.buildQueue(trackId, bundle)

        //then
        assertEquals(queue.isTrackAlreadyInQueue(trackId), true)
    }


    @Test
    fun givenInvalidTrackId_BuildInternalPlayingQueueOnly() = coroutineRule.runBlockingTest {
        //given
        trackId = -194032L

        //when
        queue.buildQueue(trackId, bundle)

        //then
        assertEquals(queue.currentIndex, -1)
        assertEquals(queue.playingQueue.size, 289)
    }

    @Test
    fun givenEmptyBundle_BuildNoQueue() = coroutineRule.runBlockingTest {
        //given
        trackId = 13L

        //when
        queue.buildQueue(trackId, bundleOf())

        //then
        assertEquals(queue.currentIndex, -1)
        assertEquals(queue.playingQueue.size, 0)
    }

    @Test
    fun skipToNext_doesNotCreateNewQueueWindow() = coroutineRule.runBlockingTest {
        //create initial queue
        givenValidTrackIdAndBundle_BuildProperQueue()

        //when Queue#currentIndex is within the SlidingWindow#queueWindow bounds i.e. { queueWindow LowerBound < currentIndex <= queueWindow UpperBound }
        for (i in 1..15) queue.skipToNext()

        //then
        assertEquals(queue.currentIndex, 15)
        assertEquals(queue.slidingWindow.start, 0)
        assertEquals(queue.slidingWindow.end, 16)
    }

    @Test
    fun skipToNext_createsNewQueueWindow() = coroutineRule.runBlockingTest {
        //If the Queue#playingQueue is smaller than the WINDOW_CAPACITY a new SlidingWindow#queueWindow will never be created.
        //create initial queue
        givenValidTrackIdAndBundle_BuildProperQueue()

        //move to end of SlidingWindow#queueWindow
        skipToNext_doesNotCreateNewQueueWindow()

        //when
        queue.skipToNext()

        //then
        assertEquals(queue.currentIndex, 16)
        assertNotEquals(queue.slidingWindow.start, 0)

    }


    @Test
    fun skipToNext_loopsPlayingQueue() {
        //create initial queue
        givenValidTrackIdAndBundle_BuildProperQueue()

        //when skipToNext is invoked indefinitely it should always loop the playing queue
        for (i in 1..350) queue.skipToNext()

        //then loop
        assertEquals(queue.currentIndex, 61)
    }

    @Test
    fun skipToPrevious_createsNewQueueWindow() = coroutineRule.runBlockingTest {
        //create initial queue
        givenValidTrackIdAndBundle_BuildProperQueue()

        //when Queue#currentIndex is within the SlidingWindow#queueWindow bounds i.e. { queueWindow LowerBound < currentIndex <= queueWindow UpperBound }
        queue.skipToPrevious()

        //then
        assertEquals(queue.currentIndex, 288)
        assertNotEquals(queue.slidingWindow.start, 0)
    }

    @Test
    fun skipToPrevious_doesNotCreateNewQueueWindow() = coroutineRule.runBlockingTest {
        //If the Queue#playingQueue is smaller than the WINDOW_CAPACITY a new SlidingWindow#queueWindow will never be created.
        //create initial queue
        givenValidTrackIdAndBundle_BuildProperQueue()

        //move to the end of the Queue#playingQueue
        skipToPrevious_createsNewQueueWindow()

        //when
        for (i in 1..15) queue.skipToPrevious()

        //then
        assertEquals(queue.currentIndex, 273)
        assertEquals(queue.slidingWindow.start, 273)
        assertEquals(queue.slidingWindow.end, 289)
    }


    @Test
    fun skipToPrevious_loopsPlayingQueue() {
        //create initial queue
        givenValidTrackIdAndBundle_BuildProperQueue()

        //when skipToPrevious is invoked indefinitely it should always loop the playing queue
        for (i in 1..350) queue.skipToPrevious()

        //then loop
        assertEquals(queue.currentIndex, 228)
    }

    @Test
    fun shuffleToNext_shufflesTracks() = coroutineRule.runBlockingTest {
        //create initial queue
        givenValidTrackIdAndBundle_BuildProperQueue()

        //when
        queue.slidingWindow.createShuffleList()
        queue.shuffleToNext()

        //then
        assertNotEquals(queue.currentIndex, 1)
    }

    @Test
    fun shuffleToNext_createsNewWindow() = coroutineRule.runBlockingTest {
//        //create initial queue
        givenValidTrackIdAndBundle_BuildProperQueue()

        //when
        queue.slidingWindow.createShuffleList()
        for (i in 1..18) queue.shuffleToNext() //nextShuffleIndexCount starts at 1

        // user skipsToNext, then enables shuffling when he/she has reached the end of the queue. A new shuffling queue is created.
//        skipToNext_doesNotCreateNewQueueWindow()
//        queue.createShuffleWindow()
//        queue.shuffleToNext()

        assertEquals(queue.slidingWindow.shuffledList.minOrNull()!!, 16)
    }

    @Test
    fun shuffleToNext_doesNotCreateNewWindow() = coroutineRule.runBlockingTest {
        givenValidTrackIdAndBundle_BuildProperQueue()

        //when
        queue.slidingWindow.createShuffleList()
        for (i in 1..16) queue.shuffleToNext()


        //then
        assertEquals(queue.slidingWindow.start, 0)
        assertEquals(queue.slidingWindow.end, 16)
    }

    @Test
    fun shuffleToPrevious_createsNewWindow() = coroutineRule.runBlockingTest {
        //given
        givenValidTrackIdAndBundle_BuildProperQueue()

        //when
        queue.createShuffleWindow()
        for (i in 0..16) queue.shuffleToPrevious()

        //then
        assertEquals(queue.slidingWindow.start, 273)

    }

    @Test
    fun shuffleToPrevious_loopsQueue() = coroutineRule.runBlockingTest {
        //given
        shuffleToNext_createsNewWindow()

        //when
        for (i in 1..17) queue.shuffleToPrevious()

        //then
        assertEquals(queue.slidingWindow.start, 0)

        //when
        for (i in 1..17) queue.shuffleToPrevious()

        //then
        assertEquals(queue.slidingWindow.start, 273)
    }


}